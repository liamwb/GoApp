package com.example.goapp.data.currentgame

import com.example.goapp.data.GameState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CurrentGameRepository(private val currentGameDao: CurrentGameStateStackDao) {

    suspend fun getGameState(): GameState {
        val gameStateStackEntity = currentGameDao.getSerializedGameStateStack()
        return Json.decodeFromString(gameStateStackEntity.gameStateStackJson)
    }

    suspend fun setGameState(gameStateStack: ArrayDeque<GameState>) {
        val serializedGameStateStack = Json.encodeToString(gameStateStack)
        val currentGameStateStackEntity = CurrentGameStateStackEntity(gameStateStackJson = serializedGameStateStack)
        currentGameDao.setSerializedGameStateStack(currentGameStateStackEntity)
    }

}