// port-lint: source serde_untagged.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.either

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.de.value.intoDeserializer
import io.github.kotlinmania.serdecore.ser.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlin.native.HiddenFromObjC

public fun <Ok> serdeUntaggedSerialize(
    thisEither: Either<*, *>,
    serializer: Serializer<Ok>,
): SerdeResult<Ok> =
    when (thisEither) {
        is Either.Left -> wrapSerialize(thisEither.value).serialize(serializer)
        is Either.Right -> wrapSerialize(thisEither.value).serialize(serializer)
    }

public fun <L, R> serdeUntaggedDeserialize(
    leftDeserialize: Deserialize<L>,
    rightDeserialize: Deserialize<R>,
): Deserialize<Either<L, R>> =
    object : Deserialize<Either<L, R>> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Either<L, R>> {
            val visitor = UntaggedEitherVisitor(leftDeserialize, rightDeserialize)
            return deserializer.deserializeAny(visitor)
        }
    }

private class UntaggedEitherVisitor<L, R>(
    private val leftDeserialize: Deserialize<L>,
    private val rightDeserialize: Deserialize<R>,
) : Visitor<Either<L, R>> {
    override fun expecting(): String = "untagged enum Either"

    private inline fun <T : Deserializer> tryBoth(createD: () -> T): SerdeResult<Either<L, R>> {
        when (val left = leftDeserialize.deserialize(createD())) {
            is SerdeResult.Success -> return SerdeResult.success(Either.Left(left.value))
            is SerdeResult.Failure -> Unit
        }
        when (val right = rightDeserialize.deserialize(createD())) {
            is SerdeResult.Success -> return SerdeResult.success(Either.Right(right.value))
            is SerdeResult.Failure -> Unit
        }
        return SerdeResult.failure(SerdeError.custom("data did not match any variant of untagged enum Either"))
    }

    override fun visitBool(v: Boolean): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitI8(v: Byte): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitI16(v: Short): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitI32(v: Int): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitI64(v: Long): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitU8(v: UByte): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitU16(v: UShort): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitU32(v: UInt): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitU64(v: ULong): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitF32(v: Float): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitF64(v: Double): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitChar(v: Char): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitStr(v: String): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitString(v: String): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitBytes(v: ByteArray): SerdeResult<Either<L, R>> = tryBoth { v.intoDeserializer() }

    override fun visitUnit(): SerdeResult<Either<L, R>> = tryBoth { Unit.intoDeserializer() }
}

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
@HiddenFromObjC
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
