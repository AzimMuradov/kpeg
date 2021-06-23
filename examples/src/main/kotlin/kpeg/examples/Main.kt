package kpeg.examples

import kpeg.PegParser
import kpeg.examples.ExprS.*
import kpeg.pe.ParsingExpression
import kpeg.pe.Symbol
import kpeg.pe.SymbolBuilder.char
import kpeg.pe.SymbolBuilder.seq


sealed class ExprS<T>(pe: ParsingExpression<T>) : Symbol<T>(pe) {

    object A : ExprS<Char>(char { it == 'a' })

    object B : ExprS<Char>(char { it == 'b' })

    object AB : ExprS<String>(seq {
        val a = +A
        val b = +B

        value { "${a.value}${b.value}" }
    })
}


fun main() {
    val parser = PegParser(grammar = setOf(A, B, AB))

    println(parser.parse(start = A, "a"))
    println(parser.parse(start = A, "ab"))
    println(parser.parse(start = B, "ab"))
    println(parser.parse(start = B, "b"))
}