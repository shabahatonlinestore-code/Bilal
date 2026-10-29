package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDraft
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.StatusInReview
import com.example.ui.theme.StatusRevision
import com.example.ui.theme.StatusSubmitted
import com.example.ui.theme.VantaAccent
import com.example.ui.theme.VantaCardBorder
import com.example.ui.theme.VantaNavy
import com.example.ui.theme.VantaNavyDark
import com.example.ui.theme.VantaNavySurface
import com.example.ui.theme.VantaSilver
import com.example.ui.theme.VantaSilverLight

@Composable
fun VantaGeometricLogo(modifier: Modifier = Modifier, sizeDp: Int = 36) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(VantaNavyDark, VantaNavySurface)
                )
            )
            .border(1.dp, VantaSilver.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size((sizeDp * 0.7f).dp)) {
            val w = size.width
            val h = size.height

            // Left leg of V in metallic silver
            val pathLeft = Path().apply {
                moveTo(w * 0.15f, h * 0.15f)
                lineTo(w * 0.40f, h * 0.15f)
                lineTo(w * 0.50f, h * 0.85f)
                lineTo(w * 0.35f, h * 0.85f)
                close()
            }
            drawPath(
                path = pathLeft,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), VantaSilver)
                )
            )

            // Right facet of V in metallic sapphire/silver glow
            val pathRight = Path().apply {
                moveTo(w * 0.85f, h * 0.15f)
                lineTo(w * 0.60f, h * 0.15f)
                lineTo(w * 0.50f, h * 0.85f)
                lineTo(w * 0.65f, h * 0.85f)
                close()
            }
            drawPath(
                path = pathRight,
                brush = Brush.verticalGradient(
                    listOf(VantaSilverLight, VantaAccent)
                )
            )

            // Center prism highlight dot
            drawCircle(
                color = Color.White,
                radius = w * 0.08f,
                center = Offset(w * 0.50f, h * 0.45f)
            )
        }
    }
}

@Composable
fun VantaBrandHeader(
    modifier: Modifier = Modifier,
    currentUser: User?,
    unreadCount: Int,
    onNotificationClick: () -> Unit,
    onRoleSwitchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = VantaNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .testTag("btn_top_back")
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = VantaSilverLight
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    VantaGeometricLogo(sizeDp = 38)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VANTA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DIGITAL",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 1.2.sp
                                ),
                                color = VantaSilver
                            )
                        }
                        Text(
                            text = "Where Products Become Powerful Ads",
                            style = MaterialTheme.typography.labelSmall,
                            color = VantaSilver.copy(alpha = 0.8f)
                        )
                    }
                }

                // Action icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Role Switch badge button
                    val role = currentUser?.role ?: "CLIENT"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (role == "ADMIN") VantaAccent else VantaNavySurface)
                            .border(1.dp, VantaSilver.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .clickable { onRoleSwitchClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("btn_switch_role")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (role == "ADMIN") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = "Current Role: $role",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = role,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Notifications icon with badge
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .testTag("btn_notifications")
                            .size(40.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = Color(0xFFEF4444)) {
                                        Text("$unreadCount", color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = VantaSilverLight
                            )
                        }
                    }

                    // Settings / Policies icon
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .testTag("btn_settings")
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings & Policies",
                            tint = VantaSilverLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "draft" -> StatusDraft.copy(alpha = 0.15f) to StatusDraft
        "submitted" -> StatusSubmitted.copy(alpha = 0.15f) to StatusSubmitted
        "in review" -> StatusInReview.copy(alpha = 0.15f) to StatusInReview
        "in progress" -> StatusInProgress.copy(alpha = 0.15f) to StatusInProgress
        "revision requested" -> StatusRevision.copy(alpha = 0.15f) to StatusRevision
        "completed" -> StatusCompleted.copy(alpha = 0.15f) to StatusCompleted
        "cancelled" -> StatusCancelled.copy(alpha = 0.15f) to StatusCancelled
        else -> VantaNavySurface.copy(alpha = 0.15f) to VantaNavy
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(6.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = textColor
            )
        }
    }
}

@Composable
fun OrderProgressTracker(currentStatus: String, modifier: Modifier = Modifier) {
    val steps = listOf("Submitted", "In Review", "In Progress", "Delivered")
    val currentStepIndex = when (currentStatus.lowercase()) {
        "draft" -> -1
        "submitted" -> 0
        "in review", "awaiting client information" -> 1
        "in progress", "revision requested" -> 2
        "completed" -> 3
        else -> 0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, VantaSilver.copy(alpha = 0.2f))))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Order Progress Tracker",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = VantaNavy
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, stepName ->
                    val isCompleted = index <= currentStepIndex
                    val isCurrent = index == currentStepIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCompleted) VantaAccent else Color(0xFFE2E8F0)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stepName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            ),
                            color = if (isCurrent) VantaAccent else if (isCompleted) VantaNavy else Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .weight(0.6f)
                                .height(3.dp)
                                .background(
                                    if (index < currentStepIndex) VantaAccent else Color(0xFFE2E8F0)
                                )
                        )
                    }
                }
            }
        }
    }
}
