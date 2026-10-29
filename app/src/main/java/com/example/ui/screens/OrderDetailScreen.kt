package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.example.R
import com.example.model.DeliverableItem
import com.example.model.Order
import com.example.model.OrderMessage
import com.example.model.User
import com.example.ui.components.OrderProgressTracker
import com.example.ui.components.StatusBadge
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    order: Order,
    currentUser: User?,
    deliverables: List<DeliverableItem>,
    messages: List<OrderMessage>,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onRequestRevision: (String) -> Unit,
    onUpdateStatusByAdmin: (newStatus: String, adminNotes: String, progressUpdate: String) -> Unit,
    onDeliverOrderByAdmin: (title: String, fileName: String, aspectRatio: String, notes: String) -> Unit,
    onSimulateDownload: (DeliverableItem) -> Unit
) {
    val isAdmin = currentUser?.role == "ADMIN"
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Brief, 1: Deliverables, 2: Messages & Updates, 3: Admin Workspace (if admin)

    var showRevisionDialog by remember { mutableStateOf(false) }
    var revisionFeedbackText by remember { mutableStateOf("") }

    var showDeliverDialog by remember { mutableStateOf(false) }
    var deliverTitle by remember { mutableStateOf("${order.productName} - Master Ad Package") }
    var deliverFileName by remember { mutableStateOf("${order.orderId}_Master_Creative.png") }
    var deliverNotes by remember { mutableStateOf("Color grading, typography, and motion framing completed according to brief.") }

    var adminStatusMenuExpanded by remember { mutableStateOf(false) }
    var selectedNewStatus by remember { mutableStateOf(order.status) }
    var adminPrivateNotesInput by remember { mutableStateOf(order.adminNotes) }
    var adminPublicProgressInput by remember { mutableStateOf(order.progressUpdate) }

    var messageInput by remember { mutableStateOf("") }

    val formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US).format(Date(order.createdAt))
    val photoList = order.getPhotoList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC))
    ) {
        // Top Order App Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = VantaNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp).testTag("btn_back_from_detail")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = order.orderId,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = order.status)
                            }
                            Text(
                                text = order.productName,
                                style = MaterialTheme.typography.bodySmall,
                                color = VantaSilver
                            )
                        }
                    }

                    if (isAdmin) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VantaAccent)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("ADMIN MODE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs for Detail View
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = VantaNavySurface,
                    contentColor = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Brief & Photos", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_brief_photos")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Deliverables (${deliverables.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_deliverables")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Messages (${messages.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_messages")
                    )
                    if (isAdmin) {
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("Manage Order", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("tab_admin_manage")
                        )
                    }
                }
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: Brief & Photos
                    item {
                        OrderProgressTracker(currentStatus = order.status)
                    }

                    // Client & Package Details Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Client & Submission Info",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VantaNavy
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                DetailRow(label = "Business / Brand", value = "${order.clientBusinessName} (${order.brandName})")
                                DetailRow(label = "Client Contact", value = "${order.clientName} • ${order.clientEmail}")
                                DetailRow(label = "Submitted At", value = formattedDate)
                                DetailRow(label = "Selected Package", value = "${order.packageTier} • ${order.packagePrice}")
                                DetailRow(label = "Target Market", value = "${order.targetMarket} (${order.preferredLanguage})")
                                DetailRow(label = "Est. Delivery", value = order.desiredDeliveryDate)
                                DetailRow(label = "Revision Allowance", value = "${order.revisionCount} used of ${order.maxRevisions} included")
                            }
                        }
                    }

                    // Uploaded Photos Gallery
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Uploaded Product Photos (${photoList.size})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VantaNavy
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFDCFCE7))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("15+ Verified", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(photoList) { photoUri ->
                                        Box(
                                            modifier = Modifier
                                                .size(90.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE2E8F0))
                                                .border(1.dp, VantaCardBorder, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Image,
                                                    contentDescription = null,
                                                    tint = VantaNavySurface,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = photoUri.takeLast(14),
                                                    fontSize = 8.sp,
                                                    color = Color(0xFF475569),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Full Creative Brief Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Full Creative Advertising Brief",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VantaNavy
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                DetailRow(label = "Format & Ratio", value = "${order.adFormat} • ${order.aspectRatio}")
                                DetailRow(label = "Platform", value = order.platform)
                                DetailRow(label = "Campaign Objective", value = order.campaignObjective)
                                DetailRow(label = "Design Aesthetic", value = order.designStyle)
                                DetailRow(label = "Ad Headline / Hook", value = order.adHeadline.ifBlank { "Agency creative discretion" })
                                DetailRow(label = "Promotional Offer", value = order.promotionalOffer.ifBlank { "None specified" })
                                DetailRow(label = "Call To Action (CTA)", value = order.callToAction)
                                DetailRow(label = "Brand Colors & Fonts", value = order.colorsTypography.ifBlank { "Luxury Vanta Palette" })
                                DetailRow(label = "Target Audience", value = order.targetAudience.ifBlank { "General demographic" })

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Special Creative Instructions:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF475569)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = order.additionalInstructions.ifBlank { "Standard brand alignment and high-converting creative layout." },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = VantaNavy
                                    )
                                }
                            }
                        }
                    }

                    // Quick Actions for Client (Request Revision)
                    if (!isAdmin && (order.status == "Completed" || order.status == "Delivered")) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFDE68A))))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFD97706))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Need Any Creative Adjustments?",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Your package includes up to ${order.maxRevisions} rounds of revisions. Revisions used: ${order.revisionCount}/${order.maxRevisions}.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF78350F)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { showRevisionDialog = true },
                                        enabled = order.revisionCount < order.maxRevisions,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("btn_request_revision")
                                    ) {
                                        Text(if (order.revisionCount < order.maxRevisions) "Request Revision" else "Max Revisions Reached", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: Deliverables & Download Files
                    if (deliverables.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        tint = VantaSilver,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No Deliverables Yet",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VantaNavy
                                    )
                                    Text(
                                        text = "Once Vanta Digital design team completes your ad creatives, master high-res files and ZIP bundle will appear here for authenticated download.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B),
                                        textAlign = TextAlign.Center
                                    )

                                    if (isAdmin) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { showDeliverDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = VantaAccent),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Upload Deliverables Now", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(deliverables) { item ->
                            DeliverableCard(item = item, onDownload = { onSimulateDownload(item) })
                        }

                        if (isAdmin) {
                            item {
                                Button(
                                    onClick = { showDeliverDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = VantaNavy),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Another Creative Deliverable / Revision File", color = Color.White)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Messages & Instructions Thread
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Agency Direct Communication",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VantaNavy
                                )
                                Text(
                                    text = "Direct thread between ${order.clientBusinessName} and the Vanta Digital creative team.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Send message input
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = messageInput,
                                        onValueChange = { messageInput = it },
                                        placeholder = { Text("Type instruction or question...") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_order_message"),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            if (messageInput.isNotBlank()) {
                                                onSendMessage(messageInput)
                                                messageInput = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(VantaNavy)
                                            .size(48.dp)
                                            .testTag("btn_send_message")
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    if (messages.isEmpty()) {
                        item {
                            Text(
                                text = "No messages sent yet. Use this channel to ask questions or refine instructions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(messages) { msg ->
                            val isMe = msg.senderRole == currentUser?.role
                            val timeStr = SimpleDateFormat("MMM dd, hh:mm a", Locale.US).format(Date(msg.timestamp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 12.dp,
                                                topEnd = 12.dp,
                                                bottomStart = if (isMe) 12.dp else 2.dp,
                                                bottomEnd = if (isMe) 2.dp else 12.dp
                                            )
                                        )
                                        .background(if (isMe) VantaNavy else Color.White)
                                        .border(1.dp, if (isMe) VantaNavy else VantaCardBorder, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .widthIn(max = 280.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "${msg.senderName} (${msg.senderRole})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isMe) VantaSilverLight else VantaAccent
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = msg.message,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isMe) Color.White else Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = timeStr,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = if (isMe) VantaSilver.copy(alpha = 0.7f) else Color(0xFF94A3B8),
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: Admin Workspace (Admin Only)
                    if (isAdmin) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaAccent.copy(alpha = 0.3f), VantaNavySurface)))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = VantaAccent)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Admin Order Controls",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = VantaNavy
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Status changer dropdown
                                    val statusOptions = listOf(
                                        "Submitted", "In Review", "In Progress",
                                        "Awaiting Client Information", "Revision Requested", "Completed", "Cancelled"
                                    )

                                    ExposedDropdownMenuBox(
                                        expanded = adminStatusMenuExpanded,
                                        onExpandedChange = { adminStatusMenuExpanded = !adminStatusMenuExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedNewStatus,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Update Order Status") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = adminStatusMenuExpanded) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                                .testTag("admin_status_dropdown")
                                        )
                                        ExposedDropdownMenu(
                                            expanded = adminStatusMenuExpanded,
                                            onDismissRequest = { adminStatusMenuExpanded = false }
                                        ) {
                                            statusOptions.forEach { st ->
                                                DropdownMenuItem(
                                                    text = { Text(st) },
                                                    onClick = {
                                                        selectedNewStatus = st
                                                        adminStatusMenuExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = adminPublicProgressInput,
                                        onValueChange = { adminPublicProgressInput = it },
                                        label = { Text("Client-Visible Progress Update") },
                                        placeholder = { Text("e.g. Design team is color grading 3 variations.") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_admin_public_update"),
                                        singleLine = false,
                                        maxLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = adminPrivateNotesInput,
                                        onValueChange = { adminPrivateNotesInput = it },
                                        label = { Text("Internal Admin Notes (Private — Not shown to client)") },
                                        placeholder = { Text("Designer assignments, asset quality notes, internal comments...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_admin_private_notes"),
                                        singleLine = false,
                                        maxLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                onUpdateStatusByAdmin(selectedNewStatus, adminPrivateNotesInput, adminPublicProgressInput)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = VantaNavy),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_save_status_admin")
                                        ) {
                                            Text("Save Status & Notes", color = Color.White)
                                        }

                                        Button(
                                            onClick = { showDeliverDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = VantaAccent),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_open_deliver_dialog")
                                        ) {
                                            Text("Deliver Order", fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Dialog: Request Revision (Client)
    if (showRevisionDialog) {
        AlertDialog(
            onDismissRequest = { showRevisionDialog = false },
            title = {
                Text("Request Creative Revision", fontWeight = FontWeight.Bold, color = VantaNavy)
            },
            text = {
                Column {
                    Text(
                        text = "Describe the adjustments you'd like on your ad creatives (e.g. typography changes, color adjustments, or logo placement).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = revisionFeedbackText,
                        onValueChange = { revisionFeedbackText = it },
                        label = { Text("Revision Notes & Feedback *") },
                        placeholder = { Text("Please make the background silver accents darker and increase headline size...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_revision_feedback"),
                        minLines = 3,
                        maxLines = 6
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This will use Revision Round ${order.revisionCount + 1} of ${order.maxRevisions}.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (revisionFeedbackText.isNotBlank()) {
                            onRequestRevision(revisionFeedbackText)
                            showRevisionDialog = false
                            revisionFeedbackText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_submit_revision_confirm")
                ) {
                    Text("Submit Revision Request", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRevisionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal Dialog: Deliver Order (Admin)
    if (showDeliverDialog) {
        AlertDialog(
            onDismissRequest = { showDeliverDialog = false },
            title = {
                Text("Deliver Finished Ad Creatives", fontWeight = FontWeight.Bold, color = VantaNavy)
            },
            text = {
                Column {
                    Text(
                        text = "Upload or package the final deliverables and ZIP archive for ${order.clientBusinessName}. This will change status to 'Completed' and dispatch notification email.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = deliverTitle,
                        onValueChange = { deliverTitle = it },
                        label = { Text("Deliverable Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deliverFileName,
                        onValueChange = { deliverFileName = it },
                        label = { Text("Deliverable File Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deliverNotes,
                        onValueChange = { deliverNotes = it },
                        label = { Text("Delivery Note to Client") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeliverOrderByAdmin(deliverTitle, deliverFileName, order.aspectRatio, deliverNotes)
                        showDeliverDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VantaAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_confirm_deliver_order")
                ) {
                    Text("Confirm Delivery & Notify Client", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeliverDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = VantaNavy,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun DeliverableCard(item: DeliverableItem, onDownload: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (item.fileType.contains("ZIP")) VantaNavySurface else VantaAccent)
                            .border(1.dp, VantaSilver.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.fileType.contains("ZIP")) Icons.Default.FolderZip else Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = VantaNavy
                        )
                        Text(
                            text = "${item.fileName} • ${item.fileSize} • ${item.aspectRatio}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Ready", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }
            }

            // Preview image if available
            if (item.assetDrawableName.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                val imageRes = if (item.assetDrawableName == "sample_ad_perfume") {
                    R.drawable.sample_ad_perfume
                } else {
                    R.drawable.sample_ad_fashion
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VantaNavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Creative Preview",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Download controls
            Button(
                onClick = onDownload,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_download_deliverable_${item.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = VantaNavy),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download File (${item.fileSize})", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
