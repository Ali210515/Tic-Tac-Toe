package com.example.tictactoa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoa.database.GameDatabase
import com.example.tictactoa.database.MatchHistory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun GameScreen(
    playerX: String,
    playerO: String,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {

    // DATABASE
    val context = LocalContext.current

    val database = remember {
        GameDatabase.getDatabase(context)
    }

    val matchDao = database.matchDao()

    val coroutineScope = rememberCoroutineScope()


    // GAME STATES

    var board by remember {
        mutableStateOf(List(9) { "" })
    }

    var currentPlayer by remember {
        mutableStateOf("X")
    }

    var winner by remember {
        mutableStateOf<String?>(null)
    }

    var winningCells by remember {
        mutableStateOf<List<Int>>(emptyList())
    }

    var isDraw by remember {
        mutableStateOf(false)
    }

    var xScore by remember {
        mutableIntStateOf(0)
    }

    var oScore by remember {
        mutableIntStateOf(0)
    }

    var round by remember {
        mutableIntStateOf(1)
    }

    // Prevent same round from saving twice
    var roundSaved by remember {
        mutableStateOf(false)
    }


    // SAVE MATCH IN DATABASE

    fun saveMatch(result: String) {

        if (roundSaved) {
            return
        }

        roundSaved = true

        val currentDate = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date())

        val currentTime = SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date())

        val match = MatchHistory(
            playerX = playerX,
            playerO = playerO,
            winner = result,
            gameMode = "Player vs Player",
            round = round,
            date = currentDate,
            time = currentTime
        )

        coroutineScope.launch {
            matchDao.insertMatch(match)
        }
    }


    // RESET ROUND

    fun resetRound(
        nextRound: Boolean = false
    ) {

        val nextStartingPlayer = when {

            winner == "X" -> "X"

            winner == "O" -> "O"

            else -> "X"
        }

        board = List(9) { "" }

        currentPlayer = nextStartingPlayer

        winner = null

        winningCells = emptyList()

        isDraw = false

        roundSaved = false

        if (nextRound) {
            round++
        }
    }


    // BACKGROUND

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
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // ------------------------------------------------
        // TOP BAR
        // ------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            CircleActionButton(
                text = "←",
                onClick = onBackClick
            )

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "TIC TAC TOE",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "ROUND $round",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF91A8BA)
                )
            }

            CircleActionButton(
                text = "⌂",
                onClick = onHomeClick
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // ------------------------------------------------
        // SCOREBOARD
        // ------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            PlayerCard(
                modifier = Modifier.weight(1f),

                name = playerX,

                symbol = "X",

                score = xScore,

                symbolColor =
                    Color(0xFF22D3EE),

                isActive =
                    currentPlayer == "X" &&
                            winner == null &&
                            !isDraw
            )


            PlayerCard(
                modifier = Modifier.weight(1f),

                name = playerO,

                symbol = "O",

                score = oScore,

                symbolColor =
                    Color(0xFFFBBF24),

                isActive =
                    currentPlayer == "O" &&
                            winner == null &&
                            !isDraw
            )
        }


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // ------------------------------------------------
        // CURRENT TURN / RESULT
        // ------------------------------------------------

        StatusCard(
            currentPlayer = currentPlayer,

            playerX = playerX,

            playerO = playerO,

            winner = winner,

            isDraw = isDraw
        )


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // ------------------------------------------------
        // GAME BOARD
        // ------------------------------------------------

        Column(
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            for (row in 0..2) {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    for (column in 0..2) {

                        val index =
                            row * 3 + column


                        PremiumGameCell(

                            value =
                                board[index],

                            isWinningCell =
                                index in winningCells,

                            onClick = {

                                if (
                                    board[index].isEmpty() &&
                                    winner == null &&
                                    !isDraw
                                ) {

                                    val newBoard =
                                        board.toMutableList()

                                    newBoard[index] =
                                        currentPlayer

                                    board = newBoard


                                    val result =
                                        checkWinner(board)


                                    // WINNER

                                    if (result != null) {

                                        winner =
                                            result.first

                                        winningCells =
                                            result.second


                                        if (
                                            result.first == "X"
                                        ) {

                                            xScore++

                                            saveMatch(
                                                playerX
                                            )

                                        } else {

                                            oScore++

                                            saveMatch(
                                                playerO
                                            )
                                        }

                                    }


                                    // DRAW

                                    else if (
                                        board.all {
                                            it.isNotEmpty()
                                        }
                                    ) {

                                        isDraw = true

                                        saveMatch(
                                            "Draw"
                                        )
                                    }


                                    // NEXT PLAYER

                                    else {

                                        currentPlayer =
                                            if (
                                                currentPlayer == "X"
                                            ) {
                                                "O"
                                            } else {
                                                "X"
                                            }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // ------------------------------------------------
        // NEXT ROUND / RESTART
        // ------------------------------------------------

        if (
            winner != null ||
            isDraw
        ) {

            Button(
                onClick = {

                    resetRound(
                        nextRound = true
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF0E7490)
                    )
            ) {

                Text(
                    text =
                        "▶  NEXT ROUND",

                    fontSize = 17.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

        } else {

            OutlinedButton(
                onClick = {

                    resetRound()

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            contentColor =
                                Color.White
                        )
            ) {

                Text(
                    text =
                        "↻  RESTART ROUND",

                    fontSize = 16.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


// ----------------------------------------------------
// PLAYER CARD
// ----------------------------------------------------

@Composable
fun PlayerCard(
    modifier: Modifier,
    name: String,
    symbol: String,
    score: Int,
    symbolColor: Color,
    isActive: Boolean
) {

    val cardColor =
        if (isActive) {
            Color(0xFF173D56)
        } else {
            Color(0xFF102A3D)
        }


    Card(
        modifier = modifier
            .height(135.dp)
            .then(

                if (isActive) {

                    Modifier.shadow(
                        elevation = 10.dp,
                        shape =
                            RoundedCornerShape(22.dp)
                    )

                } else {

                    Modifier
                }
            ),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = cardColor
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color =
                            symbolColor.copy(
                                alpha = 0.15f
                            ),

                        shape =
                            CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = symbol,

                    fontSize = 27.sp,

                    fontWeight =
                        FontWeight.Black,

                    color =
                        symbolColor
                )
            }


            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )


            Text(
                text = name,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis,

                fontSize = 15.sp,

                fontWeight =
                    FontWeight.Bold,

                color = Color.White
            )


            Text(
                text =
                    "$score WIN${if (score == 1) "" else "S"}",

                fontSize = 12.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color(0xFF9FB3C3)
            )
        }
    }
}


// ----------------------------------------------------
// STATUS CARD
// ----------------------------------------------------

@Composable
fun StatusCard(
    currentPlayer: String,
    playerX: String,
    playerO: String,
    winner: String?,
    isDraw: Boolean
) {

    val text: String

    val mainColor: Color


    when {

        winner == "X" -> {

            text =
                "🏆  $playerX WINS!"

            mainColor =
                Color(0xFF22D3EE)
        }


        winner == "O" -> {

            text =
                "🏆  $playerO WINS!"

            mainColor =
                Color(0xFFFBBF24)
        }


        isDraw -> {

            text =
                "🤝  IT'S A DRAW!"

            mainColor =
                Color.White
        }


        currentPlayer == "X" -> {

            text =
                "✦  $playerX'S TURN"

            mainColor =
                Color(0xFF22D3EE)
        }


        else -> {

            text =
                "✦  $playerO'S TURN"

            mainColor =
                Color(0xFFFBBF24)
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF0C2538)
            )
    ) {

        Box(
            modifier =
                Modifier.fillMaxSize(),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = text,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis,

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                color =
                    mainColor
            )
        }
    }
}


// ----------------------------------------------------
// PREMIUM GAME CELL
// ----------------------------------------------------

@Composable
fun PremiumGameCell(
    value: String,
    isWinningCell: Boolean,
    onClick: () -> Unit
) {

    val cellColor =
        if (isWinningCell) {

            Color(0xFF185E65)

        } else {

            Color(0xFF12364D)
        }


    Box(
        modifier = Modifier
            .size(94.dp)

            .shadow(
                elevation =
                    if (isWinningCell) {
                        12.dp
                    } else {
                        4.dp
                    },

                shape =
                    RoundedCornerShape(22.dp)
            )

            .background(
                color =
                    cellColor,

                shape =
                    RoundedCornerShape(22.dp)
            )

            .clickable {

                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text = value,

            fontSize = 52.sp,

            fontWeight =
                FontWeight.Black,

            color =
                when (value) {

                    "X" ->
                        Color(0xFF22D3EE)

                    "O" ->
                        Color(0xFFFBBF24)

                    else ->
                        Color.Transparent
                }
        )
    }
}


// ----------------------------------------------------
// ROUND BUTTON
// ----------------------------------------------------

@Composable
fun CircleActionButton(
    text: String,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .size(46.dp)
            .clickable {

                onClick()
            },

        shape =
            CircleShape,

        color =
            Color(0xFF12364D)
    ) {

        Box(
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = text,

                fontSize = 22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color.White
            )
        }
    }
}


// ----------------------------------------------------
// WINNER CHECK
// ----------------------------------------------------

fun checkWinner(
    board: List<String>
): Pair<String, List<Int>>? {

    val winningPositions =
        listOf(

            listOf(0, 1, 2),

            listOf(3, 4, 5),

            listOf(6, 7, 8),

            listOf(0, 3, 6),

            listOf(1, 4, 7),

            listOf(2, 5, 8),

            listOf(0, 4, 8),

            listOf(2, 4, 6)
        )


    for (
    position in winningPositions
    ) {

        val first =
            position[0]

        val second =
            position[1]

        val third =
            position[2]


        if (
            board[first].isNotEmpty() &&
            board[first] ==
            board[second] &&
            board[first] ==
            board[third]
        ) {

            return Pair(
                board[first],
                position
            )
        }
    }


    return null
}