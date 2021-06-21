package kpeg.examples

import kpeg.Element
import kpeg.PegParser
import kpeg.RuleBuilderBlock
import kpeg.Symbol
import kpeg.examples.ExprS.*


sealed class ExprS(block: RuleBuilderBlock) : Symbol(block) {

    object Expr : ExprS({ Sum / Num })

    object Sum : ExprS({ Expr + char('+') + Expr })

    object Num : ExprS({ char('0') / (chars('1'..'9') + chars('0'..'9').zeroOrMore()) })
}


fun main() {
    val parser = PegParser(grammar = setOf(Expr, Sum, Num))

    val elements = parser.parseOrNull(root = Expr, "4 + 5 + 6 + 1 + 2")?.sortedWith(Element.cmp())
    checkNotNull(elements) { "Wrong Template" }

    for (e in elements) {
        when (e.symbol) {
            Expr -> TODO()
            Sum -> TODO()
            Num -> TODO()
        }
    }
}