# Operators


| Operator |  Library analogy  | Description  | Precedence |    Description     |
|:--------:|:-----------------:|:------------:|:----------:|:------------------:|
|   ' '    |   char(`Char`)    |   primary    |     5      |   Literal string   |
|   " "    | literal(`String`) |   primary    |     5      |   Literal string   |
|  \[ \]   | chars(CharRange)  |   primary    |     5      |  Character class   |
|    .     |        any        |   primary    |     5      |   Any character    |
|   (e)    |      (`PE`)       |   primary    |     5      |      Grouping      |
|    e?    |     `PE`.opt      | unary suffix |     4      |      Optional      |
|    e*    |  `PE`.zeroOrMore  | unary suffix |     4      |    Zero-or-more    |
|    e+    |  `PE`.oneOrMore   | unary suffix |     4      |    One-or-more     |
|    &e    |     and(`PE`)     | unary prefix |     3      |   And-predicate    |
|    !e    |     not(`PE`)     | unary prefix |     3      |   Not-predicate    |
|  e1 e2   |    `PE` + `PE`    |    binary    |     2      |      Sequence      |
| e1 / e2  |    `PE` / `PE`    |    binary    |     1      | Prioritized Choice |
