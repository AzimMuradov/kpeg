package kpeg

import kpeg.Option.None
import kpeg.Option.Some
import kpeg.pe.NonTerminal
import kpeg.pe.Symbol
import kpeg.pe.Terminal
import java.util.stream.Stream


object TestUtils {
    const val a = 'a'
    const val d = 'd'
    const val o = 'o'
    const val alpha = "alpha"
    const val delta = "delta"
    const val omega = "omega"

    data class DataCC(val c1: Char, val c2: Char)
    data class DataCL(val c1: Char, val l2: String)


    // PT = Parametrized Test


    // Repeated

    data class PTDataForRepeated(
        val s: String,
        val sym: Symbol<List<Char>>,
        val expected: Option<List<Char>>,
    )

    fun ptDataRepCorrectProvider() = Stream.of(
        PTDataForRepeated(s = "$a$d$a", repeatedSym(range = 0u..UInt.MAX_VALUE), expected = Some(listOf(a, d, a))),
        PTDataForRepeated(s = "$a$d$a", repeatedSym(range = 0u..0u), expected = Some(listOf())),
        PTDataForRepeated(s = "$a$d$a", repeatedSym(range = 0u..1u), expected = Some(listOf(a))),
        PTDataForRepeated(s = "$a", repeatedSym(range = 0u..2u), expected = Some(listOf(a))),
        PTDataForRepeated(s = "$a$d$a", repeatedSym(range = 0u..2u), expected = Some(listOf(a, d))),
        PTDataForRepeated(s = "$a$d", repeatedSym(range = 0u..10u), expected = Some(listOf(a, d))),
        PTDataForRepeated(s = "$a$d$a", repeatedSym(range = 0u..10u), expected = Some(listOf(a, d, a))),
        PTDataForRepeated(s = "$a", repeatedSym(range = 1u..1u), expected = Some(listOf(a))),
        PTDataForRepeated(s = "$a$d$a", repeatedSym(range = 1u..1u), expected = Some(listOf(a))),
        PTDataForRepeated(s = "$a$d", repeatedSym(range = 1u..3u), expected = Some(listOf(a, d))),
        PTDataForRepeated(s = "$a$d$a", repeatedSym(range = 3u..3u), expected = Some(listOf(a, d, a))),
    )

    fun ptDataRepIncorrectProvider() = Stream.of(
        PTDataForRepeated(s = "$a", repeatedSym(range = 2u..3u), expected = None),
        PTDataForRepeated(s = "$a$d", repeatedSym(range = 3u..3u), expected = None),
    )

    fun ptDataRepEmptyProvider() = Stream.of(
        PTDataForRepeated(s = "", repeatedSym(range = 0u..UInt.MAX_VALUE), expected = Some(listOf())),
        PTDataForRepeated(s = "", repeatedSym(range = 0u..0u), expected = Some(listOf())),
        PTDataForRepeated(s = "", repeatedSym(range = 0u..1u), expected = Some(listOf())),
        PTDataForRepeated(s = "", repeatedSym(range = 0u..2u), expected = Some(listOf())),
        PTDataForRepeated(s = "", repeatedSym(range = 0u..10u), expected = Some(listOf())),
        PTDataForRepeated(s = "", repeatedSym(range = 1u..1u), expected = None),
        PTDataForRepeated(s = "", repeatedSym(range = 1u..3u), expected = None),
        PTDataForRepeated(s = "", repeatedSym(range = 3u..3u), expected = None),
    )

    fun repeatedSym(range: UIntRange) =
        object : Symbol<List<Char>>(NonTerminal.Repeated(range, Terminal.Character { it == a || it == d })) {
            override fun toString(): String = "Repeated(range = $range, pe = Character('a', 'd'))"
        }
}