// port-lint: source src/lib.rs
package io.github.kotlinmania.either

public sealed class Either<out L, out R> {
    public data class Left<out L>(
        val value: L,
    ) : Either<L, Nothing>()

    public data class Right<out R>(
        val value: R,
    ) : Either<Nothing, R>()
}

public fun <L, R> Either<L, R>.isLeft(): Boolean = this is Either.Left

public fun <L, R> Either<L, R>.isRight(): Boolean = this is Either.Right

public fun <L, R> Either<L, R>.isLeftAnd(predicate: (L) -> Boolean): Boolean =
    when (this) {
        is Either.Left -> predicate(value)
        is Either.Right -> false
    }

public fun <L, R> Either<L, R>.isRightAnd(predicate: (R) -> Boolean): Boolean =
    when (this) {
        is Either.Left -> false
        is Either.Right -> predicate(value)
    }

public fun <L, R> Either<L, R>.left(): L? =
    when (this) {
        is Either.Left -> value
        is Either.Right -> null
    }

public fun <L, R> Either<L, R>.right(): R? =
    when (this) {
        is Either.Left -> null
        is Either.Right -> value
    }

public fun <L, R, M> Either<L, R>.asRef(): Either<M, R> where L : M {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is Either.Left -> Either.Left(value as M)
        is Either.Right -> this
    }
}

public fun <L, R, S> Either<L, R>.asRefRight(): Either<L, S> where R : S {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is Either.Left -> this
        is Either.Right -> Either.Right(value as S)
    }
}

public fun <L, R> Either<L, R>.flip(): Either<R, L> =
    when (this) {
        is Either.Left -> Either.Right(value)
        is Either.Right -> Either.Left(value)
    }

public fun <L, R, M> Either<L, R>.mapLeft(f: (L) -> M): Either<M, R> =
    when (this) {
        is Either.Left -> Either.Left(f(value))
        is Either.Right -> Either.Right(value)
    }

public fun <L, R, S> Either<L, R>.mapRight(f: (R) -> S): Either<L, S> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> Either.Right(f(value))
    }

public fun <L, R, S> Either<L, R>.mapLeftOr(default: S, f: (L) -> S): S =
    when (this) {
        is Either.Left -> f(value)
        is Either.Right -> default
    }

public fun <L, R, S> Either<L, R>.mapRightOr(default: S, f: (R) -> S): S =
    when (this) {
        is Either.Left -> default
        is Either.Right -> f(value)
    }

public fun <L, R, M, S> Either<L, R>.mapEither(f: (L) -> M, g: (R) -> S): Either<M, S> =
    when (this) {
        is Either.Left -> Either.Left(f(value))
        is Either.Right -> Either.Right(g(value))
    }

public fun <L, R, M, S, Ctx> Either<L, R>.mapEitherWith(
    ctx: Ctx,
    f: (Ctx, L) -> M,
    g: (Ctx, R) -> S,
): Either<M, S> =
    when (this) {
        is Either.Left -> Either.Left(f(ctx, value))
        is Either.Right -> Either.Right(g(ctx, value))
    }

public fun <L, R, T> Either<L, R>.either(f: (L) -> T, g: (R) -> T): T =
    when (this) {
        is Either.Left -> f(value)
        is Either.Right -> g(value)
    }

public fun <L, R, Ctx, T> Either<L, R>.eitherWith(
    ctx: Ctx,
    f: (Ctx, L) -> T,
    g: (Ctx, R) -> T,
): T =
    when (this) {
        is Either.Left -> f(ctx, value)
        is Either.Right -> g(ctx, value)
    }

public fun <L, R, S> Either<L, R>.leftAnd(other: Either<S, R>): Either<S, R> =
    when (this) {
        is Either.Left -> other
        is Either.Right -> Either.Right(value)
    }

public fun <L, R, S> Either<L, R>.rightAnd(other: Either<L, S>): Either<L, S> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> other
    }

public fun <L, R, S> Either<L, R>.leftAndThen(f: (L) -> Either<S, R>): Either<S, R> =
    when (this) {
        is Either.Left -> f(value)
        is Either.Right -> Either.Right(value)
    }

public fun <L, R, S> Either<L, R>.rightAndThen(f: (R) -> Either<L, S>): Either<L, S> =
    when (this) {
        is Either.Left -> Either.Left(value)
        is Either.Right -> f(value)
    }

public fun <L, R> Either<L, R>.leftOr(default: L): L =
    when (this) {
        is Either.Left -> value
        is Either.Right -> default
    }

public fun <L, R> Either<L, R>.leftOrDefault(): L where L : Any =
    when (this) {
        is Either.Left -> value
        is Either.Right -> throw UnsupportedOperationException("Left value not present")
    }

public fun <L, R> Either<L, R>.leftOrElse(f: (R) -> L): L =
    when (this) {
        is Either.Left -> value
        is Either.Right -> f(value)
    }

public fun <L, R> Either<L, R>.rightOr(default: R): R =
    when (this) {
        is Either.Left -> default
        is Either.Right -> value
    }

public fun <L, R> Either<L, R>.rightOrDefault(): R where R : Any =
    when (this) {
        is Either.Left -> throw UnsupportedOperationException("Right value not present")
        is Either.Right -> value
    }

public fun <L, R> Either<L, R>.rightOrElse(f: (L) -> R): R =
    when (this) {
        is Either.Left -> f(value)
        is Either.Right -> value
    }

public fun <L, R> Either<L, R>.unwrapLeft(): L where R : Any =
    when (this) {
        is Either.Left -> value
        is Either.Right -> throw IllegalStateException("Called unwrapLeft on Right: $value")
    }

public fun <L, R> Either<L, R>.unwrapRight(): R where L : Any =
    when (this) {
        is Either.Left -> throw IllegalStateException("Called unwrapRight on Left: $value")
        is Either.Right -> value
    }

public fun <L, R> Either<L, R>.expectLeft(message: String): L where R : Any =
    when (this) {
        is Either.Left -> value
        is Either.Right -> throw IllegalStateException("$message: $value")
    }

public fun <L, R> Either<L, R>.expectRight(message: String): R where L : Any =
    when (this) {
        is Either.Left -> throw IllegalStateException("$message: $value")
        is Either.Right -> value
    }

public fun <L, R> Either<L, R>.inspectLeft(f: (L) -> Unit): Either<L, R> {
    if (this is Either.Left) f(value)
    return this
}

public fun <L, R> Either<L, R>.inspectRight(f: (R) -> Unit): Either<L, R> {
    if (this is Either.Right) f(value)
    return this
}

public fun <L, R> Either<L, R>.asLeft(): L? =
    when (this) {
        is Either.Left -> value
        is Either.Right -> null
    }

public fun <L, R> Either<L, R>.asRight(): R? =
    when (this) {
        is Either.Left -> null
        is Either.Right -> value
    }

public fun <L : Iterable<T>, R : Iterable<T>, T> Either<L, R>.intoIter(): Either<Iterator<T>, Iterator<T>> =
    when (this) {
        is Either.Left -> Either.Left(value.iterator())
        is Either.Right -> Either.Right(value.iterator())
    }

public fun <L, R> Either<L?, R?>.factorNone(): Either<L, R>? =
    when (this) {
        is Either.Left -> value?.let { Either.Left(it) }
        is Either.Right -> value?.let { Either.Right(it) }
    }

// Rust's Either<Result<L, E>, Result<R, E>>::factor_err maps to Kotlin Result<Either<L, R>, E>
// which is not representable as an extension function because Kotlin's Result<V> has only one
// type parameter. This API requires the caller to destructure manually.

public fun <T, L, R> Either<Pair<T, L>, Pair<T, R>>.factorFirst(): Pair<T, Either<L, R>> =
    when (this) {
        is Either.Left -> Pair(value.first, Either.Left(value.second))
        is Either.Right -> Pair(value.first, Either.Right(value.second))
    }

public fun <T, L, R> Either<Pair<L, T>, Pair<R, T>>.factorSecond(): Pair<Either<L, R>, T> =
    when (this) {
        is Either.Left -> Pair(Either.Left(value.first), value.second)
        is Either.Right -> Pair(Either.Right(value.first), value.second)
    }

public fun <T> Either<T, T>.intoInner(): T =
    when (this) {
        is Either.Left -> value
        is Either.Right -> value
    }

public const val LEFT: String = "Left"
public const val RIGHT: String = "Right"
