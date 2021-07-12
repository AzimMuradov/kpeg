package io.kpeg.pe

import io.kotest.assertions.arrow.option.beNone
import io.kotest.assertions.arrow.option.shouldBeSome
import io.kotest.assertions.assertSoftly
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kpeg.ParseError
import io.kpeg.ParserState
import io.kpeg.testutils.TerminalTestUtils.a
import io.kpeg.testutils.TerminalTestUtils.alpha
import io.kpeg.testutils.TerminalTestUtils.d
import io.kpeg.testutils.TerminalTestUtils.delta
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import io.kpeg.pe.Terminal.Character as Ch
import io.kpeg.pe.Terminal.Literal as Lit


class TerminalTest {

    private lateinit var ps: ParserState


    @Nested
    inner class Character {

        private val aPe = Ch { it == a }
        private val dPe = Ch { it == d }


        @Test
        fun `check logName`() {
            aPe.logName shouldBe "Character"
        }

        @Test
        fun `parse 1 char in correct string`() {
            ps = ParserState("$a")
            val actualA = aPe.parse(ps)

            actualA shouldBeSome a

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @Test
        fun `parse 2 chars in correct string`() {
            ps = ParserState("$a$d")
            val actualA = aPe.parse(ps)

            actualA shouldBeSome a

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }


            val actualD = dPe.parse(ps)

            actualD shouldBeSome d

            assertSoftly(ps) {
                i shouldBe 2
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @Test
        fun `parse 1 char in incorrect string`() {
            ps = ParserState("$d")
            val actualA = aPe.parse(ps)

            actualA should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Wrong Character"))
            }
        }

        @Test
        fun `parse 2 chars in incorrect string`() {
            ps = ParserState("$a$a")
            val actualA = aPe.parse(ps)

            actualA shouldBeSome a

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }


            val actualD = dPe.parse(ps)

            actualD should beNone()

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 1, message = "Wrong Character"))
            }
        }

        @Test
        fun `parse 1 char in empty string`() {
            ps = ParserState("")
            val actualA = aPe.parse(ps)

            actualA should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Can't parse Character - text is too short"))
            }
        }

        @Test
        fun `parse 2 chars in too short string`() {
            ps = ParserState("$a")
            val actualA = aPe.parse(ps)

            actualA shouldBeSome a

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }


            val actualD = dPe.parse(ps)

            actualD should beNone()

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 1, message = "Can't parse Character - text is too short"))
            }
        }
    }

    @Nested
    inner class Literal {

        private val alphaPe = Lit(len = alpha.length) { it == alpha }
        private val deltaPe = Lit(len = delta.length) { it == delta }


        @Test
        fun `check logName`() {
            alphaPe.logName shouldBe "Literal"
        }

        @Test
        fun `parse 1 literal in correct string`() {
            ps = ParserState(alpha)
            val actualAlpha = alphaPe.parse(ps)

            actualAlpha shouldBeSome alpha

            assertSoftly(ps) {
                i shouldBe alpha.length
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @Test
        fun `parse 2 literals in correct string`() {
            ps = ParserState("$alpha$delta")
            val actualAlpha = alphaPe.parse(ps)

            actualAlpha shouldBeSome alpha

            assertSoftly(ps) {
                i shouldBe alpha.length
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }


            val actualDelta = deltaPe.parse(ps)

            actualDelta shouldBeSome delta

            assertSoftly(ps) {
                i shouldBe alpha.length + delta.length
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @Test
        fun `parse 1 literal in incorrect string`() {
            ps = ParserState(delta)
            val actualAlpha = alphaPe.parse(ps)

            actualAlpha should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Wrong Literal"))
            }
        }

        @Test
        fun `parse 2 literals in incorrect string`() {
            ps = ParserState("$alpha$alpha")
            val actualAlpha = alphaPe.parse(ps)

            actualAlpha shouldBeSome alpha

            assertSoftly(ps) {
                i shouldBe alpha.length
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }


            val actualDelta = deltaPe.parse(ps)

            actualDelta should beNone()

            assertSoftly(ps) {
                i shouldBe alpha.length
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = alpha.length, message = "Wrong Literal"))
            }
        }

        @Test
        fun `parse 1 literal in empty string`() {
            ps = ParserState("")
            val actualAlpha = alphaPe.parse(ps)

            actualAlpha should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Can't parse Literal - text is too short"))
            }
        }

        @Test
        fun `parse 1 literal in too short string`() {
            ps = ParserState(alpha.dropLast(2))
            val actualAlpha = alphaPe.parse(ps)

            actualAlpha should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Can't parse Literal - text is too short"))
            }
        }

        @Test
        fun `parse 2 literals in too short string`() {
            ps = ParserState(alpha)
            val actualAlpha = alphaPe.parse(ps)

            actualAlpha shouldBeSome alpha

            assertSoftly(ps) {
                i shouldBe alpha.length
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }


            val actualDelta = deltaPe.parse(ps)

            actualDelta should beNone()

            assertSoftly(ps) {
                i shouldBe alpha.length
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = alpha.length,
                    message = "Can't parse Literal - text is too short"))
            }
        }
    }
}