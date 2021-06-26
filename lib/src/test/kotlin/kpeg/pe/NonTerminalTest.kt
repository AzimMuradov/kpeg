package kpeg.pe

import kpeg.Option
import kpeg.Option.None
import kpeg.Option.Some
import kpeg.PegParser.ParserState
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

        private val symOpt = object : Symbol<Option<Char>>(Optional(Terminal.Character { it == a })) {}


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

    @Nested
    @TestInstance(PER_CLASS)
    inner class Repeated {

        private fun correctProvider() = ptDataRepCorrectProvider()
        private fun incorrectProvider() = ptDataRepIncorrectProvider()
        private fun emptyProvider() = ptDataRepEmptyProvider()


        @ParameterizedTest
        @MethodSource("correctProvider")
        fun `parse repeated in correct string`(data: PTDataRep) {
            ps = ParserState(data.s, 0)
            val actualRepeated = data.sym.parse(ps)
            assertEquals(expected = data.expected, actual = actualRepeated)
            assertEquals(expected = data.expected.get().size, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        fun `parse repeated in incorrect string`(data: PTDataRep) {
            ps = ParserState(data.s, 0)
            val actualRepeated = data.sym.parse(ps)
            assertEquals(expected = data.expected, actual = actualRepeated)
            assertEquals(expected = 0, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        fun `parse repeated in empty string`(data: PTDataRep) {
            ps = ParserState(data.s, 0)
            val actualRepeated = data.sym.parse(ps)
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

            private val symAndP = object : Symbol<Unit>(
                Predicate(type = And, pe = Terminal.Literal(len = alpha.length) { it == alpha })
            ) {}


            @Test
            fun `parse 'and' predicate in correct string`() {
                ps = ParserState(alpha, 0)
                val actualAndPredicate = symAndP.parse(ps)
                assertEquals(expected = Some(Unit), actual = actualAndPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'and' predicate in incorrect string`() {
                ps = ParserState(delta, 0)
                val actualAndPredicate = symAndP.parse(ps)
                assertEquals(expected = None, actual = actualAndPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'and' predicate in empty string`() {
                ps = ParserState("", 0)
                val actualAndPredicate = symAndP.parse(ps)
                assertEquals(expected = None, actual = actualAndPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'and' predicate that could not fit`() {
                ps = ParserState(alpha, alpha.length)
                val actualAndPredicate = symAndP.parse(ps)
                assertEquals(expected = None, actual = actualAndPredicate)
                assertEquals(expected = alpha.length, actual = ps.i)
            }
        }

        @Nested
        @TestInstance(PER_CLASS)
        inner class Not {

            private val symNotP = object : Symbol<Unit>(
                Predicate(type = Not, pe = Terminal.Literal(len = alpha.length) { it == alpha })
            ) {}


            @Test
            fun `parse 'not' predicate in correct string`() {
                ps = ParserState(delta, 0)
                val actualNotPredicate = symNotP.parse(ps)
                assertEquals(expected = Some(Unit), actual = actualNotPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'not' predicate in incorrect string`() {
                ps = ParserState(alpha, 0)
                val actualNotPredicate = symNotP.parse(ps)
                assertEquals(expected = None, actual = actualNotPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'not' predicate in empty string`() {
                ps = ParserState("", 0)
                val actualNotPredicate = symNotP.parse(ps)
                assertEquals(expected = Some(Unit), actual = actualNotPredicate)
                assertEquals(expected = 0, actual = ps.i)
            }

            @Test
            fun `parse 'not' predicate that could not fit`() {
                ps = ParserState(delta, delta.length)
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
        fun `parse sequence in correct string`(data: PTDataSeq) {
            ps = ParserState(data.s, 0)
            val actualSequence = data.sym.parse(ps)
            assertEquals(expected = data.expected, actual = actualSequence)
            assertEquals(expected = data.expected.get().length, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        fun `parse sequence in incorrect string`(data: PTDataSeq) {
            ps = ParserState(data.s, 0)
            val actualSequence = data.sym.parse(ps)
            assertEquals(expected = data.expected, actual = actualSequence)
            assertEquals(expected = 0, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        fun `parse sequence in empty string`(data: PTDataSeq) {
            ps = ParserState(data.s, 0)
            val actualSequence = data.sym.parse(ps)
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
        fun `parse prioritized choice in correct string`(data: PTDataPrChoice) {
            ps = ParserState(data.s, 0)
            val actualPrChoice = data.sym.parse(ps)
            assertEquals(expected = data.expected, actual = actualPrChoice)
            assertEquals(expected = data.expected.get().length, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        fun `parse prioritized choice in incorrect string`(data: PTDataPrChoice) {
            ps = ParserState(data.s, 0)
            val actualPrChoice = data.sym.parse(ps)
            assertEquals(expected = data.expected, actual = actualPrChoice)
            assertEquals(expected = 0, actual = ps.i)
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        fun `parse prioritized choice in empty string`(data: PTDataPrChoice) {
            ps = ParserState(data.s, 0)
            val actualPrChoice = data.sym.parse(ps)
            assertEquals(expected = data.expected, actual = actualPrChoice)
            assertEquals(expected = 0, actual = ps.i)
        }
    }
}