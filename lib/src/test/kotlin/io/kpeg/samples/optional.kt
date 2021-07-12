package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.optional() {
    // a - OK   - Some('a')
    // A - FAIL - None
    // b - FAIL - None

    char('a')       // EvalPE<Char>
        .optional() // EvalPE<Option<Char>>
}

fun Rule.optionalWithDefault() {
    // a - OK - 'a'
    // A - OK - '?'
    // b - OK - '?'

    char('a')                   // EvalPE<Char>
        .orDefault(value = '?') // EvalPE<Char>
}