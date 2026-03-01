package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.ui.theme.*
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioVideoScreen(
    lessonTitle: String,
    lessonType: String,
    onBack: () -> Unit,
    conceptViewModel: ConceptViewModel,
    language: String
) {
    val concepts by conceptViewModel.concepts.collectAsState()
    val sourceLanguage by conceptViewModel.sourceLanguage.collectAsState()
    
    // For this refactor, we show "Intro" content if it's the first lesson
    // or the "History" content based on the selected language.
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lessonTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // 1. Language Intro Video Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        "Intro: History of $language",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // 2. Cultural Context
            Text("Cultural Context", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(12.dp))
            
            InfoSection(
                icon = Icons.Default.History,
                title = "History & Region",
                description = "Learn about the origins of $language in the South Asian region and the vibrant lifestyle of its native speakers."
            )
            
            Spacer(Modifier.height(16.dp))

            InfoSection(
                icon = Icons.Default.Language,
                title = "Real-life Examples",
                description = "We use common phrases and words that you will hear in daily conversations, making learning practical and fast."
            )

            Spacer(Modifier.height(32.dp))

            // 3. Start Learning Button
            Button(
                onClick = { /* Start the actual lesson content playback */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTeal)
            ) {
                Text("Start Lesson", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoSection(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = AppTealLight.copy(alpha = 0.3f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = AppTeal, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(description, fontSize = 14.sp, color = TextGrey)
        }
    }
}
