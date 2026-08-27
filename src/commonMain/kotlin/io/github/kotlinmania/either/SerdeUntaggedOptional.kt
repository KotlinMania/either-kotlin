// port-lint: source serde_untagged_optional.rs
package io.github.kotlinmania.either

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

/**
 * Untagged serialization/deserialization support for nullable [Either].
 *
 * Example:
 * ```kotlin
 * val serializer = UntaggedOptionalEitherSerializer(Int.serializer(), String.serializer())
 * val nullable = Json.decodeFromString(serializer, "null")
 * assertNull(nullable)
 * ```
 */
public class UntaggedOptionalEitherSerializer<L, R>(
    private val leftSerializer: KSerializer<L>,
    private val rightSerializer: KSerializer<R>,
) : KSerializer<Either<L, R>?> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("io.github.kotlinmania.either.UntaggedOptionalEither")

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Either<L, R>?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            when (value) {
                is Either.Left -> encoder.encodeSerializableValue(leftSerializer, value.value)
                is Either.Right -> encoder.encodeSerializableValue(rightSerializer, value.value)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Either<L, R>? {
        if (decoder is JsonDecoder) {
            val jsonElement: JsonElement = decoder.decodeJsonElement()
            if (jsonElement is JsonNull) {
                return null
            }
            return try {
                Either.Left(decoder.json.decodeFromJsonElement(leftSerializer, jsonElement))
            } catch (_: Exception) {
                try {
                    Either.Right(decoder.json.decodeFromJsonElement(rightSerializer, jsonElement))
                } catch (e: Exception) {
                    throw SerializationException("Failed to deserialize untagged optional Either: $e", e)
                }
            }
        }
        if (decoder.decodeNotNullMark()) {
            return try {
                Either.Left(decoder.decodeSerializableValue(leftSerializer))
            } catch (_: Exception) {
                try {
                    Either.Right(decoder.decodeSerializableValue(rightSerializer))
                } catch (e: Exception) {
                    throw SerializationException("Failed to deserialize untagged optional Either: $e", e)
                }
            }
        } else {
            decoder.decodeNull()
            return null
        }
    }
}
