package kpeg.examples

import kpeg.PegParser
import kpeg.Symbol


object Sum : Symbol({ any })

object Num : Symbol({ char('0') / (chars('1'..'9') + chars('0'..'9').zeroOrMore()) })

object Plus : Symbol({ Num + char('+') + Num })


fun main() {
    val parser = PegParser(root = Sum, grammar = arrayOf(Sum, Num, Plus))

    val elements = parser.parseOrNull("")
    checkNotNull(elements) { "Wrong Template" }
}