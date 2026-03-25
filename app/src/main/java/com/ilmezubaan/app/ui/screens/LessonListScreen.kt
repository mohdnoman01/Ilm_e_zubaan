package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ilmezubaan.app.data.model.Lesson
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    language: String,
    onLessonClick: (Lesson) -> Unit,
    conceptViewModel: ConceptViewModel
) {
    val concepts by conceptViewModel.concepts.collectAsState()
    
    // Filter concepts for the current language
    val languageConcepts = concepts.filter { 
        it.languages.containsKey(language.lowercase()) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$language Lessons") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
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
            items(languageConcepts) { concept ->
                val langData = concept.languages[language.lowercase()]
                if (langData != null) {
                    LessonCard(
                        lesson = Lesson(
                            title = langData.script,
                            type = "AUDIO",
                            conceptId = concept.conceptId,
                            subtitle = concept.englishMeaning,
                            audioUrl = langData.audioUrl
                        ),
                        onClick = onLessonClick
                    )
                }
            }
        }
    }
}
