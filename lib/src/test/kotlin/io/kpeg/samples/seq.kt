package io.kpeg.samples

import io.kpeg.pe.Symbol.Rule


fun Rule.seq() {
    // John 42     - OK
    // John 402    - OK
    // John -4     - FAIL
    // John 0      - OK
    // John 034034 - OK
    // Jon 42      - FAIL

    data class Person(val name: String, val age: Int)

    seq<Person> {
        val name = +literal("John")                  // EvalPE<String>
        val age =
            +DIGIT.oneOrMore()                       // EvalPE<List<Char>>
                .joinToString().mapPe { it.toInt() } // EvalPE<Int>

        value { Person(name.get, age.get) }
    }                                                // EvalPE<Person>
}