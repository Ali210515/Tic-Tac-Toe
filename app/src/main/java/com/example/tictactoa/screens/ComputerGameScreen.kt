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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@Composable
fun ComputerGameScreen(
    playerName: String,
    difficulty: String,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {

    val context = LocalContext.current

    val database = remember {
        GameDatabase.getDatabase(context)
    }

    val matchDao = database.matchDao()
    val scope = rememberCoroutineScope()

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

    var playerScore by remember {
        mutableIntStateOf(0)
    }

    var computerScore by remember {
        mutableIntStateOf(0)
    }

    var round by remember {
        mutableIntStateOf(1)
    }

    var roundSaved by remember {
        mutableStateOf(false)
    }


    // -----------------------------
    // SAVE HISTORY
    // -----------------------------

    fun saveMatch(result: String) {

        if (roundSaved) return

        roundSaved = true

        val date = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date())

        val time = SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date())

        scope.launch {

            matchDao.insertMatch(
                MatchHistory(
                    playerX = playerName,
                    playerO = "Computer",
                    winner = result,
                    gameMode = "Player vs Computer • $difficulty",
                    round = round,
                    date = date,
                    time = time
                )
            )
        }
    }


    // -----------------------------
    // CHECK RESULT
    // -----------------------------

    fun processResult(
        newBoard: List<String>
    ) {

        val result = checkComputerWinner(
            newBoard
        )

        if (result != null) {

            winner = result.first
            winningCells = result.second

            if (result.first == "X") {

                playerScore++

                saveMatch(playerName)

            } else {

                computerScore++

                saveMatch("Computer")
            }

        } else if (
            newBoard.all {
                it.isNotEmpty()
            }
        ) {

            isDraw = true

            saveMatch("Draw")
        }
    }


    // -----------------------------
    // COMPUTER TURN
    // -----------------------------

    LaunchedEffect(
        currentPlayer,
        board,
        winner,
        isDraw
    ) {

        if (
            currentPlayer == "O" &&
            winner == null &&
            !isDraw
        ) {

            delay(550)

            val move = getComputerMove(
                board = board,
                difficulty = difficulty
            )

            if (move != -1) {

                val newBoard =
                    board.toMutableList()

                newBoard[move] = "O"

                board = newBoard

                processResult(newBoard)

                if (
                    checkComputerWinner(newBoard) == null &&
                    newBoard.any { it.isEmpty() }
                ) {
                    currentPlayer = "X"
                }
            }
        }
    }


    // -----------------------------
    // RESET ROUND
    // -----------------------------

    fun resetRound() {

        val nextStarter = when {

            winner == "X" -> "X"

            winner == "O" -> "O"

            else -> "X"
        }

        board = List(9) { "" }

        currentPlayer = nextStarter

        winner = null

        winningCells = emptyList()

        isDraw = false

        roundSaved = false

        round++
    }


    val background =
        Brush.verticalGradient(
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

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        // -----------------------------
        // TOP BAR
        // -----------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            ComputerCircleButton(
                text = "←",
                onClick = onBackClick
            )


            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "VS COMPUTER",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "$difficulty • ROUND $round",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF91A8BA)
                )
            }


            ComputerCircleButton(
                text = "⌂",
                onClick = onHomeClick
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // -----------------------------
        // SCORE
        // -----------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            ComputerPlayerCard(
                modifier = Modifier.weight(1f),
                name = playerName,
                symbol = "X",
                score = playerScore,
                symbolColor = Color(0xFF22D3EE),
                isActive =
                    currentPlayer == "X" &&
                            winner == null &&
                            !isDraw
            )


            ComputerPlayerCard(
                modifier = Modifier.weight(1f),
                name = "Computer",
                symbol = "O",
                score = computerScore,
                symbolColor = Color(0xFFFBBF24),
                isActive =
                    currentPlayer == "O" &&
                            winner == null &&
                            !isDraw
            )
        }


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // -----------------------------
        // STATUS
        // -----------------------------

        ComputerStatusCard(
            playerName = playerName,
            currentPlayer = currentPlayer,
            winner = winner,
            isDraw = isDraw
        )


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // -----------------------------
        // BOARD
        // -----------------------------

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

                        ComputerGameCell(
                            value = board[index],

                            isWinningCell =
                                index in winningCells,

                            onClick = {

                                if (
                                    currentPlayer == "X" &&
                                    board[index].isEmpty() &&
                                    winner == null &&
                                    !isDraw
                                ) {

                                    val newBoard =
                                        board.toMutableList()

                                    newBoard[index] = "X"

                                    board = newBoard

                                    processResult(
                                        newBoard
                                    )

                                    if (
                                        checkComputerWinner(
                                            newBoard
                                        ) == null &&
                                        newBoard.any {
                                            it.isEmpty()
                                        }
                                    ) {

                                        currentPlayer = "O"
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


        // -----------------------------
        // BUTTON
        // -----------------------------

        if (
            winner != null ||
            isDraw
        ) {

            Button(
                onClick = {
                    resetRound()
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
                    text = "▶  NEXT ROUND",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        } else {

            OutlinedButton(
                onClick = {

                    board = List(9) { "" }

                    currentPlayer = "X"

                    winner = null

                    winningCells =
                        emptyList()

                    isDraw = false

                    roundSaved = false
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
                    text = "↻  RESTART ROUND",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// ====================================================
// PLAYER CARD
// ====================================================

@Composable
private fun ComputerPlayerCard(
    modifier: Modifier,
    name: String,
    symbol: String,
    score: Int,
    symbolColor: Color,
    isActive: Boolean
) {

    Card(
        modifier = modifier
            .height(135.dp)
            .then(
                if (isActive) {
                    Modifier.shadow(
                        8.dp,
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
                containerColor =
                    if (isActive) {
                        Color(0xFF173D56)
                    } else {
                        Color(0xFF102A3D)
                    }
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize(),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = symbol,
                fontSize = 31.sp,
                fontWeight = FontWeight.Black,
                color = symbolColor
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = name,
                maxLines = 1,
                overflow =
                    TextOverflow.Ellipsis,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text =
                    "$score WIN${if (score == 1) "" else "S"}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9FB3C3)
            )
        }
    }
}


// ====================================================
// STATUS
// ====================================================

@Composable
private fun ComputerStatusCard(
    playerName: String,
    currentPlayer: String,
    winner: String?,
    isDraw: Boolean
) {

    val text: String

    val color: Color


    when {

        winner == "X" -> {

            text = "🏆  $playerName WINS!"

            color = Color(0xFF22D3EE)
        }


        winner == "O" -> {

            text = "🤖  COMPUTER WINS!"

            color = Color(0xFFFBBF24)
        }


        isDraw -> {

            text = "🤝  IT'S A DRAW!"

            color = Color.White
        }


        currentPlayer == "X" -> {

            text = "✦  $playerName'S TURN"

            color = Color(0xFF22D3EE)
        }


        else -> {

            text = "🤖  COMPUTER IS THINKING..."

            color = Color(0xFFFBBF24)
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
                fontSize = 17.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color = color
            )
        }
    }
}


// ====================================================
// CELL
// ====================================================

@Composable
private fun ComputerGameCell(
    value: String,
    isWinningCell: Boolean,
    onClick: () -> Unit
) {

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
                    if (isWinningCell) {
                        Color(0xFF185E65)
                    } else {
                        Color(0xFF12364D)
                    },

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
            fontWeight = FontWeight.Black,

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


// ====================================================
// TOP BUTTON
// ====================================================

@Composable
private fun ComputerCircleButton(
    text: String,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .size(46.dp)
            .clickable {
                onClick()
            },

        shape = CircleShape,

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
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


// ====================================================
// WINNER CHECK
// ====================================================

private fun checkComputerWinner(
    board: List<String>
): Pair<String, List<Int>>? {

    val positions = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    for (position in positions) {

        val a = position[0]
        val b = position[1]
        val c = position[2]

        if (
            board[a].isNotEmpty() &&
            board[a] == board[b] &&
            board[a] == board[c]
        ) {

            return Pair(
                board[a],
                position
            )
        }
    }

    return null
}


// ====================================================
// AI MOVE
// ====================================================

private fun getComputerMove(
    board: List<String>,
    difficulty: String
): Int {

    val emptyCells =
        board.indices.filter {
            board[it].isEmpty()
        }

    if (emptyCells.isEmpty()) {
        return -1
    }


    return when (
        difficulty.lowercase()
    ) {

        "easy" -> {

            emptyCells.random()
        }


        "medium" -> {

            findWinningMove(
                board,
                "O"
            )
                ?: findWinningMove(
                    board,
                    "X"
                )
                ?: emptyCells[
                    Random.nextInt(
                        emptyCells.size
                    )
                ]
        }


        else -> {

            findBestMove(board)
        }
    }
}


// ====================================================
// WIN / BLOCK
// ====================================================

private fun findWinningMove(
    board: List<String>,
    symbol: String
): Int? {

    for (index in board.indices) {

        if (board[index].isEmpty()) {

            val testBoard =
                board.toMutableList()

            testBoard[index] = symbol

            if (
                checkComputerWinner(
                    testBoard
                )?.first == symbol
            ) {

                return index
            }
        }
    }

    return null
}


// ====================================================
// HARD AI
// ====================================================

private fun findBestMove(
    board: List<String>
): Int {

    var bestScore =
        Int.MIN_VALUE

    var bestMove = -1


    for (index in board.indices) {

        if (board[index].isEmpty()) {

            val newBoard =
                board.toMutableList()

            newBoard[index] = "O"

            val score = minimax(
                board = newBoard,
                isMaximizing = false
            )

            if (score > bestScore) {

                bestScore = score

                bestMove = index
            }
        }
    }


    return bestMove
}


// ====================================================
// MINIMAX
// ====================================================

private fun minimax(
    board: MutableList<String>,
    isMaximizing: Boolean
): Int {

    val result =
        checkComputerWinner(board)

    if (result?.first == "O") {
        return 10
    }

    if (result?.first == "X") {
        return -10
    }

    if (
        board.all {
            it.isNotEmpty()
        }
    ) {
        return 0
    }


    if (isMaximizing) {

        var bestScore =
            Int.MIN_VALUE

        for (index in board.indices) {

            if (board[index].isEmpty()) {

                board[index] = "O"

                val score =
                    minimax(
                        board,
                        false
                    )

                board[index] = ""

                bestScore =
                    maxOf(
                        bestScore,
                        score
                    )
            }
        }

        return bestScore

    } else {

        var bestScore =
            Int.MAX_VALUE

        for (index in board.indices) {

            if (board[index].isEmpty()) {

                board[index] = "X"

                val score =
                    minimax(
                        board,
                        true
                    )

                board[index] = ""

                bestScore =
                    minOf(
                        bestScore,
                        score
                    )
            }
        }

        return bestScore
    }
}