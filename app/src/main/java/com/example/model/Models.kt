package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val businessName: String,
    val country: String,
    val role: String, // "CLIENT" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val orderId: String,
    val userId: String,
    val clientName: String,
    val clientEmail: String,
    val clientBusinessName: String,
    val productName: String,
    val brandName: String,
    val category: String,
    val description: String,
    val targetAudience: String,
    val targetMarket: String,
    val preferredLanguage: String,
    val websiteUrl: String,
    val productPrice: String,
    val currency: String,
    val photoUris: String, // Comma separated URIs or local paths
    val photoCount: Int,
    val adFormat: String,
    val platform: String,
    val aspectRatio: String,
    val campaignObjective: String,
    val designStyle: String,
    val colorsTypography: String,
    val adHeadline: String,
    val promotionalOffer: String,
    val callToAction: String,
    val textToInclude: String,
    val referenceImageUri: String,
    val additionalInstructions: String,
    val creativesCount: Int,
    val packageTier: String,
    val packagePrice: String,
    val desiredDeliveryDate: String,
    val clientRightsConfirmed: Boolean,
    val status: String, // "Draft", "Submitted", "In Review", "In Progress", "Awaiting Client Information", "Revision Requested", "Completed", "Cancelled"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    val adminNotes: String = "",
    val progressUpdate: String = "",
    val revisionCount: Int = 0,
    val maxRevisions: Int = 2,
    val isPaid: Boolean = false
) {
    fun getPhotoList(): List<String> {
        if (photoUris.isBlank()) return emptyList()
        return photoUris.split("|||").filter { it.isNotBlank() }
    }
}

@Entity(tableName = "deliverables")
data class DeliverableItem(
    @PrimaryKey val id: String,
    val orderId: String,
    val title: String,
    val fileName: String,
    val fileType: String,
    val fileSize: String,
    val aspectRatio: String,
    val assetDrawableName: String = "",
    val downloadUrl: String = "",
    val deliveredAt: Long = System.currentTimeMillis(),
    val version: Int = 1
)

@Entity(tableName = "order_messages")
data class OrderMessage(
    @PrimaryKey val id: String,
    val orderId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "CLIENT" or "ADMIN"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class InAppNotification(
    @PrimaryKey val id: String,
    val recipientRole: String, // "CLIENT" or "ADMIN"
    val recipientUserId: String? = null,
    val title: String,
    val body: String,
    val orderId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "email_logs")
data class EmailLog(
    @PrimaryKey val id: String,
    val toEmail: String,
    val subject: String,
    val templateName: String,
    val status: String, // "DELIVERED", "QUEUED", "FAILED"
    val orderId: String? = null,
    val provider: String = "Vanta Digital Server Dispatcher",
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "agency_packages")
data class AgencyPackage(
    @PrimaryKey val id: String,
    val tierOrder: Int,
    val tierName: String,
    val creativesCount: Int,
    val priceUsd: String,
    val pricePkr: String,
    val turnaroundDays: String,
    val deliverableFormats: String,
    val description: String
)

@Entity(tableName = "agency_settings")
data class AgencySettings(
    @PrimaryKey val id: String = "agency_config_main",
    val agencyName: String = "Vanta Digital",
    val tagline: String = "Where Products Become Powerful Ads.",
    val contactEmail: String = "briefs@vantadigital.com",
    val phoneWhatsApp: String = "+92 300 1234567",
    val officeAddress: String = "Gulberg III, Lahore, Pakistan",
    val disclaimer: String = "Vanta Digital creates high-converting advertising creatives (static banners, carousel slides, story graphics, and motion reels). Vanta Digital does not automatically publish ads, manage ad spend, or run ad accounts on Meta/Google unless explicitly included in an enterprise agency contract.",
    val revisionPolicy: String = "• Every standard package includes 2 complimentary revision rounds.\n• Revisions cover headline adjustments, color grading, font tweaks, and composition tweaks.\n• Radical concept changes or replacement of product photos after creative production constitutes a new brief.",
    val deliveryPolicy: String = "Final deliverables are provided in master high-resolution PNG/MP4 formats along with a complete ZIP bundle. Client deliverables remain accessible on secure Vanta Digital servers for 90 days.",
    val termsOfService: String = "The client warrants that all uploaded product photos, brand marks, and copyrighted elements are owned or licensed by the client. Upon final delivery and verified payment, full commercial usage rights are granted to the client."
)
