// port-lint: source into_either.rs
package io.github.kotlinmania.either

/**
 * Provides methods for converting a type into either a [Either.Left] or [Either.Right]
 * variant of [Either].
 */
public interface IntoEither<T> {
    /**
     * Converts this value into a [Either.Left] variant of [Either] if [intoLeft] is `true`.
     * Converts this value into a [Either.Right] variant of [Either] otherwise.
     */
    public fun intoEither(intoLeft: Boolean): Either<T, T>

    /**
     * Converts this value into a [Either.Left] variant of [Either] if [predicate] returns `true`.
     * Converts this value into a [Either.Right] variant of [Either] otherwise.
     */
    public fun intoEitherWith(predicate: (T) -> Boolean): Either<T, T>
}

/**
 * Converts this value into a [Either.Left] variant of [Either] if [intoLeft] is `true`.
 * Converts this value into a [Either.Right] variant of [Either] otherwise.
 */
public fun <T> T.intoEither(intoLeft: Boolean): Either<T, T> =
    if (intoLeft) Either.Left(this) else Either.Right(this)

/**
 * Converts this value into a [Either.Left] variant of [Either] if [predicate] returns `true`.
 * Converts this value into a [Either.Right] variant of [Either] otherwise.
 */
public fun <T> T.intoEitherWith(predicate: (T) -> Boolean): Either<T, T> =
    intoEither(predicate(this))
