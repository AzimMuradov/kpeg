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
                    assertEquals(expected = Some(a), actual = p.parse(start = sym, "$a"))
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
                    assertEquals(expected = Some(o), actual = p.parse(start = sym, "$o"))
                }
            }

            @Test
            fun `None - empty string`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<Char>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = None, actual = p.parse(start = sym, ""))
                }
            }

            @Test
            fun `None - wrong character`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<Char>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = None, actual = p.parse(start = sym, "$o"))
                }
            }

            @Test
            fun `None - too long string`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<Char>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = None, actual = p.parse(start = sym, "$a" + "$a"))
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
                    assertEquals(expected = Some(alpha), actual = p.parse(start = sym, alpha))
                }
            }

            @Test
            fun `None - empty string`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<String>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = None, actual = p.parse(start = sym, ""))
                }
            }

            @Test
            fun `None - wrong literal`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<String>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = None, actual = p.parse(start = sym, omega))
                }
            }

            @Test
            fun `None - too long string`() {
                for (peBlock in peBlocks) {
                    sym = object : Symbol<String>(peBlock()) {}
                    p = PegParser(grammar = setOf(sym))
                    assertEquals(expected = None, actual = p.parse(start = sym, alpha + "$a"))
                }
            }
        }

        @Nested
        @DisplayName(value = "parse 1 any")
        inner class Any {

            private lateinit var sym: Symbol<Char>

            @BeforeEach
            internal fun setUp() {
                sym = object : Symbol<Char>(SymbolBuilder.any) {}
                p = PegParser(grammar = setOf(sym))
            }


            @Test
            fun `Some('a')`() {
                assertEquals(expected = Some(a), actual = p.parse(start = sym, "$a"))
            }

            @Test
            fun `Some('-')`() {
                assertEquals(expected = Some('-'), actual = p.parse(start = sym, "-"))
            }

            @Test
            fun `None - empty string`() {
                assertEquals(expected = None, actual = p.parse(start = sym, ""))
            }

            @Test
            fun `None - too long string`() {
                assertEquals(expected = None, actual = p.parse(start = sym, "$a" + "$a"))
            }
        }

        @Nested
        @DisplayName(value = "parse 1 option")
        inner class Option {

            private lateinit var sym: Symbol<String>

            @BeforeEach
            internal fun setUp() {
                sym = object : Symbol<String>(SymbolBuilder.seq {
                    val opt = +char(a).optional()
                    value {
                        when (val res = opt.value) {
                            is Some -> res.value.toString()
                            None -> "null"
                        }
                    }
                }) {}
                p = PegParser(grammar = setOf(sym))
            }


            @Test
            fun `Some('a')`() {
                assertEquals(expected = Some("$a"), actual = p.parse(start = sym, "$a"))
            }

            @Test
            fun `Some('null')`() {
                assertEquals(expected = Some("null"), actual = p.parse(start = sym, ""))
            }

            @Test
            fun `None - too long string - 1`() {
                assertEquals(expected = None, actual = p.parse(start = sym, "-"))
            }

            @Test
            fun `None - too long string - 2`() {
                assertEquals(expected = None, actual = p.parse(start = sym, "$a" + "$a"))
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
                    assertEquals(expected = Some(DataCC(a, o)), actual = p.parse(start = sym, "$a$o"))
                }

                @Test
                fun `None - empty string`() {
                    assertEquals(expected = None, actual = p.parse(start = sym, ""))
                }

                @Test
                fun `None - wrong DataCC`() {
                    assertEquals(expected = None, actual = p.parse(start = sym, "$o$a"))
                }

                @Test
                fun `None - too long string`() {
                    assertEquals(expected = None, actual = p.parse(start = sym, "$a$o" + "$a"))
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
                        assertEquals(expected = Some(DataCL(a, omega)), actual = p.parse(start = sym, "$a$omega"))
                    }

                    @Test
                    fun `None - empty string`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, ""))
                    }

                    @Test
                    fun `None - wrong DataCL`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, "$omega$a"))
                    }

                    @Test
                    fun `None - too long string`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, "$a$omega" + "$a"))
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
                        assertEquals(expected = Some(DataCL(a, omega)), actual = p.parse(start = sym, "$a$omega"))
                    }

                    @Test
                    fun `None - wrong DataCL`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, "$a$alpha"))
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
                        assertEquals(expected = None, actual = p.parse(start = sym, "$a$alpha"))
                    }

                    @Test
                    fun `None - wrong DataCL - 2`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, "$a$delta"))
                    }

                    @Test
                    fun `None - wrong DataCL - 3`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, "$a$omega"))
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
                        assertEquals(expected = Some(DataCL(a, alpha)), actual = p.parse(start = sym, "$a$alpha"))
                    }

                    @Test
                    fun `None - wrong DataCL`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, "$a$omega"))
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
                        assertEquals(expected = Some(DataCL(a, alpha)), actual = p.parse(start = sym, "$a$alpha"))
                    }

                    @Test
                    fun `None - wrong DataCL - 1`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, "$a$delta"))
                    }

                    @Test
                    fun `None - wrong DataCL - 2`() {
                        assertEquals(expected = None, actual = p.parse(start = sym, "$a$omega"))
                    }
                }
            }
        }
    }
}