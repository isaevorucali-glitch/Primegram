package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CloudFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudScreen(
    files: List<CloudFile>,
    onAddFile: (String, String, String, Boolean, String, String) -> Unit,
    onRemoveFile: (Long) -> Unit
) {
    var isVaultUnlocked by remember { mutableStateOf(false) }
    var pinValue by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    var showAddFileDialog by remember { mutableStateOf(false) }
    var fileToDecrypt by remember { mutableStateOf<CloudFile?>(null) }
    var fileDecryptionKeyString by remember { mutableStateOf("") }
    var fileDecryptError by remember { mutableStateOf(false) }
    var viewedDecryptedContents by remember { mutableStateOf<String?>(null) }

    if (!isVaultUnlocked) {
        // SECURE PASSCODE INPUT LOCK SCREEN TO OPEN CLOUD VAULT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "SHIELDED STORAGE NODE",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Decrypt master drive with PIN passcode.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = pinValue,
                    onValueChange = { 
                        pinValue = it
                        pinError = false 
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    placeholder = { Text("Default PIN is 1234", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                    isError = pinError,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 20.sp, letterSpacing = 4.sp),
                    modifier = Modifier
                        .width(240.dp)
                        .testTag("pin_code_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                    )
                )

                if (pinError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "INVALID DECRYPTION PIN SIG",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (pinValue == "1234" || pinValue == "1337") {
                            isVaultUnlocked = true
                        } else {
                            pinError = true
                        }
                    },
                    modifier = Modifier
                        .width(240.dp)
                        .height(50.dp)
                        .testTag("unlock_vault_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Decompress & Verify Keys", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        // EXPLOER FILES DASHBOARD
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Secure Drive Node",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF22C55E).copy(alpha = 0.12f))
                                        .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "VAULT-ON",
                                        color = Color(0xFF4ADE80),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                            Text(
                                "Client-side isolated vault • Zero logs synced",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF64748B),
                                letterSpacing = 1.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color(0xFF0F1115),
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = { isVaultUnlocked = false; pinValue = "" }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Lock Shield", tint = Color(0xFF3B82F6))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddFileDialog = true },
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("upload_file_fab")
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload secure document")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F1115))
                    .padding(innerPadding)
            ) {
                // Curved Messages Container (HTML: bg-[#15171D] rounded-t-[32px] border-t border-white/5)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color(0xFF15171D))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                ) {
                    // Header of file list
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURE DOCUMENT TREE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.5.sp
                        )

                        // Biometrics Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF3B82F6).copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.2f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF3B82F6), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BIOMETRIC LOCK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6),
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    if (files.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.White.copy(alpha = 0.08f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Zero documents mapped to vault",
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                            
                            items(files) { doc ->
                                CloudFileItem(
                                    file = doc,
                                    onClick = {
                                        if (doc.isSecret) {
                                            fileToDecrypt = doc
                                        } else {
                                            viewedDecryptedContents = doc.decryptedData
                                        }
                                    },
                                    onShred = { onRemoveFile(doc.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // UPLOAD SECURE DOCUMENT DIALOG
    if (showAddFileDialog) {
        AddFileDialog(
            onDismiss = { showAddFileDialog = false },
            onConfirm = { name, bytesStr, mime, isSecret, secretPwd, txtContents ->
                onAddFile(name, bytesStr, mime, isSecret, secretPwd, txtContents)
                showAddFileDialog = false
            }
        )
    }

    // INDIVIDUAL FILE SECURITY PASSCODE CHALLENGE
    val activeToDecrypt = fileToDecrypt
    if (activeToDecrypt != null) {
        AlertDialog(
            onDismissRequest = { 
                fileToDecrypt = null
                fileDecryptionKeyString = ""
                fileDecryptError = false
            },
            title = { Text("Locked Payload Decryption") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "This document is encrypted using extra key hashes on this vault. Enter correct decryption key sequence:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = fileDecryptionKeyString,
                        onValueChange = { 
                            fileDecryptionKeyString = it
                            fileDecryptError = false
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        placeholder = { Text("Hint: initial seeded is 1234 or 1337") },
                        isError = fileDecryptError,
                        modifier = Modifier.fillMaxWidth().testTag("file_pass_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (fileDecryptError) {
                        Text(
                            "SIGNATURE MISMATCH: KEY REJECTED BY TRUST MODULE",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fileDecryptionKeyString == activeToDecrypt.passwordHash) {
                            viewedDecryptedContents = activeToDecrypt.decryptedData
                            fileToDecrypt = null
                            fileDecryptionKeyString = ""
                        } else {
                            fileDecryptError = true
                        }
                    },
                    modifier = Modifier.testTag("submit_file_unlock_button")
                ) {
                    Text("Decrypt Payload")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        fileToDecrypt = null
                        fileDecryptionKeyString = ""
                        fileDecryptError = false
                    }
                ) {
                    Text("Abort")
                }
            }
        )
    }

    // DISPLAY PLAIN DECRYPTED CONTENT VIEW SCREEN
    val viewingMemo = viewedDecryptedContents
    if (viewingMemo != null) {
        AlertDialog(
            onDismissRequest = { viewedDecryptedContents = null },
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Decrypted Document Payload")
                }
            },
            text = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Box(modifier = Modifier.padding(16.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                        Text(
                            text = viewingMemo,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewedDecryptedContents = null }) {
                    Text("Shred Dynamic View")
                }
            }
        )
    }
}

@Composable
fun CloudFileItem(
    file: CloudFile,
    onClick: () -> Unit,
    onShred: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("cloud_file_card_${file.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (file.isSecret) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) 
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (file.isSecret) Icons.Default.VpnKey else Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = if (file.isSecret) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${file.size} • ${file.mimeType.substringAfter("/")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    if (file.isSecret) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Double Crypt Protection",
                            modifier = Modifier.size(10.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                        )
                    }
                }
                Text(
                    text = "Stored: ${file.dateStr}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
            }

            IconButton(onClick = onShred) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Shred",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun AddFileDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Boolean, String, String) -> Unit
) {
    var docName by remember { mutableStateOf("") }
    var mimeType by remember { mutableStateOf("text/plain") }
    var rawTextContents by remember { mutableStateOf("") }
    var doubleEnryptSwitch by remember { mutableStateOf(false) }
    var credentialText by remember { mutableStateOf("") }

    val typeOptions = listOf(
        Pair("Text Document", "text/plain"),
        Pair("Classified PDF", "application/pdf"),
        Pair("Image Node", "image/png")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Crypt Document Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = docName,
                    onValueChange = { docName = it },
                    label = { Text("Document Title") },
                    placeholder = { Text("e.g. secure_seeds.txt") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_file_name"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Type picker
                Column {
                    Text("Select MIME Payload Tag:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        typeOptions.forEach { opt ->
                            val active = mimeType == opt.second
                            Box(
                                modifier = Modifier
                                    .weight(11f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { mimeType = opt.second }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    opt.first,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = rawTextContents,
                    onValueChange = { rawTextContents = it },
                    label = { Text("Encrypted Data Contents") },
                    placeholder = { Text("Type classified logs here.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("dialog_file_contents"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Password Lock Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Credential Locked Block", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Requires a unique signature PIN to unlock.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                    Switch(
                        checked = doubleEnryptSwitch,
                        onCheckedChange = { doubleEnryptSwitch = it },
                        modifier = Modifier.testTag("dialog_file_sec_switch")
                    )
                }

                if (doubleEnryptSwitch) {
                    OutlinedTextField(
                        value = credentialText,
                        onValueChange = { credentialText = it },
                        label = { Text("Private Document Passcode PIN") },
                        placeholder = { Text("e.g. 1234") },
                        modifier = Modifier.fillMaxWidth().testTag("dialog_file_sec_pin"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (docName.trim().isNotEmpty() && rawTextContents.trim().isNotEmpty()) {
                        val sizeStr = "${(rawTextContents.length * 3 / 4) + 1} B"
                        onConfirm(
                            docName.trim(),
                            sizeStr,
                            mimeType,
                            doubleEnryptSwitch,
                            if (doubleEnryptSwitch) credentialText.trim() else "",
                            rawTextContents.trim()
                        )
                    }
                },
                modifier = Modifier.testTag("dialog_confirm_add_file")
            ) {
                Text("Stash Securely")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
