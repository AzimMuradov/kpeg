package kpeg.examples

import kpeg.PegParser
import kpeg.RuleBuilderBlock
import kpeg.Symbol

import kpeg.examples.Expr.*


sealed class Expr(block: RuleBuilderBlock) : Symbol(block) {

    object E : Expr({ Sum / Num })

    object Sum : Expr({ E + char('+') + E })

    object Num : Expr({
        char('0') / (chars('1'..'9') + chars('0'..'9').zeroOrMore())
    })
}


fun main() {
    val parser = PegParser(setOf(Sum, Num))

    val elements = parser.parseOrNull(root = E, "4 + 5 + 6 + 1 + 2")
    checkNotNull(elements) { "Wrong Template" }

    for (e in elements) {
        when (e.symbol) {
            E -> TODO()
            Sum -> TODO()
            Num -> TODO()
        }
    }
}