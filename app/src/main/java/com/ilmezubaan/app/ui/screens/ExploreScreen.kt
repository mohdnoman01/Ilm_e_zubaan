package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ilmezubaan.app.data.model.*
import com.ilmezubaan.app.ui.theme.*
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(
    onBack: () -> Unit,
    onWatchVideo: (title: String, url: String) -> Unit,
    viewModel: LanguageViewModel
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val exploreData = ExploreDataProvider.data[selectedLanguage.name] ?: ExploreDataProvider.data["Balochi"]!!

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("History", "Culture", "Region")

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text("Ilm e Zubaan", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Language Selection logic if needed */ }) {
                        Icon(Icons.Default.Language, contentDescription = "Languages", tint = TextWhite)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 16.dp) 
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                AsyncImage(
                    model = exploreData.heroImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, DarkBg.copy(alpha = 0.9f)),
                                startY = 300f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        text = exploreData.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = exploreData.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val introVideoUrl = exploreData.introVideoUrl
                        Button(
                            onClick = { 
                                introVideoUrl?.let {
                                    onWatchVideo("${exploreData.languageName} Intro", it)
                                }
                            },
                            enabled = introVideoUrl != null,
                            colors = ButtonDefaults.buttonColors(containerColor = AppTeal),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Watch Intro")
                        }
                        OutlinedButton(
                            onClick = { /* Share */ },
                            border = androidx.compose.foundation.BorderStroke(1.dp, TextWhite.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = TextWhite)
                            Spacer(Modifier.width(8.dp))
                            Text("Share", color = TextWhite)
                        }
                    }
                }
            }

            // Quick Stats Bar
            QuickStatsBar(exploreData)

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBg,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NeonCyan
                        )
                    }
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 14.sp) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Dynamic Content
            when (selectedTab) {
                0 -> HistoryTab(exploreData, onWatchVideo)
                1 -> CultureTab(exploreData, onWatchVideo)
                2 -> RegionTab(exploreData, onWatchVideo)
            }
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuickStatsBar(data: ExploreData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .background(DarkSurface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatItem(label = "Speakers", value = data.speakers, icon = Icons.Default.Groups)
        VerticalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.height(30.dp))
        StatItem(label = "Family", value = data.languageFamily, icon = Icons.Default.Translate)
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
        Text(text = value, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextGrey, fontSize = 10.sp)
    }
}

@Composable
fun HistoryTab(data: ExploreData, onWatchVideo: (String, String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(title = data.history.mainHeading)
            if (data.historyVideoUrl != null) {
                IconButton(onClick = { onWatchVideo("History of ${data.languageName}", data.historyVideoUrl) }) {
                    Icon(Icons.Default.PlayCircle, contentDescription = "Watch History", tint = NeonCyan)
                }
            }
        }
        Text(
            text = data.history.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey,
            lineHeight = 22.sp
        )
        
        Spacer(Modifier.height(24.dp))
        SectionHeader(title = "Key Highlights")
        data.history.keyPoints.forEach { point ->
            InfoCardItem(point, color = AppOrange)
            Spacer(Modifier.height(12.dp))
        }

        if (data.history.timeline.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            SectionHeader(title = "Historical Timeline")
            data.history.timeline.forEach { event ->
                TimelineItem(event)
            }
        }
    }
}

@Composable
fun CultureTab(data: ExploreData, onWatchVideo: (String, String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(title = data.culture.mainHeading)
            if (data.cultureVideoUrl != null) {
                IconButton(onClick = { onWatchVideo("${data.languageName} Culture", data.cultureVideoUrl) }) {
                    Icon(Icons.Default.PlayCircle, contentDescription = "Watch Culture", tint = NeonCyan)
                }
            }
        }
        Text(
            text = data.culture.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey
        )
        
        Spacer(Modifier.height(24.dp))
        
        // Large Image Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            data.culture.images.forEach { imageUrl ->
                Surface(
                    modifier = Modifier.weight(1f).height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        SectionHeader(title = "Folklore & Legends")
        FolkloreCard(data.culture.folklore)

        if (data.proverbs.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            SectionHeader(title = "Wisdom & Proverbs")
            data.proverbs.forEach { proverb ->
                ProverbCard(proverb)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegionTab(data: ExploreData, onWatchVideo: (String, String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(title = data.region.mainHeading)
            if (data.regionVideoUrl != null) {
                IconButton(onClick = { onWatchVideo("${data.languageName} Regions", data.regionVideoUrl) }) {
                    Icon(Icons.Default.PlayCircle, contentDescription = "Watch Regions", tint = NeonCyan)
                }
            }
        }
        Text(
            text = data.region.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey,
            lineHeight = 22.sp
        )
        
        Spacer(Modifier.height(24.dp))
        SectionHeader(title = "Major Hubs")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.region.majorCities.forEach { city ->
                SuggestionChip(
                    onClick = { },
                    label = { Text(city) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = DarkSurface,
                        labelColor = TextWhite
                    ),
                    border = null,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionHeader(title = "Dialects")
        data.region.dialects.forEach { dialect ->
            Text(
                text = "• $dialect",
                color = NeonCyan,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(32.dp))
        InteractiveMapPlaceholder()
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextWhite,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun TimelineItem(event: InfoCard) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(8.dp).background(NeonCyan, CircleShape))
            Box(Modifier.width(1.dp).height(40.dp).background(Color.White.copy(alpha = 0.1f)))
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.padding(bottom = 16.dp)) {
            Text(event.title, color = NeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(event.description, color = TextGrey, fontSize = 13.sp)
        }
    }
}

@Composable
fun FolkloreCard(folklore: InfoCard) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = AppTeal.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTeal.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Epic: ${folklore.title}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(folklore.description, color = TextWhite.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            Box(
                modifier = Modifier.size(40.dp).background(NeonCyan.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Headset, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ProverbCard(proverb: InfoCard) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = proverb.title,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = proverb.description,
                color = TextGrey,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun InteractiveMapPlaceholder() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Map, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text("Interactive Map", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Discover the geography and dialect variations across the region.",
                color = TextGrey, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { /* Open Map */ },
                colors = ButtonDefaults.buttonColors(containerColor = AppTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Explore, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Explore Geography")
            }
        }
    }
}

@Composable
fun InfoCardItem(point: InfoCard, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = point.title.uppercase(),
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = point.description,
                color = TextWhite.copy(alpha = 0.7f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

