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

package kpeg.pe

import kpeg.KPegDsl
import kpeg.pe.NonTerminal.*
import kpeg.pe.NonTerminal.Predicate.PredicateType.And
import kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import kpeg.pe.Terminal.Character
import kpeg.pe.Terminal.Literal
import kpeg.pe.ParsingExpression as PE


@KPegDsl
public sealed class Operators {

    // Character

    public fun char(b: CharacterBuilderBlock): PE<Char> = Character(b)

    public fun char(c: Char): PE<Char> = Character { it == c }

    public fun char(r: CharRange): PE<Char> = Character { it in r }

    public fun char(vararg cs: Char): PE<Char> = Character { it in cs }


    // Literal

    public fun literal(len: Int, b: LiteralBuilderBlock): PE<String> = Literal(len, b)

    public fun literal(l: String): PE<String> = Literal(l.length) { it == l }


    // Optional

    public fun <T> opt(pe: PE<T>): PE<T> = Optional(pe)


    // Repeated

    public fun <T> repeated(range: UIntRange, pe: PE<T>): PE<List<T>> = Repeated(range, pe)

    public fun <T> repeated(min: UInt = 0u, max: UInt = UInt.MAX_VALUE, pe: PE<T>): PE<List<T>> =
        Repeated(min..max, pe)

    public fun <T> repeatedExactly(times: UInt, pe: PE<T>): PE<List<T>> = Repeated(times..times, pe)

    public fun <T> zeroOrMore(pe: PE<T>): PE<List<T>> = Repeated(0u..UInt.MAX_VALUE, pe)

    public fun <T> oneOrMore(pe: PE<T>): PE<List<T>> = Repeated(1u..UInt.MAX_VALUE, pe)


    // Predicate

    public fun and(pe: PE<*>): PE<Unit> = Predicate(type = And, pe)

    public fun not(pe: PE<*>): PE<Unit> = Predicate(type = Not, pe)


    // Sequence

    public fun <T> seq(b: GroupBuilderBlock<T>): PE<T> = Sequence(b)


    // Prioritized Choice

    public fun <T> choice(b: GroupBuilderBlock<T>): PE<T> = PrioritizedChoice(b)
}