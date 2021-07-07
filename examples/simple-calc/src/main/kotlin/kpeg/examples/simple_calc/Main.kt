package kpeg.examples.simple_calc

import kpeg.PegParser.parse
import kpeg.examples.simple_calc.ExprGrammar.Expr
import kpeg.pe.Symbol


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


fun main() {
    val expr = Expr.value()

    val results = listOf(
        parse(symbol = expr, "1"),
        parse(symbol = expr, "+1"),
        parse(symbol = expr, "+ 1"),
        parse(symbol = expr, "+1 +"),
        parse(symbol = expr, "-17"),
        parse(symbol = expr, "-1 7"),
        parse(symbol = expr, "1+3-4-3"),
        parse(symbol = expr, "1+2+3+4+5"),
        parse(symbol = expr, "1 + +2 + -3 + +4 + 5"),
        parse(symbol = expr, "-1-2-3-4-5"),
        parse(symbol = expr, "definitely not expression"),
        parse(symbol = expr, ""),
    )

    for (res in results) {
        println(res)
    }
}