package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.ui.theme.*

data class Lesson(
    val title: String,
    val type: String // "AUDIO" or "VIDEO"
)

@Composable
fun LessonListScreen(
    language: String,
    onLessonClick: (Lesson) -> Unit
) {
    val pashtoLessons = listOf(
        Lesson("Basic Greetings", "AUDIO"),
        Lesson("Self Introduction", "VIDEO"),
        Lesson("Common Questions", "AUDIO"),
        Lesson("Numbers 1-10", "VIDEO"),
        Lesson("Family Members", "AUDIO")
    )

    val punjabiLessons = listOf(
        Lesson("Basic Greetings", "AUDIO"),
        Lesson("Family Relations", "VIDEO"),
        Lesson("Common Phrases", "AUDIO"),
        Lesson("Numbers 1-10", "VIDEO"),
        Lesson("Colors in Punjabi", "AUDIO")
    )

    val sindhiLessons = listOf(
        Lesson("Basic Greetings", "AUDIO"),
        Lesson("Daily Phrases", "VIDEO"),
        Lesson("Numbers 1-10", "AUDIO"),
        Lesson("Market Talk", "VIDEO"),
        Lesson("Time & Days", "AUDIO")
    )

    val lessons = when (language.lowercase()) {
        "pashto" -> pashtoLessons
        "punjabi" -> punjabiLessons
        "sindhi" -> sindhiLessons
        else -> listOf(
            Lesson("Basic Reading", "AUDIO"),
            Lesson("Daily Conversation", "VIDEO"),
            Lesson("Health & Hygiene", "AUDIO"),
            Lesson("Community Rules", "VIDEO"),
            Lesson("Basic Math", "AUDIO")
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Lessons in $language",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                Text(
                    text = "Pick a lesson to start learning",
                    fontSize = 16.sp,
                    color = TextGrey,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            lessons.forEach { lesson ->
                LessonCard(lesson, onLessonClick)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
