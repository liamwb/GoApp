package com.goapppackage.goapp.data.currentgame

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_GAME_ID = 0

@Entity(tableName = "current_game")
class CurrentGameStateStackEntity (
    @PrimaryKey
    val id: Int = CURRENT_GAME_ID,
    val gameStateStackJson: String // serialized gameStateStack
)

