package kpeg.pe

import arrow.core.Some
import io.kotest.assertions.arrow.option.beNone
import io.kotest.assertions.arrow.option.shouldBeSome
import io.kotest.assertions.assertSoftly
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kpeg.ParseError
import kpeg.ParserState
import kpeg.testutils.OperatorsTestUtils.PtDataCh
import kpeg.testutils.OperatorsTestUtils.PtDataLit
import kpeg.testutils.OperatorsTestUtils.ptDataBiChCorrectProvider
import kpeg.testutils.OperatorsTestUtils.ptDataBiChIncorrectProvider
import kpeg.testutils.OperatorsTestUtils.ptDataChCorrectProvider
import kpeg.testutils.OperatorsTestUtils.ptDataChIncorrectProvider
import kpeg.testutils.OperatorsTestUtils.ptDataLitCorrectProvider
import kpeg.testutils.OperatorsTestUtils.ptDataLitIncorrectProvider
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


internal class OperatorsTest {

    private lateinit var ps: ParserState


    @Nested
    inner class BuiltIn {

        private fun correctProvider() = ptDataBiChCorrectProvider()
        private fun incorrectProvider() = ptDataBiChIncorrectProvider()


        @ParameterizedTest
        @MethodSource("correctProvider")
        fun `parse 1 built-in in correct string`(data: PtDataCh) {
            ps = ParserState("${data.c}")
            val actualChar = data.pe.parse(ps)

            actualChar shouldBeSome data.c

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome shouldBe listOf(
                    mapOf(data.pe to (1 to Some(data.c))),
                    emptyMap(),
                )
                errs should beEmpty()
            }
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        fun `parse 1 built-in in incorrect string`(data: PtDataCh) {
            ps = ParserState("${data.c}")
            val actualChar = data.pe.parse(ps)

            actualChar should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone shouldBe listOf(
                    mapOf(data.pe to listOf(ParseError(index = 0, message = "Wrong Character"))),
                    emptyMap(),
                )
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Wrong Character"))
            }
        }
    }

    @Nested
    inner class Character {

        private fun correctProvider() = ptDataChCorrectProvider()
        private fun incorrectProvider() = ptDataChIncorrectProvider()


        @ParameterizedTest
        @MethodSource("correctProvider")
        fun `parse 1 char in correct string`(data: PtDataCh) {
            ps = ParserState("${data.c}")
            val actualChar = data.pe.parse(ps)

            actualChar shouldBeSome data.c

            assertSoftly(ps) {
                i shouldBe 1
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        fun `parse 1 char in incorrect string`(data: PtDataCh) {
            ps = ParserState("${data.c}")
            val actualChar = data.pe.parse(ps)

            actualChar should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Wrong Character"))
            }
        }
    }

    @Nested
    inner class Literal {

        private fun correctProvider() = ptDataLitCorrectProvider()
        private fun incorrectProvider() = ptDataLitIncorrectProvider()


        @ParameterizedTest
        @MethodSource("correctProvider")
        fun `parse 1 literal in correct string`(data: PtDataLit) {
            ps = ParserState(data.l)
            val actualLiteral = data.pe.parse(ps)

            actualLiteral shouldBeSome data.l

            assertSoftly(ps) {
                i shouldBe data.l.length
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs should beEmpty()
            }
        }

        @ParameterizedTest
        @MethodSource("incorrectProvider")
        fun `parse 1 literal in incorrect string`(data: PtDataLit) {
            ps = ParserState(data.l)
            val actualLiteral = data.pe.parse(ps)

            actualLiteral should beNone()

            assertSoftly(ps) {
                i shouldBe 0
                ignoreWS shouldBe false
                memNone.forAll { it.shouldBeEmpty() }
                memSome.forAll { it.shouldBeEmpty() }
                errs shouldBe listOf(ParseError(index = 0, message = "Wrong Literal"))
            }
        }
    }
}