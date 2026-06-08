// port-lint: source src/into_either.rs
package io.github.kotlinmania.either

public fun interface IntoEither<T> {
    public fun intoEither(intoLeft: Boolean): Either<T, T>
}

public fun <T> T.intoEither(intoLeft: Boolean): Either<T, T> =
    if (intoLeft) Either.Left(this) else Either.Right(this)

public fun <T> T.intoEitherWith(predicate: (T) -> Boolean): Either<T, T> =
    intoEither(predicate(this))
