package com.goapppackage.goapp.data.currentgame

import com.goapppackage.goapp.data.GameState
import com.goapppackage.goapp.data.util.customSerializersModule
import kotlinx.coroutines.flow.Flow
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

    fun isNotEmpty(): Flow<Boolean> {
        return currentGameDao.isNotEmpty()
    }

}