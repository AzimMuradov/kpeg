package kpeg.testutils

import com.marcinmoskala.math.times
import kpeg.pe.Symbol.Rule
import kotlin.streams.asStream
import kpeg.pe.ParsingExpression as PE


object OperatorsTestUtils {

    internal data class PtDataCh(val pe: PE<Char>, val c: Char)


    // BuiltIn Characters

    private val biCharPes = listOf(
        Rule.ANY,
        Rule.DIGIT,
        Rule.HEX_DIGIT,
        Rule.LETTER,
    )

    internal fun ptDataBiChCorrectProvider() = biCharPes.zip(listOf(
        listOf('a', 'B', '1', 'ы', '\n', '\u0000', '桜'),
        listOf('1', '0', '7'),
        listOf('1', '0', 'F', 'f', 'a', 'C'),
        listOf('a', 'B', 'ы', 'ñ', '桜'),
    ))
        .flatMap { (pe, list) -> list.map { c -> pe to c } }
        .map { (pe, c) -> PtDataCh(pe.value(), c) }
        .asSequence().asStream()

    internal fun ptDataBiChIncorrectProvider() = biCharPes.zip(listOf(
        listOf(),
        listOf('a', 'X', '桜', '\n', '\u0000'),
        listOf('X', 'ы', '\n', '\u0000'),
        listOf('5', '\n', '\u0000'),
    ))
        .flatMap { (pe, list) -> list.map { c -> pe to c } }
        .map { (pe, c) -> PtDataCh(pe.value(), c) }
        .asSequence().asStream()


    // Character

    internal fun ptDataChCorrectProvider() = (charPesA * listOf('a') + charPesAbc * listOf('a', 'b', 'c'))
        .map { (pe, c) -> PtDataCh(pe.value(), c) }
        .asSequence().asStream()

    internal fun ptDataChIncorrectProvider() = (charPesA * listOf('b', 'x') + charPesAbc * listOf('x', 'y'))
        .map { (pe, c) -> PtDataCh(pe.value(), c) }
        .asSequence().asStream()


    private val charPesA = listOf(
        Rule.char { it == 'a' },
        Rule.char('a'),
        Rule.char('a'..'a'),
        Rule.char('a', 'a', 'a', 'a'),
    )

    private val charPesAbc = listOf(
        Rule.char { it == 'a' || it == 'b' || it == 'c' },
        Rule.char('a'..'c'),
        Rule.char('a', 'b', 'c'),
    )


    // Literal

    internal data class PtDataLit(val pe: PE<String>, val l: String)

    internal fun ptDataLitCorrectProvider() =
        (litPesAlpha * listOf("alpha") + litPesAlphaCaseInsensitive * listOf("Alpha", "alpha", "ALPhA"))
            .map { (pe, l) -> PtDataLit(pe.value(), l) }
            .asSequence().asStream()

    internal fun ptDataLitIncorrectProvider() =
        (litPesAlpha * listOf("Alpha") + litPesAlphaCaseInsensitive * listOf("alpho", "omega"))
            .map { (pe, l) -> PtDataLit(pe.value(), l) }
            .asSequence().asStream()


    private val litPesAlpha = listOf(
        Rule.literal(len = 5) { it == "alpha" },
        Rule.literal("alpha"),
    )

    private val litPesAlphaCaseInsensitive = listOf(
        Rule.literal(len = 5) { it.lowercase() == "alpha" },
    )
}