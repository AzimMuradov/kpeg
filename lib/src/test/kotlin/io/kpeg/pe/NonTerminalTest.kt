package io.kpeg.pe

import arrow.core.Eval.Now
import arrow.core.None
import arrow.core.Some
import io.kotest.assertions.arrow.option.beNone
import io.kotest.assertions.arrow.option.shouldBeSome
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kpeg.ParseError
import io.kpeg.ParserState
import io.kpeg.pe.NonTerminal.Predicate.PredicateType.And
import io.kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import io.kpeg.testutils.NonTerminalTestUtils.PrChoiceCorrect
import io.kpeg.testutils.NonTerminalTestUtils.PrChoiceEmpty
import io.kpeg.testutils.NonTerminalTestUtils.PrChoiceIncorrect
import io.kpeg.testutils.NonTerminalTestUtils.RepeatedCorrect
import io.kpeg.testutils.NonTerminalTestUtils.RepeatedEmpty
import io.kpeg.testutils.NonTerminalTestUtils.RepeatedIncorrect
import io.kpeg.testutils.NonTerminalTestUtils.SequenceCorrect
import io.kpeg.testutils.NonTerminalTestUtils.SequenceEmpty
import io.kpeg.testutils.NonTerminalTestUtils.SequenceException
import io.kpeg.testutils.NonTerminalTestUtils.SequenceIncorrect
import io.kpeg.testutils.NonTerminalTestUtils.a
import io.kpeg.testutils.NonTerminalTestUtils.alpha
import io.kpeg.testutils.NonTerminalTestUtils.d
import io.kpeg.testutils.NonTerminalTestUtils.delta
import io.kpeg.testutils.NonTerminalTestUtils.prChoiceCorrectProvider
import io.kpeg.testutils.NonTerminalTestUtils.prChoiceEmptyProvider
import io.kpeg.testutils.NonTerminalTestUtils.prChoiceIncorrectProvider
import io.kpeg.testutils.NonTerminalTestUtils.repCorrectProvider
import io.kpeg.testutils.NonTerminalTestUtils.repEmptyProvider
import io.kpeg.testutils.NonTerminalTestUtils.repIncorrectProvider
import io.kpeg.testutils.NonTerminalTestUtils.seqCorrectProvider
import io.kpeg.testutils.NonTerminalTestUtils.seqEmptyProvider
import io.kpeg.testutils.NonTerminalTestUtils.seqExProvider
import io.kpeg.testutils.NonTerminalTestUtils.seqIncorrectProvider
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import io.kpeg.pe.NonTerminal.Map as M
import io.kpeg.pe.NonTerminal.Optional as Opt
import io.kpeg.pe.NonTerminal.Predicate as Pred
import io.kpeg.pe.NonTerminal.PrioritizedChoice as PrCh
import io.kpeg.pe.NonTerminal.Repeated as Rep
import io.kpeg.pe.NonTerminal.Sequence as Seq
import io.kpeg.pe.Terminal.Character as Ch
import io.kpeg.pe.Terminal.Literal as Lit


class NonTerminalTest {

    private lateinit var ps: ParserState


    @Nested
    inner class Optional {

        private val optPe = Opt(Now(Ch { it == a }))


        @Test
        fun `check logName`() {
            optPe.logName shouldBe "Optional(Character)"
        }

        @Test
        fun `parse 1 optional in correct string`() {
            ps = ParserState("$a")
            val actualOpt = optPe.parse(ps)

            actualOpt shouldBeSome Some(a)

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @Test
        fun `parse 1 optional in incorrect string`() {
            ps = ParserState("$d")
            val actualOpt = optPe.parse(ps)

            actualOpt shouldBeSome None

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @Test
        fun `parse 1 optional in empty string`() {
            ps = ParserState("")
            val actualOpt = optPe.parse(ps)

            actualOpt shouldBeSome None

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }
    }

    @Nested
    inner class Repeated {

        private fun correctProvider() = repCorrectProvider()
        private fun incorrectProvider() = repIncorrectProvider()
        private fun emptyProvider() = repEmptyProvider()


        @Test
        fun `check logName`() {
            val repPe = Rep(1u..3u, Now(Ch { it == a }))

            repPe.logName shouldBe "Repeated(Character) 1..3 times"
        }

        @Test
        fun `wrong init`() {
            val e = shouldThrow<IllegalArgumentException> {
                Rep(2u..1u, Now(Ch { it == a }))
            }

            e.message shouldBe "Range is empty"
        }

        @ParameterizedTest
        @MethodSource("correctProvider")
        internal fun `parse repeated in correct string`(data: RepeatedCorrect) {
            ps = ParserState(data.s)
            val actualRepeated = data.pe.parse(ps)

            actualRepeated shouldBe data.expected

            assertSoftly(ps) {
                i shouldBe data.i
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        internal fun `parse repeated in incorrect string`(data: RepeatedIncorrect) {
            ps = ParserState(data.s)
            val actualRepeated = data.pe.parse(ps)

            actualRepeated should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe data.errs
            }
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        internal fun `parse repeated in empty string`(data: RepeatedEmpty) {
            ps = ParserState("")
            val actualRepeated = data.pe.parse(ps)

            actualRepeated shouldBe data.expected

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe data.errs
            }
        }
    }

    @Nested
    inner class Predicate {

        @Nested
        inner class And {

            private val andPredPe = Pred(
                type = And,
                pe = Now(Lit(len = alpha.length) { it == alpha }),
            )


            @Test
            fun `check logName`() {
                andPredPe.logName shouldBe "And(Literal)"
            }

            @Test
            fun `parse 1 'and' predicate in correct string`() {
                ps = ParserState(alpha)
                val actualAndPred = andPredPe.parse(ps)

                actualAndPred shouldBeSome Unit

                assertSoftly(ps) {
                    i shouldBe 0
                    ignoreWS shouldBe false
                    memNone.forAll { it.shouldBeEmpty() }
                    memSome.forAll { it.shouldBeEmpty() }
                    errs should beEmpty()
                }
            }

            @Test
            fun `parse 1 'and' predicate in incorrect string`() {
                ps = ParserState(delta)
                val actualAndPred = andPredPe.parse(ps)

                actualAndPred should beNone()

                assertSoftly(ps) {
                    i shouldBe 0
                    ignoreWS shouldBe false
                    memNone.forAll { it.shouldBeEmpty() }
                    memSome.forAll { it.shouldBeEmpty() }
                    errs shouldBe listOf(
                        ParseError(index = 0, message = "Wrong Literal"),
                        ParseError(index = 0, message = "Wrong And(Literal)"),
                    )
                }
            }

            @Test
            fun `parse 1 'and' predicate in empty string`() {
                ps = ParserState("")
                val actualAndPred = andPredPe.parse(ps)

                actualAndPred should beNone()

                assertSoftly(ps) {
                    i shouldBe 0
                    ignoreWS shouldBe false
                    memNone.forAll { it.shouldBeEmpty() }
                    memSome.forAll { it.shouldBeEmpty() }
                    errs shouldBe listOf(
                        ParseError(index = 0, message = "Can't parse Literal - text is too short"),
                        ParseError(index = 0, message = "Wrong And(Literal)"),
                    )
                }
            }
        }

        @Nested
        inner class Not {

            private val notPredPe = Pred(
                type = Not,
                pe = Now(Lit(len = alpha.length) { it == alpha }),
            )


            @Test
            fun `check logName`() {
                notPredPe.logName shouldBe "Not(Literal)"
            }

            @Test
            fun `parse 1 'not' predicate in correct string`() {
                ps = ParserState(delta)
                val actualNotPred = notPredPe.parse(ps)

                actualNotPred shouldBeSome Unit

                assertSoftly(ps) {
                    i shouldBe 0
                    ignoreWS shouldBe false
                    memNone.forAll { it.shouldBeEmpty() }
                    memSome.forAll { it.shouldBeEmpty() }
                    errs should beEmpty()
                }
            }

            @Test
            fun `parse 1 'not' predicate in incorrect string`() {
                ps = ParserState(alpha)
                val actualNotPred = notPredPe.parse(ps)

                actualNotPred should beNone()

                assertSoftly(ps) {
                    i shouldBe 0
                    ignoreWS shouldBe false
                    memNone.forAll { it.shouldBeEmpty() }
                    memSome.forAll { it.shouldBeEmpty() }
                    errs shouldBe listOf(ParseError(index = 0, message = "Wrong Not(Literal)"))
                }
            }

            @Test
            fun `parse 1 'not' predicate in empty string`() {
                ps = ParserState("")
                val actualNotPred = notPredPe.parse(ps)

                actualNotPred shouldBeSome Unit

                assertSoftly(ps) {
                    i shouldBe 0
                    ignoreWS shouldBe false
                    memNone.forAll { it.shouldBeEmpty() }
                    memSome.forAll { it.shouldBeEmpty() }
                    errs should beEmpty()
                }
            }
        }
    }

    @Nested
    inner class Sequence {

        private fun correctProvider() = seqCorrectProvider()
        private fun incorrectProvider() = seqIncorrectProvider()
        private fun emptyProvider() = seqEmptyProvider()
        private fun exProvider() = seqExProvider()


        @Test
        fun `check logName`() {
            val seqPe = Seq<String> {
                val symChar = +Now(Ch { it == a })
                val symLiteral = +Now(Lit(len = alpha.length) { it == alpha })

                value { "${symChar.get}${symLiteral.get}" }
            }

            seqPe.logName shouldBe "Sequence(Character, Literal)"
        }

        @ParameterizedTest
        @MethodSource("correctProvider")
        internal fun `parse sequence in correct string`(data: SequenceCorrect) {
            ps = ParserState(data.s)
            val actualSequence = data.pe.parse(ps)

            actualSequence shouldBe data.expected

            assertSoftly(ps) {
                i shouldBe data.i
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        internal fun `parse sequence in incorrect string`(data: SequenceIncorrect) {
            ps = ParserState(data.s)
            val actualSequence = data.pe.parse(ps)

            actualSequence should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe data.errs
            }
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        internal fun `parse sequence in empty string`(data: SequenceEmpty) {
            ps = ParserState("")
            val actualSequence = data.pe.parse(ps)

            actualSequence should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe data.errs
            }
        }

        @ParameterizedTest
        @MethodSource("exProvider")
        internal fun `create sequence wrongly`(data: SequenceException) {
            val e = shouldThrow<IllegalStateException> { data.peBlock() }

            e.message shouldBe "The sequence must contain 1 or more subexpressions"
        }
    }

    @Nested
    inner class PrioritizedChoice {

        private fun correctProvider() = prChoiceCorrectProvider()
        private fun incorrectProvider() = prChoiceIncorrectProvider()
        private fun emptyProvider() = prChoiceEmptyProvider()


        @Test
        fun `check logName`() {
            val prChoicePe = PrCh(
                Now(M(transform = { "$it" }, pe = Now(Ch { it == a }))),
                Now(Lit(len = alpha.length) { it == alpha }),
            )

            prChoicePe.logName shouldBe "PrioritizedChoice(Character / Literal)"
        }

        @ParameterizedTest
        @MethodSource("correctProvider")
        internal fun `parse prioritized choice in correct string`(data: PrChoiceCorrect) {
            ps = ParserState(data.s)
            val actualPrChoice = data.pe.parse(ps)

            actualPrChoice shouldBe data.expected

            assertSoftly(ps) {
                i shouldBe data.i
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        internal fun `parse prioritized choice in incorrect string`(data: PrChoiceIncorrect) {
            ps = ParserState(data.s)
            val actualPrChoice = data.pe.parse(ps)

            actualPrChoice should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe data.errs
            }
        }

        @ParameterizedTest
        @MethodSource("emptyProvider")
        internal fun `parse prioritized choice in empty string`(data: PrChoiceEmpty) {
            ps = ParserState("")
            val actualPrChoice = data.pe.parse(ps)

            actualPrChoice should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe data.errs
            }
        }
    }

    @Nested
    inner class Map {

        private val mapPe = M(transform = { it.toString() }, pe = Now(Ch { it == a }))


        @Test
        fun `check logName`() {
            mapPe.logName shouldBe "Character"
        }

        @Test
        fun `parse 1 map in correct string`() {
            ps = ParserState("$a")
            val actualMap = mapPe.parse(ps)

            actualMap shouldBeSome "$a"

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @Test
        fun `parse 1 map in incorrect string`() {
            ps = ParserState("$d")
            val actualMap = mapPe.parse(ps)

            actualMap should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Wrong Character"))
            }
        }

        @Test
        fun `parse 1 map in empty string`() {
            ps = ParserState("")
            val actualMap = mapPe.parse(ps)

            actualMap should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Can't parse Character - text is too short"))
            }
        }
    }
}