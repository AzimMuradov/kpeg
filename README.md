[![logo.png](docs/resources/logo-with-bg.png)](https://kpeg.io)

[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.kpeg/kpeg?color=3A3&label=latest%20release&server=https%3A%2F%2Fs01.oss.sonatype.org)](https://search.maven.org/artifact/io.kpeg/kpeg)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.kpeg/kpeg?color=993&label=latest%20snapshot&server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/service/local/repositories/snapshots/content/io/kpeg/kpeg/maven-metadata.xml)
[![GitHub Actions - Build project](https://github.com/AzimMuradov/kpeg/actions/workflows/build.yml/badge.svg)](https://github.com/AzimMuradov/kpeg/actions/workflows/build.yml)
[![License](https://img.shields.io/github/license/AzimMuradov/kpeg?color=blue)](https://www.apache.org/licenses/LICENSE-2.0)


Welcome to the **Kotlin PEG parser** with **Kotlin DSL**!

The project is inspired by the [pest parser](https://pest.rs/) and the [kotlin-peg-dsl project](https://github.com/mikaelhg/kotlin-peg-dsl).


## Quick start
### Simple example

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

### Resources

- [Documentation](https://kpeg.io)
- Examples
  - [Simple calculator](https://kpeg.io/pages/examples/simple-calc/)
  - [Json parser](https://kpeg.io/pages/examples/json/)
- [About PEG](https://en.wikipedia.org/wiki/Parsing_expression_grammar)
- [PEG formal description](https://bford.info/pub/lang/peg.pdf)
- [Other useful papers](https://bford.info/packrat/)


## Installation guide
### Gradle

#### Kotlin DSL
```kotlin
dependencies {
    implementation("io.kpeg:kpeg:0.1.2")
}
```

#### Groovy DSL
```groovy
dependencies {
    implementation 'io.kpeg:kpeg:0.1.2'
}
```

### Maven

```xml
<dependency>
    <groupId>io.kpeg</groupId>
    <artifactId>kpeg</artifactId>
    <version>0.1.2</version>
</dependency>
```


## Things to improve before **stable**:

- **add more docs**
- **add more tests, improve the code coverage**
- add more built-in characters
- add ability to define `Comment` parsing expression
- better error messages and error handling
- support left recursion
- provide more examples
- multiplatform

[Suggestions](https://github.com/AzimMuradov/kpeg/issues) are welcome!


## License

kpeg is released under the [Apache 2.0 license](https://github.com/AzimMuradov/kpeg/blob/master/LICENSE).

```
Copyright 2021-2021 Azim Muradov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```