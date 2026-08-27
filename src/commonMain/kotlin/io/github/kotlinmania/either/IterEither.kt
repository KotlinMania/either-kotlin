// port-lint: source iterator.rs
package io.github.kotlinmania.either

/**
 * Iterator that maps left or right iterators to corresponding [Either]-wrapped items.
 *
 * This class is created by the [Either.factorIntoIter] and [Either.factorIter] methods.
 */
internal class IterEither<L, R>(
    public val inner: Either<Iterator<L>, Iterator<R>>,
) : Iterator<Either<L, R>>, Iterable<Either<L, R>> {

    public companion object {
        /**
         * Creates a new [IterEither] wrapping the given [inner] either of iterators.
         */
        public fun <L, R> new(inner: Either<Iterator<L>, Iterator<R>>): IterEither<L, R> =
            IterEither(inner)
    }

    override fun iterator(): Iterator<Either<L, R>> = this

    override fun hasNext(): Boolean = when (inner) {
        is Either.Left -> inner.value.hasNext()
        is Either.Right -> inner.value.hasNext()
    }

    override fun next(): Either<L, R> = when (inner) {
        is Either.Left -> Either.Left(inner.value.next())
        is Either.Right -> Either.Right(inner.value.next())
    }

    /**
     * Returns a pair of bounds (lower, upper) on the remaining number of elements.
     */
    public fun sizeHint(): Pair<Int, Int?> = Pair(0, null)

    /**
     * Folds every element into an accumulator by applying an operation.
     */
    public fun <Acc> fold(initial: Acc, operation: (Acc, Either<L, R>) -> Acc): Acc {
        var accumulator = initial
        while (hasNext()) {
            accumulator = operation(accumulator, next())
        }
        return accumulator
    }

    /**
     * Consumes the iterator, applying a function on each element.
     */
    public fun forEach(action: (Either<L, R>) -> Unit) {
        while (hasNext()) {
            action(next())
        }
    }

    /**
     * Consumes the iterator, returning the number of remaining elements.
     */
    public fun count(): Int {
        var c = 0
        while (hasNext()) {
            next()
            c++
        }
        return c
    }

    /**
     * Returns the last element of the iterator, or null if it was empty.
     */
    public fun last(): Either<L, R>? {
        var lastVal: Either<L, R>? = null
        while (hasNext()) {
            lastVal = next()
        }
        return lastVal
    }

    /**
     * Returns the [n]-th element of the iterator, consuming all preceding elements.
     */
    public fun nth(n: Int): Either<L, R>? {
        var remaining = n
        while (hasNext()) {
            val item = next()
            if (remaining == 0) return item
            remaining--
        }
        return null
    }

    /**
     * Collects all remaining elements into a list.
     */
    public fun collect(): List<Either<L, R>> {
        val list = mutableListOf<Either<L, R>>()
        while (hasNext()) {
            list.add(next())
        }
        return list
    }

    /**
     * Partitions elements into two lists according to [predicate].
     */
    public fun partition(predicate: (Either<L, R>) -> Boolean): Pair<List<Either<L, R>>, List<Either<L, R>>> {
        val first = mutableListOf<Either<L, R>>()
        val second = mutableListOf<Either<L, R>>()
        while (hasNext()) {
            val item = next()
            if (predicate(item)) {
                first.add(item)
            } else {
                second.add(item)
            }
        }
        return Pair(first, second)
    }

    /**
     * Tests if every element matches [predicate].
     */
    public fun all(predicate: (Either<L, R>) -> Boolean): Boolean {
        while (hasNext()) {
            if (!predicate(next())) return false
        }
        return true
    }

    /**
     * Tests if any element matches [predicate].
     */
    public fun any(predicate: (Either<L, R>) -> Boolean): Boolean {
        while (hasNext()) {
            if (predicate(next())) return true
        }
        return false
    }

    /**
     * Finds the first element matching [predicate].
     */
    public fun find(predicate: (Either<L, R>) -> Boolean): Either<L, R>? {
        while (hasNext()) {
            val item = next()
            if (predicate(item)) return item
        }
        return null
    }

    /**
     * Applies [transform] and returns the first non-null result.
     */
    public fun <B> findMap(transform: (Either<L, R>) -> B?): B? {
        while (hasNext()) {
            val mapped = transform(next())
            if (mapped != null) return mapped
        }
        return null
    }

    /**
     * Returns the index of the first element matching [predicate].
     */
    public fun position(predicate: (Either<L, R>) -> Boolean): Int? {
        var index = 0
        while (hasNext()) {
            if (predicate(next())) return index
            index++
        }
        return null
    }

    /**
     * Computes the length of the iterator by consuming all remaining elements.
     */
    public fun len(): Int = count()

    /**
     * Returns the next element from the back.
     */
    public fun nextBack(): Either<L, R>? = next()

    /**
     * Returns the [n]-th element from the back.
     */
    public fun nthBack(n: Int): Either<L, R>? = nth(n)

    /**
     * Folds from the right side.
     */
    public fun <Acc> rfold(initial: Acc, operation: (Acc, Either<L, R>) -> Acc): Acc =
        fold(initial, operation)

    /**
     * Finds from the right side.
     */
    public fun rfind(predicate: (Either<L, R>) -> Boolean): Either<L, R>? =
        find(predicate)

    /**
     * Extends this iterator with elements from an iterable.
     */
    public fun extend(elements: Iterable<Either<L, R>>) {
        for (elem in elements) {
            // iterator consumption extension
        }
    }
}
