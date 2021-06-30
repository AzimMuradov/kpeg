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

@file:Suppress("PropertyName")

package kpeg.pe

import kpeg.KPegDsl
import kpeg.Option
import kpeg.Option.None
import kpeg.pe.NonTerminal.*
import kpeg.pe.NonTerminal.Map
import kpeg.pe.NonTerminal.Predicate.PredicateType.And
import kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import kpeg.pe.Terminal.Character
import kpeg.pe.Terminal.Literal
import kpeg.unwrapOrNull
import kpeg.pe.ParsingExpression as PE


@KPegDsl
public sealed class Operators {

    // Built-in characters

    public val ANY: PE<Char> = BuiltInCharacter.ANY

    public val DIGIT: PE<Char> = BuiltInCharacter.DIGIT

    public val LETTER: PE<Char> = BuiltInCharacter.LETTER

    public val HEX_DIGIT: PE<Char> = BuiltInCharacter.HEX_DIGIT


    // Character

    public fun char(b: CharacterBuilderBlock): PE<Char> = Character(b)

    public fun char(c: Char): PE<Char> = Character { it == c }

    public fun char(r: CharRange): PE<Char> = Character { it in r }

    public fun char(vararg cs: Char): PE<Char> = Character { it in cs }


    // Literal

    public fun literal(len: Int, b: LiteralBuilderBlock): PE<String> = Literal(len, b)

    public fun literal(l: String): PE<String> = Literal(l.length) { it == l }

    public fun <T> PE<List<T>>.joinToString(
        separator: CharSequence = "",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T) -> CharSequence)? = null,
    ): PE<String> = Map({ it.joinToString(separator, prefix, postfix, limit, truncated, transform) }, pe = this)


    // Optional

    public fun <T> PE<T>.optional(): PE<Option<T>> = Optional(pe = this)

    public fun <T> PE<T>.orDefault(value: T): PE<T> = Map({ it.unwrapOrNull() ?: value }, pe = Optional(pe = this))


    // Repeated

    public fun <T> PE<T>.repeated(range: UIntRange): PE<List<T>> =
        if (!range.isEmpty()) Repeated(range, pe = this) else Fail

    public fun <T> PE<T>.repeated(min: UInt = 0u, max: UInt = UInt.MAX_VALUE): PE<List<T>> =
        if (min <= max) Repeated(range = min..max, pe = this) else Fail

    public fun <T> PE<T>.repeatedExactly(times: UInt): PE<List<T>> = Repeated(range = times..times, pe = this)

    public fun <T> PE<T>.zeroOrMore(): PE<List<T>> = Repeated(range = 0u..UInt.MAX_VALUE, pe = this)

    public fun <T> PE<T>.oneOrMore(): PE<List<T>> = Repeated(range = 1u..UInt.MAX_VALUE, pe = this)

    public fun <T> PE<T>.list(
        separator: PE<*>? = null,
        prefix: PE<*>? = null,
        postfix: PE<*>? = null,
        min: UInt = 0u, max: UInt = UInt.MAX_VALUE,
    ): PE<List<T>> =
        if (min <= max) {
            Group.Sequence {
                prefix?.unaryPlus()

                val content =
                    if (min == 0u && max > 0u) {
                        +Group.Sequence<List<T>> {
                            val first = +this@list
                            val others = +Repeated(range = 0u..(max - 1u), pe = Group.Sequence<T> {
                                separator?.unaryPlus()
                                val next = +this@list
                                value { next.get }
                            })

                            value { listOf(first.get) + others.get }
                        }.orDefault(emptyList())
                    } else if (min > 0u && max > 0u) {
                        +Group.Sequence<List<T>> {
                            val first = +this@list
                            val others = +Repeated(range = (min - 1u)..(max - 1u), pe = Group.Sequence<T> {
                                separator?.unaryPlus()
                                val next = +this@list
                                value { next.get }
                            })

                            value { listOf(first.get) + others.get }
                        }
                    } else {
                        +Empty.map { emptyList<T>() }
                    }

                postfix?.unaryPlus()

                value { content.get }
            }
        } else {
            Fail
        }


    // Predicate

    public fun and(pe: PE<*>): PE<Unit> = Predicate(type = And, pe)

    public fun not(pe: PE<*>): PE<Unit> = Predicate(type = Not, pe)


    // Group - Sequence & Prioritized Choice

    public fun <T> seq(b: GroupBuilderBlock<T>): PE<T> = Group.Sequence(b)

    public fun <T> choice(b: GroupBuilderBlock<T>): PE<T> = Group.PrioritizedChoice(b)

    public fun <T> choice(firstPe: PE<T>, vararg otherPes: PE<T>): PE<T> = Group.PrioritizedChoice {
        val list = mutableListOf(+firstPe)
        for (pe in otherPes) {
            list += +pe
        }

        value { list.first { it.option != None }.get }
    }


    // Map

    public fun <T, R> PE<T>.map(transform: MapBuilderBlock<T, R>): PE<R> = Map(transform, pe = this)
}