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

import kpeg.ParsingExpression.*
import kpeg.ParsingExpression as PE


public class RuleBuilder internal constructor() {

    internal fun build(block: RuleBuilderBlock): Rule = Rule(block())


    // Standard operators

    public val empty: PE = Empty

    public fun char(c: Char): PE = Character(c)

    public fun literal(s: String): PE = Literal(s)

    public fun chars(r: CharRange): PE {
        check(!r.isEmpty()) { "Character class shouldn't be empty!" }
        return CharacterClass(r)
    }

    public val any: PE = AnyCharacter

    public fun PE.opt(): PE = Optional(this)

    public fun PE.zeroOrMore(): PE = ZeroOrMore(this)

    public fun PE.oneOrMore(): PE = OneOrMore(this)

    public fun and(e: PE): PE = AndPredicate(e)

    public fun not(e: PE): PE = NotPredicate(e)

    public fun sequence(lhs: PE, rhs: PE): PE = Sequence(lhs, rhs)

    public operator fun PE.plus(other: PE): PE = Sequence(this, other)

    public operator fun PE.div(other: PE): PE = PrioritizedChoice(this, other)
}