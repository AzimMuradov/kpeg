package kpeg.examples

import kpeg.Option.None
import kpeg.PegParser
import kpeg.examples.Expr.Op
import kpeg.pe.Symbol
import kpeg.pe.SymbolBuilder.seq


object Expr : Symbol<Int>(seq {
    val init = +Num
    val operations = +Additive.zeroOrMore()

    value {
        operations.value.fold(init.value) { lhs, (op, rhs) ->
            when (op) {
                Op.Add -> lhs + rhs
                Op.Sub -> lhs - rhs
            }
        }
    }
}) {
    enum class Op {
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

object Additive : Symbol<Pair<Op, Int>>(seq {
    val op = +choice<Op> {
        val add = +char('+')
        val sub = +char('-')
        value { Op.fromChar(add.nullable ?: sub.value) }
    }
    val rhs = +Num

    value { op.value to rhs.value }
})

object Num : Symbol<Int>(seq {
    val minus = +char('-').optional()
    val digits = +char('0'..'9').oneOrMore()

    value { (if (minus.value != None) -1 else +1) * digits.value.joinToString(separator = "").toInt() }
})


fun main() {
    val parser = PegParser(grammar = setOf(Expr, Additive, Num))

    println(parser.parse(start = Expr, "1"))
    println(parser.parse(start = Expr, "-1"))
    println(parser.parse(start = Expr, "1+3-4-3"))
    println(parser.parse(start = Expr, "1+2+3+4+5"))
    println(parser.parse(start = Expr, "-1-2-3-4-5"))
    println(parser.parse(start = Expr, "definitely not expression"))
    println(parser.parse(start = Expr, ""))
}