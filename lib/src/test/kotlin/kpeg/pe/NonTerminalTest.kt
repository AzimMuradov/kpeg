package kpeg.pe

import kpeg.Option
import kpeg.Option.None
import kpeg.Option.Some
import kpeg.PegParser
import kpeg.PegParser.ParserState
import kpeg.TestUtils
import kpeg.TestUtils.a
import kpeg.TestUtils.d
import kpeg.unwrap
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kpeg.TestUtils.PTDataForRepeated as PTData


@TestInstance(PER_CLASS)
class NonTerminalTest {

    private lateinit var ps: ParserState


    @Nested
    @TestInstance(PER_CLASS)
    inner class Optional {

        private val symOpt = object : Symbol<Option<Char>>(Optional(Terminal.Character { it == a })) {}

        @Nested
        @TestInstance(PER_CLASS)
        inner class PeekMethod {

            @Test
            fun `peek optional in correct string`() {
                ps = ParserState("$a", 0)
                val actualOpt = symOpt.peek(ps)
                assertEquals(expected = Some(Some(a)), actual = actualOpt)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek optional in incorrect string`() {
                ps = ParserState("$d", 0)
                val actualOpt = symOpt.peek(ps)
                assertEquals(expected = Some(None), actual = actualOpt)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek optional in empty string`() {
                ps = ParserState("", 0)
                val actualOpt = symOpt.peek(ps)
                assertEquals(expected = Some(None), actual = actualOpt)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `peek optional that could not fit`() {
                ps = ParserState("$a", 1)
                val actualOpt = symOpt.peek(ps)
                assertEquals(expected = Some(None), actual = actualOpt)
                assertEquals(expected = 1, actual = ps.i)
            }
        }

        @Nested
        @TestInstance(PER_CLASS)
        inner class ParseMethod {

            @Test
            fun `parse optional in correct string`() {
                ps = ParserState("$a", 0)
                val actualOpt = symOpt.parse(ps)
                assertEquals(expected = Some(Some(a)), actual = actualOpt)
                assertEquals(expected = 1, actual = ps.i)
            }

            @Test
            fun `parse optional in incorrect string`() {
                ps = ParserState("$d", 0)
                val actualOpt = symOpt.parse(ps)
                assertEquals(expected = Some(None), actual = actualOpt)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse optional in empty string`() {
                ps = ParserState("", 0)
                val actualOpt = symOpt.parse(ps)
                assertEquals(expected = Some(None), actual = actualOpt)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse optional that could not fit`() {
                ps = ParserState("$a", 1)
                val actualOpt = symOpt.parse(ps)
                assertEquals(expected = Some(None), actual = actualOpt)
                assertEquals(expected = 1, actual = ps.i)
            }
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class Repeated {

        @Nested
        @TestInstance(PER_CLASS)
        inner class PeekMethod {

            private fun correctProvider() = TestUtils.ptDataRepCorrectProvider()
            private fun incorrectProvider() = TestUtils.ptDataRepIncorrectProvider()
            private fun emptyProvider() = TestUtils.ptDataRepEmptyProvider()


            @ParameterizedTest
            @MethodSource("correctProvider")
            fun `peek repeated in correct string`(data: PTData) {
                ps = ParserState(data.s, 0)
                val actualRepeated = data.sym.peek(ps)
                assertEquals(expected = data.expected, actual = actualRepeated)
                assertEquals(expected = 0, actual = ps.i)
            }

            @ParameterizedTest
            @MethodSource("incorrectProvider")
            fun `peek repeated in incorrect string`(data: PTData) {
                ps = ParserState(data.s, 0)
                val actualRepeated = data.sym.peek(ps)
                assertEquals(expected = data.expected, actual = actualRepeated)
                assertEquals(expected = 0, actual = ps.i)
            }

            @ParameterizedTest
            @MethodSource("emptyProvider")
            fun `peek repeated in empty string`(data: PTData) {
                ps = ParserState(data.s, 0)
                val actualRepeated = data.sym.peek(ps)
                assertEquals(expected = data.expected, actual = actualRepeated)
                assertEquals(expected = 0, actual = ps.i)
            }
        }

        @Nested
        @TestInstance(PER_CLASS)
        inner class ParseMethod {

            private fun correctProvider() = TestUtils.ptDataRepCorrectProvider()
            private fun incorrectProvider() = TestUtils.ptDataRepIncorrectProvider()
            private fun emptyProvider() = TestUtils.ptDataRepEmptyProvider()


            @ParameterizedTest
            @MethodSource("correctProvider")
            fun `parse repeated in correct string`(data: PTData) {
                ps = ParserState(data.s, 0)
                val actualRepeated = data.sym.parse(ps)
                assertEquals(expected = data.expected, actual = actualRepeated)
                assertEquals(expected = data.expected.unwrap().size, actual = ps.i)
            }

            @ParameterizedTest
            @MethodSource("incorrectProvider")
            fun `parse repeated in incorrect string`(data: PTData) {
                ps = ParserState(data.s, 0)
                val actualRepeated = data.sym.parse(ps)
                assertEquals(expected = data.expected, actual = actualRepeated)
                assertEquals(expected = 0, actual = ps.i)
            }

            @ParameterizedTest
            @MethodSource("emptyProvider")
            fun `parse repeated in empty string`(data: PTData) {
                ps = ParserState(data.s, 0)
                val actualRepeated = data.sym.parse(ps)
                assertEquals(expected = data.expected, actual = actualRepeated)
                assertEquals(expected = 0, actual = ps.i)
            }
        }
    }
}