package com.example.goapp.data.currentgame

import com.example.goapp.data.GameState
import com.example.goapp.data.util.customSerializersModule
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CurrentGameRepository(private val currentGameDao: CurrentGameStateStackDao) {
    val json = Json {serializersModule = customSerializersModule}
    suspend fun getGameStateStack(): ArrayDeque<GameState> {
        val gameStateStackEntity = currentGameDao.getSerializedGameStateStack()
        return json.decodeFromString(gameStateStackEntity.gameStateStackJson)
    }

    suspend fun setGameStateStack(gameStateStack: ArrayDeque<GameState>) {
        val serializedGameStateStack = json.encodeToString(gameStateStack)
        val currentGameStateStackEntity = CurrentGameStateStackEntity(gameStateStackJson = serializedGameStateStack)
        currentGameDao.setSerializedGameStateStack(currentGameStateStackEntity)
    }

}