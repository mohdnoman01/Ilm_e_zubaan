package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ilmezubaan.app.ui.theme.AppTeal
import com.ilmezubaan.app.ui.theme.TextDark
import com.ilmezubaan.app.ui.theme.TextGrey
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel
import timber.log.Timber

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.common.util.UnstableApi

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioVideoScreen(
    lessonTitle: String,
    lessonType: String,
    onBack: () -> Unit,
    conceptViewModel: ConceptViewModel,
    language: String,
    audioUrl: String? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isPlaying by remember { mutableStateOf(false) }
    var isFullScreen by remember { mutableStateOf(false) }
    var playerErrorMessage by remember(audioUrl) { mutableStateOf<String?>(null) }
    val mediaUri = remember(audioUrl, context) {
        audioUrl?.toPlayableMediaUri(context)
    }
    
    val exoPlayer = remember(mediaUri, lessonType) {
        ExoPlayer.Builder(context).build().apply {
            addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    Timber.e(error, "ExoPlayer Error: ${error.message} - URL: $audioUrl")
                    playerErrorMessage = "Unable to play this media"
                    isPlaying = false
                }
                
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        isPlaying = false
                    }
                }
                
                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    isPlaying = isPlayingNow
                }
            })
            
            if (mediaUri != null) {
                val mediaItem = MediaItem.Builder()
                    .setUri(mediaUri)
                    .setMimeType(if (lessonType == "VIDEO") MimeTypes.VIDEO_MP4 else MimeTypes.AUDIO_MPEG)
                    .build()
                setMediaItem(mediaItem)
                prepare()
            } else if (!audioUrl.isNullOrBlank()) {
                playerErrorMessage = "Media resource not found"
            }
        }
    }

    if (isFullScreen && lessonType == "VIDEO") {
        BackHandler { isFullScreen = false }
        Dialog(
            onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = true
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { /* Future: Video Settings */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.8f))
                        }
                        IconButton(
                            onClick = { isFullScreen = false },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Fullscreen", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
    
    DisposableEffect(exoPlayer, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                    isPlaying = false
                }
                Lifecycle.Event.ON_STOP -> {
                    exoPlayer.stop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    Scaffold(
        containerColor = com.ilmezubaan.app.ui.theme.DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = com.ilmezubaan.app.ui.theme.DarkBg),
                title = { Text(lessonTitle, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(com.ilmezubaan.app.ui.theme.DarkBg)
        ) {
            // Video Section
            if (lessonType == "VIDEO") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                ) {
                    if (mediaUri != null) {
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    player = exoPlayer
                                    useController = true
                                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { /* Future: Video Settings */ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            IconButton(
                                onClick = { isFullScreen = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Fullscreen",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(playerErrorMessage ?: "Video Not Available", color = Color.Gray)
                        }
                    }
                }
            } else {
                // Audio Section Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(com.ilmezubaan.app.ui.theme.AppTeal.copy(0.2f), com.ilmezubaan.app.ui.theme.DarkBg)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = com.ilmezubaan.app.ui.theme.AppTeal.copy(0.1f),
                        border = BorderStroke(2.dp, com.ilmezubaan.app.ui.theme.AppTeal)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = com.ilmezubaan.app.ui.theme.NeonCyan,
                                modifier = Modifier.size(64.dp).clickable {
                                    if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                                }
                            )
                        }
                    }
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = lessonTitle,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(Modifier.height(8.dp))
                
                Surface(
                    color = com.ilmezubaan.app.ui.theme.NeonCyan.copy(0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (lessonType == "VIDEO") "VIDEO LESSON" else "AUDIO GUIDE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = com.ilmezubaan.app.ui.theme.NeonCyan,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(24.dp))
                
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(0.7f)
                )
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "Learn more about $language $lessonTitle through this interactive media session.",
                    fontSize = 15.sp,
                    color = com.ilmezubaan.app.ui.theme.TextGrey,
                    lineHeight = 22.sp
                )
                
                Spacer(Modifier.weight(1f))
                
                if (lessonType == "AUDIO") {
                    // Modern Audio Progress/Controls (Simplified for now)
                    Text(
                        text = if (isPlaying) "Playing Audio..." else "Tap play to start",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = com.ilmezubaan.app.ui.theme.NeonCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@UnstableApi
private fun String.toPlayableMediaUri(context: android.content.Context): android.net.Uri? {
    val value = trim()
    if (value.isBlank()) return null

    return when {
        value.startsWith("http://", ignoreCase = true) ||
            value.startsWith("https://", ignoreCase = true) ||
            value.startsWith("android.resource://", ignoreCase = true) ||
            value.startsWith("asset://", ignoreCase = true) ||
            value.startsWith("file://", ignoreCase = true) -> android.net.Uri.parse(value)
        else -> {
            // Treat as raw resource name
            val resourceName = value.substringBeforeLast('.')
            val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resId != 0) {
                android.net.Uri.parse("rawresource:///$resId")
            } else {
                // Last resort attempt
                if (value.matches(Regex("[a-z0-9_.]+"))) {
                    android.net.Uri.parse("android.resource://${context.packageName}/raw/$resourceName")
                } else null
            }
        }
    }
}
