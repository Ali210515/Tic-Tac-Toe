package com.example.tictactoa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoa.database.GameDatabase
import com.example.tictactoa.screens.ComputerGameScreen
import com.example.tictactoa.screens.ComputerNameScreen
import com.example.tictactoa.screens.DifficultyScreen
import com.example.tictactoa.screens.GameScreen
import com.example.tictactoa.screens.HistoryScreen
import com.example.tictactoa.screens.SettingsScreen
import com.example.tictactoa.ui.theme.TicTacToaTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TicTacToaTheme {
                TicTacToeApp()
            }
        }
    }
}


// ----------------------------------------------------
// MAIN APP NAVIGATION
// ----------------------------------------------------

@Composable
fun TicTacToeApp() {

    var currentScreen by remember {
        mutableStateOf("home")
    }

    // PLAYER VS PLAYER

    var playerXName by remember {
        mutableStateOf("")
    }

    var playerOName by remember {
        mutableStateOf("")
    }


    // PLAYER VS COMPUTER

    var computerPlayerName by remember {
        mutableStateOf("")
    }

    var selectedDifficulty by remember {
        mutableStateOf("Easy")
    }


    // DATABASE

    val context = LocalContext.current

    val database = remember {
        GameDatabase.getDatabase(context)
    }

    val scope = rememberCoroutineScope()


    when (currentScreen) {


        // HOME

        "home" -> HomeScreen(

            onPlayClick = {
                currentScreen = "gameMode"
            },

            onHistoryClick = {
                currentScreen = "history"
            },

            onSettingsClick = {
                currentScreen = "settings"
            }
        )


        // GAME MODE

        "gameMode" -> GameModeScreen(

            onBackClick = {
                currentScreen = "home"
            },

            onPlayerVsPlayer = {
                currentScreen = "playerNames"
            },

            onPlayerVsComputer = {
                currentScreen = "computerName"
            }
        )


        // PLAYER NAMES

        "playerNames" -> PlayerNameScreen(

            onBackClick = {
                currentScreen = "gameMode"
            },

            onStartGame = { playerX, playerO ->

                playerXName = playerX

                playerOName = playerO

                currentScreen = "game"
            }
        )


        // PLAYER VS PLAYER GAME

        "game" -> GameScreen(

            playerX = playerXName,

            playerO = playerOName,

            onBackClick = {
                currentScreen = "playerNames"
            },

            onHomeClick = {
                currentScreen = "home"
            }
        )


        // COMPUTER NAME

        "computerName" -> ComputerNameScreen(

            onBackClick = {
                currentScreen = "gameMode"
            },

            onContinueClick = { name ->

                computerPlayerName = name

                currentScreen = "difficulty"
            }
        )


        // DIFFICULTY

        "difficulty" -> DifficultyScreen(

            onBackClick = {
                currentScreen = "computerName"
            },

            onDifficultySelected = { difficulty ->

                selectedDifficulty = difficulty

                currentScreen = "computerGame"
            }
        )


        // COMPUTER GAME

        "computerGame" -> ComputerGameScreen(

            playerName = computerPlayerName,

            difficulty = selectedDifficulty,

            onBackClick = {
                currentScreen = "difficulty"
            },

            onHomeClick = {
                currentScreen = "home"
            }
        )


        // HISTORY

        "history" -> HistoryScreen(

            onBackClick = {
                currentScreen = "home"
            }
        )


        // SETTINGS

        "settings" -> SettingsScreen(

            onBackClick = {
                currentScreen = "home"
            },

            onClearHistory = {

                scope.launch {

                    database
                        .matchDao()
                        .clearHistory()
                }
            }
        )
    }
}


// ----------------------------------------------------
// HOME SCREEN
// ----------------------------------------------------

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF06111F),
            Color(0xFF0A2034),
            Color(0xFF102F46)
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "X",
                fontSize = 82.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF22D3EE)
            )


            Spacer(
                modifier = Modifier.width(18.dp)
            )


            Text(
                text = "O",
                fontSize = 82.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFBBF24)
            )
        }


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "TIC TAC TOE",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )


        Spacer(
            modifier = Modifier.height(6.dp)
        )


        Text(
            text = "Play • Win • Repeat",
            fontSize = 15.sp,
            color = Color(0xFF9FB3C3)
        )


        Spacer(
            modifier = Modifier.height(55.dp)
        )


        MainButton(
            text = "▶  PLAY",
            onClick = onPlayClick
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        MainButton(
            text = "◷  HISTORY",
            onClick = onHistoryClick
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        MainButton(
            text = "⚙  SETTINGS",
            onClick = onSettingsClick
        )
    }
}


// ----------------------------------------------------
// GAME MODE SCREEN
// ----------------------------------------------------

@Composable
fun GameModeScreen(
    onBackClick: () -> Unit,
    onPlayerVsPlayer: () -> Unit,
    onPlayerVsComputer: () -> Unit
) {

    AppBackground {

        TopBackButton(
            onClick = onBackClick
        )


        Spacer(
            modifier = Modifier.height(80.dp)
        )


        Text(
            text = "CHOOSE GAME MODE",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "How do you want to play?",
            fontSize = 16.sp,
            color = Color(0xFF9FB3C3)
        )


        Spacer(
            modifier = Modifier.height(50.dp)
        )


        MainButton(
            text = "👥  PLAYER VS PLAYER",
            onClick = onPlayerVsPlayer
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        MainButton(
            text = "🤖  PLAYER VS COMPUTER",
            onClick = onPlayerVsComputer
        )
    }
}


// ----------------------------------------------------
// PLAYER NAME SCREEN
// ----------------------------------------------------

@Composable
fun PlayerNameScreen(
    onBackClick: () -> Unit,
    onStartGame: (String, String) -> Unit
) {

    var playerX by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var playerO by remember {
        mutableStateOf(TextFieldValue(""))
    }


    AppBackground {

        TopBackButton(
            onClick = onBackClick
        )


        Spacer(
            modifier = Modifier.height(55.dp)
        )


        Text(
            text = "ENTER PLAYER NAMES",
            fontSize = 27.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "Who's ready to win?",
            fontSize = 16.sp,
            color = Color(0xFF9FB3C3)
        )


        Spacer(
            modifier = Modifier.height(40.dp)
        )


        // PLAYER X

        Text(
            text = "X   PLAYER",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF22D3EE)
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        OutlinedTextField(

            value = playerX,

            onValueChange = { newValue ->

                playerX = newValue.copy(
                    text = capitalizeWords(
                        newValue.text
                    )
                )
            },

            modifier = Modifier.fillMaxWidth(),

            placeholder = {

                Text(
                    text = "Enter Player X name"
                )
            },

            singleLine = true,

            shape = RoundedCornerShape(18.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedTextColor = Color.White,

                unfocusedTextColor = Color.White,

                focusedBorderColor = Color(0xFF22D3EE),

                unfocusedBorderColor = Color(0xFF526D82),

                focusedPlaceholderColor = Color(0xFF8296A5),

                unfocusedPlaceholderColor = Color(0xFF8296A5),

                cursorColor = Color(0xFF22D3EE)
            )
        )


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // PLAYER O

        Text(
            text = "O   PLAYER",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFBBF24)
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        OutlinedTextField(

            value = playerO,

            onValueChange = { newValue ->

                playerO = newValue.copy(
                    text = capitalizeWords(
                        newValue.text
                    )
                )
            },

            modifier = Modifier.fillMaxWidth(),

            placeholder = {

                Text(
                    text = "Enter Player O name"
                )
            },

            singleLine = true,

            shape = RoundedCornerShape(18.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedTextColor = Color.White,

                unfocusedTextColor = Color.White,

                focusedBorderColor = Color(0xFFFBBF24),

                unfocusedBorderColor = Color(0xFF526D82),

                focusedPlaceholderColor = Color(0xFF8296A5),

                unfocusedPlaceholderColor = Color(0xFF8296A5),

                cursorColor = Color(0xFFFBBF24)
            )
        )


        Spacer(
            modifier = Modifier.height(40.dp)
        )


        MainButton(

            text = "START GAME  ▶",

            onClick = {

                if (
                    playerX.text.isNotBlank() &&
                    playerO.text.isNotBlank()
                ) {

                    onStartGame(
                        playerX.text.trim(),
                        playerO.text.trim()
                    )
                }
            }
        )
    }
}


// ----------------------------------------------------
// CAPITALIZE EVERY WORD
// ----------------------------------------------------

fun capitalizeWords(
    text: String
): String {

    return text
        .split(" ")
        .joinToString(" ") { word ->

            word.replaceFirstChar {

                if (it.isLowerCase()) {

                    it.titlecase()

                } else {

                    it.toString()
                }
            }
        }
}


// ----------------------------------------------------
// BACKGROUND
// ----------------------------------------------------

@Composable
fun AppBackground(
    content: @Composable ColumnScope.() -> Unit
) {

    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF06111F),
            Color(0xFF0A2034),
            Color(0xFF102F46)
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        content = content
    )
}


// ----------------------------------------------------
// MAIN BUTTON
// ----------------------------------------------------

@Composable
fun MainButton(
    text: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),

        shape = RoundedCornerShape(20.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0E7490)
        )
    ) {

        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


// ----------------------------------------------------
// BACK BUTTON
// ----------------------------------------------------

@Composable
fun TopBackButton(
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        Button(
            onClick = onClick,

            shape = RoundedCornerShape(16.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0E7490),
                contentColor = Color.White
            )
        ) {

            Text(
                text = "←  BACK",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}