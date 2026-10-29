package com.example.data

import android.content.Context
import com.example.model.AgencyPackage
import com.example.model.AgencySettings
import com.example.model.DeliverableItem
import com.example.model.EmailLog
import com.example.model.InAppNotification
import com.example.model.Order
import com.example.model.OrderMessage
import com.example.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class VantaRepository(private val database: VantaDatabase) {

    private val userDao = database.userDao()
    private val orderDao = database.orderDao()
    private val deliverableDao = database.deliverableDao()
    private val messageDao = database.orderMessageDao()
    private val notificationDao = database.notificationDao()
    private val emailLogDao = database.emailLogDao()
    private val packageDao = database.agencyPackageDao()
    private val settingsDao = database.agencySettingsDao()

    // Portal starts with no user selected (ID & Password screen first)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        // Clear out old Lumina demo orders and old users to ensure clean slate
        val adminEmail = "sehar.asma30@gmail.com"
        val existingAdmin = userDao.getUserByEmail(adminEmail)
        if (existingAdmin == null) {
            val adminUser = User(
                id = "admin_sehar_01",
                name = "Sehar Asma",
                email = adminEmail,
                passwordHash = "Muhammad@0101",
                businessName = "Vanta Digital Agency",
                country = "Pakistan",
                role = "ADMIN"
            )
            userDao.insertUser(adminUser)
        }

        // Clean out any legacy Lumina sample orders
        val allOrders = orderDao.getOrderDirect("VNT-2026-00001")
        if (allOrders != null && allOrders.clientBusinessName.contains("Lumina", ignoreCase = true)) {
            orderDao.clearAllOrders()
            deliverableDao.clearAllDeliverables()
            messageDao.clearAllMessages()
            notificationDao.clearAllNotifications()
            emailLogDao.clearAllLogs()
        }

        // Initialize default agency settings
        val existingSettings = settingsDao.getSettingsDirect()
        if (existingSettings == null) {
            settingsDao.saveSettings(AgencySettings())
        }

        // Initialize 20 comprehensive packages if empty
        val existingPackages = packageDao.getAllPackagesDirect()
        if (existingPackages.isEmpty()) {
            val defaultTiers = listOf(
                AgencyPackage("tier_01", 1, "Tier 1: Starter Single Ad", 1, "$39", "PKR 11,000", "2 Days", "Square 1:1 or 4:5", "Essential static promotional creative for single product highlight."),
                AgencyPackage("tier_02", 2, "Tier 2: Dual Angle Feed Kit", 2, "$69", "PKR 19,000", "2-3 Days", "Feed 4:5 & Square 1:1", "Two distinct product angle creatives with contrasting headlines."),
                AgencyPackage("tier_03", 3, "Tier 3: Instagram Reel Motion Cover", 2, "$89", "PKR 25,000", "3 Days", "Story / Reel 9:16", "High-energy vertical motion poster optimized for TikTok & Reels."),
                AgencyPackage("tier_04", 4, "Tier 4: Story & Feed 3-Piece Suite", 3, "$129", "PKR 36,000", "3 Days", "Feed 4:5, Story 9:16, Square 1:1", "Full matching format set for simultaneous feed and story promotion."),
                AgencyPackage("tier_05", 5, "Tier 5: Growth 5-Slide Carousel Pack", 5, "$179", "PKR 49,000", "4 Days", "Portrait 4:5 Carousel", "Multi-slide storytelling carousel highlighting product features & reviews."),
                AgencyPackage("tier_06", 6, "Tier 6: Multi-Market E-commerce Ad Pack", 6, "$219", "PKR 60,000", "4 Days", "Multi-Format", "Tailored creative copy for Pakistan local market + Gulf / US buyers."),
                AgencyPackage("tier_07", 7, "Tier 7: Luxury Brand Identity Ad Bundle", 6, "$259", "PKR 72,000", "4-5 Days", "Feed 4:5 + 9:16 Reel", "High-contrast editorial aesthetics with silver foil typographic layout."),
                AgencyPackage("tier_08", 8, "Tier 8: High-Conversion Video Hooks Set", 5, "$299", "PKR 84,000", "4-5 Days", "Reel / Short 9:16", "Hook variations tested for 3-second retention on paid ads."),
                AgencyPackage("tier_09", 9, "Tier 9: Omnichannel Meta & TikTok Campaign", 8, "$349", "PKR 98,000", "5 Days", "Feed, Story, TikTok", "Comprehensive multi-placement creative asset rollout."),
                AgencyPackage("tier_10", 10, "Tier 10: Scale 10-Ad Master Creatives", 10, "$449", "PKR 125,000", "5-6 Days", "Full Spectrum Formats", "Complete ad arsenal with varying hooks, promotional offers & styles."),
                AgencyPackage("tier_11", 11, "Tier 11: Seasonal Festival & Launch Pack", 10, "$499", "PKR 139,000", "5-6 Days", "All Formats", "Festive, Eid, Black Friday or Season launch themed visuals."),
                AgencyPackage("tier_12", 12, "Tier 12: VIP Product Launch Dominance", 12, "$599", "PKR 168,000", "6 Days", "Master Creative Assets", "Flagship creative suite with high-end composite render."),
                AgencyPackage("tier_13", 13, "Tier 13: 15-Creative Monthly Content Pack", 15, "$749", "PKR 209,000", "7 Days", "All Dimensions", "Two weeks of refreshed ad creatives for continuous scaling."),
                AgencyPackage("tier_14", 14, "Tier 14: 20-Creative Enterprise Brand Vault", 20, "$899", "PKR 250,000", "7-8 Days", "Full Resolution + Source PSD", "Extensive ad collection with editable source PSD layered files."),
                AgencyPackage("tier_15", 15, "Tier 15: Cross-Platform International Pack", 20, "$999", "PKR 279,000", "8 Days", "Global Sizing", "Localized messaging for Pakistan, UAE, UK, and USA markets."),
                AgencyPackage("tier_16", 16, "Tier 16: Dynamic Catalog Ads (DPA) Kit", 15, "$1,199", "PKR 335,000", "8 Days", "Square & Story DPAs", "Custom branded frames for automatic product catalog feeds."),
                AgencyPackage("tier_17", 17, "Tier 17: Viral TikTok & UGC Hook Suite", 18, "$1,399", "PKR 390,000", "9 Days", "Vertical 9:16", "Dynamic motion graphics engineered for fast scroll-stopping action."),
                AgencyPackage("tier_18", 18, "Tier 18: Full Omnichannel Ad System", 25, "$1,699", "PKR 475,000", "10 Days", "Universal Placements", "Complete paid media visual system with 25 creative angles."),
                AgencyPackage("tier_19", 19, "Tier 19: High-Volume E-Commerce Scale (30 Ads)", 30, "$2,199", "PKR 615,000", "12 Days", "Multi-Variant", "Deep testing matrix of angles, backgrounds, and call-to-actions."),
                AgencyPackage("tier_20", 20, "Tier 20: Ultimate Agency Bespoke Retainer", 40, "$2,899", "PKR 810,000", "14 Days", "Unlimited Source Assets", "Full creative agency partnership with priority turnaround & revisions.")
            )
            packageDao.insertPackages(defaultTiers)
        }
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    suspend fun login(email: String, password: String): Result<User> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        // Check for specified admin credentials
        if (cleanEmail == "sehar.asma30@gmail.com" && password == "Muhammad@0101") {
            var adminUser = userDao.getUserByEmail(cleanEmail)
            if (adminUser == null) {
                adminUser = User(
                    id = "admin_sehar_01",
                    name = "Sehar Asma",
                    email = cleanEmail,
                    passwordHash = password,
                    businessName = "Vanta Digital Agency",
                    country = "Pakistan",
                    role = "ADMIN"
                )
                userDao.insertUser(adminUser)
            }
            _currentUser.value = adminUser
            return Result.success(adminUser)
        }

        val user = userDao.getUserByEmail(cleanEmail)
        return if (user != null && user.passwordHash == password) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password."))
        }
    }

    suspend fun loginWithGoogle(email: String, name: String, businessName: String, country: String): Result<User> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        var user = userDao.getUserByEmail(cleanEmail)
        if (user == null) {
            user = User(
                id = "usr_g_" + UUID.randomUUID().toString().take(8),
                name = name.ifBlank { "Client User" },
                email = cleanEmail,
                passwordHash = "google_oauth_verified",
                businessName = businessName.ifBlank { "My Business" },
                country = country.ifBlank { "Pakistan" },
                role = "CLIENT"
            )
            userDao.insertUser(user)
        }
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun loginWithFacebook(email: String, name: String, businessName: String, country: String): Result<User> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        var user = userDao.getUserByEmail(cleanEmail)
        if (user == null) {
            user = User(
                id = "usr_fb_" + UUID.randomUUID().toString().take(8),
                name = name.ifBlank { "Client User" },
                email = cleanEmail,
                passwordHash = "fb_oauth_verified",
                businessName = businessName.ifBlank { "My Business" },
                country = country.ifBlank { "Pakistan" },
                role = "CLIENT"
            )
            userDao.insertUser(user)
        }
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun registerClient(
        name: String,
        email: String,
        password: String,
        businessName: String,
        country: String
    ): Result<User> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }
        val newUser = User(
            id = "usr_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            email = cleanEmail,
            passwordHash = password,
            businessName = businessName.trim(),
            country = country.trim(),
            role = "CLIENT"
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    suspend fun updateUserProfile(updatedUser: User): Result<User> {
        return try {
            userDao.insertUser(updatedUser)
            _currentUser.value = updatedUser
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders()

    fun getOrdersForCurrentUser(): Flow<List<Order>> {
        val user = _currentUser.value
        return if (user?.role == "ADMIN") {
            orderDao.getAllOrders()
        } else {
            orderDao.getOrdersForUser(user?.id ?: "")
        }
    }

    fun getOrderById(orderId: String): Flow<Order?> = orderDao.getOrderById(orderId)

    suspend fun createOrder(
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
        photoPaths: List<String>,
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
        isDraft: Boolean
    ): Result<String> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated."))

        if (!isDraft && photoPaths.size < 15) {
            return Result.failure(Exception("At least 15 product photos are strictly required before submitting."))
        }

        val totalOrders = orderDao.getOrderCount()
        val orderSeq = String.format(Locale.US, "%05d", totalOrders + 1)
        val orderId = "VNT-2026-$orderSeq"

        val order = Order(
            orderId = orderId,
            userId = user.id,
            clientName = user.name,
            clientEmail = user.email,
            clientBusinessName = user.businessName,
            productName = productName.trim(),
            brandName = brandName.trim(),
            category = category,
            description = description.trim(),
            targetAudience = targetAudience.trim(),
            targetMarket = targetMarket.trim(),
            preferredLanguage = preferredLanguage.trim(),
            websiteUrl = websiteUrl.trim(),
            productPrice = productPrice.trim(),
            currency = currency.trim(),
            photoUris = photoPaths.joinToString("|||"),
            photoCount = photoPaths.size,
            adFormat = adFormat,
            platform = platform,
            aspectRatio = aspectRatio,
            campaignObjective = campaignObjective,
            designStyle = designStyle,
            colorsTypography = colorsTypography.trim(),
            adHeadline = adHeadline.trim(),
            promotionalOffer = promotionalOffer.trim(),
            callToAction = callToAction.trim(),
            textToInclude = textToInclude.trim(),
            referenceImageUri = referenceImageUri,
            additionalInstructions = additionalInstructions.trim(),
            creativesCount = creativesCount,
            packageTier = packageTier,
            packagePrice = packagePrice,
            desiredDeliveryDate = desiredDeliveryDate,
            clientRightsConfirmed = clientRightsConfirmed,
            status = if (isDraft) "Draft" else "Submitted",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isPaid = false
        )

        orderDao.insertOrder(order)

        if (!isDraft) {
            // Trigger in-app notification to Admin
            notificationDao.insertNotification(
                InAppNotification(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    recipientRole = "ADMIN",
                    title = "New Order: $orderId",
                    body = "New creative order for '${order.productName}' submitted by ${user.businessName} (${photoPaths.size} photos).",
                    orderId = orderId
                )
            )

            // Trigger Email Dispatch Log
            emailLogDao.insertLog(
                EmailLog(
                    id = "email_" + UUID.randomUUID().toString().take(8),
                    toEmail = user.email,
                    subject = "Vanta Digital — Order Confirmation [$orderId]",
                    templateName = "ORDER_SUBMITTED_CLIENT",
                    status = "DELIVERED",
                    orderId = orderId,
                    details = "Confirmation email dispatched to client. Package: $packageTier. Estimated delivery: $desiredDeliveryDate."
                )
            )

            // Trigger Agency Admin Email Alert Log to Sehar Asma
            emailLogDao.insertLog(
                EmailLog(
                    id = "email_" + UUID.randomUUID().toString().take(8),
                    toEmail = "sehar.asma30@gmail.com",
                    subject = "ACTION REQUIRED: New Order Received [$orderId]",
                    templateName = "ADMIN_NEW_ORDER_ALERT",
                    status = "DELIVERED",
                    orderId = orderId,
                    details = "Admin notification sent to Sehar Asma for creative review and assignment."
                )
            )
        }

        return Result.success(orderId)
    }

    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        adminNotes: String,
        progressUpdate: String
    ): Result<Unit> {
        val order = orderDao.getOrderDirect(orderId) ?: return Result.failure(Exception("Order not found"))
        val updated = order.copy(
            status = newStatus,
            adminNotes = adminNotes,
            progressUpdate = progressUpdate,
            updatedAt = System.currentTimeMillis(),
            deliveredAt = if (newStatus == "Completed" && order.deliveredAt == null) System.currentTimeMillis() else order.deliveredAt
        )
        orderDao.updateOrder(updated)

        notificationDao.insertNotification(
            InAppNotification(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                recipientRole = "CLIENT",
                recipientUserId = order.userId,
                title = "Order Update: $orderId",
                body = "Status updated to '$newStatus'. $progressUpdate",
                orderId = orderId
            )
        )

        emailLogDao.insertLog(
            EmailLog(
                id = "email_" + UUID.randomUUID().toString().take(8),
                toEmail = order.clientEmail,
                subject = "Order Status Update [$orderId] — $newStatus",
                templateName = "STATUS_UPDATE",
                status = "DELIVERED",
                orderId = orderId,
                details = "Client status update dispatched. Progress: $progressUpdate"
            )
        )

        return Result.success(Unit)
    }

    suspend fun deliverOrder(
        orderId: String,
        deliverableTitle: String,
        fileName: String,
        aspectRatio: String,
        notes: String
    ): Result<Unit> {
        val order = orderDao.getOrderDirect(orderId) ?: return Result.failure(Exception("Order not found"))

        val delivId1 = "deliv_" + UUID.randomUUID().toString().take(8)
        val d1 = DeliverableItem(
            id = delivId1,
            orderId = orderId,
            title = deliverableTitle,
            fileName = fileName,
            fileType = "PNG High-Resolution Creative",
            fileSize = "9.6 MB",
            aspectRatio = aspectRatio,
            assetDrawableName = if (order.category == "Beauty") "sample_ad_perfume" else "sample_ad_fashion",
            downloadUrl = "https://cdn.vantadigital.agency/deliverables/${orderId}_Master.png",
            deliveredAt = System.currentTimeMillis(),
            version = order.revisionCount + 1
        )

        val delivId2 = "deliv_" + UUID.randomUUID().toString().take(8)
        val d2 = DeliverableItem(
            id = delivId2,
            orderId = orderId,
            title = "Complete Creative Package (ZIP Bundle)",
            fileName = "${orderId}_All_Creatives_Assets.zip",
            fileType = "ZIP Archive (Includes PSD, 4:5, 9:16, 1:1)",
            fileSize = "48.2 MB",
            aspectRatio = "All Formats",
            assetDrawableName = if (order.category == "Beauty") "sample_ad_perfume" else "sample_ad_fashion",
            downloadUrl = "https://cdn.vantadigital.agency/deliverables/${orderId}_Bundle.zip",
            deliveredAt = System.currentTimeMillis(),
            version = order.revisionCount + 1
        )

        deliverableDao.insertDeliverables(listOf(d1, d2))

        val updated = order.copy(
            status = "Completed",
            progressUpdate = notes.ifBlank { "Final high-resolution ad deliverables and ZIP archive have been delivered." },
            deliveredAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        notificationDao.insertNotification(
            InAppNotification(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                recipientRole = "CLIENT",
                recipientUserId = order.userId,
                title = "Creatives Delivered! [$orderId]",
                body = "Your final high-resolution advertising deliverables and ZIP archive are now ready for download.",
                orderId = orderId
            )
        )

        emailLogDao.insertLog(
            EmailLog(
                id = "email_" + UUID.randomUUID().toString().take(8),
                toEmail = order.clientEmail,
                subject = "Your Ad Creatives Are Ready! [$orderId]",
                templateName = "DELIVERY_COMPLETE_SECURE_LINK",
                status = "DELIVERED",
                orderId = orderId,
                details = "Delivered files: $fileName and full ZIP archive. Authenticated expiring token issued."
            )
        )

        return Result.success(Unit)
    }

    suspend fun requestRevision(orderId: String, revisionNotes: String): Result<Unit> {
        val order = orderDao.getOrderDirect(orderId) ?: return Result.failure(Exception("Order not found"))
        if (order.revisionCount >= order.maxRevisions) {
            return Result.failure(Exception("Maximum revisions (${order.maxRevisions}) reached for this package tier."))
        }

        val updated = order.copy(
            status = "Revision Requested",
            revisionCount = order.revisionCount + 1,
            progressUpdate = "Client requested revision (${order.revisionCount + 1}/${order.maxRevisions}): $revisionNotes",
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        messageDao.insertMessage(
            OrderMessage(
                id = "msg_" + UUID.randomUUID().toString().take(8),
                orderId = orderId,
                senderId = order.userId,
                senderName = order.clientName,
                senderRole = "CLIENT",
                message = "Revision Request: $revisionNotes"
            )
        )

        notificationDao.insertNotification(
            InAppNotification(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                recipientRole = "ADMIN",
                title = "Revision Requested: $orderId",
                body = "${order.clientBusinessName} requested changes: $revisionNotes",
                orderId = orderId
            )
        )

        emailLogDao.insertLog(
            EmailLog(
                id = "email_" + UUID.randomUUID().toString().take(8),
                toEmail = "sehar.asma30@gmail.com",
                subject = "Revision Request Received [$orderId]",
                templateName = "REVISION_REQUESTED_ADMIN",
                status = "DELIVERED",
                orderId = orderId,
                details = "Revision count: ${updated.revisionCount} of ${order.maxRevisions}. Feedback logged."
            )
        )

        return Result.success(Unit)
    }

    suspend fun sendMessage(orderId: String, text: String): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val order = orderDao.getOrderDirect(orderId) ?: return Result.failure(Exception("Order not found"))

        messageDao.insertMessage(
            OrderMessage(
                id = "msg_" + UUID.randomUUID().toString().take(8),
                orderId = orderId,
                senderId = user.id,
                senderName = user.name,
                senderRole = user.role,
                message = text.trim()
            )
        )

        val targetRole = if (user.role == "CLIENT") "ADMIN" else "CLIENT"
        notificationDao.insertNotification(
            InAppNotification(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                recipientRole = targetRole,
                recipientUserId = if (targetRole == "CLIENT") order.userId else null,
                title = "New Message: $orderId",
                body = "${user.name}: $text",
                orderId = orderId
            )
        )

        return Result.success(Unit)
    }

    fun getDeliverablesForOrder(orderId: String): Flow<List<DeliverableItem>> =
        deliverableDao.getDeliverablesForOrder(orderId)

    fun getMessagesForOrder(orderId: String): Flow<List<OrderMessage>> =
        messageDao.getMessagesForOrder(orderId)

    fun getNotifications(): Flow<List<InAppNotification>> {
        val user = _currentUser.value
        val role = user?.role ?: "CLIENT"
        return notificationDao.getNotifications(role, user?.id)
    }

    suspend fun markNotificationAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    fun getEmailLogs(): Flow<List<EmailLog>> = emailLogDao.getAllLogs()

    // Agency Packages & Settings
    fun getAllPackages(): Flow<List<AgencyPackage>> = packageDao.getAllPackages()

    suspend fun savePackage(pkg: AgencyPackage) {
        packageDao.insertPackage(pkg)
    }

    fun getAgencySettings(): Flow<AgencySettings?> = settingsDao.getSettings()

    suspend fun updateAgencySettings(settings: AgencySettings) {
        settingsDao.saveSettings(settings)
    }

    // CSV / Excel Dummy Template Generator
    fun generateDummyCsvTemplate(): String {
        return buildString {
            append("Tier_ID,Order,Tier_Name,Creatives_Count,Price_USD,Price_PKR,Turnaround_Days,Deliverable_Formats,Description\n")
            append("tier_01,1,Tier 1: Starter Single Ad,1,$39,PKR 11000,2 Days,Square 1:1,Essential static promotional ad\n")
            append("tier_02,2,Tier 2: Dual Angle Feed Kit,2,$69,PKR 19000,2-3 Days,Feed 4:5,Two distinct angle creatives\n")
            append("tier_03,3,Tier 3: Instagram Reel Motion Cover,2,$89,PKR 25000,3 Days,Story 9:16,Vertical motion poster for TikTok/Reels\n")
            append("tier_04,4,Tier 4: Story & Feed 3-Piece Suite,3,$129,PKR 36000,3 Days,Feed and Story,Full matching format set\n")
            append("tier_05,5,Tier 5: Growth 5-Slide Carousel Pack,5,$179,PKR 49000,4 Days,Portrait 4:5,Multi-slide storytelling carousel\n")
            append("tier_06,6,Tier 6: Multi-Market E-commerce Ad Pack,6,$219,PKR 60000,4 Days,Multi-Format,Localized for Pakistan and Gulf/US\n")
            append("tier_07,7,Tier 7: Luxury Brand Identity Ad Bundle,6,$259,PKR 72000,4-5 Days,Feed and Reel,High-contrast editorial aesthetics\n")
            append("tier_08,8,Tier 8: High-Conversion Video Hooks Set,5,$299,PKR 84000,4-5 Days,Reel 9:16,Hook variations for 3-sec retention\n")
            append("tier_09,9,Tier 9: Omnichannel Meta & TikTok Campaign,8,$349,PKR 98000,5 Days,All Placements,Universal multi-placement creatives\n")
            append("tier_10,10,Tier 10: Scale 10-Ad Master Creatives,10,$449,PKR 125000,5-6 Days,Full Formats,Complete ad creative arsenal\n")
            append("tier_11,11,Tier 11: Seasonal Festival & Launch Pack,10,$499,PKR 139000,5-6 Days,All Formats,Festive launch themed visuals\n")
            append("tier_12,12,Tier 12: VIP Product Launch Dominance,12,$599,PKR 168000,6 Days,Master Assets,Flagship creative launch suite\n")
            append("tier_13,13,Tier 13: 15-Creative Monthly Content Pack,15,$749,PKR 209000,7 Days,All Dimensions,Bi-weekly refreshed ad creatives\n")
            append("tier_14,14,Tier 14: 20-Creative Enterprise Brand Vault,20,$899,PKR 250000,7-8 Days,Source PSD,Extensive ad vault with source PSDs\n")
            append("tier_15,15,Tier 15: Cross-Platform International Pack,20,$999,PKR 279000,8 Days,Global Sizing,International markets ready\n")
            append("tier_16,16,Tier 16: Dynamic Catalog Ads (DPA) Kit,15,$1199,PKR 335000,8 Days,DPA Frames,Custom branded frames for catalog\n")
            append("tier_17,17,Tier 17: Viral TikTok & UGC Hook Suite,18,$1399,PKR 390000,9 Days,Vertical 9:16,Dynamic motion graphics engineered\n")
            append("tier_18,18,Tier 18: Full Omnichannel Ad System,25,$1699,PKR 475000,10 Days,All Placements,Universal paid media visual system\n")
            append("tier_19,19,Tier 19: High-Volume E-Commerce Scale (30 Ads),30,$2199,PKR 615000,12 Days,Multi-Variant,Deep testing matrix of 30 ads\n")
            append("tier_20,20,Tier 20: Ultimate Agency Bespoke Retainer,40,$2899,PKR 810000,14 Days,Source Assets,VIP agency partnership\n")
        }
    }

    suspend fun importPackagesFromCsv(csvText: String): Result<Int> {
        return try {
            val lines = csvText.lines().filter { it.isNotBlank() }
            if (lines.size < 2) return Result.failure(Exception("CSV content is empty or missing data lines."))

            val newPackages = mutableListOf<AgencyPackage>()
            val dataLines = if (lines[0].contains("Tier_ID", ignoreCase = true) || lines[0].contains("Tier", ignoreCase = true)) {
                lines.drop(1)
            } else {
                lines
            }

            dataLines.forEachIndexed { idx, line ->
                val tokens = line.split(",").map { it.trim().removeSurrounding("\"") }
                if (tokens.size >= 4) {
                    val id = tokens.getOrNull(0)?.ifBlank { null } ?: "tier_${idx + 1}"
                    val orderNum = tokens.getOrNull(1)?.toIntOrNull() ?: (idx + 1)
                    val name = tokens.getOrNull(2) ?: "Tier ${idx + 1}"
                    val count = tokens.getOrNull(3)?.toIntOrNull() ?: 1
                    val usd = tokens.getOrNull(4) ?: "$49"
                    val pkr = tokens.getOrNull(5) ?: "PKR 14,000"
                    val turnaround = tokens.getOrNull(6) ?: "3 Days"
                    val formats = tokens.getOrNull(7) ?: "Square & Story"
                    val desc = tokens.getOrNull(8) ?: "Custom advertising creative package."

                    newPackages.add(
                        AgencyPackage(
                            id = id,
                            tierOrder = orderNum,
                            tierName = name,
                            creativesCount = count,
                            priceUsd = usd,
                            pricePkr = pkr,
                            turnaroundDays = turnaround,
                            deliverableFormats = formats,
                            description = desc
                        )
                    )
                }
            }

            if (newPackages.isNotEmpty()) {
                packageDao.clearAllPackages()
                packageDao.insertPackages(newPackages)
                Result.success(newPackages.size)
            } else {
                Result.failure(Exception("No valid package rows could be parsed from CSV."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: VantaRepository? = null

        fun getRepository(context: Context): VantaRepository {
            return INSTANCE ?: synchronized(this) {
                val db = VantaDatabase.getDatabase(context)
                val repo = VantaRepository(db)
                INSTANCE = repo
                repo
            }
        }
    }
}
