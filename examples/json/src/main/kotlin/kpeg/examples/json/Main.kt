package kpeg.examples.json

import kpeg.PegParser
import kpeg.examples.json.JsonGrammar.JsonSym
import kpeg.examples.json.JsonValue.*
import kpeg.pe.Symbol


// Example is based on https://pest.rs/book/examples/json.html

object JsonGrammar {

    val JsonSym by lazy {
        Symbol.rule { choice(ObjectSym, ArraySym).map { Json(it) } }
    }

    private val ValueSym: Symbol<JsonValue> by lazy {
        Symbol.rule { choice(ObjectSym, ArraySym, NumberSym, StringSym, BooleanSym, NullSym) }
    }


    // Json values

    private val ObjectSym by lazy {
        Symbol.rule {
            ObjectPairSym
                .list(separator = Comma, prefix = LeftCurBr, postfix = RightCurBr)
                .map { JsonObject(it) }
        }
    }

    private val ObjectPairSym by lazy {
        Symbol.rule<Pair<String, JsonValue>> {
            seq {
                val name = +StringSym
                +Colon
                val jsonValue = +ValueSym

                value { name.get.value to jsonValue.get }
            }
        }
    }


    private val ArraySym by lazy {
        Symbol.rule {
            // Workaround for bug (compiler bug?)
            seq<JsonValue> {
                val sym = +ValueSym
                value { sym.get }
            }.list(separator = Comma, prefix = LeftSqBr, postfix = RightSqBr).map { JsonArray(it) }
        }
    }


    private val NumberSym by lazy {
        Symbol.rule<JsonNumber>(ignoreWS = false) {
            seq {
                val minus = +literal("-").orDefault("")

                val abs = +choice(literal("0"), seq {
                    val first = +char('1'..'9')
                    val others = +DIGIT.zeroOrMore().joinToString()

                    value { "${first.get}${others.get}" }
                })

                val pointPart = +seq<String> {
                    val point = +char('.')
                    val digits = +DIGIT.oneOrMore().joinToString()

                    value { "${point.get}${digits.get}" }
                }.orDefault("")

                val ePart = +seq<String> {
                    val e = +char('e', 'E')
                    val sign = +char('+', '-').orDefault('+')
                    val eDigits = +DIGIT.oneOrMore().joinToString()

                    value { "${e.get}${sign.get}${eDigits.get}" }
                }.orDefault("")

                value { JsonNumber("${minus.get}${abs.get}${pointPart.get}${ePart.get}".toDouble()) }
            }
        }
    }


    private val StringSym by lazy {
        Symbol.rule(ignoreWS = false) {
            CharSym
                .list(prefix = char('"'), postfix = char('"'))
                .joinToString()
                .map { JsonString(it) }
        }
    }

    private val CharSym by lazy {
        Symbol.rule(ignoreWS = false) {
            choice(
                seq {
                    +not(char('"', '\\'))
                    val c = +ANY

                    value { c.get.toString() }
                },
                literal(len = 2) {
                    it in listOf("\\\"", "\\\\", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t")
                },
                seq {
                    val prefix = +literal("\\u")
                    val unicode = +HEX_DIGIT.repeatedExactly(4u).joinToString()

                    value { prefix.get + unicode.get }
                }
            )
        }
    }


    private val BooleanSym by lazy {
        Symbol.rule { choice(TrueLiteral, FalseLiteral).map { JsonBoolean(it.toBooleanStrict()) } }
    }


    private val NullSym by lazy {
        Symbol.rule { NullLiteral.map { JsonNull } }
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