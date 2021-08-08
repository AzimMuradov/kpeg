# User guide

This is only a user guide, after reading it, it's recommended that you also read [KDoc](http://kpeg.io/kdoc/index.html).


## What is PEG?

!!! quote "From [Wikipedia](https://en.wikipedia.org/wiki/Parsing_expression_grammar):"
    In computer science, a parsing expression grammar (PEG), is a type of analytic formal grammar,
    i.e. it describes a formal language in terms of a set of rules for recognizing strings in the language.


## How to use **kpeg**?

!!! info
    **kpeg** is a PEG parser, not PEG parser generator.

To use it you need to follow these simple steps:

### 1. Define your grammar and objects

=== "Grammar"

    ```kotlin
    object MyGrammar {
    
        val MySymbol: EvalSymbol<MyObj> = Symbol.lazyRule(name = "MySymbol") { /* PE */ }
    
        private val A: EvalSymbol<Int> = Symbol.lazyRule(name = "A") { /* PE */ }
    
        private val B: EvalSymbol<BObj> = Symbol.lazyRule(name = "B") { /* PE */ }
    
        private val C: EvalSymbol<String> = Symbol.lazyRule(name = "C") { /* PE */ }
    }
    ```

=== "Objects"

    ```kotlin
    data class MyObj(val a: Int, val b: BObj, val c: String)

    data class BObj(val p: Double, val q: String)
    ```

### 2. Define a utility function for parsing

```kotlin
fun parseMySymbol(text: String) = PegParser.parse(symbol = MyGrammar.MySymbol, text)
```

### 3. Use it

```kotlin
fun main() {
    val result = parseMySymbol(text = "your text")

    println(result)
}
```


## How to define a `Symbol`?

The definition of a `Symbol` is a `Rule`. To write a rule you need to use one of the two available factory methods:

=== "`#!kotlin Symbol.lazyRule`"

    Creates symbols _lazily_, so the order in which they are declared does not matter.

    ```kotlin
    object MyGrammar {

        val MarkedPersonSym = Symbol.lazyRule(name = "MarkedPersonSym") { // `Operators` scope
            // Any available operator, e.g. a sequence:
            seq<Pair<String, Char>> { // `SequenceBuilder<Pair<String, Char>>` : `Operators` scope
                val person = +PersonSym
                val mark = +MarkSym

                value { person to mark }
            }
        }

        val PersonSym = Symbol.lazyRule(name = "PersonSym") { // `Operators` scope
            // Any available operator, e.g. a literal:
            literal("Alice")
        }

        val MarkSym = Symbol.lazyRule(name = "MarkSym") { // `Operators` scope
            // Any available operator, e.g. a character:
            char('A')
        }
    }
    ```

=== "`#!kotlin Symbol.rule`"

    Unlike its lazy counterpart, `#!kotlin Symbol.rule` creates symbols _in eager fashion_, so the order in which they are declared is of particular importance.

    ```kotlin
    object MyGrammar {

        val PersonSym = Symbol.rule(name = "PersonSym") { // `Operators` scope
            // Any available operator, e.g. a literal:
            literal("Alice")
        }

        val MarkSym = Symbol.rule(name = "MarkSym") { // `Operators` scope
            // Any available operator, e.g. a character:
            char('A')
        }

        val MarkedPersonSym = Symbol.rule(name = "MarkedPersonSym") { // `Operators` scope
            // Any available operator, e.g. a sequence:
            seq<Pair<String, Char>> { // `SequenceBuilder<Pair<String, Char>>` : `Operators` scope
                val person = +PersonSym
                val mark = +MarkSym

                value { person to mark }
            }
        }
    }
    ```

!!! tip
    It's recommended to use `#!kotlin Symbol.lazyRule` because of its high versatility and low probability of unexpected errors.


## Library operators and their comparison with the classic PEG syntax

!!! info
    The list below describes only those operators that have an analogy in the classic PEG syntax.
    A complete list of all available operators can be found [here (KDoc)](https://kpeg.io/kdoc/kpeg%20library/io.kpeg.pe/-operators/index.html).

|              Library operator              | Analogy of the classic PEG syntax |              Description              |
|:------------------------------------------:|:---------------------------------:|:-------------------------------------:|
|            `#!kotlin char(Char)`           |                ' '                |               Character               |
|      `#!kotlin char(Char, Char, ...)`      |                                   |         One of the characters         |
|    `#!kotlin char { (Char) -> Boolean }`   |                                   |    Character described by the block   |
|         `#!kotlin char(CharRange)`         |               \[ \]               |            Character class            |
|         `#!kotlin literal(String)`         |                " "                |             Literal string            |
| `#!kotlin literal { (String) -> Boolean }` |                                   | Literal string described by the block |
|               `#!kotlin ANY`               |                 .                 |             Any character             |
|               `#!kotlin (PE)`              |                (e)                |                Grouping               |
|          `#!kotlin PE.optional()`          |                 e?                |                Optional               |
|         `#!kotlin PE.zeroOrMore()`         |                 e*                |              Zero-or-more             |
|          `#!kotlin PE.oneOrMore()`         |                 e+                |              One-or-more              |
|             `#!kotlin and(PE)`             |                 &e                |             And-predicate             |
|             `#!kotlin not(PE)`             |                 !e                |             Not-predicate             |
|           `#!kotlin seq { ... }`           |         e~1~ e~2~ ... e~n~        |                Sequence               |
|       `#!kotlin choice(PE, PE, ...)`       |      e~1~ / e~2~ / ... / e~n~     |           Prioritized choice          |


## Please share your ideas

!!! attention ""
    The lib is currently **not promises backward-compatability**, so feel free to [pull request](https://github.com/AzimMuradov/kpeg/pulls).


## Need help?

For more in-depth documentation, see the [KDoc](http://kpeg.io/kdoc/index.html).

!!! help ""
    If you have any questions left, please, feel free to ask them [here](https://github.com/AzimMuradov/kpeg/issues).



<!-- Add abbreviations -->

--8<-- "resources/abbreviations.md"