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

package io.kpeg.pe

import arrow.core.Eval.Now
import arrow.core.Option
import arrow.core.getOrElse
import io.kpeg.KPegDsl
import io.kpeg.pe.NonTerminal.*
import io.kpeg.pe.NonTerminal.Predicate.PredicateType.And
import io.kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import io.kpeg.pe.Terminal.Character
import io.kpeg.pe.Terminal.Literal


/**
 * All available operators for use in the [rule definitions][Symbol.Rule] or [sequence building][SequenceBuilder].
 */
@KPegDsl
public sealed class Operators {

    // Built-in characters

    /**
     * [Parsing expression][ParsingExpression] that represents any possible character.
     */
    public val ANY: EvalPE<Char> = packratChar { true }

    /**
     * [Parsing expression][ParsingExpression] that represents any decimal digit, that is, any character in '0'..'9'.
     */
    public val DIGIT: EvalPE<Char> = packratChar { it.isDigit() }

    /**
     * [Parsing expression][ParsingExpression] that represents any letter, that is, any character for which `it.isLetter()` equals `true`.
     */
    public val LETTER: EvalPE<Char> = packratChar { it.isLetter() }

    /**
     * [Parsing expression][ParsingExpression] that represents any hexadecimal digit, that is, any character in '0'..'9', 'a'..'f', or 'A'..'F'.
     */
    public val HEX_DIGIT: EvalPE<Char> = packratChar { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }


    private fun packratChar(b: CharacterBuilderBlock): EvalPE<Char> = Now(Character(packrat = true, block = b))


    // Character

    /**
     * [Parsing expression][ParsingExpression] that represents any character that satisfies the [b] lambda.
     *
     * @sample io.kpeg.samples.charBlock
     */
    public fun char(b: CharacterBuilderBlock): EvalPE<Char> = Now(Character(block = b))

    /**
     * [Parsing expression][ParsingExpression] that represents the character [c].
     *
     * @sample io.kpeg.samples.char
     */
    public fun char(c: Char): EvalPE<Char> = char { it == c }

    /**
     * [Parsing expression][ParsingExpression] that represents any character in the char range [r].
     *
     * @sample io.kpeg.samples.charRange
     */
    public fun char(r: CharRange): EvalPE<Char> = char { it in r }

    /**
     * [Parsing expression][ParsingExpression] that represents any character from the [firstC], [secondC], or [otherCs] characters.
     *
     * @sample io.kpeg.samples.chars
     */
    public fun char(firstC: Char, secondC: Char, vararg otherCs: Char): EvalPE<Char> =
        char { it in listOf(firstC, secondC, *otherCs.toTypedArray()) }


    // Literal

    /**
     * [Parsing expression][ParsingExpression] that represents any literal of length [len] that satisfies the [b] lambda.
     *
     * @sample io.kpeg.samples.literalBlock
     */
    public fun literal(len: Int, b: LiteralBuilderBlock): EvalPE<String> = Now(Literal(len, b))

    /**
     * [Parsing expression][ParsingExpression] that represents the literal [l].
     *
     * @sample io.kpeg.samples.literal
     */
    public fun literal(l: String): EvalPE<String> = literal(l.length) { it == l }

    /**
     * This function works just like the [joinToString][kotlin.collections.joinToString] from the standard library, but for the [parsing expressions][ParsingExpression].
     *
     * @receiver [Parsing expression][ParsingExpression] representing the list that would be joined to string.
     *
     * @sample io.kpeg.samples.joinToString
     */
    public fun <T> EvalPE<List<T>>.joinToString(
        separator: CharSequence = "",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T) -> CharSequence)? = null,
    ): EvalPE<String> = mapPe { it.joinToString(separator, prefix, postfix, limit, truncated, transform) }


    // Optional

    /**
     * Optional [parsing expression][ParsingExpression] that yields [Some][arrow.core.Some] value of the type [T] on successful parsing, or [None][arrow.core.None] otherwise.
     *
     * @receiver [Parsing expression][ParsingExpression] representing the element that would be optional.
     *
     * @sample io.kpeg.samples.optional
     */
    public fun <T> EvalPE<T>.optional(): EvalPE<Option<T>> = Now(Optional(pe = this))

    /**
     * Optional [parsing expression][ParsingExpression] with the [default value][value].
     *
     * @receiver [Parsing expression][ParsingExpression] representing the element that would be potentially replaced by the [default value][value].
     *
     * @sample io.kpeg.samples.optionalWithDefault
     */
    public fun <T> EvalPE<T>.orDefault(value: T): EvalPE<T> = optional().mapPe { it.getOrElse { value } }


    // Repeated

    /**
     * Repeat [parsing expression][ParsingExpression] an arbitrary number of times in the specified [range].
     *
     * @receiver [Parsing expression][ParsingExpression] representing one element that would be repeated.
     *
     * @sample io.kpeg.samples.repeatedInRange
     */
    public fun <T> EvalPE<T>.repeated(range: UIntRange): EvalPE<List<T>> = Now(Repeated(range, pe = this))

    /**
     * Repeat [parsing expression][ParsingExpression] from the [min] to the [max] numbers of times.
     *
     * @sample io.kpeg.samples.repeatedWithMinMax
     */
    public fun <T> EvalPE<T>.repeated(min: UInt = 0u, max: UInt = UInt.MAX_VALUE): EvalPE<List<T>> =
        repeated(range = min..max)

    /**
     * Repeat [parsing expression][ParsingExpression] exactly the given [times].
     *
     * @receiver [Parsing expression][ParsingExpression] representing one element that would be repeated.
     *
     * @sample io.kpeg.samples.repeatedExactly
     */
    public fun <T> EvalPE<T>.repeatedExactly(times: UInt): EvalPE<List<T>> = repeated(range = times..times)

    /**
     * Repeat [parsing expression][ParsingExpression] 0 or more times.
     *
     * @receiver [Parsing expression][ParsingExpression] representing one element that would be repeated.
     *
     * @sample io.kpeg.samples.repeatedZeroOrMore
     */
    public fun <T> EvalPE<T>.zeroOrMore(): EvalPE<List<T>> = repeated(range = 0u..UInt.MAX_VALUE)

    /**
     * Repeat [parsing expression][ParsingExpression] 1 or more times.
     *
     * @receiver [Parsing expression][ParsingExpression] representing one element that would be repeated.
     *
     * @sample io.kpeg.samples.repeatedOneOrMore
     */
    public fun <T> EvalPE<T>.oneOrMore(): EvalPE<List<T>> = repeated(range = 1u..UInt.MAX_VALUE)

    /**
     * [Parsing expression][ParsingExpression] that represents a list of elements with the optionally defined [separator], [prefix], and [postfix].
     * Optionally you can also define the allowed size of the list, both [min] and [max].
     *
     * It worth noting that the resulting list will contain only its elements.
     *
     * @receiver [Parsing expression][ParsingExpression] representing one element of the list.
     *
     * @sample io.kpeg.samples.list
     */
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

    /**
     * "And" predicate on the [pe] parsing expression. It succeed iff [pe] succeeds.
     *
     * It worth noting that the [pe] won't be consumed, this is, its only a predicate and not something, that would be parsed.
     * Thus, repeating it will yield the same result.
     *
     * @sample io.kpeg.samples.and
     */
    public fun and(pe: EvalPE<*>): EvalPE<Unit> = Now(Predicate(type = And, pe))

    /**
     * "Not" predicate on the [pe] parsing expression. It succeed iff [pe] fails.
     *
     * It worth noting that the [pe] won't be consumed, this is, its only a predicate and not something, that would be parsed.
     * Thus, repeating it will yield the same result.
     *
     * @sample io.kpeg.samples.not
     */
    public fun not(pe: EvalPE<*>): EvalPE<Unit> = Now(Predicate(type = Not, pe))


    // Sequence

    /**
     * Sequence of [parsing expressions][ParsingExpression], defined inside the [b] block.
     * To register an element of the sequence, use the [unary plus][SequenceBuilder.unaryPlus] on any parsing expression inside the [b] block.
     * You also must use the [value][SequenceBuilder.value] block, which yields the result of this operator.
     *
     * @sample io.kpeg.samples.seq
     */
    public fun <T> seq(b: SequenceBuilderBlock<T>): EvalPE<T> = Now(Sequence(b))


    // Prioritized Choice

    /**
     * Prioritized choice of [parsing expressions][ParsingExpression].
     * Variants will be checked in the order in which they are placed, this is, [firstPe], [secondPe], [otherPes.first(), ..., otherPes.last()][otherPes].
     * The first one to be successful will be the result of this operator.
     *
     * @sample io.kpeg.samples.choice
     */
    public fun <T> choice(firstPe: EvalPE<T>, secondPe: EvalPE<T>, vararg otherPes: EvalPE<T>): EvalPE<T> =
        Now(PrioritizedChoice(firstPe, secondPe, *otherPes))


    // Map

    /**
     * Map [parsing expression][ParsingExpression] using the [transform] lambda.
     *
     * @receiver [Parsing expression][ParsingExpression] representing the element that would be mapped.
     *
     * @sample io.kpeg.samples.mapPe
     */
    public fun <T, R> EvalPE<T>.mapPe(transform: MapBuilderBlock<T, R>): EvalPE<R> = Now(MapPe(transform, pe = this))
}