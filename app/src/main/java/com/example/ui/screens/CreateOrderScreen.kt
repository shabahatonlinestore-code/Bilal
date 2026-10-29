package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AgencyPackage
import com.example.model.User
import com.example.ui.theme.VantaAccent
import com.example.ui.theme.VantaCardBorder
import com.example.ui.theme.VantaNavy
import com.example.ui.theme.VantaNavyDark
import com.example.ui.theme.VantaNavySurface
import com.example.ui.theme.VantaSilver
import com.example.ui.theme.VantaSilverLight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateOrderScreen(
    currentUser: User?,
    packages: List<AgencyPackage>,
    onExportTemplate: () -> String,
    onImportPackages: (String, (Boolean, String) -> Unit) -> Unit,
    onCancel: () -> Unit,
    onSubmitOrder: (
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
        isDraft: Boolean
    ) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) } // 1, 2, 3, 4

    // Step 1: Product Details
    var productName by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf(currentUser?.businessName ?: "") }
    var selectedCategory by remember { mutableStateOf("Fashion") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var targetAudience by remember { mutableStateOf("") }
    var targetMarket by remember { mutableStateOf("Pakistan & International") }
    var preferredLanguage by remember { mutableStateOf("English & Urdu") }
    var websiteUrl by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("PKR") }

    val categories = listOf(
        "Fashion", "Clothing", "Beauty", "Skincare", "Jewelry",
        "Food", "Electronics", "Home Decor", "Handbags", "Digital Product", "Other"
    )

    // Step 2: Upload Photos (Require at least 15 images!)
    val uploadedPhotos = remember { mutableStateListOf<String>() }
    var uploadErrorMessage by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(30)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                if (uploadedPhotos.size < 30) {
                    uploadedPhotos.add(uri.toString())
                }
            }
            uploadErrorMessage = null
        }
    }

    // Step 3: Advertising Brief & Packages
    var adFormat by remember { mutableStateOf("Carousel Ad") }
    val adFormats = listOf("Static Ad", "Carousel Ad", "Story Ad", "Reel Ad", "Promo Graphic", "Other")

    var platform by remember { mutableStateOf("Instagram") }
    val platforms = listOf("Instagram", "Facebook", "TikTok", "Pinterest", "Etsy", "Google Display")

    var aspectRatio by remember { mutableStateOf("Portrait 4:5") }
    val aspectRatios = listOf("Square 1:1", "Portrait 4:5", "Story 9:16", "Landscape 16:9", "Custom")

    var campaignObjective by remember { mutableStateOf("Sales") }
    val objectives = listOf("Sales", "Brand Awareness", "Product Launch", "Traffic", "Engagement")

    var designStyle by remember { mutableStateOf("Luxury") }
    val designStyles = listOf("Luxury", "Minimal", "Modern", "Bold", "Elegant", "Playful", "Festive")

    var colorsTypography by remember { mutableStateOf("") }
    var adHeadline by remember { mutableStateOf("") }
    var promotionalOffer by remember { mutableStateOf("") }
    var callToAction by remember { mutableStateOf("Shop Now") }
    var textToInclude by remember { mutableStateOf("") }
    var referenceImageUri by remember { mutableStateOf("") }
    var additionalInstructions by remember { mutableStateOf("") }

    // Selected package tier (from dynamic packages list, up to 20 tiers)
    var selectedPackage by remember(packages) {
        mutableStateOf(packages.firstOrNull() ?: AgencyPackage("tier_01", 1, "Tier 1: Starter Ad", 1, "$39", "PKR 11,000", "2 Days", "1:1 Square", "Standard single ad"))
    }

    // Price Popup Modal state
    var popupPackage by remember { mutableStateOf<AgencyPackage?>(null) }

    // Excel / CSV Template & Import Modal states
    var showExportTemplateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importCsvContent by remember { mutableStateOf("") }

    var desiredDeliveryDate by remember { mutableStateOf("Within 4-5 Business Days") }
    var clientRightsConfirmed by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var formValidationNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC))
    ) {
        // Step Header Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = VantaNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onCancel, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Book Advertising Creatives",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Step $currentStep of 4: " + when (currentStep) {
                                    1 -> "Product Details"
                                    2 -> "Upload Photos (Min 15)"
                                    3 -> "Brief & Package Tiers"
                                    else -> "Review & Submit"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = VantaSilver
                            )
                        }
                    }

                    // Save draft button
                    OutlinedButton(
                        onClick = {
                            if (productName.isBlank()) {
                                formValidationNotice = "Please enter product name to save draft."
                            } else {
                                onSubmitOrder(
                                    productName, brandName, selectedCategory, description,
                                    targetAudience, targetMarket, preferredLanguage, websiteUrl,
                                    productPrice, currency, uploadedPhotos.toList(), adFormat,
                                    platform, aspectRatio, campaignObjective, designStyle,
                                    colorsTypography, adHeadline, promotionalOffer, callToAction,
                                    textToInclude, referenceImageUri, additionalInstructions,
                                    selectedPackage.creativesCount,
                                    selectedPackage.tierName,
                                    "${selectedPackage.priceUsd} / ${selectedPackage.pricePkr}",
                                    desiredDeliveryDate, clientRightsConfirmed, true
                                )
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VantaSilverLight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_save_draft")
                    ) {
                        Text("Save Draft", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4-Step Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 1..4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (i <= currentStep) VantaAccent else Color.White.copy(alpha = 0.25f)
                                )
                        )
                    }
                }
            }
        }

        // Form Body Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                1 -> {
                    // STEP 1: Product Details
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Step 1: Product Details",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VantaNavy
                            )
                            Text(
                                text = "Enter the details for the product you want Vanta Digital to design ads for.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = productName,
                                onValueChange = { productName = it; formValidationNotice = null },
                                label = { Text("Product Name * (Required)") },
                                placeholder = { Text("e.g. Silk Velvet Shawl / Luxury Serum") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_product_name"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = brandName,
                                onValueChange = { brandName = it },
                                label = { Text("Brand / Business Name *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_brand_name"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Category Dropdown
                            ExposedDropdownMenuBox(
                                expanded = categoryExpanded,
                                onExpandedChange = { categoryExpanded = !categoryExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedCategory,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Product Category") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .testTag("dropdown_category")
                                )
                                ExposedDropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false }
                                ) {
                                    categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                selectedCategory = cat
                                                categoryExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Product Description & Unique Selling Points") },
                                placeholder = { Text("Describe materials, key benefits, fragrance notes, or key selling angles...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_product_desc"),
                                minLines = 3,
                                maxLines = 5
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = targetAudience,
                                onValueChange = { targetAudience = it },
                                label = { Text("Target Audience") },
                                placeholder = { Text("e.g. Women age 20-38 in Pakistan & UAE") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_target_audience"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = targetMarket,
                                    onValueChange = { targetMarket = it },
                                    label = { Text("Target Market") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_target_market"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = preferredLanguage,
                                    onValueChange = { preferredLanguage = it },
                                    label = { Text("Language") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_language"),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = websiteUrl,
                                onValueChange = { websiteUrl = it },
                                label = { Text("Website / Instagram Store Link (Optional)") },
                                placeholder = { Text("https://yourbrand.com or @yourinstagram") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_product_url"),
                                singleLine = true
                            )
                        }
                    }
                }

                2 -> {
                    // STEP 2: Upload Photos (Require at least 15 photos)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Step 2: Upload Product Photos",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = VantaNavy
                                    )
                                    Text(
                                        text = "Upload at least 15 photos to submit your order.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (uploadedPhotos.size >= 15) Color(0xFF059669) else Color(0xFFDC2626)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (uploadedPhotos.size >= 15) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                                        )
                                        .border(
                                            1.dp,
                                            if (uploadedPhotos.size >= 15) Color(0xFF16A34A) else Color(0xFFEF4444),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${uploadedPhotos.size} / 15 Minimum",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (uploadedPhotos.size >= 15) Color(0xFF16A34A) else Color(0xFFDC2626)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val progress = (uploadedPhotos.size / 15f).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (uploadedPhotos.size >= 15) Color(0xFF16A34A) else VantaAccent,
                                trackColor = Color(0xFFE2E8F0)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(
                                        2.dp,
                                        Brush.horizontalGradient(listOf(VantaAccent.copy(alpha = 0.5f), VantaSilver)),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .padding(20.dp)
                                    .testTag("box_photo_upload_zone"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = VantaAccent,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Select Product Photos from Device",
                                        fontWeight = FontWeight.Bold,
                                        color = VantaNavy
                                    )
                                    Text(
                                        text = "Supported: JPG, JPEG, PNG, WebP • Max 10MB • Up to 30 photos",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quick photoshoot angles:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                                Button(
                                    onClick = {
                                        uploadedPhotos.clear()
                                        for (i in 1..16) {
                                            uploadedPhotos.add("sample_angle_${i}_${selectedCategory.lowercase()}.jpg")
                                        }
                                        uploadErrorMessage = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VantaNavySurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("btn_add_16_sample_photos")
                                ) {
                                    Text("Auto-Attach 16 Angles", fontSize = 11.sp, color = Color.White)
                                }
                            }

                            if (uploadedPhotos.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Attached Photos (${uploadedPhotos.size}):",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = VantaNavy
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    itemsIndexed(uploadedPhotos) { index, photoPath ->
                                        Box(
                                            modifier = Modifier
                                                .height(85.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE2E8F0))
                                                .border(1.dp, VantaCardBorder, RoundedCornerShape(8.dp))
                                        ) {
                                            if (photoPath.startsWith("content://") || photoPath.startsWith("file://")) {
                                                AsyncImage(
                                                    model = photoPath,
                                                    contentDescription = "Uploaded photo $index",
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(4.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Image,
                                                        contentDescription = null,
                                                        tint = VantaNavySurface,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = "Photo #${index + 1}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = VantaNavy
                                                    )
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(2.dp)
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.6f))
                                                    .clickable { uploadedPhotos.removeAt(index) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // STEP 3: Advertising Brief & Compact Option Boxes with Price Popup & CSV Import/Export
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Step 3: Advertising Brief & Package Tiers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VantaNavy
                            )
                            Text(
                                text = "Select your package tier, dimensions, and ad objectives. Tap any tier to view full pricing details.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Excel / CSV File Import & Export Tools
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.TableChart, contentDescription = null, tint = VantaAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Excel/CSV Packages:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VantaNavy
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { showExportTemplateDialog = true },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.testTag("btn_export_dummy_excel")
                                    ) {
                                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Export Template", fontSize = 10.sp)
                                    }

                                    Button(
                                        onClick = { showImportDialog = true },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = VantaAccent),
                                        modifier = Modifier.testTag("btn_import_packages_file")
                                    ) {
                                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Import File", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // PACKAGE TIERS SELECTOR (Compact boxes / chips, up to 20 tiers)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Choose Package Tier (${packages.size} Tiers Available):",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = VantaNavy
                                )
                                Text(
                                    text = "Tap for price popup",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = VantaAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Compact grid of package boxes
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                packages.forEach { pkg ->
                                    val isSelected = selectedPackage.id == pkg.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) VantaNavy else Color(0xFFF8FAFC))
                                            .border(
                                                1.dp,
                                                if (isSelected) VantaAccent else VantaCardBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                selectedPackage = pkg
                                                // Trigger price popup modal immediately on tap as requested!
                                                popupPackage = pkg
                                            }
                                            .padding(horizontal = 10.dp, vertical = 7.dp)
                                            .testTag("tier_box_${pkg.id}")
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = pkg.tierName.take(24),
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                    color = if (isSelected) Color.White else VantaNavy
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = pkg.pricePkr,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isSelected) Color(0xFF6EE7B7) else VantaAccent
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Selected Tier Summary Banner with Click-to-reopen popup
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, VantaAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .clickable { popupPackage = selectedPackage }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Selected: ${selectedPackage.tierName}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = VantaNavy
                                        )
                                        Text(
                                            text = "${selectedPackage.creativesCount} Creatives • Turnaround: ${selectedPackage.turnaroundDays} • ${selectedPackage.deliverableFormats}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = selectedPackage.priceUsd,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = VantaAccent
                                        )
                                        Text(
                                            text = selectedPackage.pricePkr,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0D9488)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // COMPACT OPTIONS: Deliverable Format (Chohtay boxes)
                            Text(
                                text = "Deliverable Format",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = VantaNavy
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                adFormats.forEach { f ->
                                    val sel = adFormat == f
                                    CompactPill(label = f, isSelected = sel, onClick = { adFormat = f })
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // COMPACT OPTIONS: Platform
                            Text(
                                text = "Target Platform",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = VantaNavy
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                platforms.forEach { p ->
                                    val sel = platform == p
                                    CompactPill(label = p, isSelected = sel, onClick = { platform = p })
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // COMPACT OPTIONS: Dimensions & Aspect Ratio
                            Text(
                                text = "Aspect Ratio",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = VantaNavy
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                aspectRatios.forEach { ratio ->
                                    val sel = aspectRatio == ratio
                                    CompactPill(label = ratio, isSelected = sel, onClick = { aspectRatio = ratio })
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // COMPACT OPTIONS: Campaign Objective
                            Text(
                                text = "Campaign Objective",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = VantaNavy
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                objectives.forEach { obj ->
                                    val sel = campaignObjective == obj
                                    CompactPill(label = obj, isSelected = sel, onClick = { campaignObjective = obj })
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // COMPACT OPTIONS: Design Style
                            Text(
                                text = "Design Style",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = VantaNavy
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                designStyles.forEach { st ->
                                    val sel = designStyle == st
                                    CompactPill(label = st, isSelected = sel, onClick = { designStyle = st })
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = adHeadline,
                                onValueChange = { adHeadline = it },
                                label = { Text("Ad Headline (Hook)") },
                                placeholder = { Text("e.g. ULTRA GLOW - DISCOVER THE RADIANCE") },
                                modifier = Modifier.fillMaxWidth().testTag("input_ad_headline"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = promotionalOffer,
                                onValueChange = { promotionalOffer = it },
                                label = { Text("Promotional Offer (Optional)") },
                                placeholder = { Text("e.g. FLAT 20% OFF or Free Delivery") },
                                modifier = Modifier.fillMaxWidth().testTag("input_promo_offer"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = callToAction,
                                    onValueChange = { callToAction = it },
                                    label = { Text("CTA Button") },
                                    modifier = Modifier.weight(1f).testTag("input_cta"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = colorsTypography,
                                    onValueChange = { colorsTypography = it },
                                    label = { Text("Colors & Theme") },
                                    modifier = Modifier.weight(1f).testTag("input_brand_colors"),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = additionalInstructions,
                                onValueChange = { additionalInstructions = it },
                                label = { Text("Additional Creative Instructions * (Required)") },
                                placeholder = { Text("Lighting preferences, specific angles, model presence, or reference styles...") },
                                modifier = Modifier.fillMaxWidth().testTag("input_additional_instructions"),
                                minLines = 3,
                                maxLines = 5
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Rights confirmation checkbox
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(8.dp)
                                    .clickable { clientRightsConfirmed = !clientRightsConfirmed }
                            ) {
                                Checkbox(
                                    checked = clientRightsConfirmed,
                                    onCheckedChange = { clientRightsConfirmed = it },
                                    modifier = Modifier.testTag("checkbox_rights")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "I confirm that I have legal authorization to use all uploaded photos, trademarks, and brand assets for advertising creative creation.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = VantaNavy
                                )
                            }
                        }
                    }
                }

                4 -> {
                    // STEP 4: Review and Submit Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VantaCardBorder, Color(0xFFEDF2F7))))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Step 4: Review and Submit",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VantaNavy
                            )
                            Text(
                                text = "Verify your advertising creative order before sending to the Vanta Digital creative team.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            ReviewItem(
                                label = "Product & Brand",
                                value = "$productName (${brandName.ifBlank { "Unbranded" }})",
                                onEdit = { currentStep = 1 }
                            )
                            ReviewItem(
                                label = "Category & Target Market",
                                value = "$selectedCategory • $targetMarket • $preferredLanguage",
                                onEdit = { currentStep = 1 }
                            )
                            ReviewItem(
                                label = "Photos Uploaded",
                                value = "${uploadedPhotos.size} photos attached " + if (uploadedPhotos.size >= 15) "(Ready for production)" else "(Min 15 required!)",
                                onEdit = { currentStep = 2 }
                            )
                            ReviewItem(
                                label = "Selected Tier Package",
                                value = "${selectedPackage.tierName} (${selectedPackage.priceUsd} / ${selectedPackage.pricePkr})",
                                onEdit = { currentStep = 3 }
                            )
                            ReviewItem(
                                label = "Format & Platform",
                                value = "$adFormat • $platform ($aspectRatio)",
                                onEdit = { currentStep = 3 }
                            )
                            ReviewItem(
                                label = "Creative Style & Hook",
                                value = "$designStyle • Headline: ${adHeadline.ifBlank { "Agency to craft" }}",
                                onEdit = { currentStep = 3 }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Submission Readiness Checks:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = VantaNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val hasName = productName.isNotBlank()
                                val hasPhotos = uploadedPhotos.size >= 15
                                val hasRights = clientRightsConfirmed

                                CheckItem(title = "Product Name specified", passed = hasName)
                                CheckItem(title = "At least 15 product photos uploaded (${uploadedPhotos.size}/15)", passed = hasPhotos)
                                CheckItem(title = "Asset ownership rights confirmed", passed = hasRights)
                            }
                        }
                    }
                }
            }

            if (formValidationNotice != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFF87171))))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formValidationNotice ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }
        }

        // Bottom Step Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = {
                            currentStep--
                            formValidationNotice = null
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_prev_step")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (currentStep < 4) {
                    Button(
                        onClick = {
                            when (currentStep) {
                                1 -> {
                                    if (productName.isBlank()) {
                                        formValidationNotice = "Product name is required before proceeding."
                                    } else {
                                        formValidationNotice = null
                                        currentStep = 2
                                    }
                                }
                                2 -> {
                                    if (uploadedPhotos.size < 15) {
                                        formValidationNotice = "Upload at least 15 photos to submit your order (currently ${uploadedPhotos.size})."
                                    } else {
                                        formValidationNotice = null
                                        currentStep = 3
                                    }
                                }
                                3 -> {
                                    if (additionalInstructions.isBlank()) {
                                        formValidationNotice = "Please provide additional creative instructions for the agency."
                                    } else if (!clientRightsConfirmed) {
                                        formValidationNotice = "Please confirm that you own or have permission to use submitted assets."
                                    } else {
                                        formValidationNotice = null
                                        currentStep = 4
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VantaNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_next_step")
                    ) {
                        Text("Next Step")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            if (isSubmitting) return@Button
                            if (productName.isBlank() || uploadedPhotos.size < 15 || !clientRightsConfirmed) {
                                formValidationNotice = "Cannot submit: Please ensure product name, at least 15 photos, and rights confirmation are completed."
                            } else {
                                isSubmitting = true
                                formValidationNotice = null
                                onSubmitOrder(
                                    productName, brandName, selectedCategory, description,
                                    targetAudience, targetMarket, preferredLanguage, websiteUrl,
                                    productPrice, currency, uploadedPhotos.toList(), adFormat,
                                    platform, aspectRatio, campaignObjective, designStyle,
                                    colorsTypography, adHeadline, promotionalOffer, callToAction,
                                    textToInclude, referenceImageUri, additionalInstructions,
                                    selectedPackage.creativesCount,
                                    selectedPackage.tierName,
                                    "${selectedPackage.priceUsd} / ${selectedPackage.pricePkr}",
                                    desiredDeliveryDate, clientRightsConfirmed, false
                                )
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = VantaAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_submit_order_final")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSubmitting) "Submitting..." else "Submit Order to Agency", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // MODAL POPUP: Price & Package Breakdown Popup (Triggered whenever any option is selected!)
    popupPackage?.let { pkg ->
        AlertDialog(
            onDismissRequest = { popupPackage = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PriceChange, contentDescription = null, tint = VantaAccent, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = pkg.tierName, fontWeight = FontWeight.Bold, color = VantaNavy, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    // Big price tags
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEFF6FF))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Package Price (USD)", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(pkg.priceUsd, fontSize = 20.sp, fontWeight = FontWeight.Black, color = VantaAccent)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Local Price (PKR)", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(pkg.pricePkr, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailLine(label = "Creatives Included:", value = "${pkg.creativesCount} Custom Master Ads")
                    DetailLine(label = "Turnaround Time:", value = pkg.turnaroundDays)
                    DetailLine(label = "Formats & Ratios:", value = pkg.deliverableFormats)
                    DetailLine(label = "Revisions Included:", value = "2 Full Rounds Included")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Package Description:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = VantaNavy
                    )
                    Text(
                        text = pkg.description,
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedPackage = pkg
                        popupPackage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VantaNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Select This Tier", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { popupPackage = null }) {
                    Text("Close")
                }
            }
        )
    }

    // MODAL DIALOG: Export Dummy Excel / CSV Template
    if (showExportTemplateDialog) {
        val templateText = remember { onExportTemplate() }
        AlertDialog(
            onDismissRequest = { showExportTemplateDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = VantaAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Excel/CSV Packages Template", fontWeight = FontWeight.Bold, color = VantaNavy, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Here is the standard CSV/Excel template format for your 20 tiers. You can fill this in Excel or Google Sheets and re-upload it below:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = templateText,
                            fontSize = 10.sp,
                            color = Color(0xFF6EE7B7),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Columns: Tier_ID, Order, Tier_Name, Creatives_Count, Price_USD, Price_PKR, Turnaround_Days, Deliverable_Formats, Description",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color(0xFF94A3B8)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportTemplateDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VantaNavy)
                ) {
                    Text("Done", color = Color.White)
                }
            }
        )
    }

    // MODAL DIALOG: Import Packages File (CSV / Excel Content)
    if (showImportDialog) {
        var importError by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = VantaAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import Packages File (CSV / Excel)", fontWeight = FontWeight.Bold, color = VantaNavy, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Paste your filled CSV / Excel package definitions below to update all tier prices and package options dynamically:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = importCsvContent,
                        onValueChange = { importCsvContent = it; importError = null },
                        placeholder = { Text("Paste CSV rows here or load sample...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        minLines = 6
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            importCsvContent = onExportTemplate()
                        }) {
                            Text("Fill with Template Data", fontSize = 11.sp)
                        }
                    }

                    if (importError != null) {
                        Text(text = importError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importCsvContent.isBlank()) {
                            importError = "Please paste or enter CSV content to import."
                        } else {
                            onImportPackages(importCsvContent) { success, msg ->
                                if (success) {
                                    showImportDialog = false
                                    importCsvContent = ""
                                } else {
                                    importError = msg
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VantaAccent)
                ) {
                    Text("Upload & Update Packages", color = Color.White)
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
private fun CompactPill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) VantaNavy else Color(0xFFF1F5F9))
            .border(1.dp, if (isSelected) VantaNavy else VantaCardBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) Color.White else VantaNavy,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VantaNavy)
    }
}

@Composable
private fun ReviewItem(label: String, value: String, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
            Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VantaNavy)
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit $label", tint = VantaAccent, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun CheckItem(title: String, passed: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (passed) Color(0xFF16A34A) else Color(0xFFDC2626),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (passed) Color(0xFF166534) else Color(0xFF991B1B),
            fontWeight = if (passed) FontWeight.Normal else FontWeight.Bold
        )
    }
}
