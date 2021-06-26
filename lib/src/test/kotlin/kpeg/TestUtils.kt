package kpeg

import kpeg.Option.None
import kpeg.Option.Some
import kpeg.pe.NonTerminal
import kpeg.pe.Symbol
import kpeg.pe.Terminal
import java.util.stream.Stream


object TestUtils {

    // Constants

    const val a = 'a'
    const val d = 'd'
    const val o = 'o'
    const val alpha = "alpha"
    const val delta = "delta"
    const val omega = "omega"


    // Option

    fun <T> Option<T>.get(): T = when (this) {
        is Some -> value
        None -> error("")
    }


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
            override fun toString(): String = "Repeated(Character('a' or 'd') in range = $range)"
        }


    // Sequence

    data class PTDataForSequence(
        val s: String,
        val sym: Symbol<String>,
        val expected: Option<String>,
    )

    fun ptDataSeqCorrectProvider() = Stream.of(
        PTDataForSequence("$o", sequenceSym(), Some("")),
        PTDataForSequence("$a", sequenceSym(addChar = true), Some("$a")),
        PTDataForSequence(alpha, sequenceSym(addChar = true), Some("$a")),
        PTDataForSequence("$d", sequenceSym(addChar = true), Some("$d")),
        PTDataForSequence("$d$o$o", sequenceSym(addChar = true), Some("$d")),
        PTDataForSequence(alpha, sequenceSym(addLiteral = true), Some(alpha)),
        PTDataForSequence("$alpha$omega", sequenceSym(addLiteral = true), Some(alpha)),
        PTDataForSequence(delta, sequenceSym(addLiteral = true), Some(delta)),
        PTDataForSequence("$a$alpha", sequenceSym(addChar = true, addLiteral = true), Some("$a$alpha")),
        PTDataForSequence(
            "$a$alpha$delta$alpha$omega",
            sequenceSym(addChar = true, addLiteral = true),
            Some("$a$alpha")
        ),
        PTDataForSequence("$d$alpha", sequenceSym(addChar = true, addLiteral = true), Some("$d$alpha")),
        PTDataForSequence("$d$delta", sequenceSym(addChar = true, addLiteral = true), Some("$d$delta")),
    )

    fun ptDataSeqIncorrectProvider() = Stream.of(
        PTDataForSequence(omega, sequenceSym(addChar = true), None),
        PTDataForSequence("$o", sequenceSym(addChar = true), None),
        PTDataForSequence("$a$alpha", sequenceSym(addLiteral = true), None),
        PTDataForSequence("${alpha.dropLast(1)}$o", sequenceSym(addLiteral = true), None),
        PTDataForSequence(omega, sequenceSym(addLiteral = true), None),
        PTDataForSequence("$alpha$a", sequenceSym(addChar = true, addLiteral = true), None),
        PTDataForSequence("$a$omega", sequenceSym(addChar = true, addLiteral = true), None),
    )

    fun ptDataSeqEmptyProvider() = Stream.of(
        PTDataForSequence("", sequenceSym(), Some("")),
        PTDataForSequence("", sequenceSym(addChar = true), None),
        PTDataForSequence("", sequenceSym(addLiteral = true), None),
        PTDataForSequence("", sequenceSym(addChar = true, addLiteral = true), None),
    )

    fun sequenceSym(addChar: Boolean = false, addLiteral: Boolean = false) =
        object : Symbol<String>(NonTerminal.Sequence {
            val symChar = if (addChar) +Terminal.Character { it == a || it == d } else null
            val symLiteral =
                if (addLiteral) +Terminal.Literal(len = alpha.length) { it == alpha || it == delta } else null

            value { (if (addChar) "${symChar?.value}" else "") + (if (addLiteral) symLiteral?.value else "") }
        }) {
            override fun toString(): String {
                val list = mutableListOf<String>()
                if (addChar) list += "char"
                if (addLiteral) list += "literal"
                return "Sequence($list)"
            }
        }


    // Prioritized Choice

    data class PTDataForPrioritizedChoice(
        val s: String,
        val sym: Symbol<String>,
        val expected: Option<String>,
    )

    fun ptDataPrChoiceCorrectProvider() = Stream.of(
        PTDataForPrioritizedChoice("$a", prChoiceSym(), Some("")),
        PTDataForPrioritizedChoice(alpha, prChoiceSym(), Some("")),
        PTDataForPrioritizedChoice(alpha, prChoiceSym(addLiteral1 = true), Some(alpha)),
        PTDataForPrioritizedChoice("$alpha$omega", prChoiceSym(addLiteral1 = true), Some(alpha)),
        PTDataForPrioritizedChoice(alpha, prChoiceSym(addLiteral1 = true, addChar = true), Some(alpha)),
        PTDataForPrioritizedChoice("$alpha$a", prChoiceSym(addLiteral1 = true, addChar = true), Some(alpha)),
        PTDataForPrioritizedChoice("$a$alpha", prChoiceSym(addLiteral1 = true, addChar = true), Some("$a")),
        PTDataForPrioritizedChoice("$alpha$d", prChoiceSym(addLiteral1 = true, addChar = true), Some(alpha)),
        PTDataForPrioritizedChoice(alpha, prChoiceSym(addLiteral1 = true, addLiteral2 = true), Some(alpha)),
        PTDataForPrioritizedChoice(omega, prChoiceSym(addLiteral1 = true, addLiteral2 = true), Some(omega)),
        PTDataForPrioritizedChoice("$omega$alpha", prChoiceSym(addLiteral1 = true, addLiteral2 = true), Some(omega)),
        PTDataForPrioritizedChoice("$a", prChoiceSym(addChar = true), Some("$a")),
        PTDataForPrioritizedChoice(alpha, prChoiceSym(addChar = true), Some("$a")),
        PTDataForPrioritizedChoice("$a$o", prChoiceSym(addChar = true), Some("$a")),
        PTDataForPrioritizedChoice(
            alpha,
            prChoiceSym(addLiteral1 = true, addChar = true, addLiteral2 = true),
            Some(alpha)
        ),
        PTDataForPrioritizedChoice(
            "$a",
            prChoiceSym(addLiteral1 = true, addChar = true, addLiteral2 = true),
            Some("$a")
        ),
        PTDataForPrioritizedChoice(
            omega,
            prChoiceSym(addLiteral1 = true, addChar = true, addLiteral2 = true),
            Some(omega)
        ),
        PTDataForPrioritizedChoice(
            "$omega$a$alpha",
            prChoiceSym(addLiteral1 = true, addChar = true, addLiteral2 = true),
            Some(omega)
        ),
    )

    fun ptDataPrChoiceIncorrectProvider() = Stream.of(
        PTDataForPrioritizedChoice("$a", prChoiceSym(addLiteral1 = true), None),
        PTDataForPrioritizedChoice("$a$alpha", prChoiceSym(addLiteral1 = true), None),
        PTDataForPrioritizedChoice(omega, prChoiceSym(addLiteral1 = true), None),
        PTDataForPrioritizedChoice(omega, prChoiceSym(addLiteral1 = true, addChar = true), None),
        PTDataForPrioritizedChoice("$o$a", prChoiceSym(addLiteral1 = true, addChar = true), None),
        PTDataForPrioritizedChoice("$a", prChoiceSym(addLiteral1 = true, addLiteral2 = true), None),
        PTDataForPrioritizedChoice(omega, prChoiceSym(addChar = true), None),
        PTDataForPrioritizedChoice("$o", prChoiceSym(addChar = true), None),
        PTDataForPrioritizedChoice("$o", prChoiceSym(addLiteral1 = true, addChar = true, addLiteral2 = true), None),
    )

    fun ptDataPrChoiceEmptyProvider() = Stream.of(
        PTDataForPrioritizedChoice("", prChoiceSym(), Some("")),
        PTDataForPrioritizedChoice("", prChoiceSym(addLiteral1 = true), None),
        PTDataForPrioritizedChoice("", prChoiceSym(addLiteral1 = true, addChar = true), None),
        PTDataForPrioritizedChoice("", prChoiceSym(addLiteral1 = true, addLiteral2 = true), None),
        PTDataForPrioritizedChoice("", prChoiceSym(addChar = true), None),
        PTDataForPrioritizedChoice("", prChoiceSym(addLiteral1 = true, addChar = true, addLiteral2 = true), None),
    )

    fun prChoiceSym(addLiteral1: Boolean = false, addChar: Boolean = false, addLiteral2: Boolean = false) =
        object : Symbol<String>(NonTerminal.PrioritizedChoice {
            val symLiteral1 = if (addLiteral1) +Terminal.Literal(len = alpha.length) { it == alpha } else null
            val symChar = if (addChar) +Terminal.Character { it == a } else null
            val symLiteral2 = if (addLiteral2) +Terminal.Literal(len = omega.length) { it == omega } else null

            value {
                (symLiteral1?.nullable ?: "") + ("${symChar?.nullable ?: ""}") + (symLiteral2?.nullable ?: "")
            }
        }) {
            override fun toString(): String {
                val list = mutableListOf<String>()
                if (addLiteral1) list += "literal"
                if (addChar) list += "char"
                if (addLiteral2) list += "literal"
                return "PrioritizedChoice($list)"
            }
        }
}