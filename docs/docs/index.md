![logo.png](assets/images/logo-with-bg.png)

[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.kpeg/kpeg?color=green&label=latest%20release&server=https%3A%2F%2Fs01.oss.sonatype.org)](https://search.maven.org/artifact/io.kpeg/kpeg/{{ project.version }}/jar)
[![License](https://img.shields.io/github/license/AzimMuradov/kpeg?color=blue)](https://www.apache.org/licenses/LICENSE-2.0)


Welcome to the **Kotlin PEG parser** with **Kotlin DSL**!

The project is inspired by the [pest parser](https://pest.rs/) and the [kotlin-peg-dsl project](https://github.com/mikaelhg/kotlin-peg-dsl).


## Simple example

```kotlin
val num = Symbol.rule<Int>(name = "Num", ignoreWS = false) {
    seq {
        val sign = +char('+', '-').orDefault('+')
        val digits = +DIGIT.oneOrMore().joinToString()

        value { (sign.get + digits.get).toInt() }
    }
}

val sum = Symbol.rule<Int>(name = "Sum") {
    num.list(separator = char('+'), min = 1u).mapPe { it.sum() }
}


fun evalExpr(expression: String) =
    PegParser.parse(symbol = sum.value(), expression).getOrElse { null }

val results = listOf(
    evalExpr("1"),                         // 1
    evalExpr("+1"),                        // 1
    evalExpr("+ 1"),                       // null
    evalExpr("+1 +"),                      // null
    evalExpr("-17"),                       // -17
    evalExpr("-1 7"),                      // null
    evalExpr("1+2+3+4+5"),                 // 15
    evalExpr("1 + +2 + -3 + +4 + 5"),      // 9
    evalExpr("definitely not expression"), // null
    evalExpr(""),                          // null
)

for (res in results) {
    println(res)
}
```


## Resources

- [Documentation](https://kpeg.io)
- Examples
    - [Simple calculator](https://kpeg.io/pages/examples/simple-calc/)
    - [Json parser](https://kpeg.io/pages/examples/json/)
- [About PEG](https://en.wikipedia.org/wiki/Parsing_expression_grammar)
- [PEG formal description](https://bford.info/pub/lang/peg.pdf)
- [Other useful papers](https://bford.info/packrat/)


## Things to improve before **stable**:

!!! todo "TODO"
    - **add more docs**
    - **add more tests, improve the code coverage**
    - add more built-in characters
    - add ability to define `Comment` parsing expression
    - better error messages and error handling
    - support left recursion
    - provide more examples
    - multiplatform

[Suggestions](https://github.com/AzimMuradov/kpeg/issues) are welcome!



<!-- Add abbreviations -->

--8<-- "resources/abbreviations.md"