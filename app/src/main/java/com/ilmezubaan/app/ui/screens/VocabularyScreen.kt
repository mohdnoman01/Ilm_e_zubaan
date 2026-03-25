package com.ilmezubaan.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.data.model.Lesson
import com.ilmezubaan.app.ui.theme.DarkBg
import com.ilmezubaan.app.ui.theme.DarkSurface
import com.ilmezubaan.app.ui.theme.DarkSurfaceLighter
import com.ilmezubaan.app.ui.theme.NeonCyan
import com.ilmezubaan.app.ui.theme.NeonOrange
import com.ilmezubaan.app.ui.theme.NeonPurple
import com.ilmezubaan.app.ui.theme.TextGrey
import com.ilmezubaan.app.ui.theme.TextWhite
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    language: String,
    nativeLanguage: String,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    conceptViewModel: ConceptViewModel
) {
    val concepts by conceptViewModel.concepts.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }

    // Case-insensitive filtering and handling both current structure and nested structure
    val displayConcepts = concepts.filter { concept ->
        concept.languages.keys.any { it.equals(language, ignoreCase = true) }
    }
    
    val categories = listOf("All") + displayConcepts.map { it.category }.distinct()
    
    val filteredConcepts = if (selectedCategory == "All") {
        displayConcepts
    } else {
        displayConcepts.filter { it.category == selectedCategory }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
                title = { 
                    Column {
                        Text("Vocabulary", style = MaterialTheme.typography.titleLarge, color = TextWhite)
                        Text("$nativeLanguage to $language", style = MaterialTheme.typography.labelMedium, color = TextGrey)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { conceptViewModel.syncData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = NeonCyan)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .background(DarkSurfaceLighter, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(displayConcepts.size.toString(), color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar()
            CategoryRow(categories, selectedCategory) { selectedCategory = it }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (filteredConcepts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No words found for $language.", color = TextGrey)
                        }
                    }
                }

                items(filteredConcepts) { concept ->
                    // Find language data case-insensitively
                    val learnLangKey = concept.languages.keys.find { it.equals(language, ignoreCase = true) }
                    val nativeLangKey = concept.languages.keys.find { it.equals(nativeLanguage, ignoreCase = true) }
                    
                    val learnLangData = learnLangKey?.let { concept.languages[it] }
                    val nativeLangData = nativeLangKey?.let { concept.languages[it] }
                    
                    if (learnLangData != null) {
                        VocabularyFlashcard(
                            nativeText = learnLangData.script,
                            explanationText = nativeLangData?.script ?: concept.englishMeaning,
                            englishText = concept.englishMeaning,
                            romanText = learnLangData.roman,
                            category = concept.category,
                            example = learnLangData.example,
                            exampleMeaning = learnLangData.exampleMeaning,
                            onPlayAudio = { 
                                onLessonClick(
                                    Lesson(
                                        title = learnLangData.script,
                                        type = "AUDIO",
                                        subtitle = nativeLangData?.script ?: concept.englishMeaning,
                                        audioUrl = learnLangData.audioUrl
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VocabularyFlashcard(
    nativeText: String,
    explanationText: String,
    englishText: String,
    romanText: String,
    category: String,
    example: String?,
    exampleMeaning: String?,
    onPlayAudio: () -> Unit
) {
    var flipState by remember { mutableIntStateOf(0) } // 0: Word, 1: Meaning, 2: Example
    val rotation by animateFloatAsState(
        targetValue = flipState * 180f,
        animationSpec = tween(durationMillis = 600), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { flipState = (flipState + 1) % 3 }
    ) {
        val isBack = (rotation % 360f) in 90f..270f
        
        Box(Modifier.graphicsLayer { if (isBack) rotationY = 180f }) {
            when {
                (rotation % 540f) <= 90f -> {
                    FlashcardFace(
                        category = category,
                        color = DarkSurface,
                        dotsIndex = 0,
                        onPlay = onPlayAudio,
                        content = {
                            Text(nativeText, fontSize = 42.sp, fontWeight = FontWeight.Bold, color = TextWhite, textAlign = TextAlign.Center)
                            if (romanText.isNotEmpty()) Text(romanText, color = NeonCyan, fontSize = 16.sp)
                        }
                    )
                }
                (rotation % 540f) <= 270f -> {
                    FlashcardFace(
                        category = category,
                        color = DarkSurfaceLighter,
                        dotsIndex = 1,
                        borderColor = NeonPurple.copy(0.4f),
                        onPlay = onPlayAudio,
                        content = {
                            Text(explanationText, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextWhite, textAlign = TextAlign.Center)
                            Text("($englishText)", fontSize = 14.sp, color = TextGrey)
                        }
                    )
                }
                else -> {
                    FlashcardFace(
                        category = "REAL LIFE",
                        color = DarkSurface,
                        dotsIndex = 2,
                        borderColor = NeonOrange.copy(0.4f),
                        onPlay = onPlayAudio,
                        content = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = NeonOrange, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("USE IT IN REAL LIFE", color = NeonOrange, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = example ?: "Example coming soon...",
                                fontSize = 18.sp, 
                                color = TextWhite, 
                                textAlign = TextAlign.Center
                            )
                            if (!exampleMeaning.isNullOrEmpty()) {
                                Text(
                                    text = exampleMeaning,
                                    fontSize = 14.sp,
                                    color = TextGrey,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FlashcardFace(
    category: String,
    color: Color,
    dotsIndex: Int,
    borderColor: Color = Color.Transparent,
    onPlay: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(32.dp),
        color = color,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(shape = RoundedCornerShape(8.dp), color = NeonCyan.copy(0.1f)) {
                    Text(category.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp, 
                    contentDescription = "Play Audio", 
                    tint = TextGrey, 
                    modifier = Modifier.size(18.dp).clickable { onPlay() }
                )
            }

            Spacer(Modifier.weight(1f))
            content()
            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == dotsIndex) 14.dp else 6.dp, 4.dp)
                            .background(if (i == dotsIndex) NeonPurple else TextGrey.copy(0.2f), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextGrey, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text("Search words, meanings...", color = TextGrey, fontSize = 14.sp)
        }
    }
}

@Composable
fun CategoryRow(categories: List<String>, selected: String, onSelect: (String) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val isSelected = category == selected
            Surface(
                onClick = { onSelect(category) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) NeonPurple.copy(0.2f) else DarkSurface,
                border = if (isSelected) BorderStroke(1.dp, NeonPurple) else null
            ) {
                Text(text = category, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = if (isSelected) NeonPurple else TextGrey, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}
