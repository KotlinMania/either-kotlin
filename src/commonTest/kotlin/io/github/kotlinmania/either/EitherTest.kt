// port-lint: tests src/lib.rs
package io.github.kotlinmania.either

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EitherTest {
    @Test
    fun basic() {
        var e: Either<Int, Int> = Either.Left(2)
        val r: Either<Int, Int> = Either.Right(2)
        assertEquals(Either.Left(2), e)
        e = r
        assertEquals(Either.Right(2), e)
        assertNull(e.left())
        assertEquals(2, e.right())
    }

    @Test
    fun isLeftAndIsRight() {
        val left: Either<Int, String> = Either.Left(1)
        val right: Either<Int, String> = Either.Right("hello")
        assertTrue(left.isLeft())
        assertFalse(left.isRight())
        assertTrue(right.isRight())
        assertFalse(right.isLeft())
    }

    @Test
    fun isLeftAnd() {
        val left: Either<Int, Int> = Either.Left(2)
        val left0: Either<Int, Int> = Either.Left(0)
        val right: Either<Int, Int> = Either.Right(2)
        assertTrue(left.isLeftAnd { it > 1 })
        assertFalse(left0.isLeftAnd { it > 1 })
        assertFalse(right.isLeftAnd { it > 1 })
    }

    @Test
    fun isRightAnd() {
        val right: Either<Int, Int> = Either.Right(2)
        val right0: Either<Int, Int> = Either.Right(0)
        val left: Either<Int, Int> = Either.Left(2)
        assertTrue(right.isRightAnd { it > 1 })
        assertFalse(right0.isRightAnd { it > 1 })
        assertFalse(left.isRightAnd { it > 1 })
    }

    @Test
    fun leftAndRight() {
        val left: Either<String, Int> = Either.Left("value")
        val right: Either<String, Int> = Either.Right(321)
        assertEquals("value", left.left())
        assertNull(left.right())
        assertNull(right.left())
        assertEquals(321, right.right())
    }

    @Test
    fun flip() {
        val left: Either<String, Int> = Either.Left("hello")
        val right: Either<String, Int> = Either.Right(42)
        assertEquals(Either.Right("hello"), left.flip())
        assertEquals(Either.Left(42), right.flip())
    }

    @Test
    fun mapLeft() {
        val left: Either<Int, String> = Either.Left(123)
        val right: Either<Int, String> = Either.Right("hello")
        assertEquals(Either.Left(246), left.mapLeft { it * 2 })
        assertEquals(Either.Right("hello"), right.mapLeft { it * 2 })
    }

    @Test
    fun mapRight() {
        val left: Either<Int, String> = Either.Left(123)
        val right: Either<Int, String> = Either.Right("hello")
        assertEquals(Either.Left(123), left.mapRight { it.uppercase() })
        assertEquals(Either.Right("HELLO"), right.mapRight { it.uppercase() })
    }

    @Test
    fun mapEither() {
        val left: Either<String, Int> = Either.Left("hello")
        val right: Either<String, Int> = Either.Right(42)
        assertEquals(Either.Left(5), left.mapEither({ it.length }, { it.toString() }))
        assertEquals(Either.Right("42"), right.mapEither({ it.length }, { it.toString() }))
    }

    @Test
    fun either() {
        val left: Either<Int, Int> = Either.Left(4)
        val right: Either<Int, Int> = Either.Right(-4)
        assertEquals(16, left.either({ it * it }, { -it }))
        assertEquals(4, right.either({ it * it }, { -it }))
    }

    @Test
    fun leftAnd() {
        val left: Either<Int, Int> = Either.Left(123)
        val right: Either<Int, Int> = Either.Right(123)
        assertEquals(Either.Right(246), left.leftAnd(Either.Right(246)))
        assertEquals(Either.Right(123), right.leftAnd(Either.Right(246)))
        assertEquals(Either.Right(123), right.leftAnd(Either.Left(246)))
    }

    @Test
    fun rightAnd() {
        val left: Either<Int, Int> = Either.Left(123)
        val right: Either<Int, Int> = Either.Right(123)
        assertEquals(Either.Left(123), left.rightAnd(Either.Right(246)))
        assertEquals(Either.Right(246), right.rightAnd(Either.Right(246)))
        assertEquals(Either.Left(246), right.rightAnd(Either.Left(246)))
    }

    @Test
    fun leftAndThen() {
        val left: Either<Int, String> = Either.Left(3)
        val right: Either<Int, String> = Either.Right("hello")
        assertEquals(Either.Right("3"), left.leftAndThen { Either.Right(it.toString()) })
        assertEquals(Either.Right("hello"), right.leftAndThen { Either.Right(it.toString()) })
    }

    @Test
    fun rightAndThen() {
        val left: Either<Int, String> = Either.Left(123)
        val right: Either<Int, String> = Either.Right("hello")
        assertEquals(Either.Left(123), left.rightAndThen { Either.Left(it.length) })
        assertEquals(Either.Left(5), right.rightAndThen { Either.Left(it.length) })
    }

    @Test
    fun leftOrDefaultAndRightOrDefault() {
        val left: Either<String, Int> = Either.Left("hello")
        val right: Either<String, Int> = Either.Right(42)
        assertEquals("hello", left.leftOrDefault())
        assertEquals(42, right.rightOrDefault())
    }

    @Test
    fun leftOrElseAndRightOrElse() {
        val left: Either<String, Int> = Either.Left("3")
        val right: Either<String, Int> = Either.Right(3)
        assertEquals("3", left.leftOrElse { it.toString() })
        assertEquals("3", right.leftOrElse { it.toString() })
        assertEquals(3, right.rightOrElse { it.length })
    }

    @Test
    fun unwrapLeftAndExpectLeft() {
        val left: Either<Int, String> = Either.Left(3)
        assertEquals(3, left.unwrapLeft())
        assertEquals(3, left.expectLeft("should be left"))
    }

    @Test
    fun unwrapRightAndExpectRight() {
        val right: Either<String, Int> = Either.Right(3)
        assertEquals(3, right.unwrapRight())
        assertEquals(3, right.expectRight("should be right"))
    }

    @Test
    fun inspectLeftAndInspectRight() {
        val left: Either<Int, Int> = Either.Left(2)
        val right: Either<Int, Int> = Either.Right(2)
        var leftSeen = 0
        var rightSeen = 0
        val leftResult = left.inspectLeft { leftSeen = it }.leftOr(0)
        val rightResult = right.inspectRight { rightSeen = it }.leftOr(0)
        assertEquals(2, leftResult)
        assertEquals(2, leftSeen)
        assertEquals(0, rightResult)
        assertEquals(2, rightSeen)
    }

    @Test
    fun flipAndIntoInner() {
        val left: Either<Int, Int> = Either.Left(123)
        val right: Either<Int, Int> = Either.Right(123)
        assertEquals(123, left.flip().intoInner())
        assertEquals(123, right.flip().intoInner())
    }

    @Test
    fun factorNone() {
        val left: Either<String?, Int?> = Either.Left("value")
        val right: Either<String?, Int?> = Either.Right(42)
        val leftNone: Either<String?, Int?> = Either.Left(null)
        val rightNone: Either<String?, Int?> = Either.Right(null)
        assertEquals(Either.Left("value"), left.factorNone())
        assertEquals(Either.Right(42), right.factorNone())
        assertNull(leftNone.factorNone())
        assertNull(rightNone.factorNone())
    }

    @Test
    fun factorFirstAndFactorSecond() {
        val left: Either<Pair<Int, String>, Pair<Int, Int>> = Either.Left(Pair(123, "a"))
        val right: Either<Pair<Int, String>, Pair<Int, Int>> = Either.Left(Pair(456, "b"))
        assertEquals(Pair(123, Either.Left("a")), left.factorFirst())
        assertEquals(Pair(456, Either.Left("b")), right.factorFirst())

        val leftS: Either<Pair<String, Int>, Pair<Int, Int>> = Either.Left(Pair("a", 123))
        val rightS: Either<Pair<String, Int>, Pair<Int, Int>> = Either.Left(Pair("b", 456))
        assertEquals(Pair(Either.Left("a"), 123), leftS.factorSecond())
        assertEquals(Pair(Either.Left("b"), 456), rightS.factorSecond())
    }
}

class IntoEitherTest {
    @Test
    fun intoEither() {
        val x = 0
        assertEquals(Either.Left(0), x.intoEither(true))
        assertEquals(Either.Right(0), x.intoEither(false))
    }

    @Test
    fun intoEitherWith() {
        fun isEven(x: Int): Boolean = x % 2 == 0

        assertEquals(Either.Left(0), 0.intoEitherWith { isEven(it) })
        assertEquals(Either.Right(1), 1.intoEitherWith { isEven(it) })
    }
}
