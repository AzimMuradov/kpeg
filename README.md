# kpeg

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

<!--- [User guide](https://github.com/AzimMuradov/kpeg/blob/master/docs/user-guide.md)-->
- [Examples](https://github.com/AzimMuradov/kpeg/blob/master/examples)
<!--- KDOC -->


<!--## Installation guide
### Gradle

#### Kotlin DSL
```kotlin
dependencies {
    implementation("io.kpeg:kpeg:0.1.0")
}
```

#### Groovy DSL
```groovy
dependencies {
    implementation 'io.kpeg:kpeg:0.1.0'
}
```

### Maven

```xml
<dependency>
    <groupId>io.kpeg</groupId>
    <artifactId>kpeg</artifactId>
    <version>0.1.0</version>
</dependency>
```-->


## Things to improve before **stable**:

- **add more docs**
- **provide the site for the user guide and docs**
- **add more tests, improve the code coverage**
- add more built-in characters
- add ability to define `Comment` parsing expression
- better error messages and error handling
- support left recursion
- provide more examples
- multiplatform

Suggestions are welcome!


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