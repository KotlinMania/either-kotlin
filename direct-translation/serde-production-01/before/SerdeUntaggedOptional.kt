// port-lint: source serde_untagged_optional.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.either

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.de.value.intoDeserializer
import io.github.kotlinmania.serdecore.ser.Serializer
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
import kotlin.native.HiddenFromObjC

public fun <Ok> serdeUntaggedOptionalSerialize(
    thisEither: Either<*, *>?,
    serializer: Serializer<Ok>,
): SerdeResult<Ok> =
    if (thisEither == null) {
        serializer.serializeNone()
    } else {
        when (thisEither) {
            is Either.Left -> serializer.serializeSome(wrapSerialize(thisEither.value))
            is Either.Right -> serializer.serializeSome(wrapSerialize(thisEither.value))
        }
    }

public fun <L, R> serdeUntaggedOptionalDeserialize(
    leftDeserialize: Deserialize<L>,
    rightDeserialize: Deserialize<R>,
): Deserialize<Either<L, R>?> =
    object : Deserialize<Either<L, R>?> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Either<L, R>?> {
            val visitor = UntaggedOptionalEitherVisitor(leftDeserialize, rightDeserialize)
            return deserializer.deserializeOption(visitor)
        }
    }

private class UntaggedOptionalEitherVisitor<L, R>(
    private val leftDeserialize: Deserialize<L>,
    private val rightDeserialize: Deserialize<R>,
) : Visitor<Either<L, R>?> {
    override fun expecting(): String = "optional untagged enum Either"

    override fun visitNone(): SerdeResult<Either<L, R>?> = SerdeResult.success(null)

    override fun visitUnit(): SerdeResult<Either<L, R>?> = SerdeResult.success(null)

    override fun <D : Deserializer> visitSome(deserializer: D): SerdeResult<Either<L, R>?> {
        val untagged = serdeUntaggedDeserialize(leftDeserialize, rightDeserialize)
        return untagged.deserialize(deserializer).map { it }
    }

    private inline fun <T : Deserializer> tryBoth(createD: () -> T): SerdeResult<Either<L, R>?> {
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

    override fun visitBool(v: Boolean): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitI8(v: Byte): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitI16(v: Short): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitI32(v: Int): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitI64(v: Long): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitU8(v: UByte): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitU16(v: UShort): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitU32(v: UInt): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitU64(v: ULong): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitF32(v: Float): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitF64(v: Double): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitChar(v: Char): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitStr(v: String): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitString(v: String): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }

    override fun visitBytes(v: ByteArray): SerdeResult<Either<L, R>?> = tryBoth { v.intoDeserializer() }
}

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
@HiddenFromObjC
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
