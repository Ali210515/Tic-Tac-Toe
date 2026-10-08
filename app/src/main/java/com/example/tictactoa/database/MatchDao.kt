package com.example.tictactoa.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MatchDao {

    @Insert
    suspend fun insertMatch(match: MatchHistory)

    @Query("SELECT * FROM match_history ORDER BY id DESC")
    suspend fun getAllMatches(): List<MatchHistory>

    @Delete
    suspend fun deleteMatch(match: MatchHistory)

    @Query("DELETE FROM match_history")
    suspend fun clearHistory()
}