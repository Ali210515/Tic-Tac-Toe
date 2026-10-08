package com.example.tictactoa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onClearHistory: () -> Unit
) {

    var soundEnabled by remember {
        mutableStateOf(true)
    }

    var vibrationEnabled by remember {
        mutableStateOf(true)
    }

    var showAbout by remember {
        mutableStateOf(false)
    }

    var showClearDialog by remember {
        mutableStateOf(false)
    }

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

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // BACK BUTTON

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = onBackClick,

                shape = RoundedCornerShape(16.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0E7490),
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "←  BACK",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }


        Spacer(
            modifier = Modifier.height(35.dp)
        )


        Text(
            text = "⚙  SETTINGS",
            fontSize = 29.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )


        Spacer(
            modifier = Modifier.height(7.dp)
        )


        Text(
            text = "Customize your game",
            fontSize = 15.sp,
            color = Color(0xFF9FB3C3)
        )


        Spacer(
            modifier = Modifier.height(40.dp)
        )


        // SOUND

        SettingSwitchCard(
            emoji = "🔊",
            title = "Sound",
            description = "Game sound effects",
            checked = soundEnabled,

            onCheckedChange = {
                soundEnabled = it
            }
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // VIBRATION

        SettingSwitchCard(
            emoji = "📳",
            title = "Vibration",
            description = "Vibrate when you make a move",
            checked = vibrationEnabled,

            onCheckedChange = {
                vibrationEnabled = it
            }
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // CLEAR HISTORY

        SettingActionCard(
            emoji = "🗑",
            title = "Clear History",
            description = "Delete all saved matches",

            onClick = {
                showClearDialog = true
            }
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // ABOUT

        SettingActionCard(
            emoji = "ℹ️",
            title = "About App",
            description = "Tic Tac Toe app information",

            onClick = {
                showAbout = true
            }
        )
    }


    // CLEAR HISTORY DIALOG

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
                    "All saved match history will be deleted."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        onClearHistory()

                        showClearDialog = false
                    }
                ) {

                    Text(
                        text = "CLEAR"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showClearDialog = false
                    }
                ) {

                    Text(
                        text = "CANCEL"
                    )
                }
            }
        )
    }


    // ABOUT DIALOG

    if (showAbout) {

        AlertDialog(

            onDismissRequest = {
                showAbout = false
            },

            title = {

                Text(
                    text = "Tic Tac Toe"
                )
            },

            text = {

                Column {

                    Text(
                        text = "Tic Tac Toe Game"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Player vs Player"
                    )

                    Text(
                        text = "Player vs Computer"
                    )

                    Text(
                        text = "Easy • Medium • Hard"
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Version 1.0"
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showAbout = false
                    }
                ) {

                    Text(
                        text = "OK"
                    )
                }
            }
        )
    }
}


// ----------------------------------------------------
// SWITCH CARD
// ----------------------------------------------------

@Composable
private fun SettingSwitchCard(
    emoji: String,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF12364D)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = emoji,
                fontSize = 27.sp
            )


            Spacer(
                modifier = Modifier.width(16.dp)
            )


            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF9FB3C3)
                )
            }


            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}


// ----------------------------------------------------
// ACTION CARD
// ----------------------------------------------------

@Composable
private fun SettingActionCard(
    emoji: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF12364D)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = emoji,
                fontSize = 27.sp
            )


            Spacer(
                modifier = Modifier.width(16.dp)
            )


            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF9FB3C3)
                )
            }


            Text(
                text = "›",
                fontSize = 30.sp,
                color = Color(0xFF22D3EE)
            )
        }
    }
}