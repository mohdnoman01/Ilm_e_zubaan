package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.ui.theme.*
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel

data class Lesson(
    val title: String,
    val type: String,
    val conceptId: String? = null
)

@Composable
fun LessonListScreen(
    language: String,
    onLessonClick: (Lesson) -> Unit,
    conceptViewModel: ConceptViewModel
) {
    val concepts by conceptViewModel.concepts.collectAsState()
    
    // Filter concepts by level/difficulty to create "Lessons"
    val beginnerConcepts = concepts.filter { it.difficultyLevel == "1" || it.category == "Basic" }
    val intermediateConcepts = concepts.filter { it.category == "Intermediate" }
    val advancedConcepts = concepts.filter { it.category == "Advanced" }

    val lessons = listOf(
        Lesson("Beginner: Core Vocabulary", "AUDIO"),
        Lesson("Intermediate: Daily Phrases", "VIDEO"),
        Lesson("Advanced: Complex Concepts", "AUDIO")
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "$language Learning",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                Text(
                    text = "Concepts synced: ${concepts.size}",
                    fontSize = 14.sp,
                    color = AppTeal,
                    fontWeight = FontWeight.Bold
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
            Text("Learning Tracks", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(16.dp))

            lessons.forEach { lesson ->
                LessonCard(lesson, onLessonClick)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
