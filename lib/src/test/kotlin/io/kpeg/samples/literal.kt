package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.literalBlock() {
    // Bob - OK
    // bob - OK
    // BOB - OK

    literal(len = 3) {
        it.equals("Bob", ignoreCase = true)
    } // EvalPE<String>
}

fun Rule.literal() {
    // Alice - OK
    // alice - FAIL
    // ALICE - FAIL

    literal("Alice") // EvalPE<String>
}