package io.kpeg.examples.simple_calc

import arrow.core.getOrElse
import io.kpeg.PegParser
import io.kpeg.examples.simple_calc.ExprGrammar.Expr
import io.kpeg.pe.Symbol


object ExprGrammar {

    val Expr = Symbol.rule<Int>(name = "Expr") {
        seq {
            val init = +Num
            val operations = +Additive.zeroOrMore()

            value {
                operations.get.fold(init.get) { lhs, (op, rhs) ->
                    when (op) {
                        Op.Add -> lhs + rhs
                        Op.Sub -> lhs - rhs
                    }
                }
            }
        }
    }


    private val Additive = Symbol.rule<Pair<Op, Int>>(name = "Additive") {
        seq {
            val op = +char('+', '-').mapPe { Op.fromChar(it) }
            val rhs = +Num

            value { op.get to rhs.get }
        }
    }

    private val Num = Symbol.rule<Int>(name = "Num", ignoreWS = false) {
        seq {
            val sign = +char('+', '-').orDefault('+')
            val digits = +DIGIT.oneOrMore().joinToString()

            value { (sign.get + digits.get).toInt() }
        }
    }


    private enum class Op {
        Add,
        Sub;

        companion object {
            fun fromChar(c: Char) = when (c) {
                '+' -> Add
                '-' -> Sub
                else -> error("")
            }
        }
    }
}

fun evalExpr(expression: String) = PegParser.parse(symbol = Expr.value(), expression).getOrElse { null }


fun main() {
    val results = listOf(
        evalExpr("1"),
        evalExpr("+1"),
        evalExpr("+ 1"),
        evalExpr("+1 +"),
        evalExpr("-17"),
        evalExpr("-1 7"),
        evalExpr("1+3-4-3"),
        evalExpr("1+2+3+4+5"),
        evalExpr("1 + +2 + -3 + +4 + 5"),
        evalExpr("-1-2-3-4-5"),
        evalExpr("definitely not expression"),
        evalExpr(""),
    )

    for (res in results) {
        println(res)
    }
}