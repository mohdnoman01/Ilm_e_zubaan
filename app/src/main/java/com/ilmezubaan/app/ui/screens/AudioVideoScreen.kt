package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SAFE PLAYER UI
 * - No icons
 * - No media libs
 * - Pure Compose
 * - Exam-friendly
 */
@Composable
fun AudioVideoScreen(
    lessonTitle: String,
    lessonType: String,
    onBack: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.15f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        // Back
        TextButton(onClick = onBack) {
            Text("← Back", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(12.dp))

        // Title
        Text(
            text = lessonTitle,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (lessonType == "VIDEO") "Video Lesson" else "Audio Lesson",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Player Placeholder
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPlaying) "Playing…" else "Paused",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Progress
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(Modifier.height(12.dp))

        // Controls (TEXT ONLY)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            OutlinedButton(onClick = {
                progress = (progress - 0.05f).coerceAtLeast(0f)
            }) {
                Text("<< 10s")
            }

            Button(onClick = { isPlaying = !isPlaying }) {
                Text(if (isPlaying) "Pause" else "Play")
            }

            OutlinedButton(onClick = {
                progress = (progress + 0.05f).coerceAtMost(1f)
            }) {
                Text("10s >>")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Info
        Text(
            text = "Note: This is a UI placeholder.\nActual media playback will be added later.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
