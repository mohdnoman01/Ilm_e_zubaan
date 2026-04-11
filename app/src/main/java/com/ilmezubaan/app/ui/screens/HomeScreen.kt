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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    languageViewModel: LanguageViewModel,
    homeViewModel: HomeViewModel
) {
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val nativeLanguage by languageViewModel.nativeLanguage.collectAsState()
    val userStats by homeViewModel.userStats.collectAsState()
    val featuredWord by homeViewModel.featuredWord.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(onProfileClick = onProfileClick)
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
            HomeHeader(userName = userStats.userName, onProfileClick = onProfileClick)

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
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

                // Lessons Shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Mastery",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onLessonClick(selectedLanguage.name) }) {
                        Text("View All", color = NeonCyan, fontSize = 14.sp)
                    }
                }
                
                Spacer(Modifier.height(16.dp))

                NavigationGrid(
                    onLiteracyClick = onLiteracyClick,
                    onVocabularyClick = onVocabularyClick
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HomeHeader(userName: String, onProfileClick: () -> Unit) {
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
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = DarkSurface,
            onClick = onProfileClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = NeonPurple)
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
fun NavigationGrid(onLiteracyClick: () -> Unit, onVocabularyClick: () -> Unit) {
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
            onClick = {}
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
fun BottomNavigationBar(onProfileClick: () -> Unit) {
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
            BottomNavItem(Icons.Default.AutoAwesome, "AI", false)
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
