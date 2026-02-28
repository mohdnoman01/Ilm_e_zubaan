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
fun LiteracyScreen(
    language: String,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    val literacyLessons = listOf(
        Lesson("Alphabet Basics", "VIDEO"),
        Lesson("Reading Simple Words", "AUDIO"),
        Lesson("Sentence Structure", "VIDEO"),
        Lesson("Advanced Reading", "AUDIO")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$language Literacy") },
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
                    text = "Master the art of reading and writing in $language.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(literacyLessons) { lesson ->
                LessonCard(lesson, onLessonClick)
            }
        }
    }
}
