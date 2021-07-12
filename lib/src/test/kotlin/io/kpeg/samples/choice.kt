package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.choice() {
    // alpha - OK
    // beta  - OK
    // gamma - OK
    // delta - FAIL

    choice(
        literal("alpha"),
        literal("beta"),
        literal("gamma"),
    ) // EvalPE<String>
}