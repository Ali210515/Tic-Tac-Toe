package com.example.tictactoa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComputerNameScreen(
    onBackClick: () -> Unit,
    onContinueClick: (String) -> Unit
) {

    var playerName by remember {
        mutableStateOf(TextFieldValue(""))
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
                    containerColor = Color(0xFF12364D)
                )
            ) {

                Text(
                    text = "←  BACK",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier = Modifier.height(65.dp)
        )


        // COMPUTER ICON

        Text(
            text = "🤖",
            fontSize = 70.sp
        )


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        Text(
            text = "PLAYER VS COMPUTER",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "Enter your name to continue",
            fontSize = 15.sp,
            color = Color(0xFF9FB3C3)
        )


        Spacer(
            modifier = Modifier.height(45.dp)
        )


        // PLAYER NAME

        Text(
            text = "X   YOUR NAME",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF22D3EE)
        )


        Spacer(
            modifier = Modifier.height(9.dp)
        )


        OutlinedTextField(
            value = playerName,

            onValueChange = { newValue ->

                playerName = newValue.copy(
                    text = capitalizeComputerName(
                        newValue.text
                    )
                )
            },

            modifier = Modifier.fillMaxWidth(),

            placeholder = {
                Text(
                    text = "Enter your name"
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
            modifier = Modifier.height(35.dp)
        )


        // CONTINUE BUTTON

        Button(
            onClick = {

                if (playerName.text.isNotBlank()) {

                    onContinueClick(
                        playerName.text.trim()
                    )
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),

            shape = RoundedCornerShape(20.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0E7490)
            )
        ) {

            Text(
                text = "CONTINUE  ▶",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


// ----------------------------------------------------
// CAPITALIZE NAME
// ----------------------------------------------------

private fun capitalizeComputerName(
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