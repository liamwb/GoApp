package com.goapppackage.goapp.data.currentgame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentGameStateStackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSerializedGameStateStack(serializedGameStateStack: CurrentGameStateStackEntity)

    @Query("SELECT * FROM current_game WHERE id = :id")
    suspend fun getSerializedGameStateStack(id: Int = CURRENT_GAME_ID): CurrentGameStateStackEntity

    @Query("SELECT (SELECT COUNT(*) FROM current_game) != 0")
    fun isNotEmpty(): Flow<Boolean>
}