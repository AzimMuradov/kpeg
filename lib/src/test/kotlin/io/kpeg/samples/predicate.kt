package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.and() {
    // a - OK
    // b - FAIL

    and(char('a'))
}

fun Rule.not() {
    // a - FAIL
    // b - OK

    not(char('a'))
}