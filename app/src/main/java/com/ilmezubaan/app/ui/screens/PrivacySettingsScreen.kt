package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    onBack: () -> Unit
) {
    var shareProgress by remember { mutableStateOf(true) }
    var showOnLeaderboard by remember { mutableStateOf(true) }
    var marketingEmails by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
                title = { Text("Privacy Settings", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Control your data and how it's shared with the Ilm-e-Zubaan community.",
                color = TextGrey,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            PrivacyToggleItem(
                title = "Share Learning Progress",
                description = "Allow friends to see which languages you are mastering.",
                checked = shareProgress,
                onCheckedChange = { shareProgress = it },
                activeColor = NeonCyan
            )

            Spacer(Modifier.height(16.dp))

            PrivacyToggleItem(
                title = "Show on Leaderboard",
                description = "Your name and XP will be visible on regional leaderboards.",
                checked = showOnLeaderboard,
                onCheckedChange = { showOnLeaderboard = it },
                activeColor = NeonPurple
            )

            Spacer(Modifier.height(16.dp))

            PrivacyToggleItem(
                title = "Marketing Communications",
                description = "Receive updates about new languages and features.",
                checked = marketingEmails,
                onCheckedChange = { marketingEmails = it },
                activeColor = NeonGreen
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { /* Save logic or just go back */ onBack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceLighter),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Delete Account", color = NeonRed, fontWeight = FontWeight.Bold)
            }
            
            Text(
                "Warning: Deleting your account is permanent and cannot be undone.",
                color = TextGrey.copy(0.5f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}

@Composable
fun PrivacyToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    activeColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurface
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(description, color = TextGrey, fontSize = 12.sp, lineHeight = 16.sp)
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = activeColor,
                    checkedTrackColor = activeColor.copy(0.3f),
                    uncheckedThumbColor = TextGrey,
                    uncheckedTrackColor = DarkSurfaceLighter
                )
            )
        }
    }
}
