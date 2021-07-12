package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.mapPe() {
    DIGIT                          // EvalPE<Char>
        .mapPe { it.digitToInt() } // EvalPE<Int>
}