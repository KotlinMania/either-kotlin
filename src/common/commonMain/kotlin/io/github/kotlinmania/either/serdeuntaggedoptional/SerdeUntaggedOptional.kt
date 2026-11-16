package io.github.kotlinmania.either.serdeuntaggedoptional

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serde.UntaggedContent
import io.github.kotlinmania.serdecore.de.*
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer

// ! Untagged serialization/deserialization support for `Option<Either<L, R>>`.
// !
// ! `Either` uses default, externally-tagged representation.
// ! However, sometimes it is useful to support several alternative types.
// ! For example, we may have a field which is generally a `HashMap<String, i32>`
// ! but in typical cases `Vec<String>` would suffice, too.
// !
// ! ```rust
// ! # fn main() -> Result<(), Box<dyn std::error::Error>> {
// ! use either::Either;
// ! use std::collections::HashMap;
// !
// ! #[derive(serde::Serialize, serde::Deserialize, Debug)]
// ! #[serde(transparent)]
// ! struct IntOrString {
// !     #[serde(with = "either::serde_untagged_optional")]
// !     inner: Option<Either<Vec<String>, HashMap<String, i32>>>
// ! };
// !
// ! // serialization
// ! let data = IntOrString {
// !     inner: Some(Either::Left(vec!["Hello".to_string()]))
// ! };
// ! // notice: no tags are emitted.
// ! assert_eq!(serde_json::to_string(&data)?, r#"["Hello"]"#);
// !
// ! // deserialization
// ! let data: IntOrString = serde_json::from_str(
// !     r#"{"a": 0, "b": 14}"#
// ! )?;
// ! println!("found {:?}", data);
// ! # Ok(())
// ! # }
// ! ```
// Serde trait imports are mapped to the Kotlin interfaces above.

private sealed class `Either`<out `L`, out `R`> {
    class `Left`<out `L`>(
        val value: `L`,
    ) : `Either`<`L`, Nothing>()
    class `Right`<out `R`>(
        val value: `R`,
    ) : `Either`<Nothing, `R`>()
}

private fun <`L`, `R`, `__rk2`> `__rk0`(
    value: `Either`<`L`, `R`>,
    serializer: Serializer<`__rk2`>,
    encode0: (`L`) -> Serialize,
    encode1: (`R`) -> Serialize,
): SerdeResult<`__rk2`> =
    when (value) {
        is `Either`.`Left` -> encode0(value.value).serialize(serializer)
        is `Either`.`Right` -> encode1(value.value).serialize(serializer)
    }

private fun <`L`, `R`> `__rk1`(
    deserializer: Deserializer,
    decode0: Deserialize<`L`>,
    decode1: Deserialize<`R`>,
): SerdeResult<`Either`<`L`, `R`>> =
    UntaggedContent.read(deserializer).flatMap { content ->
        val attempt0 = content.decode(decode0)
        if (attempt0 is SerdeResult.Success) return@flatMap SerdeResult.success(`Either`.`Left`(attempt0.value))
        val attempt1 = content.decode(decode1)
        if (attempt1 is SerdeResult.Success) return@flatMap SerdeResult.success(`Either`.`Right`(attempt1.value))
        SerdeResult.failure(SerdeError.custom("data did not match any variant of untagged enum Either"))
    }

public fun <`L`, `R`, `__rk3`> `serialize`(
    `this`: `io`.`github`.`kotlinmania`.`either`.`Either`<`L`, `R`>?,
    `serializer`: Serializer<`__rk3`>,
    `__rk4`: (`L`) -> Serialize,
    `__rk5`: (`R`) -> Serialize,
): SerdeResult<`__rk3`> {
    val `untagged`: `Either`<`L`, `R`>? =
        run __rk7@{
            val `__rk6` = `this`
            if (`__rk6` != null) {
                val `__rk8` = `__rk6`
                if (`__rk8` is `io`.`github`.`kotlinmania`.`either`.`Either`.`Left`) {
                    val `__rk9` = `__rk8`.value
                    return@__rk7 `Either`.`Left`(`__rk9`)
                }
            }
            if (`__rk6` != null) {
                val `__rk10` = `__rk6`
                if (`__rk10` is `io`.`github`.`kotlinmania`.`either`.`Either`.`Right`) {
                    val `__rk11` = `__rk10`.value
                    return@__rk7 `Either`.`Right`(`__rk11`)
                }
            }
            if (`__rk6` == null) {
                return@__rk7 null
            }
            error("unreachable: exhaustive source match")
        }
    return run {
        val `__rk12` = `untagged`
        if (`__rk12` == null) {
            `serializer`.serializeNone()
        } else {
            `serializer`.serializeSome(
                object : Serialize {
                    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
                        `__rk0`(`__rk12`, serializer, `__rk4`, `__rk5`)
                },
            )
        }
    }
}

public fun <`L`, `R`> `deserialize`(
    `deserializer`: Deserializer,
    `__rk14`: Deserialize<`L`>,
    `__rk15`: Deserialize<`R`>,
): SerdeResult<`io`.`github`.`kotlinmania`.`either`.`Either`<`L`, `R`>?> {
    return run __rk17@{
        val `__rk16` =
            nullableDeserialize(
                object : Deserialize<`Either`<`L`, `R`>> {
                    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<`Either`<`L`, `R`>> =
                        `__rk1`(deserializer, `__rk14`, `__rk15`)
                },
            ).deserialize(`deserializer`)
        if (`__rk16` is SerdeResult.Success) {
            val `__rk18` = `__rk16`.value
            if (`__rk18` != null) {
                val `__rk19` = `__rk18`
                if (`__rk19` is `Either`.`Left`) {
                    val `__rk20` = `__rk19`.value
                    return@__rk17 SerdeResult.success(
                        `io`.`github`.`kotlinmania`.`either`.`Either`
                            .`Left`(`__rk20`),
                    )
                }
            }
        }
        if (`__rk16` is SerdeResult.Success) {
            val `__rk21` = `__rk16`.value
            if (`__rk21` != null) {
                val `__rk22` = `__rk21`
                if (`__rk22` is `Either`.`Right`) {
                    val `__rk23` = `__rk22`.value
                    return@__rk17 SerdeResult.success(
                        `io`.`github`.`kotlinmania`.`either`.`Either`
                            .`Right`(`__rk23`),
                    )
                }
            }
        }
        if (`__rk16` is SerdeResult.Success) {
            val `__rk24` = `__rk16`.value
            if (`__rk24` == null) {
                return@__rk17 SerdeResult.success(null)
            }
        }
        if (`__rk16` is SerdeResult.Failure) {
            val `__rk25` = `__rk16`.error
            return@__rk17 SerdeResult.failure(`__rk25`)
        }
        error("unreachable: exhaustive source match")
    }
}
