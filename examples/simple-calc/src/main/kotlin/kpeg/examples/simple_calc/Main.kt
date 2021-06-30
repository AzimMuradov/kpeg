package kpeg.examples.simple_calc

import kpeg.PegParser
import kpeg.examples.simple_calc.ExprGrammar.Expr
import kpeg.pe.Symbol


object ExprGrammar {

    val Expr = Symbol.rule<Int> {
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


    private val Additive = Symbol.rule<Pair<Op, Int>> {
        seq {
            val op = +char('+', '-').map { Op.fromChar(it) }
            val rhs = +Num

            value { op.get to rhs.get }
        }
    }

    private val Num = Symbol.rule<Int>(ignoreWS = false) {
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
    val parser = PegParser()

    println(parser.parse(symbol = Expr, "1"))
    println(parser.parse(symbol = Expr, "+1"))
    println(parser.parse(symbol = Expr, "+ 1"))
    println(parser.parse(symbol = Expr, "+1 +"))
    println(parser.parse(symbol = Expr, "-17"))
    println(parser.parse(symbol = Expr, "-1 7"))
    println(parser.parse(symbol = Expr, "1+3-4-3"))
    println(parser.parse(symbol = Expr, "1+2+3+4+5"))
    println(parser.parse(symbol = Expr, "1 + +2 + -3 + +4 + 5"))
    println(parser.parse(symbol = Expr, "-1-2-3-4-5"))
    println(parser.parse(symbol = Expr, "definitely not expression"))
    println(parser.parse(symbol = Expr, ""))
}