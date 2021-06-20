/*
 * Copyright 2021-2021 Azim Muradov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kpeg


public sealed class ParsingExpression {

    internal class Character(c: Char) : ParsingExpression()

    internal class Literal(s: String) : ParsingExpression()

    internal class CharacterClass(r: CharRange) : ParsingExpression()

    internal object AnyCharacter : ParsingExpression()

    // internal class Grouping(e: ParsingExpression) : ParsingExpression()

    internal class Optional(e: ParsingExpression) : ParsingExpression()

    internal class ZeroOrMore(e: ParsingExpression) : ParsingExpression()

    internal class OneOrMore(e: ParsingExpression) : ParsingExpression()

    internal class AndPredicate(e: ParsingExpression) : ParsingExpression()

    internal class NotPredicate(e: ParsingExpression) : ParsingExpression()

    internal class Sequence(lhs: ParsingExpression, rhs: ParsingExpression) : ParsingExpression()

    internal class PrioritizedChoice(lhs: ParsingExpression, rhs: ParsingExpression) : ParsingExpression()
}