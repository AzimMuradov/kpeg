package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.repeatedInRange() {
    // a      - FAIL
    // aaa    - OK
    // aaaa   - OK
    // aaaaa  - OK
    // aaaaaa - FAIL

    char('a')                     // EvalPE<Char>
        .repeated(range = 3u..5u) // EvalPE<List<Char>>
}

fun Rule.repeatedWithMinMax() {
    // a   - OK
    // aa  - OK
    // aaa - FAIL

    char('a')                         // EvalPE<Char>
        .repeated(min = 1u, max = 2u) // EvalPE<List<Char>>
}

fun Rule.repeatedExactly() {
    // a    - FAIL
    // aa   - FAIL
    // aaa  - OK
    // aaaa - FAIL

    char('a')                        // EvalPE<Char>
        .repeatedExactly(times = 3u) // EvalPE<List<Char>>
}

fun Rule.repeatedZeroOrMore() {
    //        - OK
    // a      - OK
    // aaa    - OK
    // aaaaaa - OK

    char('a')         // EvalPE<Char>
        .zeroOrMore() // EvalPE<List<Char>>
}

fun Rule.repeatedOneOrMore() {
    //        - FAIL
    // a      - OK
    // aaa    - OK
    // aaaaaa - OK

    char('a')        // EvalPE<Char>
        .oneOrMore() // EvalPE<List<Char>>
}