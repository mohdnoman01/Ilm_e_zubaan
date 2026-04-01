package com.ilmezubaan.app.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilmezubaan.app.ui.theme.AppTeal
import com.ilmezubaan.app.ui.theme.TextDark
import com.ilmezubaan.app.ui.theme.TextGrey
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
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    
    // Find the lesson in recent or passed data (In a real app, you'd fetch by ID)
    // For now, we use the title passed in Nav
    
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lessonTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = lessonTitle,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTeal
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Pronunciation Guide",
                        fontSize = 16.sp,
                        color = TextGrey
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // Audio Controls
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(24.dp),
                color = AppTeal,
                onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        // In a real scenario, get URL from your Lesson object
                        // For testing, we can use a dummy or the one from Firebase if available
                        // val url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                        
                        // if (mediaPlayer == null) {
                        //    mediaPlayer = MediaPlayer().apply {
                        //        setAudioAttributes(AudioAttributes.Builder()
                        //            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        //            .build())
                        //        setDataSource(url)
                        //        prepare()
                        //    }
                        // }
                        // mediaPlayer?.start()
                        // isPlaying = true
                    }
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isPlaying) "Playing Audio..." else "Tap to Listen",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
        }
    }
}
