package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.list() {
    // Array of digits

    // [1,3,6,2] - OK   - listOf('1', '3', '6', '2')
    // [2]       - OK   - listOf('2')
    // []        - OK   - listOf()
    // [a, 3]    - FAIL
    // 1, 2      - FAIL
    // [1, 2     - FAIL
    // 1, 2]     - FAIL
    // [1; 2]    - FAIL

    DIGIT                          // EvalPE<Char>
        .list(
            separator = char(','),
            prefix = char('['),
            postfix = char(']'),   // EvalPE<List<Char>>
        )                          // DOES NOT CONTAIN '[', ']', and ','
}