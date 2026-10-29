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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgencyPackage
import com.example.model.AgencySettings
import com.example.model.EmailLog
import com.example.model.User
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
fun AgencySettingsScreen(
    currentUser: User?,
    agencySettings: AgencySettings?,
    packages: List<AgencyPackage>,
    emailLogs: List<EmailLog>,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSaveAgencySettings: (AgencySettings) -> Unit,
    onSavePackage: (AgencyPackage) -> Unit,
    onExportTemplate: () -> String,
    onImportPackages: (String, (Boolean, String) -> Unit) -> Unit,
    onUpdateUserProfile: (User) -> Unit = {}
) {
    val isAdmin = currentUser?.role == "ADMIN"
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Profile & Agency, 1: Policies & Governance, 2: Packages & Pricing (Editable), 3: Email Logs

    // Editable user profile credentials
    var editUserName by remember(currentUser) { mutableStateOf(currentUser?.name ?: if (isAdmin) "Sehar Asma" else "Client User") }
    var editUserEmail by remember(currentUser) { mutableStateOf(currentUser?.email ?: if (isAdmin) "sehar.asma30@gmail.com" else "client@example.com") }
    var editUserPassword by remember(currentUser) { mutableStateOf(currentUser?.passwordHash ?: if (isAdmin) "Muhammad@0101" else "") }
    var editUserPasswordVisible by remember { mutableStateOf(false) }
    var editUserBusiness by remember(currentUser) { mutableStateOf(currentUser?.businessName ?: if (isAdmin) "Vanta Digital Agency" else "") }
    var editUserCountry by remember(currentUser) { mutableStateOf(currentUser?.country ?: "Pakistan") }

    // Editable agency state
    var editAgencyName by remember(agencySettings) { mutableStateOf(agencySettings?.agencyName ?: "Vanta Digital") }
    var editTagline by remember(agencySettings) { mutableStateOf(agencySettings?.tagline ?: "Where Products Become Powerful Ads.") }
    var editContactEmail by remember(agencySettings) { mutableStateOf(agencySettings?.contactEmail ?: "briefs@vantadigital.com") }
    var editPhone by remember(agencySettings) { mutableStateOf(agencySettings?.phoneWhatsApp ?: "+92 300 1234567") }
    var editOfficeAddress by remember(agencySettings) { mutableStateOf(agencySettings?.officeAddress ?: "Gulberg III, Lahore, Pakistan") }
    var editDisclaimer by remember(agencySettings) { mutableStateOf(agencySettings?.disclaimer ?: "") }
    var editRevisionPolicy by remember(agencySettings) { mutableStateOf(agencySettings?.revisionPolicy ?: "") }
    var editDeliveryPolicy by remember(agencySettings) { mutableStateOf(agencySettings?.deliveryPolicy ?: "") }
    var editTermsOfService by remember(agencySettings) { mutableStateOf(agencySettings?.termsOfService ?: "") }

    // Package edit dialog
    var editingPackage by remember { mutableStateOf<AgencyPackage?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importCsvText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC))
    ) {
        // Top App Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = VantaNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp).testTag("btn_back_from_settings")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (isAdmin) "Admin Master Configuration" else "Portal Settings & Governance",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (isAdmin) "Everything is fully editable: Policies, WhatsApp, Packages, CSV Import/Export" else "Legal policies, agency tiers, and account details",
                            style = MaterialTheme.typography.bodySmall,
                            color = VantaSilver
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = VantaNavySurface,
                    contentColor = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Agency & Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Policies & Terms", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("20 Tiers (${packages.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Logs (${emailLogs.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: Agency Info & Profile
                    if (isAdmin) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaAccent.copy(alpha = 0.4f), Color(0xFFEDF2F7))))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Editable Agency Contact Details",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = VantaNavy
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(VantaAccent)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Admin Mode", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = editAgencyName,
                                        onValueChange = { editAgencyName = it },
                                        label = { Text("Agency Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editTagline,
                                        onValueChange = { editTagline = it },
                                        label = { Text("Brand Tagline") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editContactEmail,
                                        onValueChange = { editContactEmail = it },
                                        label = { Text("Support / Briefs Email") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editPhone,
                                        onValueChange = { editPhone = it },
                                        label = { Text("Phone / WhatsApp Support Desk") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editOfficeAddress,
                                        onValueChange = { editOfficeAddress = it },
                                        label = { Text("Office Address") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            val updated = AgencySettings(
                                                agencyName = editAgencyName,
                                                tagline = editTagline,
                                                contactEmail = editContactEmail,
                                                phoneWhatsApp = editPhone,
                                                officeAddress = editOfficeAddress,
                                                disclaimer = editDisclaimer,
                                                revisionPolicy = editRevisionPolicy,
                                                deliveryPolicy = editDeliveryPolicy,
                                                termsOfService = editTermsOfService
                                            )
                                            onSaveAgencySettings(updated)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = VantaNavy),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Save Agency Contact Info", color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    // Editable Profile & Account Credentials Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaAccent.copy(alpha = 0.5f), Color(0xFFEDF2F7))))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(VantaNavy)
                                                .border(2.dp, VantaSilver, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = if (isAdmin) "Admin Master Profile & Security" else "Client Account Profile",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = VantaNavy
                                            )
                                            Text(
                                                text = "Role: ${currentUser?.role ?: "CLIENT"} • Edit credentials below",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isAdmin) VantaAccent else Color(0xFF059669)
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFEFF6FF))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("Editable", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VantaAccent)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = editUserName,
                                    onValueChange = { editUserName = it },
                                    label = { Text("Full Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_edit_profile_name"),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = editUserEmail,
                                    onValueChange = { editUserEmail = it },
                                    label = { Text("Login Email Address") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_edit_profile_email"),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Password Field with Show/Hide toggle
                                OutlinedTextField(
                                    value = editUserPassword,
                                    onValueChange = { editUserPassword = it },
                                    label = { Text("Login Password") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    trailingIcon = {
                                        IconButton(onClick = { editUserPasswordVisible = !editUserPasswordVisible }) {
                                            Icon(
                                                imageVector = if (editUserPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = if (editUserPasswordVisible) "Hide password" else "Show password",
                                                tint = VantaNavySurface
                                            )
                                        }
                                    },
                                    visualTransformation = if (editUserPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth().testTag("input_edit_profile_password"),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = editUserBusiness,
                                    onValueChange = { editUserBusiness = it },
                                    label = { Text("Agency / Brand Name") },
                                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_edit_profile_business"),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = editUserCountry,
                                    onValueChange = { editUserCountry = it },
                                    label = { Text("Country") },
                                    modifier = Modifier.fillMaxWidth().testTag("input_edit_profile_country"),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        currentUser?.let { user ->
                                            val updated = user.copy(
                                                name = editUserName.trim(),
                                                email = editUserEmail.trim(),
                                                passwordHash = editUserPassword,
                                                businessName = editUserBusiness.trim(),
                                                country = editUserCountry.trim()
                                            )
                                            onUpdateUserProfile(updated)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VantaAccent),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("btn_save_profile_credentials")
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Profile & Credentials", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = onLogoutClick,
                                    modifier = Modifier.fillMaxWidth().testTag("btn_sign_out_settings"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sign Out of Portal", color = Color.White)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: Policies & Governance (Fully Editable for Admin)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = if (isAdmin) "Edit Legal & Agency Policies" else "Agency Terms & Policies",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VantaNavy
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                if (isAdmin) {
                                    OutlinedTextField(
                                        value = editDisclaimer,
                                        onValueChange = { editDisclaimer = it },
                                        label = { Text("Business Disclaimer") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 6
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = editRevisionPolicy,
                                        onValueChange = { editRevisionPolicy = it },
                                        label = { Text("Revision Policy Terms") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 6
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = editDeliveryPolicy,
                                        onValueChange = { editDeliveryPolicy = it },
                                        label = { Text("Delivery & Storage Policy") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 6
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = editTermsOfService,
                                        onValueChange = { editTermsOfService = it },
                                        label = { Text("Terms of Service & IP Rights") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 6
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            val updated = AgencySettings(
                                                agencyName = editAgencyName,
                                                tagline = editTagline,
                                                contactEmail = editContactEmail,
                                                phoneWhatsApp = editPhone,
                                                officeAddress = editOfficeAddress,
                                                disclaimer = editDisclaimer,
                                                revisionPolicy = editRevisionPolicy,
                                                deliveryPolicy = editDeliveryPolicy,
                                                termsOfService = editTermsOfService
                                            )
                                            onSaveAgencySettings(updated)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = VantaNavy),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Save All Policy Edits", color = Color.White)
                                    }
                                } else {
                                    // Read-only for clients
                                    PolicyDisplayBlock(title = "Business Disclaimer", content = editDisclaimer)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    PolicyDisplayBlock(title = "Revision Policy", content = editRevisionPolicy)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    PolicyDisplayBlock(title = "Delivery & Cloud Storage Policy", content = editDeliveryPolicy)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    PolicyDisplayBlock(title = "Terms of Service & IP", content = editTermsOfService)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Packages & 20 Tiers (Editable with CSV Import/Export)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaAccent.copy(alpha = 0.4f), Color(0xFFEDF2F7))))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Agency Tier Catalog (${packages.size} Tiers)",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = VantaNavy
                                        )
                                        Text(
                                            text = "Manage prices, creatives count, and turnaround for all 20 tiers.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Excel / CSV tools
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showExportDialog = true },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Export Template", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { showImportDialog = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = VantaAccent),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Import Excel/CSV", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    items(packages) { pkg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pkg.tierName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = VantaNavy
                                        )
                                        Text(
                                            text = "${pkg.creativesCount} Creatives • Turnaround: ${pkg.turnaroundDays} • Formats: ${pkg.deliverableFormats}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(pkg.priceUsd, fontWeight = FontWeight.Black, color = VantaAccent, fontSize = 14.sp)
                                        Text(pkg.pricePkr, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488), fontSize = 12.sp)
                                    }
                                }

                                if (isAdmin) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        OutlinedButton(
                                            onClick = { editingPackage = pkg },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Edit Tier Prices", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: Automated Server Email Dispatcher Logs
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaAccent, Color(0xFF93C5FD))))
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = VantaAccent)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Automated Email Notification System",
                                        fontWeight = FontWeight.Bold,
                                        color = VantaNavy,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Audits all order confirmation, status update, and delivery emails sent to clients and Admin (sehar.asma30@gmail.com).",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF1E3A8A)
                                    )
                                }
                            }
                        }
                    }

                    if (emailLogs.isEmpty()) {
                        item {
                            Text(
                                text = "No email notifications logged yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(emailLogs) { log ->
                            val timeStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US).format(Date(log.timestamp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = log.templateName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = VantaAccent)
                                    Text(text = log.subject, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = VantaNavy)
                                    Text(text = "To: ${log.toEmail}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                    if (log.details.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = log.details, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color(0xFF475569))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "$timeStr • via ${log.provider}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = Color(0xFF94A3B8))
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

    // MODAL DIALOG: Edit Specific Package
    editingPackage?.let { pkg ->
        var editName by remember { mutableStateOf(pkg.tierName) }
        var editCount by remember { mutableIntStateOf(pkg.creativesCount) }
        var editUsd by remember { mutableStateOf(pkg.priceUsd) }
        var editPkr by remember { mutableStateOf(pkg.pricePkr) }
        var editDays by remember { mutableStateOf(pkg.turnaroundDays) }
        var editFormats by remember { mutableStateOf(pkg.deliverableFormats) }
        var editDesc by remember { mutableStateOf(pkg.description) }

        AlertDialog(
            onDismissRequest = { editingPackage = null },
            title = {
                Text("Edit ${pkg.tierName}", fontWeight = FontWeight.Bold, color = VantaNavy)
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Tier Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = editUsd, onValueChange = { editUsd = it }, label = { Text("Price (USD)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = editPkr, onValueChange = { editPkr = it }, label = { Text("Price (PKR)") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editCount.toString(),
                            onValueChange = { editCount = it.toIntOrNull() ?: 1 },
                            label = { Text("Creatives Count") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(value = editDays, onValueChange = { editDays = it }, label = { Text("Turnaround") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editFormats, onValueChange = { editFormats = it }, label = { Text("Formats") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editDesc, onValueChange = { editDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = pkg.copy(
                            tierName = editName,
                            creativesCount = editCount,
                            priceUsd = editUsd,
                            pricePkr = editPkr,
                            turnaroundDays = editDays,
                            deliverableFormats = editFormats,
                            description = editDesc
                        )
                        onSavePackage(updated)
                        editingPackage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VantaNavy)
                ) {
                    Text("Save Changes", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingPackage = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // MODAL DIALOG: Export CSV Template
    if (showExportDialog) {
        val template = remember { onExportTemplate() }
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text("Excel/CSV Packages Template", fontWeight = FontWeight.Bold, color = VantaNavy)
            },
            text = {
                Column {
                    Text("Copy or view the standard CSV format to define your tiers in Excel:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = template,
                            fontSize = 10.sp,
                            color = Color(0xFF6EE7B7),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showExportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = VantaNavy)) {
                    Text("Done", color = Color.White)
                }
            }
        )
    }

    // MODAL DIALOG: Import CSV Packages
    if (showImportDialog) {
        var importError by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text("Import Packages File (CSV / Excel)", fontWeight = FontWeight.Bold, color = VantaNavy)
            },
            text = {
                Column {
                    Text("Paste your filled CSV data below to update all 20 tiers and their prices:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importCsvText,
                        onValueChange = { importCsvText = it; importError = null },
                        placeholder = { Text("Paste CSV lines here...") },
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        minLines = 5
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(onClick = { importCsvText = onExportTemplate() }) {
                        Text("Fill with Default Template Data", fontSize = 11.sp)
                    }
                    if (importError != null) {
                        Text(importError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importCsvText.isBlank()) {
                            importError = "Please enter CSV data."
                        } else {
                            onImportPackages(importCsvText) { success, msg ->
                                if (success) {
                                    showImportDialog = false
                                    importCsvText = ""
                                } else {
                                    importError = msg
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VantaAccent)
                ) {
                    Text("Upload & Apply", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PolicyDisplayBlock(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, color = VantaNavy, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = content, style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569), lineHeight = 18.sp)
    }
}
