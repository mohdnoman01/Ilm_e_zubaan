package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
                            type = langData.type,
                            conceptId = concept.conceptId,
                            subtitle = concept.englishMeaning,
                            audioUrl = langData.mediaUrl ?: langData.audioUrl
                        ),
                        onClick = onLessonClick
                    )
                }
            }
        }
    }
}
