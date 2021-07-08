package kpeg.testutils

import arrow.core.Eval.Now
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kpeg.ParseError
import java.util.stream.Stream
import kotlin.UInt.Companion.MAX_VALUE
import kpeg.pe.NonTerminal.Map as M
import kpeg.pe.NonTerminal.PrioritizedChoice as PrCh
import kpeg.pe.NonTerminal.Repeated as Rep
import kpeg.pe.NonTerminal.Sequence as Seq
import kpeg.pe.ParsingExpression as PE
import kpeg.pe.Terminal.Character as Ch
import kpeg.pe.Terminal.Literal as Lit


object NonTerminalTestUtils {

    // Constants

    const val a = 'a'
    const val d = 'd'
    private const val o = 'o'
    const val alpha = "alpha"
    const val delta = "delta"
    private const val omega = "omega"


    // Repeated

    internal data class RepeatedCorrect(
        val s: String,
        val pe: PE<List<Char>>,
        val expected: Option<List<Char>>,
        val i: Int,
    )

    internal fun repCorrectProvider() = Stream.of(
        RepeatedCorrect(s = "$a$d$a", repPe(range = 0u..MAX_VALUE), expected = Some(listOf(a, d, a)), i = 3),
        RepeatedCorrect(s = "$a$d$a", repPe(range = 0u..0u), expected = Some(listOf()), i = 0),
        RepeatedCorrect(s = "$a$d$a", repPe(range = 0u..1u), expected = Some(listOf(a)), i = 1),
        RepeatedCorrect(s = "$a", repPe(range = 0u..2u), expected = Some(listOf(a)), i = 1),
        RepeatedCorrect(s = "$a$d$a", repPe(range = 0u..2u), expected = Some(listOf(a, d)), i = 2),
        RepeatedCorrect(s = "$a$d", repPe(range = 0u..10u), expected = Some(listOf(a, d)), i = 2),
        RepeatedCorrect(s = "$a$d$a", repPe(range = 0u..10u), expected = Some(listOf(a, d, a)), i = 3),
        RepeatedCorrect(s = "$a", repPe(range = 1u..1u), expected = Some(listOf(a)), i = 1),
        RepeatedCorrect(s = "$a$d$a", repPe(range = 1u..1u), expected = Some(listOf(a)), i = 1),
        RepeatedCorrect(s = "$a$d", repPe(range = 1u..3u), expected = Some(listOf(a, d)), i = 2),
        RepeatedCorrect(s = "$a$d$a", repPe(range = 3u..3u), expected = Some(listOf(a, d, a)), i = 3),
    )


    internal data class RepeatedIncorrect(
        val s: String,
        val pe: PE<List<Char>>,
        val errs: List<ParseError>,
    )

    internal fun repIncorrectProvider() = Stream.of(
        RepeatedIncorrect(s = "$o", repPe(range = 2u..3u), errs(
            0 to "Wrong Character",
            0 to "Wrong Repeated(Character) 2..3 times, but was repeated 0 times",
        )),
        RepeatedIncorrect(s = "$a$o", repPe(range = 3u..3u), errs(
            1 to "Wrong Character",
            0 to "Wrong Repeated(Character) 3..3 times, but was repeated 1 times",
        )),
        RepeatedIncorrect(s = "$a$d$o", repPe(range = 3u..3u), errs(
            2 to "Wrong Character",
            0 to "Wrong Repeated(Character) 3..3 times, but was repeated 2 times",
        )),
        RepeatedIncorrect(s = "$a$d", repPe(range = 3u..3u), errs(
            2 to "Can't parse Character - text is too short",
            0 to "Wrong Repeated(Character) 3..3 times, but was repeated 2 times",
        )),
    )


    internal data class RepeatedEmpty(
        val pe: PE<List<Char>>,
        val expected: Option<List<Char>> = None,
        val i: Int = 0,
        val errs: List<ParseError> = emptyList(),
    )

    internal fun repEmptyProvider() = Stream.of(
        RepeatedEmpty(repPe(range = 0u..MAX_VALUE), expected = Some(listOf()), i = 0),
        RepeatedEmpty(repPe(range = 0u..0u), expected = Some(listOf()), i = 0),
        RepeatedEmpty(repPe(range = 0u..1u), expected = Some(listOf()), i = 0),
        RepeatedEmpty(repPe(range = 0u..2u), expected = Some(listOf()), i = 0),
        RepeatedEmpty(repPe(range = 0u..10u), expected = Some(listOf()), i = 0),
        RepeatedEmpty(repPe(range = 1u..1u), errs = errs(
            0 to "Can't parse Character - text is too short",
            0 to "Wrong Repeated(Character) 1..1 times, but was repeated 0 times",
        )),
        RepeatedEmpty(repPe(range = 1u..3u), errs = errs(
            0 to "Can't parse Character - text is too short",
            0 to "Wrong Repeated(Character) 1..3 times, but was repeated 0 times",
        )),
        RepeatedEmpty(repPe(range = 3u..3u), errs = errs(
            0 to "Can't parse Character - text is too short",
            0 to "Wrong Repeated(Character) 3..3 times, but was repeated 0 times",
        )),
    )


    private fun repPe(range: UIntRange) = Rep(range, Now(Ch { it == a || it == d }))


    // Sequence

    internal data class SequenceCorrect(
        val s: String,
        val pe: PE<String>,
        val expected: Some<String>,
        val i: Int,
    )

    internal fun seqCorrectProvider() = Stream.of(
        SequenceCorrect(s = "$a", seqPe(ch = true), expected = Some("$a"), i = 1),
        SequenceCorrect(s = alpha, seqPe(ch = true), expected = Some("$a"), i = 1),
        SequenceCorrect(s = "$d", seqPe(ch = true), expected = Some("$d"), i = 1),
        SequenceCorrect(s = "$d$o$o", seqPe(ch = true), expected = Some("$d"), i = 1),
        SequenceCorrect(s = alpha, seqPe(lit = true), expected = Some(alpha), i = 5),
        SequenceCorrect(s = "$alpha$omega", seqPe(lit = true), expected = Some(alpha), i = 5),
        SequenceCorrect(s = delta, seqPe(lit = true), expected = Some(delta), i = 5),
        SequenceCorrect(s = "$a$alpha", seqPe(ch = true, lit = true), expected = Some("$a$alpha"), i = 6),
        SequenceCorrect(
            s = "$a$alpha$delta$alpha$omega",
            seqPe(ch = true, lit = true),
            expected = Some("$a$alpha"),
            i = 6,
        ),
        SequenceCorrect(s = "$d$alpha", seqPe(ch = true, lit = true), expected = Some("$d$alpha"), i = 6),
        SequenceCorrect(s = "$d$delta", seqPe(ch = true, lit = true), expected = Some("$d$delta"), i = 6),
    )


    internal data class SequenceIncorrect(
        val s: String,
        val pe: PE<String>,
        val errs: List<ParseError>,
    )

    internal fun seqIncorrectProvider() = Stream.of(
        SequenceIncorrect(s = omega, seqPe(ch = true), errs(
            0 to "Wrong Character",
            0 to "Wrong Sequence(Character)",
        )),
        SequenceIncorrect(s = "$o", seqPe(ch = true), errs(
            0 to "Wrong Character",
            0 to "Wrong Sequence(Character)",
        )),
        SequenceIncorrect(s = "$a$alpha", seqPe(lit = true), errs(
            0 to "Wrong Literal",
            0 to "Wrong Sequence(Literal)",
        )),
        SequenceIncorrect(s = "${alpha.dropLast(1)}$o", seqPe(lit = true), errs(
            0 to "Wrong Literal",
            0 to "Wrong Sequence(Literal)",
        )),
        SequenceIncorrect(s = alpha.dropLast(1), seqPe(lit = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong Sequence(Literal)",
        )),
        SequenceIncorrect(s = omega, seqPe(lit = true), errs(
            0 to "Wrong Literal",
            0 to "Wrong Sequence(Literal)",
        )),
        SequenceIncorrect(s = "$alpha$a", seqPe(ch = true, lit = true), errs(
            1 to "Wrong Literal",
            0 to "Wrong Sequence(Character, Literal)",
        )),
        SequenceIncorrect(s = "$a$omega", seqPe(ch = true, lit = true), errs(
            1 to "Wrong Literal",
            0 to "Wrong Sequence(Character, Literal)",
        )),
    )


    internal data class SequenceEmpty(
        val pe: PE<String>,
        val errs: List<ParseError>,
    )

    internal fun seqEmptyProvider() = Stream.of(
        SequenceEmpty(seqPe(ch = true), errs(
            0 to "Can't parse Character - text is too short",
            0 to "Wrong Sequence(Character)",
        )),
        SequenceEmpty(seqPe(lit = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong Sequence(Literal)",
        )),
        SequenceEmpty(seqPe(ch = true, lit = true), errs(
            0 to "Can't parse Character - text is too short",
            0 to "Wrong Sequence(Character, Literal)",
        )),
    )


    private fun seqPe(ch: Boolean = false, lit: Boolean = false) =
        Seq<String> {
            val chPe = if (ch) +Now(Ch { it == a || it == d }) else null
            val litPe = if (lit) +Now(Lit(len = alpha.length) { it == alpha || it == delta }) else null

            value { (if (ch) "${chPe?.get}" else "") + (if (lit) litPe?.get else "") }
        }


    // Prioritized Choice

    internal data class PrChoiceCorrect(
        val s: String,
        val pe: PE<String>,
        val expected: Option<String>,
        val i: Int,
    )

    internal fun prChoiceCorrectProvider() = Stream.of(
        PrChoiceCorrect(s = alpha, prChoicePe(lit1 = true), expected = Some(alpha), i = 5),
        PrChoiceCorrect(s = "$alpha$omega", prChoicePe(lit1 = true), expected = Some(alpha), i = 5),
        PrChoiceCorrect(s = alpha, prChoicePe(lit1 = true, ch = true), expected = Some(alpha), i = 5),
        PrChoiceCorrect(s = "$alpha$a", prChoicePe(lit1 = true, ch = true), expected = Some(alpha), i = 5),
        PrChoiceCorrect(s = "$a$alpha", prChoicePe(lit1 = true, ch = true), expected = Some("$a"), i = 1),
        PrChoiceCorrect(s = "$alpha$d", prChoicePe(lit1 = true, ch = true), expected = Some(alpha), i = 5),
        PrChoiceCorrect(s = alpha, prChoicePe(lit1 = true, lit2 = true), expected = Some(alpha), i = 5),
        PrChoiceCorrect(s = omega, prChoicePe(lit1 = true, lit2 = true), expected = Some(omega), i = 5),
        PrChoiceCorrect(s = "$omega$alpha", prChoicePe(lit1 = true, lit2 = true), expected = Some(omega), i = 5),
        PrChoiceCorrect(s = "$a", prChoicePe(ch = true), expected = Some("$a"), i = 1),
        PrChoiceCorrect(s = alpha, prChoicePe(ch = true), expected = Some("$a"), i = 1),
        PrChoiceCorrect(s = "$a$o", prChoicePe(ch = true), expected = Some("$a"), i = 1),
        PrChoiceCorrect(s = alpha, prChoicePe(lit1 = true, ch = true, lit2 = true), expected = Some(alpha), i = 5),
        PrChoiceCorrect(s = "$a", prChoicePe(lit1 = true, ch = true, lit2 = true), expected = Some("$a"), i = 1),
        PrChoiceCorrect(s = omega, prChoicePe(lit1 = true, ch = true, lit2 = true), expected = Some(omega), i = 5),
        PrChoiceCorrect(
            s = "$omega$a$alpha",
            prChoicePe(lit1 = true, ch = true, lit2 = true),
            expected = Some(omega),
            i = 5,
        ),
    )


    internal data class PrChoiceIncorrect(
        val s: String,
        val pe: PE<String>,
        val errs: List<ParseError>,
    )

    internal fun prChoiceIncorrectProvider() = Stream.of(
        PrChoiceIncorrect(s = "$a", prChoicePe(lit1 = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong PrioritizedChoice(Literal)",
        )),
        PrChoiceIncorrect(s = "$a$alpha", prChoicePe(lit1 = true), errs(
            0 to "Wrong Literal",
            0 to "Wrong PrioritizedChoice(Literal)",
        )),
        PrChoiceIncorrect(s = omega, prChoicePe(lit1 = true), errs(
            0 to "Wrong Literal",
            0 to "Wrong PrioritizedChoice(Literal)",
        )),
        PrChoiceIncorrect(s = omega, prChoicePe(lit1 = true, ch = true), errs(
            0 to "Wrong Literal",
            0 to "Wrong Character",
            0 to "Wrong PrioritizedChoice(Literal / Character)",
        )),
        PrChoiceIncorrect(s = "$o$a", prChoicePe(lit1 = true, ch = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong Character",
            0 to "Wrong PrioritizedChoice(Literal / Character)",
        )),
        PrChoiceIncorrect(s = "$a", prChoicePe(lit1 = true, lit2 = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong PrioritizedChoice(Literal / Literal)",
        )),
        PrChoiceIncorrect(s = omega, prChoicePe(ch = true), errs(
            0 to "Wrong Character",
            0 to "Wrong PrioritizedChoice(Character)",
        )),
        PrChoiceIncorrect(s = "$o", prChoicePe(ch = true), errs(
            0 to "Wrong Character",
            0 to "Wrong PrioritizedChoice(Character)",
        )),
        PrChoiceIncorrect(s = "$o", prChoicePe(lit1 = true, ch = true, lit2 = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong Character",
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong PrioritizedChoice(Literal / Character / Literal)",
        )),
    )


    internal data class PrChoiceEmpty(
        val pe: PE<String>,
        val errs: List<ParseError>,
    )

    internal fun prChoiceEmptyProvider() = Stream.of(
        PrChoiceEmpty(prChoicePe(lit1 = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong PrioritizedChoice(Literal)",
        )),
        PrChoiceEmpty(prChoicePe(lit1 = true, ch = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Can't parse Character - text is too short",
            0 to "Wrong PrioritizedChoice(Literal / Character)",
        )),
        PrChoiceEmpty(prChoicePe(lit1 = true, lit2 = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong PrioritizedChoice(Literal / Literal)",
        )),
        PrChoiceEmpty(prChoicePe(ch = true), errs(
            0 to "Can't parse Character - text is too short",
            0 to "Wrong PrioritizedChoice(Character)",
        )),
        PrChoiceEmpty(prChoicePe(lit1 = true, ch = true, lit2 = true), errs(
            0 to "Can't parse Literal - text is too short",
            0 to "Can't parse Character - text is too short",
            0 to "Can't parse Literal - text is too short",
            0 to "Wrong PrioritizedChoice(Literal / Character / Literal)",
        )),
    )


    private fun prChoicePe(lit1: Boolean = false, ch: Boolean = false, lit2: Boolean = false) =
        PrCh(
            listOfNotNull(
                Now(Lit(len = alpha.length) { it == alpha }).takeIf { lit1 },
                Now(M(transform = { "$it" }, pe = Now(Ch { it == a }))).takeIf { ch },
                Now(Lit(len = omega.length) { it == omega }).takeIf { lit2 }
            )
        )


    // Errs

    private fun errs(vararg pairs: Pair<Int, String>) = pairs.map { (i, msg) -> ParseError(i, msg) }
}