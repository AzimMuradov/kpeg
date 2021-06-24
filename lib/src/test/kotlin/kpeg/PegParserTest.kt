package kpeg

import kpeg.Option.None
import kpeg.Option.Some
import kpeg.pe.ParsingExpression
import kpeg.pe.Symbol
import kpeg.pe.SymbolBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class PegParserTest {

    companion object {
        const val a = 'a'
        const val d = 'd'
        const val o = 'o'
        const val alpha = "alpha"
        const val delta = "delta"
        const val omega = "omega"

        data class DataCC(val c1: Char, val c2: Char)
        data class DataCL(val c1: Char, val l2: String)
    }


    @Nested
    inner class Parse {

        private lateinit var p: PegParser


        @Nested
        @DisplayName(value = "parse 1 character")
        inner class Character {

            private lateinit var sym: Symbol<Char>

            private val peBlocks: List<() -> ParsingExpression<Char>> = listOf(
                { -> SymbolBuilder.char { it == a } },
                { -> SymbolBuilder.char(a) },
                { -> SymbolBuilder.char(a..a) },
                { -> SymbolBuilder.char(a, a, a) },
            )


            @Test
            fun `Some('a')`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<Char>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, "$a"), actual = Some(a))
                }
            }

            @Test
            fun `Some('o')`() {
                val peBlocksWithMultiChars: List<() -> ParsingExpression<Char>> = listOf(
                    { -> SymbolBuilder.char { it == a || it == o } },
                    { -> SymbolBuilder.char(a..o) },
                    { -> SymbolBuilder.char(a, a, o) },
                )

                for (peBlock in peBlocksWithMultiChars) {
                    sym = object : Symbol<Char>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, "$o"), actual = Some(o))
                }
            }

            @Test
            fun `None - empty string`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<Char>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, ""), actual = None)
                }
            }

            @Test
            fun `None - wrong character`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<Char>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, "$o"), actual = None)
                }
            }

            @Test
            fun `None - too long string`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<Char>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, "$a" + "$a"), actual = None)
                }
            }
        }

        @Nested
        @DisplayName(value = "parse 1 literal")
        inner class Literal {

            private lateinit var sym: Symbol<String>

            private val peBlocks: List<() -> ParsingExpression<String>> = listOf(
                { -> SymbolBuilder.literal(alpha) },
                { -> SymbolBuilder.literal(len = alpha.length) { it == alpha } },
            )


            @Test
            fun `Some('alpha')`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<String>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, alpha), actual = Some(alpha))
                }
            }

            @Test
            fun `None - empty string`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<String>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, ""), actual = None)
                }
            }

            @Test
            fun `None - wrong literal`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<String>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, omega), actual = None)
                }
            }

            @Test
            fun `None - too long string`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<String>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = p.parse(start = sym, alpha + "$a"), actual = None)
                }
            }
        }

        @Nested
        @DisplayName(value = "parse sequence")
        inner class Sequence {

            @Nested
            @DisplayName(value = "char ~ char")
            inner class CC {

                private lateinit var sym: Symbol<DataCC>

                @BeforeEach
                internal fun setUp() {
                    sym = object : Symbol<DataCC>(SymbolBuilder.seq {
                        val c1 = +char(a)
                        val c2 = +char(o)

                        value { DataCC(c1.value, c2.value) }
                    }) {}
                    p = PegParser(grammar = setOf(sym))
                }


                @Test
                fun `Some('DataCC('a', 'b')')`() {
                    assertEquals(expected = p.parse(start = sym, "$a$o"), actual = Some(DataCC(a, o)))
                }

                @Test
                fun `None - empty string`() {
                    assertEquals(expected = p.parse(start = sym, ""), actual = None)
                }

                @Test
                fun `None - wrong DataCC`() {
                    assertEquals(expected = p.parse(start = sym, "$o$a"), actual = None)
                }

                @Test
                fun `None - too long string`() {
                    assertEquals(expected = p.parse(start = sym, "$a$o" + "$a"), actual = None)
                }
            }

            @Nested
            @DisplayName(value = "char ~ literal")
            inner class CL {

                private lateinit var sym: Symbol<DataCL>

                inner class Simple {

                    @BeforeEach
                    internal fun setUp() {
                        sym = object : Symbol<DataCL>(SymbolBuilder.seq {
                            val c1 = +char(a)
                            val l2 = +literal(omega)

                            value { DataCL(c1.value, l2.value) }
                        }) {}
                        p = PegParser(grammar = setOf(sym))
                    }


                    @Test
                    fun `Some('DataCL('a', 'omega')')`() {
                        assertEquals(expected = p.parse(start = sym, "$a$omega"), actual = Some(DataCL(a, omega)))
                    }

                    @Test
                    fun `None - empty string`() {
                        assertEquals(expected = p.parse(start = sym, ""), actual = None)
                    }

                    @Test
                    fun `None - wrong DataCL`() {
                        assertEquals(expected = p.parse(start = sym, "$omega$a"), actual = None)
                    }

                    @Test
                    fun `None - too long string`() {
                        assertEquals(expected = p.parse(start = sym, "$a$omega" + "$a"), actual = None)
                    }
                }
            }
        }

        @Nested
        @DisplayName(value = "parse CL using predicate")
        inner class CLWithPredicate {

            @Nested
            inner class And {

                private lateinit var sym: Symbol<DataCL>


                @Nested
                inner class One {

                    @BeforeEach
                    internal fun setUp() {
                        sym = object : Symbol<DataCL>(SymbolBuilder.seq {
                            val c1 = +char(a)
                            +and(char(o))
                            val l2 = +literal(len = alpha.length) { it in listOf(alpha, omega) }

                            value { DataCL(c1.value, l2.value) }
                        }) {}
                        p = PegParser(grammar = setOf(sym))
                    }


                    @Test
                    fun `Some('DataCL('a', 'omega')')`() {
                        assertEquals(expected = p.parse(start = sym, "$a$omega"), actual = Some(DataCL(a, omega)))
                    }

                    @Test
                    fun `None - wrong DataCL`() {
                        assertEquals(expected = p.parse(start = sym, "$a$alpha"), actual = None)
                    }
                }

                @Nested
                inner class Two {

                    @BeforeEach
                    internal fun setUp() {
                        sym = object : Symbol<DataCL>(SymbolBuilder.seq {
                            val c1 = +char(a)
                            +and(char(d))
                            +and(char(o))
                            val l2 = +literal(len = alpha.length) { it in listOf(alpha, delta, omega) }

                            value { DataCL(c1.value, l2.value) }
                        }) {}
                        p = PegParser(grammar = setOf(sym))
                    }


                    @Test
                    fun `None - wrong DataCL - 1`() {
                        assertEquals(expected = p.parse(start = sym, "$a$alpha"), actual = None)
                    }

                    @Test
                    fun `None - wrong DataCL - 2`() {
                        assertEquals(expected = p.parse(start = sym, "$a$delta"), actual = None)
                    }

                    @Test
                    fun `None - wrong DataCL - 3`() {
                        assertEquals(expected = p.parse(start = sym, "$a$omega"), actual = None)
                    }
                }
            }

            @Nested
            inner class Not {

                private lateinit var sym: Symbol<DataCL>


                @Nested
                inner class One {

                    @BeforeEach
                    internal fun setUp() {
                        sym = object : Symbol<DataCL>(SymbolBuilder.seq {
                            val c1 = +char(a)
                            +not(char(o))
                            val l2 = +literal(len = alpha.length) { it in listOf(alpha, omega) }

                            value { DataCL(c1.value, l2.value) }
                        }) {}
                        p = PegParser(grammar = setOf(sym))
                    }


                    @Test
                    fun `Some('DataCL('a', 'alpha')')`() {
                        assertEquals(expected = p.parse(start = sym, "$a$alpha"), actual = Some(DataCL(a, alpha)))
                    }

                    @Test
                    fun `None - wrong DataCL`() {
                        assertEquals(expected = p.parse(start = sym, "$a$omega"), actual = None)
                    }
                }

                @Nested
                inner class Two {

                    @BeforeEach
                    internal fun setUp() {
                        sym = object : Symbol<DataCL>(SymbolBuilder.seq {
                            val c1 = +char(a)
                            +not(char(d))
                            +not(char(o))
                            val l2 = +literal(len = alpha.length) { it in listOf(alpha, delta, omega) }

                            value { DataCL(c1.value, l2.value) }
                        }) {}
                        p = PegParser(grammar = setOf(sym))
                    }


                    @Test
                    fun `Some('DataCL('a', 'alpha')')`() {
                        assertEquals(expected = p.parse(start = sym, "$a$alpha"), actual = Some(DataCL(a, alpha)))
                    }

                    @Test
                    fun `None - wrong DataCL - 1`() {
                        assertEquals(expected = p.parse(start = sym, "$a$delta"), actual = None)
                    }

                    @Test
                    fun `None - wrong DataCL - 2`() {
                        assertEquals(expected = p.parse(start = sym, "$a$omega"), actual = None)
                    }
                }
            }
        }
    }
}