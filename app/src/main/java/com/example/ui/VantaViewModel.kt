package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VantaRepository
import com.example.model.AgencyPackage
import com.example.model.AgencySettings
import com.example.model.DeliverableItem
import com.example.model.EmailLog
import com.example.model.InAppNotification
import com.example.model.Order
import com.example.model.OrderMessage
import com.example.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object ClientDashboard : Screen()
    object AdminDashboard : Screen()
    object CreateOrder : Screen()
    data class OrderDetail(val orderId: String) : Screen()
    object SettingsAndPolicies : Screen()
    object Auth : Screen()
}

class VantaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VantaRepository.getRepository(application)

    val currentUser: StateFlow<User?> = repository.currentUser

    // Start with Auth / Login screen first as requested
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Auth)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val backStack = mutableListOf<Screen>()

    // Search and filter state
    val searchQuery = MutableStateFlow("")
    val selectedStatusFilter = MutableStateFlow("All")
    val selectedSortOrder = MutableStateFlow("Newest") // "Newest", "Oldest", "Due Date"

    // Raw orders from repo
    private val rawOrders: StateFlow<List<Order>> = repository.getOrdersForCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All orders for admin
    val allOrdersForAdmin: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered orders
    val filteredOrders: StateFlow<List<Order>> = combine(
        rawOrders,
        searchQuery,
        selectedStatusFilter,
        selectedSortOrder
    ) { orders, query, status, sort ->
        var list = orders
        if (status != "All") {
            list = list.filter { it.status.equals(status, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.orderId.lowercase().contains(q) ||
                it.productName.lowercase().contains(q) ||
                it.brandName.lowercase().contains(q) ||
                it.clientName.lowercase().contains(q) ||
                it.clientBusinessName.lowercase().contains(q) ||
                it.clientEmail.lowercase().contains(q)
            }
        }
        when (sort) {
            "Oldest" -> list.sortedBy { it.createdAt }
            "Due Date" -> list.sortedBy { it.desiredDeliveryDate }
            else -> list.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<InAppNotification>> = repository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val emailLogs: StateFlow<List<EmailLog>> = repository.getEmailLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Packages (up to 20 tiers)
    val packages: StateFlow<List<AgencyPackage>> = repository.getAllPackages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Agency Settings
    val agencySettings: StateFlow<AgencySettings?> = repository.getAgencySettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Feedback state
    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    fun clearSnackMessage() {
        _snackMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackMessage.value = msg
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        } else {
            val user = currentUser.value
            if (user == null) {
                _currentScreen.value = Screen.Auth
                return true
            }
            val root = if (user.role == "ADMIN") Screen.AdminDashboard else Screen.ClientDashboard
            if (_currentScreen.value != root) {
                _currentScreen.value = root
                return true
            }
        }
        return false
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.login(email, pass)
            res.onSuccess { user ->
                _snackMessage.value = "Welcome back, ${user.name}!"
                _currentScreen.value = if (user.role == "ADMIN") Screen.AdminDashboard else Screen.ClientDashboard
                onSuccess()
            }.onFailure { err ->
                _snackMessage.value = err.message ?: "Login failed"
            }
        }
    }

    fun loginWithGoogle(email: String, name: String, business: String, country: String) {
        viewModelScope.launch {
            val res = repository.loginWithGoogle(email, name, business, country)
            res.onSuccess { user ->
                _snackMessage.value = "Connected with Google as ${user.name} (${user.businessName})"
                _currentScreen.value = Screen.ClientDashboard
            }.onFailure {
                _snackMessage.value = it.message ?: "Google connection failed"
            }
        }
    }

    fun loginWithFacebook(email: String, name: String, business: String, country: String) {
        viewModelScope.launch {
            val res = repository.loginWithFacebook(email, name, business, country)
            res.onSuccess { user ->
                _snackMessage.value = "Connected with Facebook as ${user.name} (${user.businessName})"
                _currentScreen.value = Screen.ClientDashboard
            }.onFailure {
                _snackMessage.value = it.message ?: "Facebook connection failed"
            }
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String,
        business: String,
        country: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.registerClient(name, email, pass, business, country)
            res.onSuccess { user ->
                _snackMessage.value = "Account created for ${user.businessName}!"
                _currentScreen.value = Screen.ClientDashboard
                onSuccess()
            }.onFailure { err ->
                _snackMessage.value = err.message ?: "Registration failed"
            }
        }
    }

    fun logout() {
        repository.setCurrentUser(null)
        backStack.clear()
        _currentScreen.value = Screen.Auth
        _snackMessage.value = "Logged out successfully"
    }

    fun submitOrder(
        productName: String,
        brandName: String,
        category: String,
        description: String,
        targetAudience: String,
        targetMarket: String,
        preferredLanguage: String,
        websiteUrl: String,
        productPrice: String,
        currency: String,
        photos: List<String>,
        adFormat: String,
        platform: String,
        aspectRatio: String,
        campaignObjective: String,
        designStyle: String,
        colorsTypography: String,
        adHeadline: String,
        promotionalOffer: String,
        callToAction: String,
        textToInclude: String,
        referenceImageUri: String,
        additionalInstructions: String,
        creativesCount: Int,
        packageTier: String,
        packagePrice: String,
        desiredDeliveryDate: String,
        clientRightsConfirmed: Boolean,
        isDraft: Boolean,
        onComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.createOrder(
                productName = productName,
                brandName = brandName,
                category = category,
                description = description,
                targetAudience = targetAudience,
                targetMarket = targetMarket,
                preferredLanguage = preferredLanguage,
                websiteUrl = websiteUrl,
                productPrice = productPrice,
                currency = currency,
                photoPaths = photos,
                adFormat = adFormat,
                platform = platform,
                aspectRatio = aspectRatio,
                campaignObjective = campaignObjective,
                designStyle = designStyle,
                colorsTypography = colorsTypography,
                adHeadline = adHeadline,
                promotionalOffer = promotionalOffer,
                callToAction = callToAction,
                textToInclude = textToInclude,
                referenceImageUri = referenceImageUri,
                additionalInstructions = additionalInstructions,
                creativesCount = creativesCount,
                packageTier = packageTier,
                packagePrice = packagePrice,
                desiredDeliveryDate = desiredDeliveryDate,
                clientRightsConfirmed = clientRightsConfirmed,
                isDraft = isDraft
            )
            res.onSuccess { orderId ->
                _snackMessage.value = if (isDraft) "Draft saved successfully!" else "Order $orderId submitted to Vanta Digital!"
                onComplete(orderId)
            }.onFailure { err ->
                _snackMessage.value = err.message ?: "Failed to submit order"
            }
        }
    }

    fun updateStatus(orderId: String, newStatus: String, adminNotes: String, progressUpdate: String) {
        viewModelScope.launch {
            val res = repository.updateOrderStatus(orderId, newStatus, adminNotes, progressUpdate)
            res.onSuccess {
                _snackMessage.value = "Order $orderId status updated to $newStatus"
            }.onFailure {
                _snackMessage.value = it.message ?: "Failed to update order"
            }
        }
    }

    fun deliverOrder(orderId: String, title: String, fileName: String, aspectRatio: String, notes: String) {
        viewModelScope.launch {
            val res = repository.deliverOrder(orderId, title, fileName, aspectRatio, notes)
            res.onSuccess {
                _snackMessage.value = "Order $orderId marked Delivered! Client notified & email log created."
            }.onFailure {
                _snackMessage.value = it.message ?: "Failed to deliver order"
            }
        }
    }

    fun requestRevision(orderId: String, notes: String) {
        viewModelScope.launch {
            val res = repository.requestRevision(orderId, notes)
            res.onSuccess {
                _snackMessage.value = "Revision request submitted to Vanta Digital team."
            }.onFailure {
                _snackMessage.value = it.message ?: "Failed to request revision"
            }
        }
    }

    fun sendMessage(orderId: String, msg: String) {
        viewModelScope.launch {
            val res = repository.sendMessage(orderId, msg)
            res.onSuccess {
                _snackMessage.value = "Message sent"
            }.onFailure {
                _snackMessage.value = it.message ?: "Failed to send message"
            }
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    // CSV Template & Import
    fun getDummyCsvTemplate(): String = repository.generateDummyCsvTemplate()

    fun importPackagesFromCsv(csvText: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.importPackagesFromCsv(csvText)
            res.onSuccess { count ->
                _snackMessage.value = "Successfully imported $count package tiers!"
                onResult(true, "Successfully imported $count packages.")
            }.onFailure { err ->
                _snackMessage.value = "Import failed: ${err.message}"
                onResult(false, err.message ?: "Import error")
            }
        }
    }

    fun updateAgencySettings(settings: AgencySettings) {
        viewModelScope.launch {
            repository.updateAgencySettings(settings)
            _snackMessage.value = "Agency settings saved successfully."
        }
    }

    fun savePackage(pkg: AgencyPackage) {
        viewModelScope.launch {
            repository.savePackage(pkg)
            _snackMessage.value = "Package ${pkg.tierName} updated."
        }
    }

    fun updateUserProfile(user: User, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val res = repository.updateUserProfile(user)
            res.onSuccess {
                _snackMessage.value = "Admin profile & credentials saved successfully!"
                onComplete?.invoke(true)
            }.onFailure {
                _snackMessage.value = "Failed to update profile: ${it.message}"
                onComplete?.invoke(false)
            }
        }
    }

    fun getOrderFlow(orderId: String) = repository.getOrderById(orderId)
    fun getDeliverablesFlow(orderId: String) = repository.getDeliverablesForOrder(orderId)
    fun getMessagesFlow(orderId: String) = repository.getMessagesForOrder(orderId)
}
