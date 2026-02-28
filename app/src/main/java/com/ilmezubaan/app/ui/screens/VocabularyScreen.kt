package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    language: String,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    val vocabularyLessons = listOf(
        Lesson("Common Greetings", "AUDIO"),
        Lesson("Numbers and Counting", "VIDEO"),
        Lesson("Food and Drinks", "AUDIO"),
        Lesson("Travel Essentials", "VIDEO"),
        Lesson("Daily Objects", "AUDIO")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$language Vocabulary") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Expand your $language word bank with daily use vocabulary.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(vocabularyLessons) { lesson ->
                LessonCard(lesson, onLessonClick)
            }
        }
    }
}
