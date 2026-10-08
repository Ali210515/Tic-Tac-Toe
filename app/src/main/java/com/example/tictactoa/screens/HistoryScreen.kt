
package com.example.tictactoa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

private val HistoryBg = Color(0xFF06111F)
private val HistoryCard = Color(0xFF102A3D)
private val Cyan = Color(0xFF22D3EE)
private val Yellow = Color(0xFFFBBF24)

@Composable
fun HistoryScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember {
        GameDatabase.getDatabase(context)
    }
    val matchDao = database.matchDao()
    val scope = rememberCoroutineScope()

    var matches by remember {
        mutableStateOf<List<MatchHistory>>(emptyList())
    }
    var showClearDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        matches = matchDao.getAllMatches()
    }

    val xWins = matches.count {
        it.winner == it.playerX && it.winner != "Draw"
    }
    val oWins = matches.count {
        it.winner == it.playerO && it.winner != "Draw"
    }
    val draws = matches.count {
        it.winner == "Draw"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        HistoryBg,
                        Color(0xFF0A2034),
                        Color(0xFF102F46)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {

        // SAME TOP BAR AS GAME SCREEN

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HistoryCircleButton(
                text = "←",
                onClick = onBackClick
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HISTORY",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "YOUR MATCH RECORDS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF91A8BA)
                )
            }

            // Invisible spacer keeps title centered
            Spacer(modifier = Modifier.size(46.dp))
        }

        Spacer(modifier = Modifier.height(26.dp))

        // SUMMARY

        Text(
            text = "Match Overview",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HistoryStatCard(
                modifier = Modifier.weight(1f),
                value = matches.size.toString(),
                title = "MATCHES",
                color = Color.White
            )

            HistoryStatCard(
                modifier = Modifier.weight(1f),
                value = xWins.toString(),
                title = "X WINS",
                color = Cyan
            )

            HistoryStatCard(
                modifier = Modifier.weight(1f),
                value = oWins.toString(),
                title = "O WINS",
                color = Yellow
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Draws: $draws",
            fontSize = 13.sp,
            color = Color(0xFF9FB3C3)
        )

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recent Matches",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            if (matches.isNotEmpty()) {
                Text(
                    text = "Clear all",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF87171),
                    modifier = Modifier.clickable {
                        showClearDialog = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "◷",
                        fontSize = 55.sp,
                        color = Cyan
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No Matches Yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Play a game to see your history",
                        fontSize = 14.sp,
                        color = Color(0xFF9FB3C3)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 18.dp)
            ) {
                items(
                    items = matches,
                    key = { it.id }
                ) { match ->
                    PremiumHistoryCard(match)
                }
            }
        }
    }

    // CONFIRM BEFORE DELETING

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
            },
            title = {
                Text("Clear History?")
            },
            text = {
                Text(
                    "All saved match records will be deleted."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            matchDao.clearHistory()
                            matches = emptyList()
                            showClearDialog = false
                        }
                    }
                ) {
                    Text(
                        text = "DELETE",
                        color = Color(0xFFDC2626)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                    }
                ) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
private fun HistoryCircleButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(46.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF12364D)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 15.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "←",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Text(
                text = "BACK",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun HistoryStatCard(
    modifier: Modifier,
    value: String,
    title: String,
    color: Color
) {
    Card(
        modifier = modifier.height(94.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = HistoryCard
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 27.sp,
                fontWeight = FontWeight.Black,
                color = color
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9FB3C3)
            )
        }
    }
}

@Composable
private fun PremiumHistoryCard(
    match: MatchHistory
) {

    val isDraw = match.winner == "Draw"

    val winnerColor = when {
        isDraw -> Color.White
        match.winner == match.playerX -> Cyan
        else -> Yellow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = winnerColor.copy(alpha = 0.30f),
                shape = RoundedCornerShape(20.dp)
            ),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = HistoryCard
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp)
        ) {

            // -----------------------------------------
            // PLAYERS
            // X Ali          VS          Abdullah O
            // -----------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // LEFT PLAYER
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "X",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Cyan
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = match.playerX,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }


                // CENTER VS
                Text(
                    text = "VS",
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    ),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF91A8BA)
                )


                // RIGHT PLAYER
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = match.playerO,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "O",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Yellow
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // -----------------------------------------
            // WINNER + ROUND
            // -----------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        text = if (isDraw) {
                            "🤝  DRAW"
                        } else {
                            "★  WINNER"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = winnerColor
                    )


                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )


                    Text(
                        text = if (isDraw) {
                            "It's a Draw!"
                        } else {
                            match.winner
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = winnerColor
                    )
                }


                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF173D56)
                ) {

                    Text(
                        text = "ROUND ${match.round}",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB4C6D3)
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            HorizontalDivider(
                color = Color.White.copy(
                    alpha = 0.10f
                )
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // -----------------------------------------
            // MODE
            // -----------------------------------------

            Text(
                text = match.gameMode,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9FB3C3)
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            // -----------------------------------------
            // DATE + TIME
            // -----------------------------------------

            Text(
                text = "${match.date}  •  ${match.time}",
                fontSize = 12.sp,
                color = Color(0xFF9FB3C3)
            )
        }
    }
}