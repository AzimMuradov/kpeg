package io.kpeg.samples

import arrow.core.getOrElse
import io.kpeg.PegParser
import io.kpeg.pe.Symbol


fun dsl() {
    val num = Symbol.rule<Int>(name = "Num", ignoreWS = false) {
        seq {
            val sign = +char('+', '-').orDefault('+')
            val digits = +DIGIT.oneOrMore().joinToString()

            value { (sign.get + digits.get).toInt() }
        }
    }

    val sum = Symbol.rule<Int>(name = "Sum") {
        num.list(separator = char('+'), min = 1u).mapPe { it.sum() }
    }


    fun evalExpr(expression: String) =
        PegParser.parse(symbol = sum.value(), expression).getOrElse { null }

    val results = listOf(
        evalExpr("1"),                         // 1
        evalExpr("+1"),                        // 1
        evalExpr("+ 1"),                       // null
        evalExpr("+1 +"),                      // null
        evalExpr("-17"),                       // -17
        evalExpr("-1 7"),                      // null
        evalExpr("1+2+3+4+5"),                 // 15
        evalExpr("1 + +2 + -3 + +4 + 5"),      // 9
        evalExpr("definitely not expression"), // null
        evalExpr(""),                          // null
    )

    for (res in results) {
        println(res)
    }
}