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

import arrow.core.Eval.Later
import arrow.core.Eval.Now
import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import kpeg.KPegDsl
import kpeg.pe.NonTerminal.*
import kpeg.pe.NonTerminal.Map
import kpeg.pe.NonTerminal.Predicate.PredicateType.And
import kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import kpeg.pe.Terminal.Character
import kpeg.pe.Terminal.Literal


@KPegDsl
public sealed class Operators {

    // Built-in characters

    public val ANY: EvalPE<Char> = packratChar { true }

    public val DIGIT: EvalPE<Char> = packratChar { it.isDigit() }

    public val LETTER: EvalPE<Char> = packratChar { it.isLetter() }

    public val HEX_DIGIT: EvalPE<Char> = packratChar { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }


    private fun packratChar(b: CharacterBuilderBlock): EvalPE<Char> = Now(Character(packrat = true, b))


    // Character

    public fun char(b: CharacterBuilderBlock): EvalPE<Char> = Now(Character(b = b))

    public fun char(c: Char): EvalPE<Char> = char { it == c }

    public fun char(r: CharRange): EvalPE<Char> = char { it in r }

    public fun char(vararg cs: Char): EvalPE<Char> = char { it in cs }


    // Literal

    public fun literal(len: Int, b: LiteralBuilderBlock): EvalPE<String> = Now(Literal(len, b))

    public fun literal(l: String): EvalPE<String> = literal(l.length) { it == l }

    public fun <T> EvalPE<List<T>>.joinToString(
        separator: CharSequence = "",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T) -> CharSequence)? = null,
    ): EvalPE<String> = mapPe { it.joinToString(separator, prefix, postfix, limit, truncated, transform) }


    // Optional

    public fun <T> EvalPE<T>.optional(): EvalPE<Option<T>> = Now(Optional(pe = this))

    public fun <T> EvalPE<T>.orDefault(value: T): EvalPE<T> = optional().mapPe { it.getOrElse { value } }


    // Repeated

    public fun <T> EvalPE<T>.repeated(range: UIntRange): EvalPE<List<T>> = Now(Repeated(range, pe = this))

    public fun <T> EvalPE<T>.repeated(min: UInt = 0u, max: UInt = UInt.MAX_VALUE): EvalPE<List<T>> =
        repeated(range = min..max)

    public fun <T> EvalPE<T>.repeatedExactly(times: UInt): EvalPE<List<T>> = repeated(range = times..times)

    public fun <T> EvalPE<T>.zeroOrMore(): EvalPE<List<T>> = repeated(range = 0u..UInt.MAX_VALUE)

    public fun <T> EvalPE<T>.oneOrMore(): EvalPE<List<T>> = repeated(range = 1u..UInt.MAX_VALUE)

    public fun <T> EvalPE<T>.list(
        separator: EvalPE<*>? = null,
        prefix: EvalPE<*>? = null,
        postfix: EvalPE<*>? = null,
        min: UInt = 0u, max: UInt = UInt.MAX_VALUE,
    ): EvalPE<List<T>> {

        require(min <= max) { "Range is empty" }

        val pe = this

        return seq {
            prefix?.unaryPlus()

            val content =
                if (min == 0u) {
                    +seq<List<T>> {
                        val first = +pe
                        val others = +seq<T> {
                            separator?.unaryPlus()
                            val next = +pe
                            value { next.get }
                        }.repeated(max = if (max > 0u) max - 1u else 0u)

                        value { listOf(first.get) + others.get }
                    }.orDefault(emptyList())
                } else {
                    +seq<List<T>> {
                        val first = +pe
                        val others = +seq<T> {
                            separator?.unaryPlus()
                            val next = +pe
                            value { next.get }
                        }.repeated(min = min - 1u, max = max - 1u)

                        value { listOf(first.get) + others.get }
                    }
                }

            postfix?.unaryPlus()

            value { content.get }
        }
    }


    // Predicate

    public fun and(pe: EvalPE<*>): EvalPE<Unit> = Now(Predicate(type = And, pe))

    public fun not(pe: EvalPE<*>): EvalPE<Unit> = Now(Predicate(type = Not, pe))


    // Group - Sequence & Prioritized Choice

    public fun <T> seq(b: GroupBuilderBlock<T>): EvalPE<T> = Later { Group.Sequence(b) }

    public fun <T> choice(b: GroupBuilderBlock<T>): EvalPE<T> = Later { Group.PrioritizedChoice(b) }

    public fun <T> choice(firstPe: EvalPE<T>, vararg otherPes: EvalPE<T>): EvalPE<T> = choice {
        val list = mutableListOf(+firstPe)
        for (pe in otherPes) {
            list += +pe
        }

        value { list.first { it.option != None }.get }
    }


    // Map

    public fun <T, R> EvalPE<T>.mapPe(transform: MapBuilderBlock<T, R>): EvalPE<R> = Now(Map(transform, pe = this))
}