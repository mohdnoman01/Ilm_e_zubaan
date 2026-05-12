package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.data.remote.gemini.WordInsight
import com.ilmezubaan.app.ui.theme.DarkBg
import com.ilmezubaan.app.ui.theme.DarkSurface
import com.ilmezubaan.app.ui.theme.DarkSurfaceLighter
import com.ilmezubaan.app.ui.theme.NeonCyan
import com.ilmezubaan.app.ui.theme.NeonPurple
import com.ilmezubaan.app.ui.theme.TextGrey
import com.ilmezubaan.app.ui.theme.TextWhite
import com.ilmezubaan.app.ui.viewmodel.WordInsightViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIScreen(
    onBack: () -> Unit,
    learningLanguage: String,
    nativeLanguage: String,
    viewModel: WordInsightViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var wordInput by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = DarkBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Word Helper",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$nativeLanguage support for $learningLanguage",
                            color = TextGrey,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AIWordLookupSection(
                wordInput = wordInput,
                onWordInputChange = { wordInput = it },
                isLoading = uiState.isLoading,
            ) {
                viewModel.loadWordInsight(
                    word = wordInput,
                    learningLanguage = learningLanguage,
                    nativeLanguage = nativeLanguage
                )
            }

            if (uiState.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkSurface,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = NeonCyan)
                        Text(
                            text = "Getting a short, simple explanation...",
                            color = TextGrey
                        )
                    }
                }
            }

            uiState.insight?.let { insight ->
                WordInsightCard(
                    selectedWord = uiState.selectedWord,
                    insight = insight
                )
            }
        }
    }
}

@Composable
fun AIWordLookupSection(
    wordInput: String,
    onWordInputChange: (String) -> Unit,
    isLoading: Boolean,
    onLookupClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Enter a word",
                color = TextWhite,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "The answer stays short and easy for beginners.",
                color = TextGrey
            )
            OutlinedTextField(
                value = wordInput,
                onValueChange = onWordInputChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text("Example: kitab", color = TextGrey)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceLighter,
                    unfocusedContainerColor = DarkSurfaceLighter,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedIndicatorColor = NeonCyan,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = NeonCyan
                ),
                shape = RoundedCornerShape(18.dp)
            )
            Button(
                onClick = onLookupClick,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Explain Word", color = TextWhite)
            }
        }
    }
}

@Composable
fun WordInsightCard(
    selectedWord: String,
    insight: WordInsight
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = selectedWord,
                color = NeonCyan,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            InsightItem(label = "Meaning", value = insight.meaning)
            InsightItem(label = "Urdu Meaning", value = insight.urduMeaning)
            InsightItem(label = "Pronunciation", value = insight.pronunciation)
            InsightItem(label = "Example Sentence", value = insight.exampleSentence)
        }
    }
}

@Composable
private fun InsightItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = NeonPurple,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = value,
            color = TextWhite,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Spacer(modifier = Modifier.height(2.dp))
}
