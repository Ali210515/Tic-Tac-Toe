package com.example.tictactoa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DifficultyScreen(
    onBackClick: () -> Unit,
    onDifficultySelected: (String) -> Unit
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

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = onBackClick,
                shape = RoundedCornerShape(16.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF12364D)
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
            modifier = Modifier.height(65.dp)
        )


        Text(
            text = "🤖",
            fontSize = 65.sp
        )


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        Text(
            text = "CHOOSE DIFFICULTY",
            fontSize = 27.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "How strong should the computer be?",
            fontSize = 15.sp,
            color = Color(0xFF9FB3C3)
        )


        Spacer(
            modifier = Modifier.height(45.dp)
        )


        DifficultyButton(
            emoji = "🙂",
            title = "EASY",
            description = "Perfect for beginners",
            onClick = {
                onDifficultySelected("Easy")
            }
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        DifficultyButton(
            emoji = "⚡",
            title = "MEDIUM",
            description = "A balanced challenge",
            onClick = {
                onDifficultySelected("Medium")
            }
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        DifficultyButton(
            emoji = "🔥",
            title = "HARD",
            description = "Try to beat the computer",
            onClick = {
                onDifficultySelected("Hard")
            }
        )
    }
}


@Composable
private fun DifficultyButton(
    emoji: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),

        shape = RoundedCornerShape(20.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0E7490),
            contentColor = Color.White
        ),

        contentPadding = PaddingValues(
            horizontal = 20.dp
        )
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
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
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