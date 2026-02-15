package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.ui.theme.*
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel
import com.ilmezubaan.app.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onLanguageClick: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    onProfileClick: () -> Unit,
    languageViewModel: LanguageViewModel,
    homeViewModel: HomeViewModel
) {
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val userStats by homeViewModel.userStats.collectAsState()
    val greeting = homeViewModel.getGreeting()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Area with Dynamic Greeting and Stats
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$greeting, ${userStats.userName}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Ready to improve your ${selectedLanguage.name} today?",
                            fontSize = 16.sp,
                            color = TextGrey
                        )
                    }
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AppTealLight.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = AppTeal)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Streak and XP Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatBadge(icon = Icons.Default.Whatshot, text = "${userStats.currentStreak} Day Streak", color = Color(0xFFFF5722))
                    StatBadge(icon = Icons.Default.Star, text = "${userStats.xpPoints} XP", color = Color(0xFFFFC107))
                }
            }
        }

        // Main Content Card
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                
                // 1. Continue Learning Section
                if (userStats.lastLessonTitle != null) {
                    Text("Continue Learning", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.height(12.dp))
                    ContinueLearningCard(
                        title = userStats.lastLessonTitle!!,
                        progress = userStats.lastLessonProgress,
                        type = userStats.lastLessonType ?: "AUDIO",
                        onResume = { 
                            onLessonClick(Lesson(userStats.lastLessonTitle!!, userStats.lastLessonType ?: "AUDIO"))
                        }
                    )
                    Spacer(Modifier.height(32.dp))
                }

                Text("Current Language", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(Modifier.height(12.dp))
                LanguageSurface(selectedLanguage.name, selectedLanguage.nativeName, onLanguageClick)

                Spacer(Modifier.height(32.dp))

                Text("Categories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryItem("Literacy", Icons.Default.MenuBook, AppTeal, AppTealLight)
                    CategoryItem("Work", Icons.Default.Construction, AppOrange, AppOrangeLight)
                    CategoryItem("Life Skills", Icons.Default.VolunteerActivism, AppPeach, AppPeachLight)
                }

                Spacer(Modifier.height(32.dp))

                Text("Featured Lessons", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(Modifier.height(12.dp))
                LessonCard(Lesson("Basic ${selectedLanguage.name} Reading", "AUDIO"), onLessonClick)
                Spacer(Modifier.height(16.dp))
                LessonCard(Lesson("Everyday Greetings", "VIDEO"), onLessonClick)
            }
        }
    }
}

@Composable
fun StatBadge(icon: ImageVector, text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun ContinueLearningCard(title: String, progress: Float, type: String, onResume: () -> Unit) {
    Surface(
        onClick = onResume,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = AppTeal,
                    trackColor = Color.LightGray
                )
                Spacer(Modifier.height(4.dp))
                Text("${(progress * 100).toInt()}% complete", fontSize = 12.sp, color = TextGrey)
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = onResume, shape = RoundedCornerShape(12.dp)) {
                Text("Resume")
            }
        }
    }
}

@Composable
fun LanguageSurface(name: String, nativeName: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder(),
        color = AppTealLight.copy(alpha = 0.2f)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = null, tint = AppTeal)
            Spacer(Modifier.width(12.dp))
            Text("$name ($nativeName)", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text("Change", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppTeal)
        }
    }
}

@Composable
fun CategoryItem(title: String, icon: ImageVector, color: Color, bgColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(20.dp),
            color = bgColor,
            onClick = { /* Handle Category click if needed */ }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = title, 
            fontSize = 15.sp, 
            fontWeight = FontWeight.Medium,
            color = TextDark,
            maxLines = 1
        )
    }
}

@Composable
fun LessonCard(lesson: Lesson, onClick: (Lesson) -> Unit) {
    Surface(
        onClick = { onClick(lesson) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 88.dp),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder(),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (lesson.type == "VIDEO") AppOrangeLight else AppTealLight
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (lesson.type == "VIDEO") Icons.Default.PlayArrow else Icons.Default.Headset,
                        contentDescription = null,
                        tint = if (lesson.type == "VIDEO") AppOrange else AppTeal,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title, 
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, 
                    color = TextDark
                )
                Text(
                    text = if (lesson.type == "VIDEO") "Watch Video" else "Listen Audio",
                    fontSize = 15.sp,
                    color = TextGrey,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = TextGrey.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
