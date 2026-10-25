package `io`.`github`.`kotlinmania`.`either`.`serdeuntagged`

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.UntaggedContent
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
import io.github.kotlinmania.serdecore.de.*

//! Untagged serialization/deserialization support for `Either<L, R>`.
//!
//! `Either` uses default, externally-tagged representation.
//! However, sometimes it is useful to support several alternative types.
//! For example, we may have a field which is generally a `HashMap<String, i32>`
//! but in typical cases `Vec<String>` would suffice, too.
//!
//! ```rust
//! # fn main() -> Result<(), Box<dyn std::error::Error>> {
//! use either::Either;
//! use std::collections::HashMap;
//!
//! #[derive(serde::Serialize, serde::Deserialize, Debug)]
//! #[serde(transparent)]
//! struct IntOrString {
//!     #[serde(with = "either::serde_untagged")]
//!     inner: Either<Vec<String>, HashMap<String, i32>>
//! };
//!
//! // serialization
//! let data = IntOrString {
//!     inner: Either::Left(vec!["Hello".to_string()])
//! };
//! // notice: no tags are emitted.
//! assert_eq!(serde_json::to_string(&data)?, r#"["Hello"]"#);
//!
//! // deserialization
//! let data: IntOrString = serde_json::from_str(
//!     r#"{"a": 0, "b": 14}"#
//! )?;
//! println!("found {:?}", data);
//! # Ok(())
//! # }
//! ```
// Serde trait imports are mapped to the Kotlin interfaces above.

private sealed class `Either`<out `L`, out `R`> {
    class `Left`<out `L`>(val value: `L`) : `Either`<`L`, Nothing>()
    class `Right`<out `R`>(val value: `R`) : `Either`<Nothing, `R`>()
}

private fun <`L`, `R`, `__rk2`> `__rk0`(value: `Either`<`L`, `R`>, serializer: Serializer<`__rk2`>,
    encode0: (`L`) -> Serialize,
    encode1: (`R`) -> Serialize
): SerdeResult<`__rk2`> = when (value) {
    is `Either`.`Left` -> encode0(value.value).serialize(serializer)
    is `Either`.`Right` -> encode1(value.value).serialize(serializer)
}

private fun <`L`, `R`> `__rk1`(deserializer: Deserializer,
    decode0: Deserialize<`L`>,
    decode1: Deserialize<`R`>
): SerdeResult<`Either`<`L`, `R`>> =
    UntaggedContent.read(deserializer).flatMap { content ->
        val attempt0 = content.decode(decode0)
        if (attempt0 is SerdeResult.Success) return@flatMap SerdeResult.success(`Either`.`Left`(attempt0.value))
        val attempt1 = content.decode(decode1)
        if (attempt1 is SerdeResult.Success) return@flatMap SerdeResult.success(`Either`.`Right`(attempt1.value))
        SerdeResult.failure(SerdeError.custom("data did not match any variant of untagged enum Either"))
    }

public fun <`L`, `R`, `__rk3`> `serialize`(
    `this`: `io`.`github`.`kotlinmania`.`either`.`Either`<`L`, `R`>,
    `serializer`: Serializer<`__rk3`>,
    `__rk4`: (`L`) -> Serialize,
    `__rk5`: (`R`) -> Serialize
): SerdeResult<`__rk3`> {
    val `untagged`: `Either`<`L`, `R`> = run __rk7@ {
        val `__rk6` = `this`
        if (`__rk6` is `io`.`github`.`kotlinmania`.`either`.`Either`.`Left`) {
            val `__rk8` = `__rk6`.value
            return@__rk7 `Either`.`Left`(`__rk8`)
        }
        if (`__rk6` is `io`.`github`.`kotlinmania`.`either`.`Either`.`Right`) {
            val `__rk9` = `__rk6`.value
            return@__rk7 `Either`.`Right`(`__rk9`)
        }
        error("unreachable: exhaustive source match")
    }
    return `__rk0`(`untagged`, `serializer`, `__rk4`, `__rk5`)
    
}

public fun <`L`, `R`> `deserialize`(
    `deserializer`: Deserializer,
    `__rk11`: Deserialize<`L`>,
    `__rk12`: Deserialize<`R`>
): SerdeResult<`io`.`github`.`kotlinmania`.`either`.`Either`<`L`, `R`>> {
    return run __rk14@ {
        val `__rk13` = `__rk1`(`deserializer`, `__rk11`, `__rk12`)
        if (`__rk13` is SerdeResult.Success) {
            val `__rk15` = `__rk13`.value
            if (`__rk15` is `Either`.`Left`) {
                val `__rk16` = `__rk15`.value
                return@__rk14 SerdeResult.success(`io`.`github`.`kotlinmania`.`either`.`Either`.`Left`(`__rk16`))
            }
        }
        if (`__rk13` is SerdeResult.Success) {
            val `__rk17` = `__rk13`.value
            if (`__rk17` is `Either`.`Right`) {
                val `__rk18` = `__rk17`.value
                return@__rk14 SerdeResult.success(`io`.`github`.`kotlinmania`.`either`.`Either`.`Right`(`__rk18`))
            }
        }
        if (`__rk13` is SerdeResult.Failure) {
            val `__rk19` = `__rk13`.error
            return@__rk14 SerdeResult.failure(`__rk19`)
        }
        error("unreachable: exhaustive source match")
    }
    
}

