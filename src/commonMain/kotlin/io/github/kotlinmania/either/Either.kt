// port-lint: source lib.rs
package io.github.kotlinmania.either

import kotlin.jvm.JvmName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The enum [Either] with variants [Left] and [Right] is a general purpose
 * sum type with two cases.
 *
 * The [Either] type is symmetric and treats its variants the same way, without preference.
 *
 * Example:
 * ```kotlin
 * val values = listOf(Either.Left(1), Either.Right("two"), Either.Left(3))
 * val ints = values.mapNotNull { it.left() }
 * assertEquals(listOf(1, 3), ints)
 * ```
 */
@Serializable
public sealed class Either<out L, out R> {
    /**
     * A value of type [L].
     */
    @Serializable
    @SerialName("Left")
    public data class Left<out L>(
        public val value: L,
    ) : Either<L, Nothing>() {
        override fun toString(): String = "Left($value)"
    }

    /**
     * A value of type [R].
     */
    @Serializable
    @SerialName("Right")
    public data class Right<out R>(
        public val value: R,
    ) : Either<Nothing, R>() {
        override fun toString(): String = "Right($value)"
    }
}

/**
 * Return true if the value is the [Either.Left] variant.
 */
public fun <L, R> Either<L, R>.isLeft(): Boolean = this is Either.Left

/**
 * Returns true if the value is [Either.Left] and the value inside of it matches a [predicate].
 */
public fun <L, R> Either<L, R>.isLeftAnd(predicate: (L) -> Boolean): Boolean =
    when (this) {
        is Either.Left -> predicate(value)
        is Either.Right -> false
    }

/**
 * Returns true if the value is [Either.Right] and the value inside of it matches a [predicate].
 */
public fun <L, R> Either<L, R>.isRightAnd(predicate: (R) -> Boolean): Boolean =
    when (this) {
        is Either.Left -> false
        is Either.Right -> predicate(value)
    }

/**
 * Return true if the value is the [Either.Right] variant.
 */
public fun <L, R> Either<L, R>.isRight(): Boolean = this is Either.Right

/**
 * Convert the left side of [Either] to a nullable value.
 */
public fun <L, R> Either<L, R>.left(): L? =
    when (this) {
        is Either.Left -> value
        is Either.Right -> null
    }

/**
 * Convert the right side of [Either] to a nullable value.
 */
public fun <L, R> Either<L, R>.right(): R? =
    when (this) {
        is Either.Left -> null
        is Either.Right -> value
    }

/**
 * Convert the left side of [Either] to a nullable value.
 */
public fun <L, R> Either<L, R>.asLeft(): L? = left()

/**
 * Convert the right side of [Either] to a nullable value.
 */
public fun <L, R> Either<L, R>.asRight(): R? = right()

/**
 * Upcast reference to common type.
 */
public fun <L, R, M> Either<L, R>.asRef(): Either<M, R> where L : M =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> Either.Right(value)
    }

/**
 * Upcast reference to common type on right.
 */
public fun <L, R, S> Either<L, R>.asRefRight(): Either<L, S> where R : S =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> Either.Right(value)
    }

/**
 * Returns this either as a mutable representation.
 */
public fun <L, R> Either<L, R>.asMut(): Either<L, R> = this

/**
 * Convert pinned reference to projections.
 */
public fun <L, R> Either<L, R>.asPinRef(): Either<L, R> = this

/**
 * Convert pinned mutable reference to projections.
 */
public fun <L, R> Either<L, R>.asPinMut(): Either<L, R> = this

/**
 * Convert [Either] from left to right and right to left.
 */
public fun <L, R> Either<L, R>.flip(): Either<R, L> =
    when (this) {
        is Either.Left -> Either.Right(value)
        is Either.Right -> Either.Left(value)
    }

/**
 * Apply the function [f] on the value in the [Either.Left] variant if it is present.
 */
public fun <L, R, M> Either<L, R>.mapLeft(f: (L) -> M): Either<M, R> =
    when (this) {
        is Either.Left -> Either.Left(f(value))
        is Either.Right -> Either.Right(value)
    }

/**
 * Apply the function [f] on the value in the [Either.Right] variant if it is present.
 */
public fun <L, R, S> Either<L, R>.mapRight(f: (R) -> S): Either<L, S> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> Either.Right(f(value))
    }

/**
 * Returns the provided [default] (if [Either.Right]), or applies [f] to the contained value (if [Either.Left]).
 */
public fun <L, R, S> Either<L, R>.mapLeftOr(default: S, f: (L) -> S): S =
    when (this) {
        is Either.Left -> f(value)
        is Either.Right -> default
    }

/**
 * Returns the provided [default] (if [Either.Left]), or applies [f] to the contained value (if [Either.Right]).
 */
public fun <L, R, S> Either<L, R>.mapRightOr(default: S, f: (R) -> S): S =
    when (this) {
        is Either.Left -> default
        is Either.Right -> f(value)
    }

/**
 * Apply the functions [f] and [g] to the [Either.Left] and [Either.Right] variants respectively.
 */
public fun <L, R, M, S> Either<L, R>.mapEither(f: (L) -> M, g: (R) -> S): Either<M, S> =
    when (this) {
        is Either.Left -> Either.Left(f(value))
        is Either.Right -> Either.Right(g(value))
    }

/**
 * Similar to [mapEither], with an added context [ctx] accessible to both functions.
 */
public fun <L, R, M, S, Ctx> Either<L, R>.mapEitherWith(
    ctx: Ctx,
    f: (Ctx, L) -> M,
    g: (Ctx, R) -> S,
): Either<M, S> =
    when (this) {
        is Either.Left -> Either.Left(f(ctx, value))
        is Either.Right -> Either.Right(g(ctx, value))
    }

/**
 * Apply one of two functions depending on contents, unifying their result.
 */
public fun <L, R, T> Either<L, R>.either(f: (L) -> T, g: (R) -> T): T =
    when (this) {
        is Either.Left -> f(value)
        is Either.Right -> g(value)
    }

/**
 * Like [either], but provide some context [ctx] to whichever of the functions ends up being called.
 */
public fun <L, R, Ctx, T> Either<L, R>.eitherWith(
    ctx: Ctx,
    f: (Ctx, L) -> T,
    g: (Ctx, R) -> T,
): T =
    when (this) {
        is Either.Left -> f(ctx, value)
        is Either.Right -> g(ctx, value)
    }

/**
 * Returns [other] if the value is [Either.Left], otherwise returns the [Either.Right] value of this either.
 */
public fun <L, R, S> Either<L, R>.leftAnd(other: Either<S, R>): Either<S, R> =
    when (this) {
        is Either.Left -> other
        is Either.Right -> Either.Right(value)
    }

/**
 * Returns [other] if the value is [Either.Right], otherwise returns the [Either.Left] value of this either.
 */
public fun <L, R, S> Either<L, R>.rightAnd(other: Either<L, S>): Either<L, S> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> other
    }

/**
 * Apply the function [f] on the value in the [Either.Left] variant if it is present.
 */
public fun <L, R, S> Either<L, R>.leftAndThen(f: (L) -> Either<S, R>): Either<S, R> =
    when (this) {
        is Either.Left -> f(value)
        is Either.Right -> Either.Right(value)
    }

/**
 * Apply the function [f] on the value in the [Either.Right] variant if it is present.
 */
public fun <L, R, S> Either<L, R>.rightAndThen(f: (R) -> Either<L, S>): Either<L, S> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> f(value)
    }

/**
 * Return left value or given [other] value.
 */
public fun <L, R> Either<L, R>.leftOr(other: L): L =
    when (this) {
        is Either.Left -> value
        is Either.Right -> other
    }

/**
 * Return left value or default.
 */
public fun <L, R> Either<L, R>.leftOrDefault(): L? =
    when (this) {
        is Either.Left -> value
        is Either.Right -> null
    }

/**
 * Returns left value or computes it from a closure [f].
 */
public fun <L, R> Either<L, R>.leftOrElse(f: (R) -> L): L =
    when (this) {
        is Either.Left -> value
        is Either.Right -> f(value)
    }

/**
 * Return right value or given [other] value.
 */
public fun <L, R> Either<L, R>.rightOr(other: R): R =
    when (this) {
        is Either.Left -> other
        is Either.Right -> value
    }

/**
 * Return right value or default.
 */
public fun <L, R> Either<L, R>.rightOrDefault(): R? =
    when (this) {
        is Either.Left -> null
        is Either.Right -> value
    }

/**
 * Returns right value or computes it from a closure [f].
 */
public fun <L, R> Either<L, R>.rightOrElse(f: (L) -> R): R =
    when (this) {
        is Either.Left -> f(value)
        is Either.Right -> value
    }

/**
 * Returns the left value, or throws [IllegalStateException] if right.
 */
public fun <L, R> Either<L, R>.unwrapLeft(): L =
    when (this) {
        is Either.Left -> value
        is Either.Right -> throw IllegalStateException("called unwrapLeft on a Right value: $value")
    }

/**
 * Returns the right value, or throws [IllegalStateException] if left.
 */
public fun <L, R> Either<L, R>.unwrapRight(): R =
    when (this) {
        is Either.Right -> value
        is Either.Left -> throw IllegalStateException("called unwrapRight on a Left value: $value")
    }

/**
 * Returns the left value, or throws [IllegalStateException] with [message] if right.
 */
public fun <L, R> Either<L, R>.expectLeft(message: String): L =
    when (this) {
        is Either.Left -> value
        is Either.Right -> throw IllegalStateException("$message: $value")
    }

/**
 * Returns the right value, or throws [IllegalStateException] with [message] if left.
 */
public fun <L, R> Either<L, R>.expectRight(message: String): R =
    when (this) {
        is Either.Right -> value
        is Either.Left -> throw IllegalStateException("$message: $value")
    }

/**
 * Calls a function [f] with a reference to the contained value if [Either.Left].
 */
public fun <L, R> Either<L, R>.inspectLeft(f: (L) -> Unit): Either<L, R> {
    if (this is Either.Left) f(value)
    return this
}

/**
 * Calls a function [f] with a reference to the contained value if [Either.Right].
 */
public fun <L, R> Either<L, R>.inspectRight(f: (R) -> Unit): Either<L, R> {
    if (this is Either.Right) f(value)
    return this
}

/**
 * Convert the contained value into common supertype [T].
 */
public fun <T, L : T, R : T> Either<L, R>.eitherInto(): T =
    when (this) {
        is Either.Left -> value
        is Either.Right -> value
    }

/**
 * Convert the inner value to an iterator when both left and right are iterables of the same type.
 */
public fun <T> Either<Iterable<T>, Iterable<T>>.intoIter(): Iterator<T> =
    when (this) {
        is Either.Left -> value.iterator()
        is Either.Right -> value.iterator()
    }

/**
 * Convert the inner value to an iterator when both left and right are iterators of the same type.
 */
@JvmName("intoIterFromIterators")
public fun <T> Either<Iterator<T>, Iterator<T>>.intoIter(): Iterator<T> =
    when (this) {
        is Either.Left -> value
        is Either.Right -> value
    }

/**
 * Borrow the inner value as an iterator when both left and right are iterables of the same type.
 */
public fun <T> Either<Iterable<T>, Iterable<T>>.iter(): Iterator<T> = intoIter()

/**
 * Borrow the inner value as an iterator when both left and right are iterators of the same type.
 */
@JvmName("iterFromIterators")
public fun <T> Either<Iterator<T>, Iterator<T>>.iter(): Iterator<T> = intoIter()

/**
 * Mutably borrow the inner value as an iterator.
 */
public fun <T> Either<Iterable<T>, Iterable<T>>.iterMut(): Iterator<T> = intoIter()

/**
 * Mutably borrow the inner value as an iterator when both left and right are iterators of the same type.
 */
@JvmName("iterMutFromIterators")
public fun <T> Either<Iterator<T>, Iterator<T>>.iterMut(): Iterator<T> = intoIter()

/**
 * Converts an [Either] of [Iterable]s to be an iterator of [Either] elements.
 */
public fun <L, R> Either<Iterable<L>, Iterable<R>>.factorIntoIter(): Iterator<Either<L, R>> =
    IterEither(mapEither({ it.iterator() }, { it.iterator() }))

/**
 * Converts an [Either] of [Iterator]s to be an iterator of [Either] elements.
 */
@JvmName("factorIntoIterFromIterators")
public fun <L, R> Either<Iterator<L>, Iterator<R>>.factorIntoIter(): Iterator<Either<L, R>> =
    IterEither(this)

/**
 * Borrows an [Either] of [Iterable]s to be an iterator of [Either] elements.
 */
public fun <L, R> Either<Iterable<L>, Iterable<R>>.factorIter(): Iterator<Either<L, R>> =
    factorIntoIter()

/**
 * Borrows an [Either] of [Iterator]s to be an iterator of [Either] elements.
 */
@JvmName("factorIterFromIterators")
public fun <L, R> Either<Iterator<L>, Iterator<R>>.factorIter(): Iterator<Either<L, R>> =
    factorIntoIter()

/**
 * Mutably borrows an [Either] of [Iterable]s to be an iterator of [Either] elements.
 */
public fun <L, R> Either<Iterable<L>, Iterable<R>>.factorIterMut(): Iterator<Either<L, R>> =
    factorIntoIter()

/**
 * Mutably borrows an [Either] of [Iterator]s to be an iterator of [Either] elements.
 */
@JvmName("factorIterMutFromIterators")
public fun <L, R> Either<Iterator<L>, Iterator<R>>.factorIterMut(): Iterator<Either<L, R>> =
    factorIntoIter()

/**
 * Factors out null from an [Either] of nullable values.
 */
public fun <L, R> Either<L?, R?>.factorNone(): Either<L, R>? =
    when (this) {
        is Either.Left -> value?.let { Either.Left(it) }
        is Either.Right -> value?.let { Either.Right(it) }
    }

/**
 * Factors out a homogeneous type from an [Either] of [Result]s where failure is the homogeneous type.
 */
public fun <L, R> Either<Result<L>, Result<R>>.factorErr(): Result<Either<L, R>> =
    when (this) {
        is Either.Left -> value.map { Either.Left(it) }
        is Either.Right -> value.map { Either.Right(it) }
    }

/**
 * Factors out a homogeneous type from an [Either] of [Result]s where success is the homogeneous type.
 */
public fun <T> Either<Result<T>, Result<T>>.factorOk(): Result<T> =
    when (this) {
        is Either.Left -> value
        is Either.Right -> value
    }

/**
 * Factor out a homogeneous type from an either of pairs (first element).
 */
public fun <T, L, R> Either<Pair<T, L>, Pair<T, R>>.factorFirst(): Pair<T, Either<L, R>> =
    when (this) {
        is Either.Left -> Pair(value.first, Either.Left(value.second))
        is Either.Right -> Pair(value.first, Either.Right(value.second))
    }

/**
 * Factor out a homogeneous type from an either of pairs (second element).
 */
public fun <T, L, R> Either<Pair<L, T>, Pair<R, T>>.factorSecond(): Pair<Either<L, R>, T> =
    when (this) {
        is Either.Left -> Pair(Either.Left(value.first), value.second)
        is Either.Right -> Pair(Either.Right(value.first), value.second)
    }

/**
 * Extract the value of an either over two equivalent types.
 */
public fun <T> Either<T, T>.intoInner(): T =
    when (this) {
        is Either.Left -> value
        is Either.Right -> value
    }

/**
 * Map [f] over the contained value and return the result in the corresponding variant.
 */
public fun <T, M> Either<T, T>.map(f: (T) -> M): Either<M, M> =
    when (this) {
        is Either.Left -> Either.Left(f(value))
        is Either.Right -> Either.Right(f(value))
    }

/**
 * Maps an either to a cloned version.
 */
public fun <L, R> Either<L, R>.cloned(): Either<L, R> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> Either.Right(value)
    }

/**
 * Maps an either to a copied version.
 */
public fun <L, R> Either<L, R>.copied(): Either<L, R> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> Either.Right(value)
    }

/**
 * Clones this either.
 */
public fun <L, R> Either<L, R>.clone(): Either<L, R> = cloned()

/**
 * Copies values from source.
 */
public fun <L, R> Either<L, R>.cloneFrom(source: Either<L, R>): Either<L, R> = source.cloned()

/**
 * Convert from standard Result to Either with success to Right and failure to Left.
 */
public fun <R> from(result: Result<R>): Either<Throwable, R> =
    result.fold(
        onSuccess = { Either.Right(it) },
        onFailure = { Either.Left(it) },
    )
