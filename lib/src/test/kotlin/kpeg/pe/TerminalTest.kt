package kpeg.pe

import kpeg.Option.None
import kpeg.Option.Some
import kpeg.PegParser.ParserState
import kpeg.TestUtils.a
import kpeg.TestUtils.alpha
import kpeg.TestUtils.d
import kpeg.TestUtils.delta
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import kotlin.test.assertEquals


@TestInstance(PER_CLASS)
class TerminalTest {

    private lateinit var ps: ParserState


    @Nested
    inner class Character {

        private val symA = object : Symbol<Char>(Terminal.Character { it == a }) {}
        private val symD = object : Symbol<Char>(Terminal.Character { it == d }) {}

        @Nested
        inner class PeekMethod {

            @Test
            fun `peek 1 char in correct string`() {
                ps = ParserState("$a", 0)
                val actualA = symA.peek(ps)
                assertEquals(expected = Some(a), actual = actualA)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek 2 chars in correct string`() {
                ps = ParserState("$a$d", 0)
                val actualA = symA.peek(ps)
                assertEquals(expected = Some(a), actual = actualA)
                assertEquals(expected = 0, actual = ps.i)

                ps.i += 1
                val actualD = symD.peek(ps)
                assertEquals(expected = Some(d), actual = actualD)
                assertEquals(expected = 1, actual = ps.i)
            }

            @Test
            fun `peek char in incorrect string`() {
                ps = ParserState("$d", 0)
                val actualA = symA.peek(ps)
                assertEquals(expected = None, actual = actualA)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek 2 chars in incorrect string`() {
                ps = ParserState("$d$a", 0)
                val actualA = symA.peek(ps)
                assertEquals(expected = None, actual = actualA)
                assertEquals(expected = 0, actual = ps.i)

                ps.i += 1
                val actualD = symD.peek(ps)
                assertEquals(expected = None, actual = actualD)
                assertEquals(expected = 1, actual = ps.i)
            }

            @Test
            fun `peek char in empty string`() {
                ps = ParserState("", 0)
                val actualA = symA.peek(ps)
                assertEquals(expected = None, actual = actualA)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek char that could not fit`() {
                ps = ParserState("$a", 1)
                val actualA = symA.peek(ps)
                assertEquals(expected = None, actual = actualA)
                assertEquals(expected = 1, actual = ps.i)
            }
        }

        @Nested
        inner class ParseMethod {

            @Test
            fun `parse 1 char in correct string`() {
                ps = ParserState("$a", 0)
                val actualA = symA.parse(ps)
                assertEquals(expected = Some(a), actual = actualA)
                assertEquals(expected = 1, actual = ps.i)
            }

            @Test
            fun `parse 2 chars in correct string`() {
                ps = ParserState("$a$d", 0)
                val actualA = symA.parse(ps)
                assertEquals(expected = Some(a), actual = actualA)
                assertEquals(expected = 1, actual = ps.i)

                val actualD = symD.parse(ps)
                assertEquals(expected = Some(d), actual = actualD)
                assertEquals(expected = 2, actual = ps.i)
            }

            @Test
            fun `parse char in incorrect string`() {
                ps = ParserState("$d", 0)
                val actualA = symA.parse(ps)
                assertEquals(expected = None, actual = actualA)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 2 chars in incorrect string`() {
                ps = ParserState("$d$a", 0)
                val actualA = symA.parse(ps)
                assertEquals(expected = None, actual = actualA)
                assertEquals(expected = 0, actual = ps.i)

                ps.i += 1
                val actualD = symD.parse(ps)
                assertEquals(expected = None, actual = actualD)
                assertEquals(expected = 1, actual = ps.i)
            }

            @Test
            fun `parse char in empty string`() {
                ps = ParserState("", 0)
                val actualA = symA.parse(ps)
                assertEquals(expected = None, actual = actualA)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse char that could not fit`() {
                ps = ParserState("$a", 1)
                val actualA = symA.parse(ps)
                assertEquals(expected = None, actual = actualA)
                assertEquals(expected = 1, actual = ps.i)
            }
        }
    }

    @Nested
    inner class Literal {

        private val symAlpha = object : Symbol<String>(Terminal.Literal(len = alpha.length) { it == alpha }) {}
        private val symDelta = object : Symbol<String>(Terminal.Literal(len = delta.length) { it == delta }) {}

        @Nested
        inner class PeekMethod {

            @Test
            fun `peek 1 literal in correct string`() {
                ps = ParserState(alpha, 0)
                val actualAlpha = symAlpha.peek(ps)
                assertEquals(expected = Some(alpha), actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek 2 literals in correct string`() {
                ps = ParserState("$alpha$delta", 0)
                val actualAlpha = symAlpha.peek(ps)
                assertEquals(expected = Some(alpha), actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)

                ps.i += alpha.length
                val actualDelta = symDelta.peek(ps)
                assertEquals(expected = Some(delta), actual = actualDelta)
                assertEquals(expected = alpha.length, actual = ps.i)
            }

            @Test
            fun `peek literal in incorrect string`() {
                ps = ParserState(delta, 0)
                val actualAlpha = symAlpha.peek(ps)
                assertEquals(expected = None, actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek 2 literals in incorrect string`() {
                ps = ParserState("$delta$alpha", 0)
                val actualAlpha = symAlpha.peek(ps)
                assertEquals(expected = None, actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)

                ps.i += delta.length
                val actualDelta = symDelta.peek(ps)
                assertEquals(expected = None, actual = actualDelta)
                assertEquals(expected = delta.length, actual = ps.i)
            }

            @Test
            fun `peek literal in empty string`() {
                ps = ParserState("", 0)
                val actualAlpha = symAlpha.peek(ps)
                assertEquals(expected = None, actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek literal that could not fit`() {
                ps = ParserState(alpha.dropLast(2), 0)
                val actualAlpha = symAlpha.peek(ps)
                assertEquals(expected = None, actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)
            }
        }

        @Nested
        inner class ParseMethod {

            @Test
            fun `parse 1 literal in correct string`() {
                ps = ParserState(alpha, 0)
                val actualAlpha = symAlpha.parse(ps)
                assertEquals(expected = Some(alpha), actual = actualAlpha)
                assertEquals(expected = alpha.length, actual = ps.i)
            }

            @Test
            fun `parse 2 literals in correct string`() {
                ps = ParserState("$alpha$delta", 0)
                val actualAlpha = symAlpha.parse(ps)
                assertEquals(expected = Some(alpha), actual = actualAlpha)
                assertEquals(expected = alpha.length, actual = ps.i)

                val actualDelta = symDelta.parse(ps)
                assertEquals(expected = Some(delta), actual = actualDelta)
                assertEquals(expected = alpha.length + delta.length, actual = ps.i)
            }

            @Test
            fun `parse literal in incorrect string`() {
                ps = ParserState(delta, 0)
                val actualAlpha = symAlpha.parse(ps)
                assertEquals(expected = None, actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 2 literals in incorrect string`() {
                ps = ParserState("$delta$alpha", 0)
                val actualAlpha = symAlpha.parse(ps)
                assertEquals(expected = None, actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)

                ps.i += delta.length
                val actualDelta = symDelta.parse(ps)
                assertEquals(expected = None, actual = actualDelta)
                assertEquals(expected = delta.length, actual = ps.i)
            }

            @Test
            fun `parse literal in empty string`() {
                ps = ParserState("", 0)
                val actualAlpha = symAlpha.parse(ps)
                assertEquals(expected = None, actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse literal that could not fit`() {
                ps = ParserState(alpha.dropLast(2), 0)
                val actualAlpha = symAlpha.parse(ps)
                assertEquals(expected = None, actual = actualAlpha)
                assertEquals(expected = 0, actual = ps.i)
            }
        }
    }
}