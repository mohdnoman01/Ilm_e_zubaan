package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.data.local.entities.UserStats
import com.ilmezubaan.app.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userStats: UserStats,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onPrivacySettingsClick: () -> Unit,
    onUpdateAvatar: (String) -> Unit,
    onClearData: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    val avatars = listOf(
        "👤" to 0, "🔥" to 0, "🌟" to 500, "🎓" to 1000,
        "🏆" to 2000, "💎" to 3000, "🚀" to 5, "👑" to 10,
        "🦁" to 15, "🐉" to 20, "🧠" to 5000, "🌍" to 30
    )

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            containerColor = DarkSurface,
            titleContentColor = TextWhite,
            title = { Text("Choose Avatar") },
            text = {
                Column {
                    Text("Unlock new avatars by gaining XP or maintaining streaks!", color = TextGrey, fontSize = 12.sp)
                    Spacer(Modifier.height(16.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.height(250.dp)
                    ) {
                        items(avatars.size) { index ->
                            val (emoji, requirement) = avatars[index]
                            val isUnlocked = if (index < 6 || index == 10) {
                                userStats.xpPoints >= requirement
                            } else {
                                userStats.currentStreak >= requirement
                            }

                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (isUnlocked) DarkSurfaceLighter else Color.Black.copy(0.3f))
                                    .clickable(enabled = isUnlocked) {
                                        onUpdateAvatar(emoji)
                                        showAvatarDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier.alpha(if (isUnlocked) 1f else 0.3f)
                                )
                                if (!isUnlocked) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = TextGrey
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("Close", color = NeonPurple)
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = DarkSurface,
            titleContentColor = TextWhite,
            textContentColor = TextGrey,
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout from Ilm-e-Zubaan?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Logout", color = NeonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextWhite)
                }
            }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            containerColor = DarkSurface,
            titleContentColor = TextWhite,
            textContentColor = TextGrey,
            title = { Text("Clear All Data") },
            text = { Text("This will delete all your local progress, streaks, and settings. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearDataDialog = false
                    onClearData()
                }) {
                    Text("Clear Everything", color = NeonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = TextWhite)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
                title = { Text("Profile", color = TextWhite, fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, NeonPurple),
                    onClick = { showAvatarDialog = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(userStats.avatarEmoji, fontSize = 60.sp)
                    }
                }
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = NeonPurple,
                    onClick = { showAvatarDialog = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkBg)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(userStats.userName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Text("Pro Learner", fontSize = 14.sp, color = NeonCyan, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(32.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(userStats.currentStreak.toString(), "Streak", NeonOrange)
                ProfileStatItem(
                    if (userStats.xpPoints >= 1000) "${String.format(Locale.US, "%.1f", userStats.xpPoints / 1000.0)}k"
                    else userStats.xpPoints.toString(), 
                    "XP", 
                    NeonGreen
                )
                ProfileStatItem("12", "Badges", NeonPurple)
            }

            Spacer(Modifier.height(32.dp))

            // Learning Progress Chart (From reference image)
            SectionTitle("Learning Progress")
            LearningProgressChart()

            Spacer(Modifier.height(32.dp))

            // Sections
            SectionTitle("Learning")
            ProfileOptionItem("My Courses", Icons.Default.Book, NeonCyan) {}
            ProfileOptionItem("Achievements", Icons.Default.EmojiEvents, NeonOrange) {}
            
            Spacer(Modifier.height(24.dp))

            SectionTitle("Account")
            ProfileOptionItem("Notifications", Icons.Default.Notifications, NeonPurple) {}
            ProfileOptionItem("Privacy Settings", Icons.Default.Lock, TextGrey, onPrivacySettingsClick)
            
            ProfileOptionItem("Clear All Data", Icons.Default.DeleteForever, NeonOrange) {
                showClearDataDialog = true
            }

            ProfileOptionItem("Logout", Icons.AutoMirrored.Filled.Logout, NeonRed) {
                showLogoutDialog = true
            }
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun LearningProgressChart() {
    val progressData = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 0.4f)
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    
    Surface(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurface
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            progressData.forEachIndexed { index, value ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .fillMaxHeight(value)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(NeonCyan, NeonPurple)
                                ),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(days[index], color = TextGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(label, fontSize = 12.sp, color = TextGrey)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        color = TextGrey,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@Composable
fun ProfileOptionItem(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Text(title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DarkSurfaceLighter)
        }
    }
}
