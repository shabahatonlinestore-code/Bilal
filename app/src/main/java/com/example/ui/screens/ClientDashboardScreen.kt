package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Order
import com.example.model.User
import com.example.ui.components.OrderProgressTracker
import com.example.ui.components.StatusBadge
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDraft
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.StatusRevision
import com.example.ui.theme.StatusSubmitted
import com.example.ui.theme.VantaAccent
import com.example.ui.theme.VantaCardBorder
import com.example.ui.theme.VantaNavy
import com.example.ui.theme.VantaNavyDark
import com.example.ui.theme.VantaNavySurface
import com.example.ui.theme.VantaSilver
import com.example.ui.theme.VantaSilverLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClientDashboardScreen(
    currentUser: User?,
    orders: List<Order>,
    onCreateNewProductClick: () -> Unit,
    onOrderClick: (String) -> Unit,
    onMessagesClick: () -> Unit,
    onCompletedAdsClick: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onFilterByStatus: (String) -> Unit
) {
    val draftCount = orders.count { it.status.equals("Draft", ignoreCase = true) }
    val submittedCount = orders.count { it.status.equals("Submitted", ignoreCase = true) }
    val inProgressCount = orders.count { it.status.equals("In Progress", ignoreCase = true) || it.status.equals("In Review", ignoreCase = true) }
    val revisionCount = orders.count { it.status.equals("Revision Requested", ignoreCase = true) }
    val completedCount = orders.count { it.status.equals("Completed", ignoreCase = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Welcome Client Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_client_welcome"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VantaNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(VantaNavyDark, VantaNavy, VantaNavySurface)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CLIENT PORTAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = VantaSilver
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Welcome, ${currentUser?.businessName ?: "Valued Partner"}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "${currentUser?.name ?: "Client"} • ${currentUser?.country ?: "Pakistan"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VantaSilver.copy(alpha = 0.8f)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(VantaNavySurface)
                                    .border(1.dp, VantaSilver.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Primary CTA: Create New Product
                        Button(
                            onClick = onCreateNewProductClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_create_new_product_hero"),
                            colors = ButtonDefaults.buttonColors(containerColor = VantaAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Create New Product / Order",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Navigation Buttons Row
        item {
            Column {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = VantaNavy
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        ActionPill(
                            icon = Icons.Default.AddPhotoAlternate,
                            title = "New Order",
                            onClick = onCreateNewProductClick,
                            isPrimary = true,
                            testTag = "pill_new_order"
                        )
                    }
                    item {
                        ActionPill(
                            icon = Icons.Default.Dashboard,
                            title = "My Orders (${orders.size})",
                            onClick = { onFilterByStatus("All") },
                            isPrimary = false,
                            testTag = "pill_my_orders"
                        )
                    }
                    item {
                        ActionPill(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            title = "Messages / Brief",
                            onClick = onMessagesClick,
                            isPrimary = false,
                            testTag = "pill_messages"
                        )
                    }
                    item {
                        ActionPill(
                            icon = Icons.Default.CheckCircle,
                            title = "Completed Ads ($completedCount)",
                            onClick = onCompletedAdsClick,
                            isPrimary = false,
                            testTag = "pill_completed_ads"
                        )
                    }
                    item {
                        ActionPill(
                            icon = Icons.Default.Settings,
                            title = "Profile Settings",
                            onClick = onProfileSettingsClick,
                            isPrimary = false,
                            testTag = "pill_profile_settings"
                        )
                    }
                }
            }
        }

        // Order Summary Metric Cards
        item {
            Column {
                Text(
                    text = "Order Pipeline Overview",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = VantaNavy
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Drafts",
                        count = draftCount,
                        color = StatusDraft,
                        modifier = Modifier.weight(1f),
                        onClick = { onFilterByStatus("Draft") }
                    )
                    MetricCard(
                        title = "Submitted",
                        count = submittedCount,
                        color = StatusSubmitted,
                        modifier = Modifier.weight(1f),
                        onClick = { onFilterByStatus("Submitted") }
                    )
                    MetricCard(
                        title = "In Progress",
                        count = inProgressCount,
                        color = StatusInProgress,
                        modifier = Modifier.weight(1f),
                        onClick = { onFilterByStatus("In Progress") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Revision Req.",
                        count = revisionCount,
                        color = StatusRevision,
                        modifier = Modifier.weight(1f),
                        onClick = { onFilterByStatus("Revision Requested") }
                    )
                    MetricCard(
                        title = "Completed",
                        count = completedCount,
                        color = StatusCompleted,
                        modifier = Modifier.weight(1f),
                        onClick = { onFilterByStatus("Completed") }
                    )
                }
            }
        }

        // Recent Orders Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Orders",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = VantaNavy
                )
                Text(
                    text = "${orders.size} Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Order Cards List
        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFE2E8F0))))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = VantaSilver,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Creative Orders Yet",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = VantaNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Submit at least 15 photos of your product and our design team will craft high-converting ad creatives.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onCreateNewProductClick,
                            colors = ButtonDefaults.buttonColors(containerColor = VantaNavy)
                        ) {
                            Text("Create First Order")
                        }
                    }
                }
            }
        } else {
            items(orders) { order ->
                OrderCard(order = order, onClick = { onOrderClick(order.orderId) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActionPill(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isPrimary: Boolean,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isPrimary) VantaNavy else Color.White)
            .border(
                1.dp,
                if (isPrimary) VantaNavy else VantaCardBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag(testTag)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isPrimary) Color.White else VantaNavy,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isPrimary) Color.White else VantaNavy
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("metric_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(color.copy(alpha = 0.3f), VantaCardBorder)))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                ),
                color = color
            )
        }
    }
}

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(order.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("order_card_${order.orderId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderId,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = VantaNavy
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• $dateStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.productName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${order.category} • ${order.adFormat} • ${order.photoCount} Photos Attached",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )

            if (order.progressUpdate.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Agency Update: ${order.progressUpdate}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = VantaNavy,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Embedded Step Tracker
            OrderProgressTracker(currentStatus = order.status)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Package: ${order.packageTier}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF475569)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Details",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = VantaAccent
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = VantaAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
