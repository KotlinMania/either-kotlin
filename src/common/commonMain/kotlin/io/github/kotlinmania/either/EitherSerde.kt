package io.github.kotlinmania.either

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.*
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer

// / The enum `Either` with variants `Left` and `Right` is a general purpose
// / sum type with two cases.
// /
// / The `Either` type is symmetric and treats its variants the same way, without
// / preference.
// / (For representing success or error, use the regular `Result` enum instead.)
public object `EitherSerde` {
    fun <`L`, `R`> value(value: `Either`<`L`, `R`>, `__serde1`: (`L`) -> Serialize, `__serde2`: (`R`) -> Serialize): Serialize =
        object : Serialize {
            override fun <`__serde0`> serialize(serializer: Serializer<`__serde0`>): SerdeResult<`__serde0`> =
                `EitherSerde`.serialize(value, serializer, `__serde1`, `__serde2`)
        }

    fun <`L`, `R`, `__serde0`> serialize(value: `Either`<`L`, `R`>, serializer: Serializer<`__serde0`>, `__serde1`: (`L`) -> Serialize, `__serde2`: (`R`) -> Serialize): SerdeResult<`__serde0`> =
        when (value) {
            // / A value of type `L`.
            is `Either`.`Left` -> serializer.serializeNewtypeVariant("Either", 0u, "Left", `__serde1`(value.value))
            // / A value of type `R`.
            is `Either`.`Right` -> serializer.serializeNewtypeVariant("Either", 1u, "Right", `__serde2`(value.value))
        }

    fun <`L`, `R`> decoder(`__serde3`: Deserialize<`L`>, `__serde4`: Deserialize<`R`>): Deserialize<`Either`<`L`, `R`>> =
        object : Deserialize<`Either`<`L`, `R`>> {
            override fun <`__serde5` : Deserializer> deserialize(deserializer: `__serde5`): SerdeResult<`Either`<`L`, `R`>> =
                deserializer.deserializeEnum(
                    "Either",
                    variants,
                    object : Visitor<`Either`<`L`, `R`>> {
                        override fun expecting(): String = "enum Either"
                        override fun <`__serde6` : EnumAccess> visitEnum(access: `__serde6`): SerdeResult<`Either`<`L`, `R`>> =
                            access.variantSeed(VariantSeed).flatMap { (index, payload) ->
                                when (index) {
                                    0 ->
                                        payload
                                            .newtypeVariantSeed(asSeed(`__serde3`))
                                            .map { `Either`.`Left`(it) }
                                    1 ->
                                        payload
                                            .newtypeVariantSeed(asSeed(`__serde4`))
                                            .map { `Either`.`Right`(it) }
                                    else -> error("unreachable: validated variant index")
                                }
                            }
                    },
                )
        }

    private fun <`T`> asSeed(delegate: Deserialize<`T`>): DeserializeSeed<`T`> =
        object : DeserializeSeed<`T`> {
            override fun <`__serde7` : Deserializer> deserialize(deserializer: `__serde7`): SerdeResult<`T`> =
                delegate.deserialize(deserializer)
        }

    private val variants = listOf("Left", "Right")
    private object VariantSeed : DeserializeSeed<Int> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Int> = deserializer.deserializeIdentifier(IdentifierVisitor)
    }
    private object IdentifierVisitor : Visitor<Int> {
        override fun expecting(): String = "variant identifier"
        override fun visitStr(v: String): SerdeResult<Int> =
            when (v) {
                "Left" -> SerdeResult.success(0)
                "Right" -> SerdeResult.success(1)
                else -> SerdeResult.failure(SerdeError.unknownVariant(v, variants))
            }
        override fun visitBytes(v: ByteArray): SerdeResult<Int> =
            when {
                v.contentEquals("Left".encodeToByteArray()) -> SerdeResult.success(0)
                v.contentEquals("Right".encodeToByteArray()) -> SerdeResult.success(1)
                else -> SerdeResult.failure(SerdeError.unknownVariant(v.decodeToString(), variants))
            }
        override fun visitU64(v: ULong): SerdeResult<Int> =
            when (v) {
                0uL -> SerdeResult.success(0)
                1uL -> SerdeResult.success(1)
                else ->
                    SerdeResult.failure(
                        SerdeError.invalidValue(
                            Unexpected.Unsigned(v),
                            object : Expected {
                                override fun expecting(): String = "variant index 0 <= i < 2"
                            },
                        ),
                    )
            }
    }
}
