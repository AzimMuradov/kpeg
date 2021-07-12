package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.joinToString() {
    //      - FAIL
    // a    - OK   - a
    // aa   - OK   - a|a
    // aaa  - OK   - a|a|a
    // aaaa - FAIL

    char('a')                          // EvalPE<Char>
        .repeated(range = 1u..3u)      // EvalPE<List<Char>>
        .joinToString(separator = "|") // EvalPE<String>
}