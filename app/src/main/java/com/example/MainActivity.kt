package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Order
import com.example.ui.Screen
import com.example.ui.VantaViewModel
import com.example.ui.components.NotificationsModal
import com.example.ui.components.VantaBrandHeader
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AgencySettingsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ClientDashboardScreen
import com.example.ui.screens.CreateOrderScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.theme.VantaDigitalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VantaDigitalTheme {
                VantaApp()
            }
        }
    }
}

@Composable
fun VantaApp(viewModel: VantaViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val filteredOrders by viewModel.filteredOrders.collectAsStateWithLifecycle()
    val allOrdersForAdmin by viewModel.allOrdersForAdmin.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val emailLogs by viewModel.emailLogs.collectAsStateWithLifecycle()
    val snackMessage by viewModel.snackMessage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val selectedSortOrder by viewModel.selectedSortOrder.collectAsStateWithLifecycle()
    val packages by viewModel.packages.collectAsStateWithLifecycle()
    val agencySettings by viewModel.agencySettings.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showNotifsModal by remember { mutableStateOf(false) }

    // Display snackbar alerts
    LaunchedEffect(snackMessage) {
        snackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackMessage()
        }
    }

    // Hardware back button navigation
    BackHandler(enabled = currentScreen != Screen.ClientDashboard && currentScreen != Screen.AdminDashboard && currentScreen != Screen.Auth) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentUser != null && (currentScreen is Screen.ClientDashboard || currentScreen is Screen.AdminDashboard)) {
                VantaBrandHeader(
                    currentUser = currentUser,
                    unreadCount = unreadNotifsCount,
                    onNotificationClick = { showNotifsModal = true },
                    onRoleSwitchClick = {
                        // Quick switch role
                        val nextRole = if (currentUser?.role == "ADMIN") "CLIENT" else "ADMIN"
                        if (nextRole == "ADMIN") {
                            viewModel.login("sehar.asma30@gmail.com", "Muhammad@0101") {}
                        } else {
                            viewModel.loginWithGoogle("client.brand@gmail.com", "Client Partner", "Prestige Apparel", "Pakistan")
                        }
                    },
                    onSettingsClick = { viewModel.navigateTo(Screen.SettingsAndPolicies) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Auth -> {
                    AuthScreen(
                        onLogin = { email, pass ->
                            viewModel.login(email, pass) {}
                        },
                        onRegister = { name, email, pass, business, country ->
                            viewModel.register(name, email, pass, business, country) {}
                        },
                        onGoogleConnect = { email, name, business, country ->
                            viewModel.loginWithGoogle(email, name, business, country)
                        },
                        onFacebookConnect = { email, name, business, country ->
                            viewModel.loginWithFacebook(email, name, business, country)
                        }
                    )
                }

                is Screen.ClientDashboard -> {
                    ClientDashboardScreen(
                        currentUser = currentUser,
                        orders = filteredOrders,
                        onCreateNewProductClick = { viewModel.navigateTo(Screen.CreateOrder) },
                        onOrderClick = { orderId -> viewModel.navigateTo(Screen.OrderDetail(orderId)) },
                        onMessagesClick = {
                            val firstOrder = filteredOrders.firstOrNull()
                            if (firstOrder != null) {
                                viewModel.navigateTo(Screen.OrderDetail(firstOrder.orderId))
                            } else {
                                viewModel.showMessage("Create an order first to initiate agency messaging.")
                            }
                        },
                        onCompletedAdsClick = {
                            viewModel.selectedStatusFilter.value = "Completed"
                        },
                        onProfileSettingsClick = {
                            viewModel.navigateTo(Screen.SettingsAndPolicies)
                        },
                        onFilterByStatus = { status ->
                            viewModel.selectedStatusFilter.value = status
                        }
                    )
                }

                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(
                        currentUser = currentUser,
                        allOrders = allOrdersForAdmin,
                        filteredOrders = filteredOrders,
                        notifications = notifications,
                        unreadCount = unreadNotifsCount,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.searchQuery.value = it },
                        selectedStatusFilter = selectedStatusFilter,
                        onStatusFilterChange = { viewModel.selectedStatusFilter.value = it },
                        selectedSortOrder = selectedSortOrder,
                        onSortOrderChange = { viewModel.selectedSortOrder.value = it },
                        onOrderClick = { orderId -> viewModel.navigateTo(Screen.OrderDetail(orderId)) },
                        onOpenNotificationsModal = { showNotifsModal = true },
                        onOpenSettings = { viewModel.navigateTo(Screen.SettingsAndPolicies) },
                        onExportTemplate = { viewModel.getDummyCsvTemplate() },
                        onImportPackages = { csv, onResult -> viewModel.importPackagesFromCsv(csv, onResult) },
                        packages = packages,
                        agencySettings = agencySettings,
                        onSaveAgencySettings = { settings -> viewModel.updateAgencySettings(settings) },
                        onSavePackage = { pkg -> viewModel.savePackage(pkg) },
                        onUpdateUserProfile = { updatedUser -> viewModel.updateUserProfile(updatedUser) }
                    )
                }

                is Screen.CreateOrder -> {
                    CreateOrderScreen(
                        currentUser = currentUser,
                        packages = packages,
                        onExportTemplate = { viewModel.getDummyCsvTemplate() },
                        onImportPackages = { csv, onResult -> viewModel.importPackagesFromCsv(csv, onResult) },
                        onCancel = { viewModel.navigateBack() },
                        onSubmitOrder = { productName, brandName, category, description,
                                          targetAudience, targetMarket, preferredLanguage, websiteUrl,
                                          productPrice, currency, photos, adFormat,
                                          platform, aspectRatio, campaignObjective, designStyle,
                                          colorsTypography, adHeadline, promotionalOffer, callToAction,
                                          textToInclude, referenceImageUri, additionalInstructions,
                                          creativesCount, packageTier, packagePrice,
                                          desiredDeliveryDate, clientRightsConfirmed, isDraft ->
                            viewModel.submitOrder(
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
                                photos = photos,
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
                            ) { createdOrderId ->
                                viewModel.navigateTo(Screen.OrderDetail(createdOrderId))
                            }
                        }
                    )
                }

                is Screen.OrderDetail -> {
                    val orderState by viewModel.getOrderFlow(screen.orderId).collectAsStateWithLifecycle(initialValue = null)
                    val deliverables by viewModel.getDeliverablesFlow(screen.orderId).collectAsStateWithLifecycle(initialValue = emptyList())
                    val messages by viewModel.getMessagesFlow(screen.orderId).collectAsStateWithLifecycle(initialValue = emptyList())

                    orderState?.let { order ->
                        OrderDetailScreen(
                            order = order,
                            currentUser = currentUser,
                            deliverables = deliverables,
                            messages = messages,
                            onBackClick = { viewModel.navigateBack() },
                            onSendMessage = { msg -> viewModel.sendMessage(order.orderId, msg) },
                            onRequestRevision = { revNotes -> viewModel.requestRevision(order.orderId, revNotes) },
                            onUpdateStatusByAdmin = { newSt, notes, prog ->
                                viewModel.updateStatus(order.orderId, newSt, notes, prog)
                            },
                            onDeliverOrderByAdmin = { title, file, ratio, notes ->
                                viewModel.deliverOrder(order.orderId, title, file, ratio, notes)
                            },
                            onSimulateDownload = { deliv ->
                                viewModel.showMessage("Secure download started: ${deliv.fileName} (${deliv.fileSize})")
                            }
                        )
                    }
                }

                is Screen.SettingsAndPolicies -> {
                    AgencySettingsScreen(
                        currentUser = currentUser,
                        agencySettings = agencySettings,
                        packages = packages,
                        emailLogs = emailLogs,
                        onBackClick = { viewModel.navigateBack() },
                        onLogoutClick = { viewModel.logout() },
                        onSaveAgencySettings = { settings -> viewModel.updateAgencySettings(settings) },
                        onSavePackage = { pkg -> viewModel.savePackage(pkg) },
                        onExportTemplate = { viewModel.getDummyCsvTemplate() },
                        onImportPackages = { csv, onResult -> viewModel.importPackagesFromCsv(csv, onResult) },
                        onUpdateUserProfile = { updatedUser -> viewModel.updateUserProfile(updatedUser) }
                    )
                }
            }
        }
    }

    if (showNotifsModal) {
        NotificationsModal(
            notifications = notifications,
            onDismiss = { showNotifsModal = false },
            onNotificationClick = { orderId ->
                showNotifsModal = false
                orderId?.let { id ->
                    viewModel.navigateTo(Screen.OrderDetail(id))
                }
            },
            onMarkAsRead = { id -> viewModel.markNotificationRead(id) }
        )
    }
}
