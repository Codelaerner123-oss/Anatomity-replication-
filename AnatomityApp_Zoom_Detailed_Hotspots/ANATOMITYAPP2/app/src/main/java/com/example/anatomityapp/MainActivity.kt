package com.example.anatomityapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import kotlin.math.roundToInt
import kotlin.math.pow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.gestures.detectTransformGestures


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AnatomityApp(this)
        }
    }
}

/* =========================================================
   COLORS
   ========================================================= */

private val DarkBlue = Color(0xFF020D20)
private val DeepBlue = Color(0xFF06355B)
private val Orange = Color(0xFFFF9800)
private val Gold = Color(0xFFFFD52A)
private val BlueButton = Color(0xFF287CF0)
private val GreenButton = Color(0xFF25C65A)
private val RedButton = Color(0xFFE53935)
private val CardWhite = Color(0xFFF0EDF2)

/* =========================================================
   NAVIGATION
   ========================================================= */

private enum class AppScreen {
    HOME,
    GRADES,
    LEARNING,
    LESSONS,
    QUIZ,
    MATCHING,
    MODEL2D,
    MODEL3D
}

/* =========================================================
   APP
   ========================================================= */

@Composable
fun AnatomityApp(context: Context) {

    val prefs = remember {
        context.getSharedPreferences(
            "ANATOMITY_PREFS",
            Context.MODE_PRIVATE
        )
    }

    var screen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    var userName by remember {
        mutableStateOf(
            prefs.getString("USER_NAME", "") ?: ""
        )
    }

    var selectedGrade by remember {
        mutableIntStateOf(
            prefs.getInt("LAST_GRADE", 7)
        )
    }

    BackHandler(
        enabled = screen != AppScreen.HOME
    ) {

        screen = when (screen) {

            AppScreen.GRADES ->
                AppScreen.HOME

            AppScreen.LEARNING ->
                AppScreen.GRADES

            AppScreen.LESSONS,
            AppScreen.QUIZ,
            AppScreen.MATCHING,
            AppScreen.MODEL2D,
            AppScreen.MODEL3D ->
                AppScreen.LEARNING

            AppScreen.HOME ->
                AppScreen.HOME
        }
    }

    when (screen) {

        AppScreen.HOME -> {

            HomeScreen(
                name = userName,

                onNameChange = {
                    userName = it
                },

                onStart = {

                    val cleanName =
                        userName.trim()

                    if (cleanName.isNotEmpty()) {

                        userName = cleanName

                        prefs.edit()
                            .putString(
                                "USER_NAME",
                                cleanName
                            )
                            .apply()

                        screen =
                            AppScreen.GRADES
                    }
                },

                onPrevious = {

                    val savedName =
                        prefs.getString(
                            "USER_NAME",
                            ""
                        )

                    val savedGrade =
                        prefs.getInt(
                            "LAST_GRADE",
                            -1
                        )

                    if (
                        !savedName.isNullOrBlank() &&
                        savedGrade >= 7
                    ) {

                        userName =
                            savedName

                        selectedGrade =
                            savedGrade

                        screen =
                            AppScreen.LEARNING
                    }
                }
            )
        }

        AppScreen.GRADES -> {

            GradeSelectionScreen(

                userName = userName,

                onBack = {
                    screen =
                        AppScreen.HOME
                },

                onGradeSelected = { grade ->

                    selectedGrade =
                        grade

                    prefs.edit()
                        .putInt(
                            "LAST_GRADE",
                            grade
                        )
                        .apply()

                    screen =
                        AppScreen.LEARNING
                }
            )
        }

        AppScreen.LEARNING -> {

            LearningOptionsScreen(

                userName = userName,

                grade = selectedGrade,

                onBack = {
                    screen =
                        AppScreen.GRADES
                },

                onLessons = {
                    screen =
                        AppScreen.LESSONS
                },

                onQuiz = {
                    screen =
                        AppScreen.QUIZ
                },

                onMatching = {
                    screen =
                        AppScreen.MATCHING
                },

                onModel2D = {
                    screen =
                        AppScreen.MODEL2D
                },

                onModel3D = {
                    screen =
                        AppScreen.MODEL3D
                }
            )
        }

        AppScreen.LESSONS -> {

            LessonScreen(
                grade = selectedGrade,
                onBack = {
                    screen =
                        AppScreen.LEARNING
                }
            )
        }

        AppScreen.QUIZ -> {

            QuizScreen(
                grade = selectedGrade,
                onBack = {
                    screen =
                        AppScreen.LEARNING
                }
            )
        }

        AppScreen.MATCHING -> {

            MatchingGameScreen(
                grade = selectedGrade,
                onBack = {
                    screen =
                        AppScreen.LEARNING
                }
            )
        }

        AppScreen.MODEL2D -> {

            AnatomyModelScreen(
                grade = selectedGrade,
                is3D = false,
                onBack = {
                    screen =
                        AppScreen.LEARNING
                }
            )
        }

        AppScreen.MODEL3D -> {

            AnatomyModelScreen(
                grade = selectedGrade,
                is3D = true,
                onBack = {
                    screen =
                        AppScreen.LEARNING
                }
            )
        }
    }
}

/* =========================================================
   BACKGROUND
   ========================================================= */

@Composable
private fun AnatomyBackground(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color(0xFF010A18),
                    Color(0xFF052B4C),
                    Color(0xFF064A78),
                    Color(0xFF03172B),
                    Color(0xFF010914)
                )
            )
        )
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize(),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier =
                    Modifier.height(35.dp)
            )

            Text(
                text = "🧠",
                fontSize = 70.sp
            )

            Text(
                text = "🫀",
                fontSize = 110.sp
            )

            Text(
                text = "🫁",
                fontSize = 70.sp
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color(0x66000000)
                    )
        )
    }
}

/* =========================================================
   HOME
   ========================================================= */

@Composable
private fun HomeScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onStart: () -> Unit,
    onPrevious: () -> Unit
) {

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        AnatomyBackground(
            modifier =
                Modifier.fillMaxSize()
        )

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Box(
                modifier =
                    Modifier
                        .size(150.dp)
                        .clip(
                            RoundedCornerShape(28.dp)
                        )
                        .background(Color.Green),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🧬",
                        fontSize = 60.sp
                    )

                    Text(
                        text = "ANATOMITY",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(25.dp)
            )

            Text(
                text =
                    "HUMAN ANATOMY\nEXPLORER",

                color = Color.Yellow,

                fontSize = 29.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )

            OutlinedTextField(

                value = name,

                onValueChange =
                    onNameChange,

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                placeholder = {
                    Text(
                        text =
                            "Enter your name"
                    )
                },

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White,

                        focusedTextColor =
                            Color.Black,

                        unfocusedTextColor =
                            Color.Black,

                        focusedBorderColor =
                            Orange,

                        unfocusedBorderColor =
                            Color.Transparent
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            StartButton(
                text = "START",
                imageRes = R.drawable.heart,
                onClick = onStart
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            PreviousButton(
                text = "PREVIOUS",
                onClick = onPrevious,
                imageRes = R.drawable.lungs,
            )

            Spacer(
                modifier =
                    Modifier.height(35.dp)
            )

            Text(
                text =
                    "INTERACTIVE HUMAN ANATOMY\nLEARNING JOURNEY",

                color = Color.White,

                fontSize = 16.sp,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text = "READY TO LEARN?",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

/* =========================================================
   Start BUTTON
   ========================================================= */

@Composable
private fun StartButton(
    imageRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {

        // Background image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = text,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay so text is readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.35f)
                )
        )

        // Text on top of the image
        Text(
            text = text,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun PreviousButton(
    imageRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {

        // Background image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = text,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay so text is readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.35f)
                )
        )

        // Text on top of the image
        Text(
            text = text,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/* =========================================================
   HEADER
   ========================================================= */

@Composable
private fun ScreenHeader(
    title: String,
    onBack: () -> Unit
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Button(

            onClick = onBack,

            modifier =
                Modifier.size(
                    width = 70.dp,
                    height = 50.dp
                ),

            contentPadding =
                PaddingValues(0.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        Color.White,
                    contentColor =
                        Color.Black
                ),

            shape =
                RoundedCornerShape(15.dp)
        ) {

            Text(
                text = "←",
                fontSize = 25.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }

        Text(
            text = title,

            modifier =
                Modifier.weight(1f),

            color = Color.White,

            fontSize = 20.sp,

            fontWeight =
                FontWeight.ExtraBold,

            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.width(70.dp)
        )
    }
}

/* =========================================================
   GRADES
   ========================================================= */

@Composable
private fun GradeSelectionScreen(
    userName: String,
    onBack: () -> Unit,
    onGradeSelected: (Int) -> Unit
) {

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        AnatomyBackground(
            modifier =
                Modifier.fillMaxSize()
        )

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            ScreenHeader(
                title = "SELECT GRADE",
                onBack = onBack
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Text(
                text =
                    "Welcome, $userName",

                color = Color.White,

                fontSize = 19.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )

            GradeCard(
                7,
                "🫀",
                onGradeSelected
            )

            GradeCard(
                8,
                "🔬",
                onGradeSelected
            )

            GradeCard(
                9,
                "🧠",
                onGradeSelected
            )

            GradeCard(
                10,
                "🫁",
                onGradeSelected
            )

            GradeCard(
                11,
                "🧬",
                onGradeSelected
            )

            GradeCard(
                12,
                "🔬",
                onGradeSelected
            )
        }
    }
}

@Composable
private fun GradeCard(
    grade: Int,
    icon: String,
    onSelected: (Int) -> Unit
) {

    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(
                    vertical = 5.dp
                )
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF153F61),
                            Color(0xFF071D34)
                        )
                    )
                )
                .clickable {
                    onSelected(grade)
                },

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = icon,
                fontSize = 45.sp
            )

            Text(
                text = "GRADE $grade",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }
    }
}

/* =========================================================
   LEARNING
   ========================================================= */

@Composable
private fun LearningOptionsScreen(
    userName: String,
    grade: Int,
    onBack: () -> Unit,
    onLessons: () -> Unit,
    onQuiz: () -> Unit,
    onMatching: () -> Unit,
    onModel2D: () -> Unit,
    onModel3D: () -> Unit
) {

    val options = remember(grade) { getGradeOptions(grade) }

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        AnatomyBackground(
            modifier =
                Modifier.fillMaxSize()
        )

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            ScreenHeader(
                "LEARNING",
                onBack
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Text(
                text =
                    "Welcome, $userName",

                color = Color.White,

                fontSize = 19.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = "Grade $grade",
                color = Gold,
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(25.dp)
            )

            Text(
                text =
                    "CHOOSE A LEARNING ACTIVITY",

                color =
                    Color(0xFF39A9FF),

                fontSize = 19.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            if (options.showLessons) {

                LearningCard(
                    R.drawable.logo,
                    "Lessons",
                    "Read short lessons for your grade level.",
                    "START LESSON",
                    Color(0xFF7C4DFF),
                    onLessons
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )
            }

            if (options.showQuiz) {

                LearningCard(
                    R.drawable.logo,
                    "Interactive Quiz",
                    "Test your anatomy knowledge.",
                    "START QUIZ",
                    BlueButton,
                    onQuiz
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )
            }

            if (options.showMatching) {

                LearningCard(
                    R.drawable.logo,
                    "Matching Game",
                    "Match anatomy parts with their pairs.",
                    "START GAME",
                    GreenButton,
                    onMatching
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )
            }

            if (options.show2D) {

                LearningCard(
                    R.drawable.logo,
                    "2D Anatomy Model",
                    "Tap body parts to learn about them.",
                    "OPEN MODEL",
                    Color(0xFF00A6C8),
                    onModel2D
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )
            }

            if (options.show3D) {

                LearningCard(
                    R.drawable.logo,
                    "3D Anatomy Model",
                    "Explore an interactive 3D body.",
                    "OPEN MODEL",
                    Color(0xFF006E8C),
                    onModel3D
                )
            }
        }
    }
}

@Composable
private fun LearningCard(
    imageRes: Int,
    title: String,
    description: String,
    buttonText: String,
    buttonColor: Color,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardWhite)
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .height(65.dp)
                .width(190.dp)

        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = title,
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = description,
            color = Color.DarkGray,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            shape = RoundedCornerShape(15.dp)
        ) {

            Text(
                text = buttonText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* =========================================================
   LESSONS
   ========================================================= */

private data class Lesson(
    val title: String,
    val body: String
)

private fun getLessons(grade: Int): List<Lesson> {

    return when (grade) {

        7 -> listOf(
            Lesson("What Is Anatomy?", "Anatomy is the study of body structure. Cells group into tissues, tissues form organs, and organs work together in systems."),
            Lesson("Your Skeleton", "Your skeleton gives your body shape, protects organs like your brain and heart, and lets you move using joints."),
            Lesson("Heart & Lungs", "Your heart pumps blood around your body. Your lungs bring in oxygen and remove carbon dioxide when you breathe."),
            Lesson("Digestion Basics", "Digestion breaks food into nutrients your body can use for energy and growth.")
        )

        8 -> listOf(
            Lesson("Circulatory System", "The heart, blood, and blood vessels move oxygen and nutrients to every part of your body."),
            Lesson("Respiratory System", "Your lungs and airways exchange oxygen and carbon dioxide with every breath."),
            Lesson("Skeletal & Muscular Systems", "Bones and muscles work together — muscles pull on bones to create movement."),
            Lesson("Digestive System", "Food travels through your mouth, stomach, and intestines, where it's broken down and absorbed."),
            Lesson("Nervous System Intro", "Your brain, spinal cord, and nerves send electrical signals that control your whole body.")
        )

        9 -> listOf(
            Lesson("Nervous System", "The brain and spinal cord (CNS) coordinate with nerves (PNS) to control sensation and movement."),
            Lesson("Endocrine System", "Glands release hormones into the blood to regulate growth, metabolism, and mood."),
            Lesson("Circulatory System", "The heart, arteries, veins, and capillaries form a closed loop delivering blood throughout the body."),
            Lesson("Respiratory System", "Gas exchange happens in tiny air sacs called alveoli, deep inside the lungs."),
            Lesson("Muscular System", "Skeletal, smooth, and cardiac muscle each play a different role in movement and body function."),
            Lesson("Skeletal System", "Bones store minerals, produce blood cells in marrow, and form the body's supporting framework.")
        )

        10 -> listOf(
            Lesson("Homeostasis", "The body constantly self-regulates temperature, pH, and other conditions to stay in balance."),
            Lesson("Cardiovascular Physiology", "Blood pressure and heart rate change based on the body's oxygen and nutrient demands."),
            Lesson("Respiratory Physiology", "Breathing rate adjusts automatically based on carbon dioxide levels in the blood."),
            Lesson("Neuromuscular Function", "Nerve impulses trigger muscle fibers to contract, enabling precise, controlled movement."),
            Lesson("Digestive & Metabolic Processes", "Enzymes break down food into usable molecules that fuel cellular metabolism."),
            Lesson("Excretory System", "The kidneys filter waste and excess fluid from the blood to form urine."),
            Lesson("Immune Basics", "White blood cells and antibodies identify and fight off pathogens.")
        )

        11 -> listOf(
            Lesson("Homeostasis & Feedback Loops", "Negative feedback loops (like temperature regulation) keep the body's internal environment stable."),
            Lesson("Cardiovascular Advanced", "The cardiac cycle, electrical conduction system, and blood pressure regulation keep circulation efficient."),
            Lesson("Respiratory Advanced", "Oxygen and carbon dioxide diffuse across the alveolar-capillary membrane, driven by pressure gradients."),
            Lesson("Nervous System Advanced", "The CNS and PNS coordinate reflexes, voluntary movement, and autonomic functions like digestion and heart rate."),
            Lesson("Endocrine Regulation", "Hormone levels are controlled by feedback loops between glands like the pituitary, thyroid, and adrenal glands."),
            Lesson("Renal & Excretory System", "Nephrons in the kidney filter blood and reabsorb needed substances, balancing fluid and electrolytes."),
            Lesson("Reproductive System Overview", "The reproductive system enables growth, hormone regulation, and human reproduction."),
            Lesson("Immune System & Defense", "Innate and adaptive immunity work together to recognize and eliminate threats.")
        )

        else -> listOf( // grade 12
            Lesson("Full Systems Review", "A recap of how the skeletal, muscular, circulatory, respiratory, nervous, endocrine, digestive, and excretory systems interconnect."),
            Lesson("Case Study: Cardiovascular Disease", "Explore how conditions like hypertension and atherosclerosis affect heart function."),
            Lesson("Case Study: Respiratory Disorders", "Examine how asthma and COPD disrupt normal gas exchange."),
            Lesson("Case Study: Endocrine Disorders", "Look at how conditions like diabetes result from hormone imbalances."),
            Lesson("Case Study: Neurological Conditions", "Understand how nerve or brain damage can affect movement and cognition."),
            Lesson("Case Study: Renal Disorders", "See how kidney dysfunction affects waste filtration and fluid balance."),
            Lesson("Case Study: Immune Disorders", "Explore autoimmune conditions and immunodeficiency."),
            Lesson("Mixed Exam Review", "A final review question set spanning every major body system.")
        )
    }
}

@Composable
private fun LessonScreen(
    grade: Int,
    onBack: () -> Unit
) {

    val lessons = remember(grade) { getLessons(grade) }
    var index by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBlue, DeepBlue)
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ScreenHeader("GRADE $grade LESSONS", onBack)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Lesson ${index + 1} / ${lessons.size}",
                color = Gold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(22.dp)
            ) {

                Column {

                    Text(
                        text = lessons[index].title,
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = lessons[index].body,
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                if (index > 0) {
                    Button(
                        onClick = { index-- },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text("BACK", fontWeight = FontWeight.Bold)
                    }
                }

                if (index < lessons.lastIndex) {
                    Button(
                        onClick = { index++ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueButton
                        ),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text("NEXT", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenButton
                        ),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text("FINISH", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/* =========================================================
   GRADE OPTIONS
   ========================================================= */

private data class GradeOptions(
    val showLessons: Boolean = true,
    val showQuiz: Boolean = true,
    val showMatching: Boolean,
    val show2D: Boolean,
    val show3D: Boolean
)

private fun getGradeOptions(grade: Int): GradeOptions {

    return when (grade) {
        7, 8 -> GradeOptions(showMatching = true, show2D = true, show3D = false)
        9 -> GradeOptions(showMatching = true, show2D = true, show3D = true)
        10 -> GradeOptions(showMatching = false, show2D = true, show3D = true)
        else -> GradeOptions(showMatching = false, show2D = false, show3D = true) // 11, 12
    }
}

/* =========================================================
   QUIZ
   ========================================================= */

private data class QuizQuestion(
    val question: String,
    val answers: List<String>,
    val correct: Int
)

private fun getQuestions(grade: Int): List<QuizQuestion> {

    return when (grade) {

        7 -> listOf(
            QuizQuestion("What is the basic unit of life?", listOf("Organ", "Cell", "Tissue", "System"), 1),
            QuizQuestion("Which organ pumps blood?", listOf("Lung", "Brain", "Heart", "Kidney"), 2),
            QuizQuestion("Which organs help us breathe?", listOf("Kidneys", "Lungs", "Liver", "Heart"), 1),
            QuizQuestion("Which system supports the body?", listOf("Skeletal", "Digestive", "Nervous", "Endocrine"), 0)
        )

        8 -> listOf(
            QuizQuestion("Which system moves blood around the body?", listOf("Digestive", "Circulatory", "Skeletal", "Endocrine"), 1),
            QuizQuestion("Which system exchanges oxygen and CO2?", listOf("Respiratory", "Muscular", "Nervous", "Digestive"), 0),
            QuizQuestion("Muscles move the body by pulling on what?", listOf("Skin", "Bones", "Blood vessels", "Nerves"), 1),
            QuizQuestion("Where does digestion mainly finish absorbing nutrients?", listOf("Stomach", "Small intestine", "Lungs", "Heart"), 1),
            QuizQuestion("What controls signals throughout the body?", listOf("Nervous system", "Skeletal system", "Digestive system", "Endocrine system"), 0)
        )

        9 -> listOf(
            QuizQuestion("Which organ pumps blood?", listOf("Lung", "Heart", "Liver", "Brain"), 1),
            QuizQuestion("Which system is responsible for gas exchange?", listOf("Digestive", "Respiratory", "Skeletal", "Endocrine"), 1),
            QuizQuestion("Which system produces hormones?", listOf("Endocrine", "Skeletal", "Muscular", "Respiratory"), 0),
            QuizQuestion("Where does most nutrient absorption occur?", listOf("Small intestine", "Lung", "Heart", "Brain"), 0),
            QuizQuestion("Bone marrow produces which of these?", listOf("Blood cells", "Hormones", "Digestive enzymes", "Nerve signals"), 0),
            QuizQuestion("The CNS is made up of the brain and what else?", listOf("Nerves", "Spinal cord", "Muscles", "Glands"), 1)
        )

        10 -> listOf(
            QuizQuestion("The body keeping internal conditions stable is called:", listOf("Metabolism", "Homeostasis", "Digestion", "Respiration"), 1),
            QuizQuestion("What increases when the body needs more oxygen?", listOf("Heart rate", "Bone density", "Digestion speed", "Hormone storage"), 0),
            QuizQuestion("Where does gas exchange occur in the lungs?", listOf("Bronchi", "Trachea", "Alveoli", "Diaphragm"), 2),
            QuizQuestion("A nerve impulse causes what to contract?", listOf("A gland", "A muscle fiber", "A blood vessel", "A bone"), 1),
            QuizQuestion("Which organ filters waste from the blood?", listOf("Liver", "Kidney", "Lung", "Stomach"), 1),
            QuizQuestion("What cells help fight off pathogens?", listOf("Red blood cells", "White blood cells", "Bone cells", "Muscle cells"), 1),
            QuizQuestion("Enzymes help the body do what?", listOf("Break down food", "Pump blood", "Send nerve signals", "Store minerals"), 0)
        )

        11 -> listOf(
            QuizQuestion("A negative feedback loop generally works to:", listOf("Increase change", "Restore balance", "Speed up growth", "Stop hormone release"), 1),
            QuizQuestion("The heart's electrical signal starts at the:", listOf("AV node", "SA node", "Ventricle", "Aorta"), 1),
            QuizQuestion("Gas exchange across the alveolar membrane is driven by:", listOf("Muscle contraction", "Pressure gradients", "Hormone signals", "Nerve impulses"), 1),
            QuizQuestion("Which nervous system division controls involuntary functions?", listOf("Somatic", "Autonomic", "Central", "Peripheral"), 1),
            QuizQuestion("The pituitary gland is often called the:", listOf("Master gland", "Filter organ", "Pump organ", "Storage gland"), 0),
            QuizQuestion("Nephrons are the functional units of the:", listOf("Liver", "Kidney", "Lung", "Heart"), 1),
            QuizQuestion("Adaptive immunity differs from innate immunity because it:", listOf("Acts immediately", "Has memory", "Only involves skin", "Doesn't use white blood cells"), 1),
            QuizQuestion("Hormone levels are typically regulated by:", listOf("Feedback loops", "Muscle contraction", "Bone density", "Digestion rate"), 0)
        )

        else -> listOf( // grade 12
            QuizQuestion("Atherosclerosis is most closely linked to which system?", listOf("Cardiovascular", "Digestive", "Skeletal", "Immune"), 0),
            QuizQuestion("Asthma primarily affects which system?", listOf("Respiratory", "Endocrine", "Renal", "Muscular"), 0),
            QuizQuestion("Diabetes results from a problem with which hormone?", listOf("Adrenaline", "Insulin", "Melatonin", "Estrogen"), 1),
            QuizQuestion("Damage to the CNS can most directly affect:", listOf("Digestion speed", "Movement and cognition", "Bone density", "Blood type"), 1),
            QuizQuestion("Kidney dysfunction most directly disrupts:", listOf("Waste filtration and fluid balance", "Oxygen intake", "Hormone production", "Muscle contraction"), 0),
            QuizQuestion("Autoimmune disorders occur when the immune system:", listOf("Ignores real threats", "Attacks the body's own cells", "Stops producing blood cells", "Overproduces bone tissue"), 1),
            QuizQuestion("Which system integrates with nearly all others via hormones?", listOf("Endocrine", "Skeletal", "Muscular", "Integumentary"), 0),
            QuizQuestion("A mixed review question: which organ is part of 3 systems (circulatory, endocrine signaling target, and lymphatic filtering)?", listOf("Liver", "Spleen", "Stomach", "Trachea"), 1)
        )
    }
}

@Composable
private fun QuizScreen(
    grade: Int,
    onBack: () -> Unit
) {

    val questions =
        remember(grade) {
            getQuestions(grade)
        }

    var index by remember {
        mutableIntStateOf(0)
    }

    var score by remember {
        mutableIntStateOf(0)
    }

    var selected by remember {
        mutableStateOf<Int?>(null)
    }

    var finished by remember {
        mutableStateOf(false)
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DarkBlue,
                            DeepBlue
                        )
                    )
                )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            ScreenHeader(
                "GRADE $grade QUIZ",
                onBack
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            if (finished) {

                Text(
                    text = "🎉",
                    fontSize = 70.sp
                )

                Text(
                    text = "QUIZ COMPLETE!",
                    color = Gold,
                    fontSize = 27.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )

                Text(
                    text =
                        "Score: $score / ${questions.size}",

                    color = Color.White,

                    fontSize = 24.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(25.dp)
                )

                Button(
                    onClick = {
                        index = 0
                        score = 0
                        selected = null
                        finished = false
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(58.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                BlueButton
                        )
                ) {

                    Text(
                        text = "RETRY QUIZ",
                        fontWeight =
                            FontWeight.Bold
                    )
                }

            } else {

                val q =
                    questions[index]

                Text(
                    text =
                        "Question ${index + 1} / ${questions.size}",

                    color = Gold,

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(20.dp)
                            )
                            .background(Color.White)
                            .padding(22.dp)
                ) {

                    Text(
                        text = q.question,
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.Bold,
                        textAlign =
                            TextAlign.Center,
                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )

                q.answers.forEachIndexed {
                        answerIndex,
                        answer ->

                    val answerColor =
                        when {

                            selected == null ->
                                Color.White

                            answerIndex ==
                                    q.correct ->
                                GreenButton

                            answerIndex ==
                                    selected ->
                                RedButton

                            else ->
                                Color.White
                        }

                    Button(

                        onClick = {

                            if (selected == null) {

                                selected =
                                    answerIndex

                                if (
                                    answerIndex ==
                                    q.correct
                                ) {
                                    score++
                                }
                            }
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(
                                    vertical = 3.dp
                                ),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    answerColor,
                                contentColor =
                                    Color.Black
                            ),

                        shape =
                            RoundedCornerShape(15.dp)
                    ) {

                        Text(
                            text = answer,
                            fontSize = 16.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                if (selected != null) {

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            if (
                                selected ==
                                q.correct
                            ) {
                                "✓ Correct!"
                            } else {
                                "✗ Correct answer: " +
                                        q.answers[q.correct]
                            },

                        color =
                            if (
                                selected ==
                                q.correct
                            )
                                GreenButton
                            else
                                Color(0xFFFF8A80),

                        fontSize = 17.sp,

                        fontWeight =
                            FontWeight.Bold,

                        textAlign =
                            TextAlign.Center
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Button(

                        onClick = {

                            if (
                                index ==
                                questions.lastIndex
                            ) {

                                finished =
                                    true

                            } else {

                                index++

                                selected =
                                    null
                            }
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(58.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    BlueButton
                            )
                    ) {

                        Text(
                            text =
                                if (
                                    index ==
                                    questions.lastIndex
                                )
                                    "SEE RESULT"
                                else
                                    "NEXT QUESTION",

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/* =========================================================
   MATCHING GAME
   ========================================================= */

private data class MatchCard(
    val id: Int,
    val name: String,
    val icon: String
)

@Composable
private fun MatchingGameScreen(
    grade: Int,
    onBack: () -> Unit
) {

    val cards =
        remember {

            listOf(

                MatchCard(
                    1,
                    "HEART",
                    "❤️"
                ),

                MatchCard(
                    2,
                    "HEART",
                    "❤️"
                ),

                MatchCard(
                    3,
                    "BRAIN",
                    "🧠"
                ),

                MatchCard(
                    4,
                    "BRAIN",
                    "🧠"
                ),

                MatchCard(
                    5,
                    "LUNGS",
                    "🫁"
                ),

                MatchCard(
                    6,
                    "LUNGS",
                    "🫁"
                ),

                MatchCard(
                    7,
                    "BONE",
                    "🦴"
                ),

                MatchCard(
                    8,
                    "BONE",
                    "🦴"
                )



            ).shuffled()
        }

    var firstCard by remember {
        mutableStateOf<Int?>(null)
    }

    var revealed by remember {
        mutableStateOf(emptySet<Int>())
    }

    var matched by remember {
        mutableStateOf(emptySet<Int>())
    }

    var moves by remember {
        mutableIntStateOf(0)
    }

    var message by remember {
        mutableStateOf(
            "Choose two cards"
        )
    }

    var complete by remember {
        mutableStateOf(false)
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DarkBlue,
                            DeepBlue
                        )
                    )
                )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            ScreenHeader(
                "MATCHING GAME",
                onBack
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text = "GRADE $grade",
                color = Gold,
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
                textAlign =
                    TextAlign.Center
            )

            Text(
                text = "Moves: $moves",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )

            cards.chunked(2).forEach { row ->

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    row.forEach { card ->

                        val isVisible =
                            revealed.contains(
                                card.id
                            )

                        val isMatched =
                            matched.contains(
                                card.id
                            )

                        Box(

                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(115.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            17.dp
                                        )
                                    )
                                    .background(

                                        when {

                                            isMatched ->
                                                GreenButton

                                            isVisible ->
                                                Color.White

                                            else ->
                                                Color(0xFF174E76)
                                        }
                                    )
                                    .clickable {

                                        if (
                                            complete ||
                                            isMatched ||
                                            isVisible
                                        ) {
                                            return@clickable
                                        }

                                        if (
                                            firstCard == null
                                        ) {

                                            firstCard =
                                                card.id

                                            revealed =
                                                revealed +
                                                        card.id

                                            message =
                                                "Choose the matching card"

                                        } else {

                                            val first =
                                                cards.firstOrNull {
                                                    it.id ==
                                                            firstCard
                                                }

                                            revealed =
                                                revealed +
                                                        card.id

                                            moves++

                                            if (
                                                first != null &&
                                                first.name ==
                                                card.name
                                            ) {

                                                matched =
                                                    matched +
                                                            first.id +
                                                            card.id

                                                message =
                                                    "✓ Match!"

                                                firstCard =
                                                    null

                                                if (
                                                    matched.size ==
                                                    cards.size
                                                ) {

                                                    complete =
                                                        true

                                                    message =
                                                        "🎉 ALL PAIRS MATCHED!"
                                                }

                                            } else {

                                                firstCard =
                                                    null

                                                message =
                                                    "✗ Not a match. Reset and try again."
                                            }
                                        }
                                    },

                            contentAlignment =
                                Alignment.Center
                        ) {

                            if (
                                isVisible ||
                                isMatched
                            ) {

                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        text =
                                            card.icon,
                                        fontSize =
                                            40.sp
                                    )

                                    Text(
                                        text =
                                            card.name,
                                        color =
                                            Color.Black,
                                        fontSize =
                                            14.sp,
                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }

                            } else {

                                Text(
                                    text = "?",
                                    color =
                                        Color.White,
                                    fontSize =
                                        40.sp,
                                    fontWeight =
                                        FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
            }

            Button(

                onClick = {

                    firstCard = null
                    revealed = emptySet()
                    matched = emptySet()
                    moves = 0
                    complete = false
                    message =
                        "Choose two cards"
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(58.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            BlueButton
                    )
            ) {

                Text(
                    text = "RESET GAME",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}

/* =========================================================
   ANATOMY MODEL (2D / 3D) - ORGAN SYSTEM LAYERS
   ========================================================= */

private data class AnatomyPart(
    val name: String,
    val icon: String,
    val overview: String,
    val function: String,
    val location: String,
    val hotspotX: Float,
    val hotspotY: Float,
    val minZoom: Float = 1f
)

private data class AnatomySystemLayer(
    val name: String,
    val icon: String,
    val imageRes: Int,
    val imageAspectRatio: Float,
    val parts: List<AnatomyPart>
)

private val anatomySystemLayers = listOf(
    AnatomySystemLayer(
        name = "Respiratory",
        icon = "🫁",
        imageRes = R.drawable.anatomy_respiratory,
        imageAspectRatio = 547f / 900f,
        parts = listOf(
            AnatomyPart(
                name = "Nasal Cavity",
                icon = "👃",
                overview = "The nasal cavity is the air-filled passage inside the nose.",
                function = "Filters, warms, and humidifies inhaled air.",
                location = "Inside the nose and upper face.",
                hotspotX = 0.660f,
                hotspotY = 0.300f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Oral Cavity",
                icon = "●",
                overview = "The oral cavity can also serve as an airway during breathing.",
                function = "Provides an alternate pathway for air to enter the pharynx.",
                location = "Mouth.",
                hotspotX = 0.610f,
                hotspotY = 0.360f,
                minZoom = 1.4f
            ),
            AnatomyPart(
                name = "Pharynx",
                icon = "●",
                overview = "The pharynx is a shared passage for air and food.",
                function = "Conducts air from the nose and mouth toward the larynx.",
                location = "Behind the nasal and oral cavities.",
                hotspotX = 0.510f,
                hotspotY = 0.380f,
                minZoom = 1.4f
            ),
            AnatomyPart(
                name = "Epiglottis",
                icon = "●",
                overview = "The epiglottis is a flap of elastic cartilage near the entrance to the larynx.",
                function = "Helps direct food away from the airway during swallowing.",
                location = "Upper throat behind the tongue.",
                hotspotX = 0.560f,
                hotspotY = 0.400f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Larynx",
                icon = "●",
                overview = "The larynx is a cartilaginous structure that contains the vocal cords.",
                function = "Keeps the airway open and contributes to voice production.",
                location = "Front of the neck above the trachea.",
                hotspotX = 0.500f,
                hotspotY = 0.440f,
                minZoom = 1.4f
            ),
            AnatomyPart(
                name = "Trachea",
                icon = "↕",
                overview = "The trachea is the main airway leading toward the lungs.",
                function = "Carries air between the larynx and the bronchi.",
                location = "Neck and upper chest.",
                hotspotX = 0.500f,
                hotspotY = 0.510f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Right Main Bronchus",
                icon = "●",
                overview = "The right main bronchus branches from the trachea into the right lung.",
                function = "Conducts air into the right lung.",
                location = "At the tracheal split in the chest.",
                hotspotX = 0.430f,
                hotspotY = 0.640f,
                minZoom = 1.7f
            ),
            AnatomyPart(
                name = "Left Main Bronchus",
                icon = "●",
                overview = "The left main bronchus branches from the trachea into the left lung.",
                function = "Conducts air into the left lung.",
                location = "At the tracheal split in the chest.",
                hotspotX = 0.570f,
                hotspotY = 0.640f,
                minZoom = 1.7f
            ),
            AnatomyPart(
                name = "Right Lung",
                icon = "🫁",
                overview = "The right lung is the larger lung and has three lobes.",
                function = "Performs gas exchange between air and blood.",
                location = "Right side of the chest.",
                hotspotX = 0.300f,
                hotspotY = 0.730f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Left Lung",
                icon = "🫁",
                overview = "The left lung has two lobes and leaves space for the heart.",
                function = "Performs gas exchange between air and blood.",
                location = "Left side of the chest.",
                hotspotX = 0.680f,
                hotspotY = 0.730f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Bronchioles",
                icon = "●",
                overview = "Bronchioles are small branching airways within the lungs.",
                function = "Distribute air toward the microscopic gas-exchange regions.",
                location = "Inside both lungs.",
                hotspotX = 0.630f,
                hotspotY = 0.700f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Diaphragm",
                icon = "〰",
                overview = "The diaphragm is a dome-shaped muscle beneath the lungs.",
                function = "Changes chest volume to help move air in and out.",
                location = "Between the chest and abdominal cavities.",
                hotspotX = 0.500f,
                hotspotY = 0.900f,
                minZoom = 1.0f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Circulatory",
        icon = "🫀",
        imageRes = R.drawable.anatomy_circulatory,
        imageAspectRatio = 1496f / 2048f,
        parts = listOf(
            AnatomyPart(
                name = "Heart",
                icon = "🫀",
                overview = "The heart is the muscular pump of the cardiovascular system.",
                function = "Maintains blood flow through the pulmonary and systemic circuits.",
                location = "Chest between the lungs.",
                hotspotX = 0.500f,
                hotspotY = 0.280f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Aorta",
                icon = "●",
                overview = "The aorta is the largest artery in the body.",
                function = "Carries oxygen-rich blood from the left ventricle to the systemic circulation.",
                location = "From the heart through the chest and abdomen.",
                hotspotX = 0.510f,
                hotspotY = 0.360f,
                minZoom = 1.3f
            ),
            AnatomyPart(
                name = "Superior Vena Cava",
                icon = "●",
                overview = "The superior vena cava is a large vein returning blood from the upper body.",
                function = "Delivers deoxygenated blood to the right atrium.",
                location = "Upper chest beside the ascending aorta.",
                hotspotX = 0.470f,
                hotspotY = 0.230f,
                minZoom = 1.7f
            ),
            AnatomyPart(
                name = "Inferior Vena Cava",
                icon = "●",
                overview = "The inferior vena cava returns blood from the lower body.",
                function = "Delivers deoxygenated blood to the right atrium.",
                location = "Abdomen and lower chest.",
                hotspotX = 0.470f,
                hotspotY = 0.390f,
                minZoom = 1.7f
            ),
            AnatomyPart(
                name = "Carotid Arteries",
                icon = "●",
                overview = "The carotid arteries are major vessels of the neck.",
                function = "Supply blood to the head and brain.",
                location = "Both sides of the neck.",
                hotspotX = 0.490f,
                hotspotY = 0.120f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Jugular Veins",
                icon = "●",
                overview = "The jugular veins are major veins of the neck.",
                function = "Drain blood from the head toward the heart.",
                location = "Both sides of the neck.",
                hotspotX = 0.530f,
                hotspotY = 0.130f,
                minZoom = 1.7f
            ),
            AnatomyPart(
                name = "Subclavian Vessels",
                icon = "●",
                overview = "Subclavian arteries and veins pass beneath the clavicles.",
                function = "Carry blood to and from the upper limbs.",
                location = "Upper chest near each shoulder.",
                hotspotX = 0.390f,
                hotspotY = 0.190f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Brachial Artery",
                icon = "●",
                overview = "The brachial artery is the main artery of the upper arm.",
                function = "Supplies the arm and continues toward the forearm.",
                location = "Upper arm.",
                hotspotX = 0.370f,
                hotspotY = 0.300f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Radial and Ulnar Vessels",
                icon = "●",
                overview = "Radial and ulnar vessels run through the forearm.",
                function = "Supply and drain the forearm and hand.",
                location = "Forearm.",
                hotspotX = 0.340f,
                hotspotY = 0.390f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Renal Vessels",
                icon = "●",
                overview = "Renal arteries and veins connect the kidneys to the main circulation.",
                function = "Deliver blood for filtration and return filtered blood.",
                location = "Upper abdomen.",
                hotspotX = 0.500f,
                hotspotY = 0.370f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Common Iliac Vessels",
                icon = "●",
                overview = "The common iliac vessels branch toward the pelvis and legs.",
                function = "Carry blood between the central circulation and lower limbs.",
                location = "Lower abdomen and pelvis.",
                hotspotX = 0.500f,
                hotspotY = 0.470f,
                minZoom = 1.4f
            ),
            AnatomyPart(
                name = "Femoral Vessels",
                icon = "●",
                overview = "Femoral vessels are major vessels of the thigh.",
                function = "Carry blood to and from the lower limb.",
                location = "Upper thigh.",
                hotspotX = 0.560f,
                hotspotY = 0.570f,
                minZoom = 1.3f
            ),
            AnatomyPart(
                name = "Popliteal Vessels",
                icon = "●",
                overview = "Popliteal vessels pass behind the knee.",
                function = "Carry blood between the thigh and lower leg.",
                location = "Knee region.",
                hotspotX = 0.550f,
                hotspotY = 0.720f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Anterior Tibial Vessels",
                icon = "●",
                overview = "Anterior tibial vessels run down the lower leg.",
                function = "Supply and drain the front of the lower leg and foot.",
                location = "Lower leg.",
                hotspotX = 0.550f,
                hotspotY = 0.820f,
                minZoom = 2.0f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Digestive",
        icon = "🍽",
        imageRes = R.drawable.anatomy_digestive,
        imageAspectRatio = 321f / 600f,
        parts = listOf(
            AnatomyPart(
                name = "Oral Cavity",
                icon = "●",
                overview = "Digestion begins in the mouth with chewing and saliva.",
                function = "Breaks food mechanically and mixes it with saliva.",
                location = "Mouth.",
                hotspotX = 0.310f,
                hotspotY = 0.170f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Salivary Glands",
                icon = "●",
                overview = "Salivary glands produce saliva.",
                function = "Moisten food and begin chemical digestion of some nutrients.",
                location = "Around the mouth and jaw.",
                hotspotX = 0.390f,
                hotspotY = 0.160f,
                minZoom = 1.7f
            ),
            AnatomyPart(
                name = "Pharynx",
                icon = "●",
                overview = "The pharynx is a muscular passage used during swallowing.",
                function = "Moves swallowed material toward the esophagus.",
                location = "Throat behind the mouth.",
                hotspotX = 0.380f,
                hotspotY = 0.230f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Esophagus",
                icon = "●",
                overview = "The esophagus is a muscular tube connecting the throat and stomach.",
                function = "Moves swallowed food to the stomach by peristalsis.",
                location = "Neck and chest.",
                hotspotX = 0.500f,
                hotspotY = 0.370f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Liver",
                icon = "●",
                overview = "The liver is a large metabolic organ.",
                function = "Produces bile and processes, stores, and detoxifies many substances.",
                location = "Upper right abdomen.",
                hotspotX = 0.370f,
                hotspotY = 0.520f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Gallbladder",
                icon = "●",
                overview = "The gallbladder is a small sac under the liver.",
                function = "Stores and concentrates bile before releasing it into the small intestine.",
                location = "Beneath the liver.",
                hotspotX = 0.390f,
                hotspotY = 0.570f,
                minZoom = 1.7f
            ),
            AnatomyPart(
                name = "Stomach",
                icon = "●",
                overview = "The stomach is a muscular sac that receives food from the esophagus.",
                function = "Mixes food with acid and enzymes to begin protein digestion.",
                location = "Upper left abdomen.",
                hotspotX = 0.610f,
                hotspotY = 0.530f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Pancreas",
                icon = "●",
                overview = "The pancreas is a gland behind the stomach.",
                function = "Releases digestive enzymes and bicarbonate into the small intestine.",
                location = "Upper abdomen behind the stomach.",
                hotspotX = 0.510f,
                hotspotY = 0.560f,
                minZoom = 1.3f
            ),
            AnatomyPart(
                name = "Duodenum",
                icon = "●",
                overview = "The duodenum is the first section of the small intestine.",
                function = "Receives stomach contents, bile, and pancreatic secretions.",
                location = "Upper abdomen just beyond the stomach.",
                hotspotX = 0.560f,
                hotspotY = 0.590f,
                minZoom = 1.9f
            ),
            AnatomyPart(
                name = "Small Intestine",
                icon = "●",
                overview = "The small intestine is a long coiled digestive tube.",
                function = "Completes much digestion and absorbs most nutrients.",
                location = "Central and lower abdomen.",
                hotspotX = 0.500f,
                hotspotY = 0.700f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Large Intestine",
                icon = "●",
                overview = "The large intestine frames much of the small intestine.",
                function = "Absorbs water and electrolytes and forms feces.",
                location = "Around the edges of the abdominal cavity.",
                hotspotX = 0.660f,
                hotspotY = 0.680f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Rectum",
                icon = "●",
                overview = "The rectum is the final straight section of the large intestine.",
                function = "Stores fecal material before elimination.",
                location = "Lower pelvis.",
                hotspotX = 0.500f,
                hotspotY = 0.830f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Anus",
                icon = "●",
                overview = "The anus is the terminal opening of the digestive tract.",
                function = "Controls the final passage of feces from the body.",
                location = "Inferior end of the digestive tract.",
                hotspotX = 0.500f,
                hotspotY = 0.880f,
                minZoom = 2.1f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Endocrine",
        icon = "⚗",
        imageRes = R.drawable.anatomy_endocrine,
        imageAspectRatio = 258f / 300f,
        parts = listOf(
            AnatomyPart(
                name = "Hypothalamus",
                icon = "●",
                overview = "The hypothalamus links the nervous and endocrine systems.",
                function = "Regulates pituitary activity and many homeostatic processes.",
                location = "Base of the brain.",
                hotspotX = 0.500f,
                hotspotY = 0.100f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Pituitary Gland",
                icon = "●",
                overview = "The pituitary is a small endocrine gland connected to the hypothalamus.",
                function = "Releases hormones that regulate growth, reproduction, water balance, and other glands.",
                location = "Base of the brain.",
                hotspotX = 0.500f,
                hotspotY = 0.130f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Pineal Gland",
                icon = "●",
                overview = "The pineal gland is a small endocrine structure in the brain.",
                function = "Produces melatonin involved in sleep-wake timing.",
                location = "Near the center of the brain.",
                hotspotX = 0.540f,
                hotspotY = 0.090f,
                minZoom = 2.4f
            ),
            AnatomyPart(
                name = "Thyroid Gland",
                icon = "●",
                overview = "The thyroid is a butterfly-shaped gland in the neck.",
                function = "Produces hormones that influence metabolism, growth, and development.",
                location = "Front of the neck.",
                hotspotX = 0.500f,
                hotspotY = 0.240f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Parathyroid Glands",
                icon = "●",
                overview = "Parathyroid glands are tiny glands associated with the thyroid.",
                function = "Help regulate calcium levels in the blood.",
                location = "On the posterior surface of the thyroid.",
                hotspotX = 0.530f,
                hotspotY = 0.240f,
                minZoom = 2.2f
            ),
            AnatomyPart(
                name = "Thymus",
                icon = "●",
                overview = "The thymus is an immune and endocrine-related organ.",
                function = "Supports maturation of T lymphocytes, especially earlier in life.",
                location = "Upper chest behind the sternum.",
                hotspotX = 0.500f,
                hotspotY = 0.330f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Adrenal Glands",
                icon = "●",
                overview = "The adrenal glands sit on top of the kidneys.",
                function = "Produce hormones involved in stress responses, blood pressure, and salt balance.",
                location = "Upper abdomen above each kidney.",
                hotspotX = 0.400f,
                hotspotY = 0.520f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Pancreas",
                icon = "●",
                overview = "The endocrine pancreas contains hormone-producing islets.",
                function = "Produces insulin and glucagon to help regulate blood glucose.",
                location = "Upper abdomen behind the stomach.",
                hotspotX = 0.570f,
                hotspotY = 0.530f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Testes",
                icon = "●",
                overview = "The testes are male reproductive glands with endocrine functions.",
                function = "Produce testosterone and support sperm production.",
                location = "Scrotum.",
                hotspotX = 0.500f,
                hotspotY = 0.780f,
                minZoom = 1.4f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Urinary",
        icon = "💧",
        imageRes = R.drawable.anatomy_urinary,
        imageAspectRatio = 379f / 527f,
        parts = listOf(
            AnatomyPart(
                name = "Right Kidney",
                icon = "●",
                overview = "The right kidney is a bean-shaped filtering organ.",
                function = "Filters blood, regulates fluid and electrolytes, and helps form urine.",
                location = "Upper posterior abdomen on the right.",
                hotspotX = 0.330f,
                hotspotY = 0.380f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Left Kidney",
                icon = "●",
                overview = "The left kidney is a bean-shaped filtering organ.",
                function = "Filters blood, regulates fluid and electrolytes, and helps form urine.",
                location = "Upper posterior abdomen on the left.",
                hotspotX = 0.640f,
                hotspotY = 0.360f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Renal Cortex",
                icon = "●",
                overview = "The renal cortex is the outer region of the kidney.",
                function = "Contains structures involved in blood filtration and early urine formation.",
                location = "Outer part of each kidney.",
                hotspotX = 0.700f,
                hotspotY = 0.340f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Renal Medulla",
                icon = "●",
                overview = "The renal medulla is the inner kidney region organized into pyramids.",
                function = "Concentrates urine and carries it toward the renal pelvis.",
                location = "Inner part of each kidney.",
                hotspotX = 0.620f,
                hotspotY = 0.370f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Renal Pelvis",
                icon = "●",
                overview = "The renal pelvis is the funnel-shaped collecting region of the kidney.",
                function = "Collects urine and directs it into the ureter.",
                location = "Central inner kidney.",
                hotspotX = 0.600f,
                hotspotY = 0.400f,
                minZoom = 2.2f
            ),
            AnatomyPart(
                name = "Ureters",
                icon = "●",
                overview = "The ureters are narrow muscular tubes from the kidneys to the bladder.",
                function = "Move urine to the urinary bladder.",
                location = "Posterior abdomen and pelvis.",
                hotspotX = 0.500f,
                hotspotY = 0.580f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Urinary Bladder",
                icon = "●",
                overview = "The urinary bladder is a hollow muscular organ.",
                function = "Temporarily stores urine before urination.",
                location = "Pelvic cavity.",
                hotspotX = 0.500f,
                hotspotY = 0.780f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Urethra",
                icon = "●",
                overview = "The urethra is the tube that carries urine out of the bladder.",
                function = "Provides the final passage for urine leaving the body.",
                location = "From the bladder to the exterior.",
                hotspotX = 0.500f,
                hotspotY = 0.860f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Renal Artery",
                icon = "●",
                overview = "The renal artery carries blood into the kidney.",
                function = "Supplies blood that will be filtered by the kidney.",
                location = "At the kidney hilum.",
                hotspotX = 0.550f,
                hotspotY = 0.380f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Renal Vein",
                icon = "●",
                overview = "The renal vein carries blood away from the kidney.",
                function = "Returns filtered blood to the inferior vena cava.",
                location = "At the kidney hilum.",
                hotspotX = 0.570f,
                hotspotY = 0.360f,
                minZoom = 1.8f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Nervous",
        icon = "🧠",
        imageRes = R.drawable.anatomy_nervous,
        imageAspectRatio = 137f / 300f,
        parts = listOf(
            AnatomyPart(
                name = "Brain",
                icon = "🧠",
                overview = "The brain is the main control center of the nervous system.",
                function = "Processes information and coordinates movement, thought, senses, memory, and many automatic functions.",
                location = "Inside the skull.",
                hotspotX = 0.500f,
                hotspotY = 0.080f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Spinal Cord",
                icon = "●",
                overview = "The spinal cord is a long bundle of nervous tissue connected to the brain.",
                function = "Carries signals between the brain and body and participates in reflexes.",
                location = "Inside the vertebral column.",
                hotspotX = 0.500f,
                hotspotY = 0.350f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Cervical Nerves",
                icon = "●",
                overview = "Cervical spinal nerves emerge from the neck region.",
                function = "Carry sensory and motor signals to the neck, shoulders, and parts of the upper limbs.",
                location = "Neck.",
                hotspotX = 0.530f,
                hotspotY = 0.180f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Brachial Plexus",
                icon = "●",
                overview = "The brachial plexus is a network of nerves supplying the upper limb.",
                function = "Distributes motor and sensory fibers to the shoulder, arm, forearm, and hand.",
                location = "Lower neck and shoulder region.",
                hotspotX = 0.400f,
                hotspotY = 0.270f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Upper-Limb Nerves",
                icon = "●",
                overview = "Major peripheral nerves travel down each arm.",
                function = "Carry motor and sensory signals between the spinal cord and upper limbs.",
                location = "Arms and hands.",
                hotspotX = 0.300f,
                hotspotY = 0.450f,
                minZoom = 1.3f
            ),
            AnatomyPart(
                name = "Lumbar Plexus",
                icon = "●",
                overview = "The lumbar plexus is a network of nerves in the lower trunk.",
                function = "Supplies parts of the abdomen, pelvis, and lower limb.",
                location = "Lumbar region.",
                hotspotX = 0.490f,
                hotspotY = 0.490f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Sacral Plexus",
                icon = "●",
                overview = "The sacral plexus is a nerve network in the pelvis.",
                function = "Gives rise to major nerves of the pelvis and lower limbs.",
                location = "Pelvic region.",
                hotspotX = 0.500f,
                hotspotY = 0.570f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Sciatic Nerve",
                icon = "●",
                overview = "The sciatic nerve is the largest single nerve in the body.",
                function = "Carries motor and sensory fibers to much of the lower limb.",
                location = "From the pelvis through the posterior thigh.",
                hotspotX = 0.570f,
                hotspotY = 0.660f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Lower-Limb Nerves",
                icon = "●",
                overview = "Peripheral nerves branch throughout the legs and feet.",
                function = "Carry sensory information and motor commands in the lower limbs.",
                location = "Legs and feet.",
                hotspotX = 0.550f,
                hotspotY = 0.800f,
                minZoom = 1.3f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Skeletal",
        icon = "🦴",
        imageRes = R.drawable.anatomy_skeletal,
        imageAspectRatio = 350f / 830f,
        parts = listOf(
            AnatomyPart(
                name = "Cranium",
                icon = "💀",
                overview = "The cranium forms the upper part of the skull.",
                function = "Encloses and protects the brain.",
                location = "Head.",
                hotspotX = 0.500f,
                hotspotY = 0.045f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Nasal Bone",
                icon = "●",
                overview = "The paired nasal bones form the bridge of the nose.",
                function = "Support the upper external nose.",
                location = "Central face.",
                hotspotX = 0.500f,
                hotspotY = 0.071f,
                minZoom = 3.0f
            ),
            AnatomyPart(
                name = "Zygomatic Bone",
                icon = "●",
                overview = "The zygomatic bone forms the prominence of the cheek.",
                function = "Contributes to the orbit and cheek contour.",
                location = "Lateral face.",
                hotspotX = 0.460f,
                hotspotY = 0.078f,
                minZoom = 2.7f
            ),
            AnatomyPart(
                name = "Maxilla",
                icon = "●",
                overview = "The maxilla is the upper jaw bone.",
                function = "Supports the upper teeth and contributes to the nose, palate, and orbit.",
                location = "Upper jaw and central face.",
                hotspotX = 0.500f,
                hotspotY = 0.086f,
                minZoom = 2.8f
            ),
            AnatomyPart(
                name = "Mandible",
                icon = "●",
                overview = "The mandible is the lower jaw bone.",
                function = "Supports the lower teeth and moves at the jaw joint for chewing and speech.",
                location = "Lower face.",
                hotspotX = 0.500f,
                hotspotY = 0.102f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Cervical Vertebrae",
                icon = "●",
                overview = "The cervical vertebrae are the seven vertebrae of the neck.",
                function = "Support the head and protect the upper spinal cord.",
                location = "Neck.",
                hotspotX = 0.500f,
                hotspotY = 0.135f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Clavicle",
                icon = "●",
                overview = "The clavicle is the collarbone.",
                function = "Connects the upper limb to the trunk and helps brace the shoulder.",
                location = "Upper chest between sternum and shoulder.",
                hotspotX = 0.420f,
                hotspotY = 0.158f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Scapula",
                icon = "●",
                overview = "The scapula is the shoulder blade.",
                function = "Provides attachment sites for muscles and forms part of the shoulder joint.",
                location = "Upper back behind the rib cage.",
                hotspotX = 0.340f,
                hotspotY = 0.190f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Sternum",
                icon = "●",
                overview = "The sternum is the flat breastbone at the center of the chest.",
                function = "Protects thoracic organs and anchors ribs and clavicles.",
                location = "Midline of the chest.",
                hotspotX = 0.500f,
                hotspotY = 0.215f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Ribs",
                icon = "●",
                overview = "The ribs are curved bones forming most of the rib cage.",
                function = "Protect the heart and lungs and assist chest movement during breathing.",
                location = "Chest.",
                hotspotX = 0.400f,
                hotspotY = 0.235f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Thoracic Vertebrae",
                icon = "●",
                overview = "Thoracic vertebrae form the middle section of the vertebral column.",
                function = "Support the trunk, protect the spinal cord, and articulate with ribs.",
                location = "Upper and middle back.",
                hotspotX = 0.500f,
                hotspotY = 0.255f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Humerus",
                icon = "●",
                overview = "The humerus is the long bone of the upper arm.",
                function = "Forms the shoulder and elbow joints and acts as a lever for arm movement.",
                location = "Upper arm.",
                hotspotX = 0.260f,
                hotspotY = 0.290f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Lumbar Vertebrae",
                icon = "●",
                overview = "Lumbar vertebrae are the large vertebrae of the lower back.",
                function = "Bear substantial body weight and permit trunk movement.",
                location = "Lower back.",
                hotspotX = 0.500f,
                hotspotY = 0.355f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Radius",
                icon = "●",
                overview = "The radius is the forearm bone on the thumb side.",
                function = "Participates in elbow and wrist movement and forearm rotation.",
                location = "Forearm.",
                hotspotX = 0.220f,
                hotspotY = 0.390f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Ulna",
                icon = "●",
                overview = "The ulna is the forearm bone on the little-finger side.",
                function = "Forms a major part of the elbow joint and stabilizes the forearm.",
                location = "Forearm.",
                hotspotX = 0.270f,
                hotspotY = 0.390f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Carpals",
                icon = "●",
                overview = "The carpals are the small bones of the wrist.",
                function = "Provide flexible connections between the forearm and hand.",
                location = "Wrist.",
                hotspotX = 0.180f,
                hotspotY = 0.470f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Metacarpals",
                icon = "●",
                overview = "The metacarpals form the bony framework of the palm.",
                function = "Support the hand and connect the wrist to the fingers.",
                location = "Palm.",
                hotspotX = 0.150f,
                hotspotY = 0.500f,
                minZoom = 2.3f
            ),
            AnatomyPart(
                name = "Hand Phalanges",
                icon = "●",
                overview = "Phalanges are the bones of the fingers.",
                function = "Support finger movement for grip and fine motor tasks.",
                location = "Fingers.",
                hotspotX = 0.120f,
                hotspotY = 0.540f,
                minZoom = 2.6f
            ),
            AnatomyPart(
                name = "Ilium",
                icon = "●",
                overview = "The ilium is the broad upper part of each hip bone.",
                function = "Transfers body weight and provides large muscle attachment areas.",
                location = "Upper pelvis.",
                hotspotX = 0.390f,
                hotspotY = 0.430f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Sacrum",
                icon = "●",
                overview = "The sacrum is a triangular bone formed from fused vertebrae.",
                function = "Connects the vertebral column to the pelvis and transmits body weight.",
                location = "Back of the pelvis.",
                hotspotX = 0.500f,
                hotspotY = 0.455f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Pubis",
                icon = "●",
                overview = "The pubis forms the anterior part of each hip bone.",
                function = "Contributes to the pelvic ring and pubic symphysis.",
                location = "Front of the pelvis.",
                hotspotX = 0.470f,
                hotspotY = 0.490f,
                minZoom = 1.9f
            ),
            AnatomyPart(
                name = "Ischium",
                icon = "●",
                overview = "The ischium forms the lower posterior part of each hip bone.",
                function = "Bears weight during sitting and provides muscle attachments.",
                location = "Lower pelvis.",
                hotspotX = 0.400f,
                hotspotY = 0.500f,
                minZoom = 1.9f
            ),
            AnatomyPart(
                name = "Femur",
                icon = "●",
                overview = "The femur is the long bone of the thigh and the longest bone in the body.",
                function = "Supports body weight and forms the hip and knee joints.",
                location = "Thigh.",
                hotspotX = 0.420f,
                hotspotY = 0.640f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Patella",
                icon = "●",
                overview = "The patella is the kneecap.",
                function = "Protects the front of the knee and improves leverage of the quadriceps tendon.",
                location = "Front of the knee.",
                hotspotX = 0.420f,
                hotspotY = 0.740f,
                minZoom = 1.3f
            ),
            AnatomyPart(
                name = "Tibia",
                icon = "●",
                overview = "The tibia is the larger, medial bone of the lower leg.",
                function = "Bears most lower-leg weight and forms parts of the knee and ankle.",
                location = "Lower leg.",
                hotspotX = 0.430f,
                hotspotY = 0.820f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Fibula",
                icon = "●",
                overview = "The fibula is the slender lateral bone of the lower leg.",
                function = "Stabilizes the ankle and provides muscle attachment sites.",
                location = "Outer lower leg.",
                hotspotX = 0.370f,
                hotspotY = 0.820f,
                minZoom = 1.4f
            ),
            AnatomyPart(
                name = "Tarsals",
                icon = "●",
                overview = "Tarsals are the bones of the ankle and rear foot.",
                function = "Support weight and allow complex foot and ankle movement.",
                location = "Ankle and hindfoot.",
                hotspotX = 0.420f,
                hotspotY = 0.930f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Metatarsals",
                icon = "●",
                overview = "Metatarsals are the long bones of the midfoot.",
                function = "Help support the arches and transmit forces during walking.",
                location = "Midfoot.",
                hotspotX = 0.420f,
                hotspotY = 0.965f,
                minZoom = 2.4f
            ),
            AnatomyPart(
                name = "Foot Phalanges",
                icon = "●",
                overview = "Phalanges form the toes.",
                function = "Assist balance and push-off during walking and running.",
                location = "Toes.",
                hotspotX = 0.420f,
                hotspotY = 0.985f,
                minZoom = 2.7f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Muscular",
        icon = "💪",
        imageRes = R.drawable.anatomy_muscular,
        imageAspectRatio = 1f,
        parts = listOf(
            AnatomyPart(
                name = "Frontalis",
                icon = "●",
                overview = "The frontalis is a muscle of facial expression over the forehead.",
                function = "Raises the eyebrows and wrinkles the forehead.",
                location = "Front of the scalp.",
                hotspotX = 0.270f,
                hotspotY = 0.080f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Deltoid",
                icon = "●",
                overview = "The deltoid forms the rounded contour of the shoulder.",
                function = "Abducts the arm and assists several shoulder movements.",
                location = "Outer shoulder.",
                hotspotX = 0.200f,
                hotspotY = 0.200f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Pectoralis Major",
                icon = "●",
                overview = "The pectoralis major is a broad chest muscle.",
                function = "Moves the arm across the body and assists shoulder flexion and rotation.",
                location = "Front of the chest.",
                hotspotX = 0.290f,
                hotspotY = 0.240f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Biceps Brachii",
                icon = "●",
                overview = "The biceps brachii lies on the front of the upper arm.",
                function = "Flexes the elbow and helps supinate the forearm.",
                location = "Front of the upper arm.",
                hotspotX = 0.140f,
                hotspotY = 0.310f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Forearm Flexors",
                icon = "●",
                overview = "Forearm flexor muscles form much of the front inner forearm.",
                function = "Flex the wrist and fingers and assist grip.",
                location = "Anterior forearm.",
                hotspotX = 0.120f,
                hotspotY = 0.420f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Rectus Abdominis",
                icon = "●",
                overview = "The rectus abdominis runs vertically along the front of the abdomen.",
                function = "Flexes the trunk and helps stabilize the abdominal wall.",
                location = "Front of the abdomen.",
                hotspotX = 0.280f,
                hotspotY = 0.380f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "External Oblique",
                icon = "●",
                overview = "The external oblique is a broad muscle on each side of the abdomen.",
                function = "Rotates and bends the trunk and compresses abdominal contents.",
                location = "Lateral abdomen.",
                hotspotX = 0.200f,
                hotspotY = 0.420f,
                minZoom = 1.4f
            ),
            AnatomyPart(
                name = "Sartorius",
                icon = "●",
                overview = "The sartorius is a long strap-like muscle crossing the front of the thigh.",
                function = "Assists flexion and rotation at the hip and flexion at the knee.",
                location = "Anterior thigh.",
                hotspotX = 0.260f,
                hotspotY = 0.580f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Quadriceps",
                icon = "●",
                overview = "The quadriceps are four major muscles on the front of the thigh.",
                function = "Extend the knee and contribute to standing, walking, running, and jumping.",
                location = "Front of the thigh.",
                hotspotX = 0.240f,
                hotspotY = 0.660f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Tibialis Anterior",
                icon = "●",
                overview = "The tibialis anterior lies along the front of the shin.",
                function = "Dorsiflexes and inverts the foot.",
                location = "Front of the lower leg.",
                hotspotX = 0.230f,
                hotspotY = 0.820f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Trapezius",
                icon = "●",
                overview = "The trapezius is a broad superficial muscle of the upper back and neck.",
                function = "Moves and stabilizes the shoulder blades and assists neck movement.",
                location = "Upper back and posterior neck.",
                hotspotX = 0.720f,
                hotspotY = 0.200f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Triceps Brachii",
                icon = "●",
                overview = "The triceps lies on the back of the upper arm.",
                function = "Extends the elbow.",
                location = "Posterior upper arm.",
                hotspotX = 0.840f,
                hotspotY = 0.310f,
                minZoom = 1.3f
            ),
            AnatomyPart(
                name = "Latissimus Dorsi",
                icon = "●",
                overview = "The latissimus dorsi is a broad muscle of the lower back.",
                function = "Extends, adducts, and internally rotates the arm.",
                location = "Mid and lower back.",
                hotspotX = 0.720f,
                hotspotY = 0.370f,
                minZoom = 1.2f
            ),
            AnatomyPart(
                name = "Gluteus Maximus",
                icon = "●",
                overview = "The gluteus maximus is the large superficial muscle of the buttock.",
                function = "Extends and externally rotates the hip and helps maintain upright posture.",
                location = "Buttock.",
                hotspotX = 0.720f,
                hotspotY = 0.520f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Hamstrings",
                icon = "●",
                overview = "The hamstrings are a group of muscles on the back of the thigh.",
                function = "Flex the knee and extend the hip.",
                location = "Posterior thigh.",
                hotspotX = 0.720f,
                hotspotY = 0.670f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Gastrocnemius",
                icon = "●",
                overview = "The gastrocnemius is the prominent calf muscle.",
                function = "Plantar-flexes the ankle and assists knee flexion.",
                location = "Posterior lower leg.",
                hotspotX = 0.720f,
                hotspotY = 0.810f,
                minZoom = 1.0f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Lymphatic",
        icon = "🛡",
        imageRes = R.drawable.anatomy_lymphatic,
        imageAspectRatio = 256f / 500f,
        parts = listOf(
            AnatomyPart(
                name = "Tonsils",
                icon = "●",
                overview = "Tonsillar lymphatic tissue helps sample material entering through the mouth and nose.",
                function = "Participates in immune defense at the entrance to the respiratory and digestive tracts.",
                location = "Throat region.",
                hotspotX = 0.500f,
                hotspotY = 0.120f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Cervical Lymph Nodes",
                icon = "●",
                overview = "Cervical lymph nodes are clusters of lymph nodes in the neck.",
                function = "Filter lymph from the head and neck and support immune responses.",
                location = "Neck.",
                hotspotX = 0.500f,
                hotspotY = 0.200f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Axillary Lymph Nodes",
                icon = "●",
                overview = "Axillary lymph nodes are located in the armpits.",
                function = "Filter lymph from much of the upper limb, chest wall, and breast region.",
                location = "Armpits.",
                hotspotX = 0.380f,
                hotspotY = 0.290f,
                minZoom = 1.3f
            ),
            AnatomyPart(
                name = "Thymus",
                icon = "●",
                overview = "The thymus is a lymphoid organ that is especially active in younger people.",
                function = "Supports maturation of T lymphocytes.",
                location = "Upper chest behind the sternum.",
                hotspotX = 0.500f,
                hotspotY = 0.310f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Thoracic Duct",
                icon = "●",
                overview = "The thoracic duct is the largest lymphatic vessel.",
                function = "Returns lymph from most of the body to the venous circulation.",
                location = "Runs upward through the trunk.",
                hotspotX = 0.500f,
                hotspotY = 0.430f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Inguinal Lymph Nodes",
                icon = "●",
                overview = "Inguinal lymph nodes are clusters in the groin.",
                function = "Filter lymph from much of the lower limb and lower trunk.",
                location = "Groin.",
                hotspotX = 0.500f,
                hotspotY = 0.550f,
                minZoom = 1.2f
            ),
            AnatomyPart(
                name = "Lymphatic Vessels",
                icon = "●",
                overview = "Lymphatic vessels form a network that transports lymph.",
                function = "Return excess tissue fluid to the bloodstream and transport immune cells.",
                location = "Distributed throughout the body.",
                hotspotX = 0.570f,
                hotspotY = 0.620f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Popliteal Lymph Nodes",
                icon = "●",
                overview = "Popliteal nodes are small lymph nodes behind the knee.",
                function = "Filter lymph from parts of the lower leg and foot.",
                location = "Behind the knee.",
                hotspotX = 0.470f,
                hotspotY = 0.750f,
                minZoom = 1.8f
            )
        )
    ),
    AnatomySystemLayer(
        name = "Integumentary",
        icon = "🖐",
        imageRes = R.drawable.anatomy_integumentary,
        imageAspectRatio = 300f / 534f,
        parts = listOf(
            AnatomyPart(
                name = "Hair Shaft",
                icon = "●",
                overview = "The hair shaft is the visible portion of a hair above the skin surface.",
                function = "Provides minor protection and contributes to sensation and temperature regulation.",
                location = "Above the epidermis.",
                hotspotX = 0.390f,
                hotspotY = 0.100f,
                minZoom = 1.6f
            ),
            AnatomyPart(
                name = "Epidermis",
                icon = "●",
                overview = "The epidermis is the outermost layer of the skin.",
                function = "Forms a protective barrier and helps limit water loss.",
                location = "Outer surface of the skin.",
                hotspotX = 0.570f,
                hotspotY = 0.200f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Dermis",
                icon = "●",
                overview = "The dermis is the connective-tissue layer beneath the epidermis.",
                function = "Provides strength and houses vessels, nerves, follicles, and glands.",
                location = "Below the epidermis.",
                hotspotX = 0.550f,
                hotspotY = 0.370f,
                minZoom = 1.2f
            ),
            AnatomyPart(
                name = "Sweat Pore",
                icon = "●",
                overview = "A sweat pore is the opening of a sweat duct at the skin surface.",
                function = "Allows sweat to reach the surface for cooling.",
                location = "Epidermal surface.",
                hotspotX = 0.770f,
                hotspotY = 0.280f,
                minZoom = 2.0f
            ),
            AnatomyPart(
                name = "Hair Follicle",
                icon = "●",
                overview = "A hair follicle is the tubular skin structure from which hair grows.",
                function = "Anchors and supports the growing hair.",
                location = "Extends from the surface into the dermis.",
                hotspotX = 0.360f,
                hotspotY = 0.500f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Sebaceous Gland",
                icon = "●",
                overview = "Sebaceous glands are oil-producing glands commonly connected to hair follicles.",
                function = "Release sebum that lubricates the skin and hair.",
                location = "Dermis beside hair follicles.",
                hotspotX = 0.400f,
                hotspotY = 0.420f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Arrector Pili Muscle",
                icon = "●",
                overview = "The arrector pili is a tiny smooth muscle attached to a hair follicle.",
                function = "Raises the hair and contributes to goosebumps.",
                location = "Dermis beside a hair follicle.",
                hotspotX = 0.470f,
                hotspotY = 0.460f,
                minZoom = 1.8f
            ),
            AnatomyPart(
                name = "Sweat Gland",
                icon = "●",
                overview = "Sweat glands are coiled tubular glands in the skin.",
                function = "Produce sweat for temperature regulation.",
                location = "Deep dermis and upper subcutaneous tissue.",
                hotspotX = 0.700f,
                hotspotY = 0.670f,
                minZoom = 1.0f
            ),
            AnatomyPart(
                name = "Sensory Nerve",
                icon = "●",
                overview = "Cutaneous sensory nerves detect touch, pressure, pain, and temperature.",
                function = "Carry sensory signals from skin receptors toward the nervous system.",
                location = "Dermis and subcutaneous tissue.",
                hotspotX = 0.440f,
                hotspotY = 0.620f,
                minZoom = 1.7f
            ),
            AnatomyPart(
                name = "Blood Vessels",
                icon = "●",
                overview = "Skin blood vessels supply tissues and help regulate heat loss.",
                function = "Deliver oxygen and nutrients and adjust blood flow for thermoregulation.",
                location = "Dermis and subcutaneous tissue.",
                hotspotX = 0.300f,
                hotspotY = 0.760f,
                minZoom = 1.5f
            ),
            AnatomyPart(
                name = "Subcutaneous Tissue",
                icon = "●",
                overview = "Subcutaneous tissue contains fat and connective tissue beneath the skin.",
                function = "Provides cushioning, insulation, energy storage, and anchoring.",
                location = "Below the dermis.",
                hotspotX = 0.530f,
                hotspotY = 0.800f,
                minZoom = 1.0f
            )
        )
    )
)

@Composable
private fun AnatomyModelScreen(
    grade: Int,
    is3D: Boolean = false,
    onBack: () -> Unit
) {

    var selectedSystemIndex by remember {
        mutableIntStateOf(0)
    }

    var selectedPart by remember {
        mutableStateOf<AnatomyPart?>(null)
    }

    val selectedSystem = anatomySystemLayers[selectedSystemIndex]

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DarkBlue,
                            DeepBlue
                        )
                    )
                )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            ScreenHeader(
                if (is3D) "3D ANATOMY LAYERS" else "2D ANATOMY LAYERS",
                onBack
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "GRADE $grade",
                color = Gold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = if (is3D) {
                    "Choose an organ system layer, then tap a body part to reveal its information."
                } else {
                    "Choose an organ system. Pinch to zoom, drag to pan, and tap glowing markers. Smaller structures appear as you zoom in."
                },
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "ORGAN SYSTEM LAYERS",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            anatomySystemLayers.chunked(2).forEach { rowSystems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowSystems.forEach { system ->
                        val index = anatomySystemLayers.indexOf(system)
                        SystemLayerButton(
                            text = "${system.icon} ${system.name}",
                            selected = index == selectedSystemIndex,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedSystemIndex = index
                                selectedPart = null
                            }
                        )
                    }

                    if (rowSystems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(24.dp)
                        )
                        .background(
                            Color(0xFF0B3555)
                        )
                        .padding(14.dp)
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "${selectedSystem.icon} ${selectedSystem.name.uppercase()} SYSTEM",
                        color = Gold,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    if (is3D) {
                        Text(
                            text = "3D model files have not been added yet. Use the buttons below to preview the information behavior.",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        selectedSystem.parts.forEach { part ->
                            ModelButton(
                                text = "${part.icon} ${part.name.uppercase()}",
                                onClick = {
                                    selectedPart = part
                                }
                            )
                        }
                    } else {
                        AnatomyImageWithHotspots(
                            system = selectedSystem,
                            selectedPart = selectedPart,
                            onPartSelected = {
                                selectedPart = it
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text = if (selectedPart == null) {
                                "Tap a glowing circle to identify a body part. Zoom in to reveal more detailed structures."
                            } else {
                                "Selected: ${selectedPart!!.name}"
                            },
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = if (selectedPart == null) FontWeight.Normal else FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (selectedPart != null) {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                AnatomyPartInfoCard(
                    systemName = selectedSystem.name,
                    part = selectedPart!!,
                    onClose = {
                        selectedPart = null
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@Composable
private fun AnatomyImageWithHotspots(
    system: AnatomySystemLayer,
    selectedPart: AnatomyPart?,
    onPartSelected: (AnatomyPart) -> Unit
) {
    var zoom by remember(system.name) { mutableFloatStateOf(1f) }
    var pan by remember(system.name) { mutableStateOf(Offset.Zero) }
    var viewportSize by remember(system.name) { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }

    fun clampPan(candidate: Offset, scale: Float, widthPx: Float, heightPx: Float): Offset {
        val maxX = (widthPx * (scale - 1f) / 2f).coerceAtLeast(0f)
        val maxY = (heightPx * (scale - 1f) / 2f).coerceAtLeast(0f)
        return Offset(
            x = candidate.x.coerceIn(-maxX, maxX),
            y = candidate.y.coerceIn(-maxY, maxY)
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(system.imageAspectRatio)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .onSizeChanged { viewportSize = it }
                    .pointerInput(system.name) {
                        detectTransformGestures { centroid, panChange, zoomChange, _ ->
                            val oldZoom = zoom
                            val newZoom = (oldZoom * zoomChange).coerceIn(1f, 5f)
                            val widthPx = size.width.toFloat()
                            val heightPx = size.height.toFloat()

                            val adjustedPan = if (newZoom <= 1.001f) {
                                Offset.Zero
                            } else {
                                val scaleCorrection = if (oldZoom > 0f) newZoom / oldZoom else 1f
                                val center = Offset(widthPx / 2f, heightPx / 2f)
                                Offset(
                                    x = pan.x * scaleCorrection +
                                            (centroid.x - center.x) * (1f - scaleCorrection) +
                                            panChange.x,
                                    y = pan.y * scaleCorrection +
                                            (centroid.y - center.y) * (1f - scaleCorrection) +
                                            panChange.y
                                )
                            }

                            zoom = newZoom
                            pan = clampPan(adjustedPan, newZoom, widthPx, heightPx)
                        }
                    }
        ) {
            val widthPx = constraints.maxWidth.toFloat()
            val heightPx = constraints.maxHeight.toFloat()
            val centerX = widthPx / 2f
            val centerY = heightPx / 2f

            Image(
                painter = painterResource(id = system.imageRes),
                contentDescription = "${system.name} system anatomy diagram",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = zoom
                            scaleY = zoom
                            translationX = pan.x
                            translationY = pan.y
                            transformOrigin = TransformOrigin.Center
                        },
                contentScale = ContentScale.Fit
            )

            // Markers become smaller as the anatomy is magnified so dense details stay selectable.
            // The hit target remains larger than the visible dot to preserve comfortable tapping.
            val glowSize = (31f / zoom.pow(0.42f)).coerceIn(15f, 31f).dp
            val coreSize = (17f / zoom.pow(0.34f)).coerceIn(9f, 17f).dp
            val hitSize = (42f / zoom.pow(0.35f)).coerceIn(26f, 42f).dp
            val hitSizePx = with(androidx.compose.ui.platform.LocalDensity.current) { hitSize.toPx() }

            system.parts
                .filter { zoom + 0.02f >= it.minZoom }
                .forEach { part ->
                    val baseX = widthPx * part.hotspotX
                    val baseY = heightPx * part.hotspotY
                    val markerCenterX = centerX + (baseX - centerX) * zoom + pan.x
                    val markerCenterY = centerY + (baseY - centerY) * zoom + pan.y
                    val isSelected = selectedPart == part
                    val glowColor = if (isSelected) Orange else Color(0xFF00D9FF)
                    val coreColor = if (isSelected) Color(0xFFFFB300) else Color(0xFF008FC7)

                    Box(
                        modifier =
                            Modifier
                                .offset {
                                    IntOffset(
                                        x = (markerCenterX - hitSizePx / 2f).roundToInt(),
                                        y = (markerCenterY - hitSizePx / 2f).roundToInt()
                                    )
                                }
                                .size(hitSize)
                                .clickable { onPartSelected(part) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(glowSize)
                                    .clip(CircleShape)
                                    .background(glowColor.copy(alpha = 0.30f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(coreSize)
                                        .clip(CircleShape)
                                        .background(coreColor)
                                        .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            )
                        }
                    }
                }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val newZoom = (zoom - 0.5f).coerceAtLeast(1f)
                    zoom = newZoom
                    pan = if (newZoom == 1f) {
                        Offset.Zero
                    } else {
                        clampPan(
                            pan,
                            newZoom,
                            viewportSize.width.toFloat(),
                            viewportSize.height.toFloat()
                        )
                    }
                },
                modifier = Modifier.weight(1f).height(46.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF123D5F))
            ) {
                Text("−", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    zoom = 1f
                    pan = Offset.Zero
                },
                modifier = Modifier.weight(1.7f).height(46.dp),
                contentPadding = PaddingValues(horizontal = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF123D5F))
            ) {
                Text("${(zoom * 100).roundToInt()}% • RESET", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val newZoom = (zoom + 0.5f).coerceAtMost(5f)
                    zoom = newZoom
                    pan = clampPan(
                        pan,
                        newZoom,
                        viewportSize.width.toFloat(),
                        viewportSize.height.toFloat()
                    )
                },
                modifier = Modifier.weight(1f).height(46.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF123D5F))
            ) {
                Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val visibleCount = system.parts.count { zoom + 0.02f >= it.minZoom }
        Text(
            text = "Pinch to zoom • drag to pan • ${visibleCount}/${system.parts.size} markers visible",
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        if (visibleCount < system.parts.size) {
            val nextDetailZoom = system.parts
                .map { it.minZoom }
                .filter { it > zoom + 0.02f }
                .minOrNull()

            Text(
                text = if (nextDetailZoom != null) {
                    "Zoom to ${(nextDetailZoom * 100).roundToInt()}% to reveal more detailed structures."
                } else {
                    "Zoom in to reveal smaller anatomical structures."
                },
                color = Gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SystemLayerButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier =
            modifier.heightIn(min = 55.dp),
        contentPadding =
            PaddingValues(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    if (selected) Orange
                    else Color(0xFF123D5F),
                contentColor =
                    if (selected) Color.Black
                    else Color.White
            ),
        shape =
            RoundedCornerShape(15.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AnatomyPartInfoCard(
    systemName: String,
    part: AnatomyPart,
    onClose: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(22.dp)
                )
                .background(Color.White)
                .padding(20.dp)
    ) {
        Text(
            text = "${part.icon} ${part.name}",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "$systemName System",
            color = Color(0xFF315A78),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        InfoSection(
            title = "Overview",
            text = part.overview
        )

        InfoSection(
            title = "Function",
            text = part.function
        )

        InfoSection(
            title = "Location",
            text = part.location
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = DeepBlue,
                    contentColor = Color.White
                ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "CLOSE INFO",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    text: String
) {
    Text(
        text = title.uppercase(),
        color = Color(0xFF1C4C70),
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold
    )

    Spacer(
        modifier = Modifier.height(3.dp)
    )

    Text(
        text = text,
        color = Color.Black,
        fontSize = 16.sp
    )

    Spacer(
        modifier = Modifier.height(14.dp)
    )
}

@Composable
private fun ModelButton(
    text: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 55.dp)
                .padding(
                    vertical = 3.dp
                ),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
        shape =
            RoundedCornerShape(15.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
