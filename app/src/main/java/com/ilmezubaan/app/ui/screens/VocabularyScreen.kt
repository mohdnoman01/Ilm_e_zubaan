package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.data.util.DataImporter
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    language: String,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    conceptViewModel: ConceptViewModel
) {
    val context = LocalContext.current
    val sourceLanguage by conceptViewModel.sourceLanguage.collectAsState()
    val targetLanguage by conceptViewModel.targetLanguage.collectAsState()
    val concepts by conceptViewModel.concepts.collectAsState()

    // Filter concepts that have the current language
    val displayConcepts = concepts.filter { 
        it.languages.containsKey(language.lowercase()) 
    }

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
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (language == "Punjabi") {
                    ExtendedFloatingActionButton(
                        onClick = { DataImporter.importPunjabiData(context, conceptViewModel) },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Import Punjabi") }
                    )
                }
            }
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
                    text = "Concepts in DB: ${displayConcepts.size}",
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            items(displayConcepts) { concept ->
                val langData = concept.languages[language.lowercase()]
                if (langData != null) {
                    LessonCard(
                        lesson = Lesson(langData.script, "AUDIO"),
                        onClick = onLessonClick
                    )
                }
            }
        }
    }
}
