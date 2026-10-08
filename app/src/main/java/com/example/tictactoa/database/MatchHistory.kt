package com.example.tictactoa.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchHistory(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val playerX: String,

    val playerO: String,

    val winner: String,

    val gameMode: String,

    val round: Int,

    val date: String,

    val time: String
)