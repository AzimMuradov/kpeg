package kpeg.pe

import arrow.core.None
import arrow.core.Some
import kpeg.ParserState
import kpeg.TestUtils.a
import kpeg.TestUtils.alpha
import kpeg.TestUtils.d
import kpeg.TestUtils.delta
import kpeg.TestUtils.get
import kpeg.TestUtils.ptDataPrChoiceCorrectProvider
import kpeg.TestUtils.ptDataPrChoiceEmptyProvider
import kpeg.TestUtils.ptDataPrChoiceIncorrectProvider
import kpeg.TestUtils.ptDataRepCorrectProvider
import kpeg.TestUtils.ptDataRepEmptyProvider
import kpeg.TestUtils.ptDataRepIncorrectProvider
import kpeg.TestUtils.ptDataSeqCorrectProvider
import kpeg.TestUtils.ptDataSeqEmptyProvider
import kpeg.TestUtils.ptDataSeqIncorrectProvider
import kpeg.pe.NonTerminal.Predicate.PredicateType.And
import kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kpeg.TestUtils.PTDataForPrioritizedChoice as PTDataPrChoice
import kpeg.TestUtils.PTDataForRepeated as PTDataRep
import kpeg.TestUtils.PTDataForSequence as PTDataSeq


@TestInstance(PER_CLASS)
class NonTerminalTest {

    private lateinit var ps: ParserState


    @Nested
    @TestInstance(PER_CLASS)
    inner class Optional {

        private val symOpt = NonTerminal.Optional(Terminal.Character { it == a })


        @Test
        fun `parse optional in correct string`() {
            ps = ParserState("$a")
            val actualOpt = symOpt.parse(ps)
            assertEquals(expected = Some(Some(a)), actual = actualOpt)
            assertEquals(expected = 1, actual = ps.i)
        }

        @Test
        fun `parse optional in incorrect string`() {
            ps = ParserState("$d")
            val actualOpt = symOpt.parse(ps)
            assertEquals(expected = Some(None), actual = actualOpt)
            assertEquals(expected = 0, actual = ps.i)
        }

        @Test
        fun `parse optional in empty string`() {
            ps = ParserState("")
            val actualOpt = symOpt.parse(ps)
            assertEquals(expected = Some(None), actual = actualOpt)
            assertEquals(expected = 0, actual = ps.i)
        }

        @Test
        fun `parse optional that could not fit`() {
            ps = ParserState("$a").apply { i = 1 }
            val actualOpt = symOpt.parse(ps)
            assertEquals(expected = Some(None), actual = actualOpt)
            assertEquals(expected = 1, actual = ps.i)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class Repeated {

        private fun correctProvider() = ptDataRepCorrectProvider()
        private fun incorrectProvider() = ptDataRepIncorrectProvider()
        private fun emptyProvider() = ptDataRepEmptyProvider()


        @ParameterizedTest
        @MethodSource("correctProvider")
        internal fun `parse repeated in correct string`(data: PTDataRep) {
            ps = ParserState(data.s)
            val actualRepeated = data.namedPE.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualRepeated)
            assertEquals(expected = data.expected.get().size, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        internal fun `parse repeated in incorrect string`(data: PTDataRep) {
            ps = ParserState(data.s)
            val actualRepeated = data.namedPE.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualRepeated)
            assertEquals(expected = 0, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        internal fun `parse repeated in empty string`(data: PTDataRep) {
            ps = ParserState(data.s)
            val actualRepeated = data.namedPE.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualRepeated)
            assertEquals(expected = 0, actual = ps.i)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class Predicate {

        @Nested
        @TestInstance(PER_CLASS)
        inner class And {

            private val symAndP = NonTerminal.Predicate(
                type = And,
                pe = Terminal.Literal(len = alpha.length) { it == alpha },
            )


            @Test
            fun `parse 'and' predicate in correct string`() {
                ps = ParserState(alpha)
                val actualAndPredicate = symAndP.parse(ps)
                assertEquals(expected = Some(Unit), actual = actualAndPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'and' predicate in incorrect string`() {
                ps = ParserState(delta)
                val actualAndPredicate = symAndP.parse(ps)
                assertEquals(expected = None, actual = actualAndPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'and' predicate in empty string`() {
                ps = ParserState("")
                val actualAndPredicate = symAndP.parse(ps)
                assertEquals(expected = None, actual = actualAndPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'and' predicate that could not fit`() {
                ps = ParserState(alpha).apply { i = alpha.length }
                val actualAndPredicate = symAndP.parse(ps)
                assertEquals(expected = None, actual = actualAndPredicate)
                assertEquals(expected = alpha.length, actual = ps.i)
            }
        }

        @Nested
        @TestInstance(PER_CLASS)
        inner class Not {

            private val symNotP = NonTerminal.Predicate(
                type = Not,
                pe = Terminal.Literal(len = alpha.length) { it == alpha },
            )


            @Test
            fun `parse 'not' predicate in correct string`() {
                ps = ParserState(delta)
                val actualNotPredicate = symNotP.parse(ps)
                assertEquals(expected = Some(Unit), actual = actualNotPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'not' predicate in incorrect string`() {
                ps = ParserState(alpha)
                val actualNotPredicate = symNotP.parse(ps)
                assertEquals(expected = None, actual = actualNotPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'not' predicate in empty string`() {
                ps = ParserState("")
                val actualNotPredicate = symNotP.parse(ps)
                assertEquals(expected = Some(Unit), actual = actualNotPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'not' predicate that could not fit`() {
                ps = ParserState(delta).apply { i = delta.length }
                val actualNotPredicate = symNotP.parse(ps)
                assertEquals(expected = Some(Unit), actual = actualNotPredicate)
                assertEquals(expected = delta.length, actual = ps.i)
            }
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class Sequence {

        private fun correctProvider() = ptDataSeqCorrectProvider()
        private fun incorrectProvider() = ptDataSeqIncorrectProvider()
        private fun emptyProvider() = ptDataSeqEmptyProvider()


        @ParameterizedTest
        @MethodSource("correctProvider")
        internal fun `parse sequence in correct string`(data: PTDataSeq) {
            ps = ParserState(data.s)
            val actualSequence = data.namedPE.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualSequence)
            assertEquals(expected = data.expected.get().length, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        internal fun `parse sequence in incorrect string`(data: PTDataSeq) {
            ps = ParserState(data.s)
            val actualSequence = data.namedPE.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualSequence)
            assertEquals(expected = 0, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        internal fun `parse sequence in empty string`(data: PTDataSeq) {
            ps = ParserState(data.s)
            val actualSequence = data.namedPE.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualSequence)
            assertEquals(expected = 0, actual = ps.i)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class PrioritizedChoice {

        private fun correctProvider() = ptDataPrChoiceCorrectProvider()
        private fun incorrectProvider() = ptDataPrChoiceIncorrectProvider()
        private fun emptyProvider() = ptDataPrChoiceEmptyProvider()


        @ParameterizedTest
        @MethodSource("correctProvider")
        internal fun `parse prioritized choice in correct string`(data: PTDataPrChoice) {
            ps = ParserState(data.s)
            val actualPrChoice = data.sym.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualPrChoice)
            assertEquals(expected = data.expected.get().length, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        internal fun `parse prioritized choice in incorrect string`(data: PTDataPrChoice) {
            ps = ParserState(data.s)
            val actualPrChoice = data.sym.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualPrChoice)
            assertEquals(expected = 0, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        internal fun `parse prioritized choice in empty string`(data: PTDataPrChoice) {
            ps = ParserState(data.s)
            val actualPrChoice = data.sym.pe.parse(ps)
            assertEquals(expected = data.expected, actual = actualPrChoice)
            assertEquals(expected = 0, actual = ps.i)
        }
    }
}