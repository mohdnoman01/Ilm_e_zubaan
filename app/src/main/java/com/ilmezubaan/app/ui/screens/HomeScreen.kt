package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.ui.theme.*

@Composable
fun HomeScreen(
    onLanguageClick: () -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Area - Increased emphasis and larger text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            Column {
                Text(
                    text = "Ilm-e-Zaban",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Learn in your own voice,\ngrow in your own world",
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    color = TextGrey,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Main Content Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Current Language",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                
                Spacer(Modifier.height(16.dp))

                // Language Selector - Larger touch target and clearer icon
                Surface(
                    onClick = onLanguageClick,
                    modifier = Modifier.fillMaxWidth().height(72.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    color = AppTealLight.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = AppTeal,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "Punjabi (ਪੰਜਾਬੀ / پنجابی)", 
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                        }
                        Text("Change", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppTeal)
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Category Icons - Larger and more descriptive
                Text(
                    text = "What do you want to learn?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryItem("Literacy", Icons.Default.MenuBook, AppTeal, AppTealLight)
                    CategoryItem("Work", Icons.Default.Construction, AppOrange, AppOrangeLight)
                    CategoryItem("Life Skills", Icons.Default.VolunteerActivism, AppPeach, AppPeachLight)
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Featured Lessons",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(Modifier.height(16.dp))

                // Lesson List - Enhanced for accessibility
                LessonCard(Lesson("Basic Urdu Reading", "AUDIO"), onLessonClick)
                Spacer(Modifier.height(16.dp))
                LessonCard(Lesson("Introduction Tailoring", "VIDEO"), onLessonClick)
            }
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
