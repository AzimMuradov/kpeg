package kpeg.examples.json

import kpeg.PegParser
import kpeg.examples.json.JsonGrammar.JsonSym
import kpeg.examples.json.JsonValue.*
import kpeg.pe.Symbol


object JsonGrammar {

    val JsonSym by lazy {
        Symbol.rule<Json> {
            choice(ObjectSym, ArraySym).map { Json(it) }
        }
    }

    private val ValueSym: Symbol<JsonValue> by lazy {
        Symbol.rule<JsonValue> {
            choice(ObjectSym, ArraySym, NumberSym, StringSym, BooleanSym, NullSym)
        }
    }


    // Json values

    private val ObjectSym by lazy {
        Symbol.rule<JsonObject> {
            choice<List<Pair<String, JsonValue>>>(
                seq {
                    +LeftCurBr
                    +RightCurBr

                    value { listOf() }
                },
                seq {
                    +LeftCurBr

                    val first = +ObjectPairSym
                    val others = +seq<Pair<String, JsonValue>> {
                        +Comma
                        val pair = +ObjectPairSym
                        value { pair.value }
                    }.zeroOrMore()

                    +RightCurBr

                    value { listOf(first.value) + others.value }
                },
            ).map { JsonObject(it) }
        }
    }

    private val ObjectPairSym by lazy {
        Symbol.rule<Pair<String, JsonValue>> {
            seq {
                val name = +StringSym
                +Colon
                val jsonValue = +ValueSym

                value { name.value.value to jsonValue.value }
            }
        }
    }


    private val ArraySym by lazy {
        Symbol.rule<JsonArray> {
            choice<List<JsonValue>>(
                seq {
                    +LeftSqBr
                    +RightSqBr

                    value { listOf() }
                },
                seq {
                    +LeftSqBr

                    val first = +ValueSym
                    val others = +seq<JsonValue> {
                        +Comma
                        val jsonValue = +ValueSym
                        value { jsonValue.value }
                    }.zeroOrMore()

                    +RightSqBr

                    value { listOf(first.value) + others.value }
                },
            ).map { JsonArray(it) }
        }
    }


    private val NumberSym by lazy {
        Symbol.rule<JsonNumber>(ignoreWS = false) {
            seq {
                val minus = +literal("-").orDefault("")

                val abs = +choice(literal("0"), seq {
                    val first = +char('1'..'9')
                    val others = +char('0'..'9').zeroOrMore().map { it.joinToString(separator = "") }

                    value { "${first.value}${others.value}" }
                })

                val pointPart = +seq<String> {
                    val point = +char('.')
                    val digits = +char('0'..'9').oneOrMore().map { it.joinToString(separator = "") }

                    value { "${point.value}${digits.value}" }
                }.orDefault("")

                val ePart = +seq<String> {
                    val e = +char('e', 'E')
                    val sign = +char('+', '-').orDefault('+')
                    val eDigits = +char('0'..'9').oneOrMore().map { it.joinToString(separator = "") }

                    value { "${e.value}${sign.value}${eDigits.value}" }
                }.orDefault("")

                value { JsonNumber("${minus.value}${abs.value}${pointPart.value}${ePart.value}".toDouble()) }
            }
        }
    }


    private val StringSym by lazy {
        Symbol.rule<JsonString>(ignoreWS = false) {
            seq {
                +char('"')
                val chars = +CharSym.zeroOrMore()
                +char('"')

                value { JsonString(chars.value.joinToString(separator = "")) }
            }
        }
    }

    private val CharSym by lazy {
        Symbol.rule<String>(ignoreWS = false) {
            choice(
                seq {
                    +not(char('"', '\\'))
                    val c = +any

                    value { c.value.toString() }
                },
                literal(len = 2) {
                    it in listOf("\\\"", "\\\\", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t")
                },
                seq {
                    val prefix = +literal("\\u")
                    val unicode = +char {
                        it.isDigit() || it in 'a'..'f' || it in 'A'..'F'
                    }.repeatedExactly(4u).map { it.joinToString(separator = "") }

                    value { prefix.value + unicode.value }
                }
            )
        }
    }


    private val BooleanSym by lazy {
        Symbol.rule<JsonBoolean> {
            choice(TrueLiteral, FalseLiteral).map { JsonBoolean(it.toBooleanStrict()) }
        }
    }


    private val NullSym by lazy {
        Symbol.rule<JsonNull> {
            NullLiteral.map { JsonNull }
        }
    }


    // Util symbols

    private val LeftSqBr by lazy { Symbol.rule { char('[') } }
    private val LeftCurBr by lazy { Symbol.rule { char('{') } }
    private val RightSqBr by lazy { Symbol.rule { char(']') } }
    private val RightCurBr by lazy { Symbol.rule { char('}') } }
    private val Colon by lazy { Symbol.rule { char(':') } }
    private val Comma by lazy { Symbol.rule { char(',') } }

    private val TrueLiteral by lazy { Symbol.rule { literal("true") } }
    private val FalseLiteral by lazy { Symbol.rule { literal("false") } }
    private val NullLiteral by lazy { Symbol.rule { literal("null") } }
}


fun main() {
    val parser = PegParser()

    // Example from https://pest.rs/book/examples/json.html
    println(parser.parse(
        symbol = JsonSym,
        text = """
            {
                "nesting": { "inner object": {} },
                "an array": [1.5, true, null, 1e-6],
                "string with escaped double quotes": "\"quick brown foxes\""
            }
            """.trimIndent()
    ))
}