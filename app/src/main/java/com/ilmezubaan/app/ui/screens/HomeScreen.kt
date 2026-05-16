package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ilmezubaan.app.data.local.entities.UserStats
import com.ilmezubaan.app.data.model.Concept
import com.ilmezubaan.app.data.model.Lesson
import com.ilmezubaan.app.ui.theme.*
import com.ilmezubaan.app.ui.viewmodel.HomeViewModel
import com.ilmezubaan.app.ui.viewmodel.Language
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel

@Composable
fun HomeScreen(
    onLanguageClick: () -> Unit,
    onLessonClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onLiteracyClick: () -> Unit,
    onVocabularyClick: () -> Unit,
    onAIClick: () -> Unit,
    onExploreClick: () -> Unit,
    languageViewModel: LanguageViewModel,
    homeViewModel: HomeViewModel
) {
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val nativeLanguage by languageViewModel.nativeLanguage.collectAsState()
    val userStats by homeViewModel.userStats.collectAsState()
    val featuredWord by homeViewModel.featuredWord.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.refreshHomeData()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onProfileClick = onProfileClick, 
                onAIClick = onAIClick,
                onExploreClick = onExploreClick
            )
        },
        containerColor = DarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            HomeHeader(
                userName = userStats.userName, 
                avatar = userStats.avatarEmoji,
                onProfileClick = onProfileClick
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Streak & Points Summary (From reference image)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryCard(
                        value = "${userStats.currentStreak}",
                        label = "Days Streak",
                        icon = Icons.Default.Whatshot,
                        color = NeonOrange,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        value = "${userStats.xpPoints}",
                        label = "Total XP",
                        icon = Icons.Default.Stars,
                        color = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Featured Lesson (Large Image Card)
                FeaturedLessonCard(
                    title = "Urdu Verbs Mastery",
                    subtitle = "Foundations of Syntax",
                    image = "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?q=80&w=400&auto=format&fit=crop",
                    progress = 0.65f,
                    onClick = { onLiteracyClick() }
                )

                Spacer(Modifier.height(28.dp))

                // Learning Status Card
                LearningStatusCard(
                    native = nativeLanguage?.name ?: "Unknown",
                    target = selectedLanguage.name,
                    onLanguageClick = onLanguageClick
                )

                Spacer(Modifier.height(28.dp))

                // Word of the Day
                FeaturedWordCard(
                    language = selectedLanguage.name,
                    featuredWord = featuredWord
                )

                Spacer(Modifier.height(28.dp))

                Text(
                    "Mastery Path",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(16.dp))

                NavigationGrid(
                    onLiteracyClick = onLiteracyClick,
                    onVocabularyClick = onVocabularyClick,
                    onAIClick = onAIClick
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SummaryCard(value: String, label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(value, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(label, color = TextGrey, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun FeaturedLessonCard(title: String, subtitle: String, image: String, progress: Float, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurface
    ) {
        Box {
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Surface(
                    color = NeonCyan.copy(0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "CONTINUE LEARNING",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = NeonCyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(title, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextWhite.copy(0.7f), fontSize = 12.sp)
                
                Spacer(Modifier.height(12.dp))
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = NeonCyan,
                    trackColor = Color.White.copy(0.1f)
                )
            }
        }
    }
}

@Composable
fun HomeHeader(userName: String, avatar: String, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Assalam-u-Alaikum,",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGrey
            )
            Text(
                userName,
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        }
        
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(2.dp, NeonPurple),
            onClick = onProfileClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(avatar, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun LearningStatusCard(native: String, target: String, onLanguageClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = DarkSurface,
        onClick = onLanguageClick
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LanguageBadge(native, NeonCyan)
                Icon(
                    Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = TextGrey,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                LanguageBadge(target, NeonPurple)
            }
            Icon(Icons.Default.Edit, contentDescription = "Change", tint = TextGrey, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun LanguageBadge(name: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1), color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(name, fontSize = 10.sp, color = TextWhite)
    }
}

@Composable
fun FeaturedWordCard(language: String, featuredWord: Concept?) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(32.dp),
        color = DarkSurfaceLighter
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative background element
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(150.dp)
                    .graphicsLayer {
                        translationX = -50f
                        translationY = 50f
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(0.15f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = NeonPurple.copy(0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "WORD OF THE DAY",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = NeonPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = language,
                        color = TextGrey,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(Modifier.weight(1f))
                
                if (featuredWord != null) {
                    val langKey = featuredWord.languages.keys.find { it.equals(language, ignoreCase = true) }
                    val langData = langKey?.let { featuredWord.languages[it] }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = langData?.script ?: "...",
                            fontSize = 42.sp,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 48.sp
                        )
                        
                        Spacer(Modifier.height(4.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (!langData?.roman.isNullOrEmpty()) {
                                Text(
                                    text = langData?.roman ?: "",
                                    fontSize = 16.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = " • ",
                                    fontSize = 16.sp,
                                    color = TextGrey.copy(0.5f)
                                )
                            }
                            Text(
                                text = featuredWord.context ?: featuredWord.englishMeaning,
                                fontSize = 16.sp,
                                color = TextWhite.copy(0.7f),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                } else {
                    // Loading placeholder
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            "Loading word...",
                            fontSize = 24.sp,
                            color = TextGrey.copy(0.3f),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier
                                .size(120.dp, 16.dp)
                                .background(Color.White.copy(0.05f), RoundedCornerShape(4.dp))
                        )
                    }
                }
                
                Spacer(Modifier.weight(1.2f))
            }
        }
    }
}

@Composable
fun NavigationGrid(onLiteracyClick: () -> Unit, onVocabularyClick: () -> Unit, onAIClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NavCard(
            title = "Vocabulary",
            subtitle = "Visual word learning",
            icon = Icons.Default.Translate,
            color = NeonCyan,
            onClick = onVocabularyClick
        )
        NavCard(
            title = "Literacy",
            subtitle = "Script & Grammar",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            color = NeonPurple,
            onClick = onLiteracyClick
        )
        NavCard(
            title = "AI Tutor",
            subtitle = "Practice speaking",
            icon = Icons.Default.AutoAwesome,
            color = NeonGreen,
            onClick = onAIClick
        )
    }
}

@Composable
fun NavCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(color.copy(0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextGrey, fontSize = 13.sp)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = DarkSurfaceLighter, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun BottomNavigationBar(onProfileClick: () -> Unit, onAIClick: () -> Unit, onExploreClick: () -> Unit) {
    Surface(
        color = DarkBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 24.dp, top = 12.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(24.dp))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, "Home", true)
            BottomNavItem(Icons.Default.Explore, "Explore", false, onClick = onExploreClick)
            BottomNavItem(Icons.Default.AutoAwesome, "AI", false, onClick = onAIClick)
            BottomNavItem(Icons.Default.Person, "Profile", false, onClick = onProfileClick)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isSelected) NeonCyan else TextGrey.copy(0.5f),
            modifier = Modifier.size(26.dp)
        )
    }
}
