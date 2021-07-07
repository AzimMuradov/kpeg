package kpeg.examples.json

import arrow.core.Either
import arrow.core.Eval
import kpeg.PegParser
import kpeg.examples.json.JsonGrammar.JsonSym
import kpeg.examples.json.JsonValue.*
import kpeg.pe.Symbol


// Example is based on https://pest.rs/book/examples/json.html

object JsonGrammar {

    val JsonSym = Symbol.lazyRule(name = "Json") {
        choice(ObjectSym, ArraySym).mapPe { Json(it) }
    }

    private val ValueSym: Eval<Symbol<JsonValue>> = Symbol.lazyRule(name = "JsonValue") {
        choice(ObjectSym, ArraySym, NumberSym, StringSym, BooleanSym, NullSym)
    }


    // Json values

    private val ObjectSym = Symbol.lazyRule(name = "Object") {
        ObjectPairSym
            .list(separator = Comma, prefix = LeftCurBr, postfix = RightCurBr)
            .mapPe { JsonObject(it) }
    }

    private val ObjectPairSym = Symbol.lazyRule<Pair<String, JsonValue>>(name = "ObjectPair") {
        seq {
            val name = +StringSym
            +Colon
            val jsonValue = +ValueSym

            value { name.get.value to jsonValue.get }
        }
    }


    private val ArraySym = Symbol.lazyRule(name = "Array") {
        ValueSym.list(separator = Comma, prefix = LeftSqBr, postfix = RightSqBr).mapPe { JsonArray(it) }
    }


    private val NumberSym = Symbol.lazyRule<JsonNumber>(name = "Number", ignoreWS = false) {
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


    private val StringSym = Symbol.lazyRule(name = "String", ignoreWS = false) {
        CharSym
            .list(prefix = char('"'), postfix = char('"'))
            .joinToString()
            .mapPe { JsonString(it) }
    }

    private val CharSym = Symbol.lazyRule(name = "Char", ignoreWS = false) {
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


    private val BooleanSym = Symbol.lazyRule(name = "Boolean") {
        choice(TrueLiteral, FalseLiteral).mapPe { JsonBoolean(it.toBooleanStrict()) }
    }


    private val NullSym = Symbol.lazyRule(name = "Null") {
        NullLiteral.mapPe { JsonNull }
    }


    // Util symbols

    private val LeftSqBr = Symbol.rule(name = "LeftSqBr") { char('[') }
    private val LeftCurBr = Symbol.rule(name = "LeftCurBr") { char('{') }
    private val RightSqBr = Symbol.rule(name = "RightSqBr") { char(']') }
    private val RightCurBr = Symbol.rule(name = "RightCurBr") { char('}') }

    private val Colon = Symbol.rule(name = "Colon") { char(':') }
    private val Comma = Symbol.rule(name = "Comma") { char(',') }

    private val TrueLiteral = Symbol.rule(name = "TrueLiteral") { literal("true") }
    private val FalseLiteral = Symbol.rule(name = "FalseLiteral") { literal("false") }
    private val NullLiteral = Symbol.rule(name = "NullLiteral") { literal("null") }
}


fun main() {
    val result = PegParser.parse(
        symbol = JsonSym.value(),
        text = """
            {
                "nesting": { "inner object": {} },
                "an array": [1.5, true, null, 1e-6],
                "string with escaped double quotes": "\"quick brown foxes\""
            }
            """.trimIndent()
    )

    when (result) {
        is Either.Left -> {
            println("Fail")
            for ((index, message) in result.value) {
                println("$index: $message")
            }
        }
        is Either.Right -> {
            println("Success")
            println(result.value)
        }
    }
}