// port-lint: source src/iterator.rs
package io.github.kotlinmania.either

public data class IterEither<L, R>(
    val left: L?,
    val right: R?,
) {
    public companion object {
        public fun <L, R> fromLeft(left: L): IterEither<L, R> = IterEither(left, null)

        public fun <L, R> fromRight(right: R): IterEither<L, R> = IterEither(null, right)
    }
}
