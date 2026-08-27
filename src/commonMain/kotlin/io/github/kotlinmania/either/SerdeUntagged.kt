// port-lint: source serde_untagged.rs
package io.github.kotlinmania.either

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement

/**
 * Untagged serialization/deserialization support for [Either].
 *
 * Example:
 * ```kotlin
 * val serializer = UntaggedEitherSerializer(Int.serializer(), String.serializer())
 * val left = Json.decodeFromString(serializer, "42")
 * assertEquals(Either.Left(42), left)
 * ```
 */
public class UntaggedEitherSerializer<L, R>(
    private val leftSerializer: KSerializer<L>,
    private val rightSerializer: KSerializer<R>,
) : KSerializer<Either<L, R>> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("io.github.kotlinmania.either.UntaggedEither")

    override fun serialize(encoder: Encoder, value: Either<L, R>) {
        when (value) {
            is Either.Left -> encoder.encodeSerializableValue(leftSerializer, value.value)
            is Either.Right -> encoder.encodeSerializableValue(rightSerializer, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): Either<L, R> {
        if (decoder is JsonDecoder) {
            val jsonElement: JsonElement = decoder.decodeJsonElement()
            return try {
                Either.Left(decoder.json.decodeFromJsonElement(leftSerializer, jsonElement))
            } catch (_: Exception) {
                try {
                    Either.Right(decoder.json.decodeFromJsonElement(rightSerializer, jsonElement))
                } catch (e: Exception) {
                    throw SerializationException("Failed to deserialize untagged Either: $e", e)
                }
            }
        }
        return try {
            Either.Left(decoder.decodeSerializableValue(leftSerializer))
        } catch (_: Exception) {
            try {
                Either.Right(decoder.decodeSerializableValue(rightSerializer))
            } catch (e: Exception) {
                throw SerializationException("Failed to deserialize untagged Either: $e", e)
            }
        }
    }
}
