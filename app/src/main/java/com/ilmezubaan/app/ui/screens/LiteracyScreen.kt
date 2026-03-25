package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.data.model.Lesson
import com.ilmezubaan.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiteracyScreen(
    language: String,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    val literacyLessons = listOf(
        Lesson("Alphabet Basics", "VIDEO", subtitle = "Learn the script characters"),
        Lesson("Reading Simple Words", "AUDIO", subtitle = "Join characters to form words"),
        Lesson("Sentence Structure", "VIDEO", subtitle = "Basic grammar rules"),
        Lesson("Advanced Reading", "AUDIO", subtitle = "Fluent reading practice")
    )

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
                title = { 
                    Column {
                        Text("Literacy", style = MaterialTheme.typography.titleLarge, color = TextWhite, fontWeight = FontWeight.Bold)
                        Text(language, style = MaterialTheme.typography.labelMedium, color = NeonPurple)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Master the art of reading and writing in $language.",
                    fontSize = 14.sp,
                    color = TextGrey,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(literacyLessons) { lesson ->
                LessonCard(lesson, onLessonClick)
            }
        }
    }
}
