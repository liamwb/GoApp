package com.example.goapp.data.util

import com.example.goapp.data.GameState
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

object ArrayDequeSerializer : KSerializer<ArrayDeque<GameState>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ArrayDeque") {
        element("elements", ListSerializer(GameState.serializer()).descriptor)
    }

    override fun serialize(encoder: Encoder, value: ArrayDeque<GameState>) {
        encoder.encodeSerializableValue(ListSerializer(GameState.serializer()), value.toList())
    }

    override fun deserialize(decoder: Decoder): ArrayDeque<GameState> {
        return ArrayDeque(decoder.decodeSerializableValue(ListSerializer(GameState.serializer())))
    }
}

val customSerializersModule = SerializersModule {
    contextual(ArrayDeque::class) {ArrayDequeSerializer}
}