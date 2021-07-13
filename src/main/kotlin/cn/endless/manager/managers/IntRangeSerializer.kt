package cn.endless.manager.managers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive


object IntRangeSerializer : KSerializer<IntRange> {

    private object JsonObjectDescriptor : SerialDescriptor by serialDescriptor<HashMap<String, JsonElement>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.json.JsonObject"
    }

    override val descriptor: SerialDescriptor = JsonObjectDescriptor

    override fun deserialize(decoder: Decoder): IntRange {
        val jsonObj = decoder.decodeSerializableValue(JsonObject.serializer())
        return IntRange(
            jsonObj["start"]!!.jsonPrimitive.int,
            jsonObj["end"]!!.jsonPrimitive.int
        )

    }

    override fun serialize(encoder: Encoder, value: IntRange) {

    }
}