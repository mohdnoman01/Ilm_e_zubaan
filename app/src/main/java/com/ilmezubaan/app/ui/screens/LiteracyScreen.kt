package com.ilmezubaan.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.ActivityNotFoundException
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.ilmezubaan.app.data.model.Lesson
import com.ilmezubaan.app.speech.SpeechText
import com.ilmezubaan.app.speech.TtsLocaleRegistry
import com.ilmezubaan.app.speech.TtsManager
import com.ilmezubaan.app.ui.theme.*

import androidx.compose.foundation.layout.FlowRow

data class AlphabetItem(val char: String, val roman: String)
data class SentenceItem(val native: String, val translation: String)

val languageAlphabets = mapOf(
    "Urdu" to listOf(
        AlphabetItem("ا", "Alif"), AlphabetItem("ب", "Be"), AlphabetItem("پ", "Pe"), AlphabetItem("ت", "Te"),
        AlphabetItem("ٹ", "Tte"), AlphabetItem("ث", "Se"), AlphabetItem("ج", "Jeem"), AlphabetItem("چ", "Che"),
        AlphabetItem("ح", "He"), AlphabetItem("خ", "Khe"), AlphabetItem("د", "Dal"), AlphabetItem("ڈ", "Dal"),
        AlphabetItem("ذ", "Zal"), AlphabetItem("ر", "Re"), AlphabetItem("ڑ", "Re"), AlphabetItem("ز", "Ze"),
        AlphabetItem("ژ", "Zhe"), AlphabetItem("س", "Seen"), AlphabetItem("ش", "Sheen"), AlphabetItem("ص", "Suad"),
        AlphabetItem("ض", "Zuad"), AlphabetItem("ط", "Toe"), AlphabetItem("ظ", "Zoe"), AlphabetItem("ع", "Ain"),
        AlphabetItem("غ", "Ghain"), AlphabetItem("ف", "Fe"), AlphabetItem("ق", "Qaf"), AlphabetItem("ک", "Kaf"),
        AlphabetItem("گ", "Gaf"), AlphabetItem("ل", "Lam"), AlphabetItem("م", "Meem"), AlphabetItem("ن", "Noon"),
        AlphabetItem("و", "Vao"), AlphabetItem("ہ", "He"), AlphabetItem("ی", "Ye"), AlphabetItem("ے", "Ye")
    ),
    "Punjabi" to listOf(
        AlphabetItem("ا", "Alif"), AlphabetItem("ب", "Be"), AlphabetItem("پ", "Pe"), AlphabetItem("ت", "Te"),
        AlphabetItem("ٹ", "Tte"), AlphabetItem("ث", "Se"), AlphabetItem("ج", "Jeem"), AlphabetItem("چ", "Che"),
        AlphabetItem("ح", "He"), AlphabetItem("خ", "Khe"), AlphabetItem("د", "Dal"), AlphabetItem("ڈ", "Dal"),
        AlphabetItem("ذ", "Zal"), AlphabetItem("ر", "Re"), AlphabetItem("ڑ", "Re"), AlphabetItem("ز", "Ze"),
        AlphabetItem("س", "Seen"), AlphabetItem("ش", "Sheen"), AlphabetItem("ل", "Lam"), AlphabetItem("م", "Meem"),
        AlphabetItem("ن", "Noon"), AlphabetItem("ݨ", "Noon Gunna"), AlphabetItem("و", "Vao"), AlphabetItem("ہ", "He")
    ),
    "Sindhi" to listOf(
        AlphabetItem("ا", "Alif"), AlphabetItem("ب", "Be"), AlphabetItem("ٻ", "Bbe"), AlphabetItem("ڀ", "Bhe"),
        AlphabetItem("ت", "Te"), AlphabetItem("ٿ", "The"), AlphabetItem("ٽ", "Tte"), AlphabetItem("ٺ", "Tthe"),
        AlphabetItem("ث", "Se"), AlphabetItem("پ", "Pe"), AlphabetItem("ج", "Je"), AlphabetItem("ڄ", "Jje"),
        AlphabetItem("ڃ", "Jnye"), AlphabetItem("ڇ", "Che"), AlphabetItem("ح", "He"), AlphabetItem("خ", "Khe"),
        AlphabetItem("د", "Dal"), AlphabetItem("ڌ", "Dhal"), AlphabetItem("ڏ", "Dde"), AlphabetItem("ڊ", "Dal"),
        AlphabetItem("ڍ", "Dhal"), AlphabetItem("ذ", "Zal"), AlphabetItem("ر", "Re"), AlphabetItem("ڙ", "Re"),
        AlphabetItem("ز", "Ze"), AlphabetItem("س", "Seen"), AlphabetItem("ش", "Sheen"), AlphabetItem("ص", "Suad"),
        AlphabetItem("ض", "Zuad"), AlphabetItem("ط", "Toe"), AlphabetItem("ظ", "Zoe"), AlphabetItem("ع", "Ain"),
        AlphabetItem("غ", "Ghain"), AlphabetItem("ف", "Fe"), AlphabetItem("ڦ", "Phe"), AlphabetItem("ق", "Qaf"),
        AlphabetItem("ڪ", "Kaf"), AlphabetItem("ک", "Kh"), AlphabetItem("گ", "Gaf"), AlphabetItem("ڳ", "Gge"),
        AlphabetItem("ڱ", "Nge"), AlphabetItem("ل", "Lam"), AlphabetItem("م", "Meem"), AlphabetItem("ن", "Noon"),
        AlphabetItem("ڻ", "Nna"), AlphabetItem("و", "Vao"), AlphabetItem("ہ", "He"), AlphabetItem("ي", "Ye")
    ),
    "Pashto" to listOf(
        AlphabetItem("ا", "Alif"), AlphabetItem("ب", "Be"), AlphabetItem("پ", "Pe"), AlphabetItem("ت", "Te"),
        AlphabetItem("ټ", "Tte"), AlphabetItem("ث", "Se"), AlphabetItem("ج", "Jeem"), AlphabetItem("چ", "Che"),
        AlphabetItem("څ", "Tse"), AlphabetItem("ځ", "Dze"), AlphabetItem("ح", "He"), AlphabetItem("خ", "Khe"),
        AlphabetItem("د", "Dal"), AlphabetItem("ډ", "Dal"), AlphabetItem("ذ", "Zal"), AlphabetItem("ر", "Re"),
        AlphabetItem("ړ", "Re"), AlphabetItem("ز", "Ze"), AlphabetItem("ژ", "Zhe"), AlphabetItem("ږ", "Gze"),
        AlphabetItem("س", "Seen"), AlphabetItem("ش", "Sheen"), AlphabetItem("ښ", "Sheen"), AlphabetItem("ص", "Suad"),
        AlphabetItem("ض", "Zuad"), AlphabetItem("ط", "Toe"), AlphabetItem("ظ", "Zoe"), AlphabetItem("ع", "Ain"),
        AlphabetItem("غ", "Ghain"), AlphabetItem("ف", "Fe"), AlphabetItem("ق", "Qaf"), AlphabetItem("ک", "Kaf"),
        AlphabetItem("ګ", "Gaf"), AlphabetItem("ل", "Lam"), AlphabetItem("م", "Meem"), AlphabetItem("ن", "Noon"),
        AlphabetItem("ڼ", "Noon"), AlphabetItem("و", "Vao"), AlphabetItem("ه", "He"), AlphabetItem("ي", "Ye")
    ),
    "Saraiki" to listOf(
        AlphabetItem("ا", "Alif"), AlphabetItem("ب", "Be"), AlphabetItem("ٻ", "Bbe"), AlphabetItem("پ", "Pe"),
        AlphabetItem("ت", "Te"), AlphabetItem("ٹ", "Tte"), AlphabetItem("ث", "Se"), AlphabetItem("ج", "Jeem"),
        AlphabetItem("ڄ", "Jje"), AlphabetItem("چ", "Che"), AlphabetItem("ح", "He"), AlphabetItem("خ", "Khe"),
        AlphabetItem("د", "Dal"), AlphabetItem("ڈ", "Dal"), AlphabetItem("ݙ", "Dde"), AlphabetItem("ذ", "Zal"),
        AlphabetItem("ر", "Re"), AlphabetItem("ڑ", "Re"), AlphabetItem("ز", "Ze"), AlphabetItem("س", "Seen"),
        AlphabetItem("ش", "Sheen"), AlphabetItem("ص", "Suad"), AlphabetItem("ض", "Zuad"), AlphabetItem("ط", "Toe"),
        AlphabetItem("ظ", "Zoe"), AlphabetItem("ع", "Ain"), AlphabetItem("غ", "Ghain"), AlphabetItem("ف", "Fe"),
        AlphabetItem("ق", "Qaf"), AlphabetItem("ک", "Kaf"), AlphabetItem("گ", "Gaf"), AlphabetItem("ڳ", "Gge"),
        AlphabetItem("ل", "Lam"), AlphabetItem("م", "Meem"), AlphabetItem("ن", "Noon"), AlphabetItem("ݨ", "Nna")
    ),
    "Balochi" to listOf(
        AlphabetItem("ا", "Alif"), AlphabetItem("ب", "Be"), AlphabetItem("پ", "Pe"), AlphabetItem("ت", "Te"),
        AlphabetItem("ٹ", "Tte"), AlphabetItem("ج", "Jeem"), AlphabetItem("چ", "Che"), AlphabetItem("ح", "He"),
        AlphabetItem("خ", "Khe"), AlphabetItem("د", "Dal"), AlphabetItem("ڈ", "Dal"), AlphabetItem("ذ", "Zal"),
        AlphabetItem("ر", "Re"), AlphabetItem("ڑ", "Re"), AlphabetItem("ز", "Ze"), AlphabetItem("س", "Seen"),
        AlphabetItem("ش", "Sheen"), AlphabetItem("ک", "Kaf"), AlphabetItem("گ", "Gaf"), AlphabetItem("ل", "Lam"),
        AlphabetItem("م", "Meem"), AlphabetItem("ن", "Noon"), AlphabetItem("و", "Vao"), AlphabetItem("ہ", "He")
    )
)

val languageLiteracyData = mapOf(
    "Urdu" to mapOf(
        "Words" to listOf(AlphabetItem("کتاب", "Kitab"), AlphabetItem("آم", "Aam"), AlphabetItem("پانی", "Paani"), AlphabetItem("گھر", "Ghar"), AlphabetItem("سکول", "School"), AlphabetItem("دوست", "Dost")),
        "Sentences" to listOf(SentenceItem("میرا نام احمد ہے۔", "My name is Ahmed."), SentenceItem("یہ ایک کتاب ہے۔", "This is a book."), SentenceItem("میں پاکستان میں رہتا ہوں۔", "I live in Pakistan.")),
        "Reading" to "اردو پاکستان کی قومی زبان ہے۔ یہ بہت میٹھی زبان ہے۔ اس کے حروفِ تہجی کی تعداد 39 ہے۔"
    ),
    "Punjabi" to mapOf(
        "Words" to listOf(AlphabetItem("کتاب", "Kitab"), AlphabetItem("انب", "Amb"), AlphabetItem("پاݨی", "Paani"), AlphabetItem("گھر", "Ghar"), AlphabetItem("سکول", "School"), AlphabetItem("دوست", "Dost")),
        "Sentences" to listOf(SentenceItem("میڈا ناں احمد ہے۔", "My name is Ahmed."), SentenceItem("ایہہ اک کتاب ہے۔", "This is a book."), SentenceItem("میں پنجاب وچ رہندا ہاں۔", "I live in Punjab.")),
        "Reading" to "پنجابی ساڈی ماں بولی ہے۔ ایہہ بہت پیاری زبان ہے۔ پنجاب دی دھرتی پنج دریاواں دی دھرتی ہے۔"
    ),
    "Sindhi" to mapOf(
        "Words" to listOf(AlphabetItem("ڪتاب", "Kitab"), AlphabetItem("انب", "Anb"), AlphabetItem("پاڻي", "Paani"), AlphabetItem("گھر", "Ghar"), AlphabetItem("اسڪول", "School"), AlphabetItem("دوست", "Dost")),
        "Sentences" to listOf(SentenceItem("منھنجو نالو احمد آھي.", "My name is Ahmed."), SentenceItem("ھي ھڪ ڪتاب آھي.", "This is a book."), SentenceItem("مان سنڌ ۾ رھان ٿو.", "I live in Sindh.")),
        "Reading" to "سنڌي هڪ قديم ۽ مالدار ٻولي آهي. سنڌ جي تهذيب تمام پراڻي آهي. صوفي بزرگن هن ٻوليءَ ۾ تمام گهڻو ڪم ڪيو آهي."
    ),
    "Pashto" to mapOf(
        "Words" to listOf(AlphabetItem("کتاب", "Kitab"), AlphabetItem("مالټه", "Malta"), AlphabetItem("اوبه", "Obe"), AlphabetItem("کور", "Kor"), AlphabetItem("ښوونځی", "Skowanzay"), AlphabetItem("ملګری", "Malgari")),
        "Sentences" to listOf(SentenceItem("زما نوم احمد دی.", "My name is Ahmed."), SentenceItem("دا یو کتاب دی.", "This is a book."), SentenceItem("زه په پښتونخوا کې اوسیږم.", "I live in Pashtunkhwa.")),
        "Reading" to "پښتو یوه لرغونې او غني ژبه ده. دا د غیرت او پښتونولۍ ژبه ده. د خوشحال خان خټک او رحمان بابا شاعري په پښتو کې ده."
    ),
    "Saraiki" to mapOf(
        "Words" to listOf(AlphabetItem("کتاب", "Kitab"), AlphabetItem("انب", "Anb"), AlphabetItem("پاڻي", "Paani"), AlphabetItem("گھر", "Ghar"), AlphabetItem("سکول", "School"), AlphabetItem("دوست", "Dost")),
        "Sentences" to listOf(SentenceItem("ميڏا ناں احمد ہے۔", "My name is Ahmed."), SentenceItem("اے ہک کتاب ہے۔", "This is a book."), SentenceItem("ميں سرائيکي وسيب وچ راہندا ہاں۔", "I live in Saraiki Waseeb.")),
        "Reading" to "سرائيکي ہک مٹھی زبان ہے۔ اے صوفياں دی زبان ہے۔ خواجہ غلام فرید ديں کافياں سرائيکي ادب دا سرمايہ ہن۔"
    ),
    "Balochi" to mapOf(
        "Words" to listOf(AlphabetItem("کتاب", "Kitab"), AlphabetItem("امب", "Amb"), AlphabetItem("آپ", "Aap"), AlphabetItem(" لوگ", "Log"), AlphabetItem("اسکول", "School"), AlphabetItem("سنگت", "Sangat")),
        "Sentences" to listOf(SentenceItem("منی نام احمد انت.", "My name is Ahmed."), SentenceItem("اے یک کتابے.", "This is a book."), SentenceItem("من بلوچستان ءَ ننداں.", "I live in Balochistan.")),
        "Reading" to "بلوچی یک شرّیں ءُ کوہنیں زبانے۔ اے غیرت ءُ مھمان نوازی ءِ زبان اِنت۔ بلوچی ادب باز مزن انت।"
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LiteracyScreen(
    language: String,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    val context = LocalContext.current
    val ttsManager = remember { TtsManager(context) }
    
    val installTtsDataLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { }
    
    val checkTtsDataLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (ttsManager.shouldPromptForMissingData(result.resultCode)) {
            ttsManager.markInstallPromptShown()
            try {
                installTtsDataLauncher.launch(ttsManager.installDataIntent())
            } catch (_: ActivityNotFoundException) { }
        }
    }

    LaunchedEffect(ttsManager) {
        try {
            checkTtsDataLauncher.launch(ttsManager.checkDataIntent())
        } catch (_: ActivityNotFoundException) { }
    }

    var selectedLessonTitle by remember { mutableStateOf("Alphabet Basics") }
    
    val currentAlphabets = remember(language) {
        languageAlphabets.entries.find { it.key.equals(language, ignoreCase = true) }?.value 
            ?: languageAlphabets["Urdu"] ?: emptyList()
    }
    
    val literacyData = remember(language) {
        languageLiteracyData.entries.find { it.key.equals(language, ignoreCase = true) }?.value
            ?: languageLiteracyData["Urdu"]!!
    }

    val languageCode = remember(language) { TtsLocaleRegistry.codeFor(language) }

    var showTraceDialog by remember { mutableStateOf(false) }
    var traceChar by remember { mutableStateOf("") }

    DisposableEffect(ttsManager) {
        onDispose { ttsManager.shutdown() }
    }

    val literacyLessons = listOf(
        Lesson("Alphabet Basics", "READING", subtitle = "Master the script characters"),
        Lesson("Reading Simple Words", "AUDIO", subtitle = "Join characters to form words"),
        Lesson("Sentence Structure", "PRACTICE", subtitle = "Basic grammar rules"),
        Lesson("Advanced Reading", "QUIZ", subtitle = "Fluent reading practice")
    )

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg),
                title = { 
                    Column {
                        Text("Literacy", style = MaterialTheme.typography.titleLarge, color = TextWhite, fontWeight = FontWeight.Bold)
                        Text(language, style = MaterialTheme.typography.labelMedium, color = NeonPurple)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text(
                        text = selectedLessonTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when(selectedLessonTitle) {
                            "Alphabet Basics" -> "Tap on a character to hear its pronunciation"
                            "Reading Simple Words" -> "Learn how to read common words"
                            "Sentence Structure" -> "See how words form meaningful sentences"
                            else -> "Practice reading longer paragraphs"
                        },
                        fontSize = 12.sp,
                        color = TextGrey,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    
                    when (selectedLessonTitle) {
                        "Alphabet Basics" -> {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                currentAlphabets.forEach { alphabet ->
                                    AlphabetBox(
                                        item = alphabet,
                                        onClick = {
                                            ttsManager.speak(SpeechText(alphabet.char, languageCode))
                                        },
                                        onLongClick = {
                                            traceChar = alphabet.char
                                            showTraceDialog = true
                                        }
                                    )
                                }
                            }
                        }
                        "Reading Simple Words" -> {
                            val words = literacyData["Words"] as List<AlphabetItem>
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                words.forEach { word ->
                                    WordBox(word) {
                                        ttsManager.speak(SpeechText(word.char, languageCode))
                                    }
                                }
                            }
                        }
                        "Sentence Structure" -> {
                            val sentences = literacyData["Sentences"] as List<SentenceItem>
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                sentences.forEach { item ->
                                    SentenceCard(item) {
                                        ttsManager.speak(SpeechText(item.native, languageCode))
                                    }
                                }
                            }
                        }
                        "Advanced Reading" -> {
                            val readingText = literacyData["Reading"] as String
                            AdvancedReadingBox(readingText) {
                                ttsManager.speak(SpeechText(readingText, languageCode))
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Learning Path",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(literacyLessons) { lesson ->
                LessonCard(
                    lesson = lesson,
                    onClick = {
                        selectedLessonTitle = it.title
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showTraceDialog) {
        TracingDialog(
            char = traceChar,
            onDismiss = { showTraceDialog = false }
        )
    }
}

@Composable
fun TracingDialog(char: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = DarkBg
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Trace to Learn",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Text("✕", color = TextWhite, fontSize = 24.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    "Follow the guide to write '$char'",
                    color = TextGrey,
                    fontSize = 14.sp
                )

                Spacer(Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(DarkSurface, RoundedCornerShape(16.dp))
                        .border(2.dp, NeonCyan.copy(0.2f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Guide Letter (Ghost)
                    Text(
                        text = char,
                        fontSize = 180.sp,
                        color = TextWhite.copy(0.1f),
                        textAlign = TextAlign.Center
                    )

                    // Canvas for Drawing
                    TracingCanvas(modifier = Modifier.fillMaxSize())
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("I've Learned It!", color = DarkBg, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TracingCanvas(modifier: Modifier = Modifier) {
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var updateTrigger by remember { mutableStateOf(0) } // Dummy state to trigger redraw

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val path = Path().apply { moveTo(offset.x, offset.y) }
                        currentPath = path
                        paths.add(path)
                        updateTrigger++
                    },
                    onDrag = { change, _ ->
                        currentPath?.lineTo(change.position.x, change.position.y)
                        updateTrigger++
                    },
                    onDragEnd = {
                        currentPath = null
                        updateTrigger++
                    }
                )
            }
    ) {
        updateTrigger // Read the state to trigger redraw
        paths.forEach { path ->
            drawPath(
                path = path,
                color = NeonCyan,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlphabetBox(item: AlphabetItem, onClick: () -> Unit, onLongClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(width = 64.dp, height = 84.dp),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, NeonCyan.copy(0.2f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(4.dp)
        ) {
            Text(
                text = item.char,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.roman,
                fontSize = 10.sp,
                color = NeonCyan,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WordBox(item: AlphabetItem, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.height(60.dp).padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceLighter,
        border = BorderStroke(1.dp, NeonPurple.copy(0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(item.char, fontSize = 22.sp, color = TextWhite, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text(item.roman, fontSize = 14.sp, color = NeonPurple)
        }
    }
}

@Composable
fun SentenceCard(item: SentenceItem, onPlay: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, Color.White.copy(0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.native, fontSize = 20.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                Text(item.translation, fontSize = 14.sp, color = TextGrey, modifier = Modifier.padding(top = 4.dp))
            }
            IconButton(onClick = onPlay, modifier = Modifier.background(NeonCyan.copy(0.1f), CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun AdvancedReadingBox(text: String, onPlay: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = DarkSurfaceLighter,
        border = BorderStroke(1.dp, NeonOrange.copy(0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = text,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                color = TextWhite,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = NeonOrange.copy(0.1f)),
                modifier = Modifier.align(Alignment.Start),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = NeonOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Listen Full Paragraph", color = NeonOrange, fontSize = 12.sp)
            }
        }
    }
}
