package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.charBlock() {
    // 'a' - OK
    // 'F' - OK
    // 'ы' - OK
    // 'ñ' - OK
    // '2' - FAIL

    char { it.isLetter() } // EvalPE<Char>
}

fun Rule.char() {
    // 'a' - OK
    // 'A' - FAIL
    // 'f' - FAIL

    char('a') // EvalPE<Char>
}

fun Rule.charRange() {
    // 'a' - OK
    // 'A' - FAIL
    // 'f' - OK

    char('a'..'f') // EvalPE<Char>
}

fun Rule.chars() {
    // 'a' - OK
    // 'A' - FAIL
    // 'f' - OK
    // 'e' - FAIL

    char('a', 'b', 'f') // EvalPE<Char>
}