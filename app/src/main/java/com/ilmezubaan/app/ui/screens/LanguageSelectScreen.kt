package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilmezubaan.app.ui.theme.*
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel
import com.ilmezubaan.app.ui.viewmodel.Language

@Composable
fun LanguageSelectScreen(
    title: String = "Choose Your Language",
    subtitle: String = "Select the language you want to learn in",
    onLanguageChosen: (String) -> Unit,
    onSelect: (Language) -> Unit,
    viewModel: LanguageViewModel = viewModel()
) {
    val languages = viewModel.languages

    Scaffold(
        containerColor = DarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = title,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite
            )
            
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = TextGrey,
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(languages) { language ->
                    LanguageSelectionItem(
                        language = language,
                        onClick = {
                            onSelect(language)
                            onLanguageChosen(language.name)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionItem(
    language: Language,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = DarkSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NeonPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = language.nativeName.take(1),
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = language.nativeName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = language.name,
                    fontSize = 13.sp,
                    color = TextGrey
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = DarkSurfaceLighter,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
