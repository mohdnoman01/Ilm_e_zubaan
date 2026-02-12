package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel

@Composable
fun LanguageSelectScreen(
    onLanguageChosen: (String) -> Unit,
    viewModel: LanguageViewModel = viewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Language")
        Spacer(Modifier.height(16.dp))

        Button(onClick = { 
            viewModel.selectLanguage("Punjabi")
            onLanguageChosen("Punjabi") 
        }) {
            Text("Punjabi")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { 
            viewModel.selectLanguage("Sindhi")
            onLanguageChosen("Sindhi") 
        }) {
            Text("Sindhi")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { 
            viewModel.selectLanguage("Pashto")
            onLanguageChosen("Pashto") 
        }) {
            Text("Pashto")
        }
    }
}
