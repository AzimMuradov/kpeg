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

package io.kpeg.pe

import arrow.core.*
import io.kpeg.KPegDsl
import io.kpeg.ParseErrorMessages.wrong
import io.kpeg.ParserState
import io.kpeg.pe.NonTerminal.Predicate.PredicateType.And
import io.kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import io.kpeg.pe.SequenceBuilder.ValueBuilder


internal sealed class NonTerminal<T> : ParsingExpression<T>(packrat = false) {

    internal class Optional<T>(private val pe: EvalPE<T>) : NonTerminal<Option<T>>() {

        override val logName: String by lazy { "Optional(${pe.value().logName})" }

        private val repeated = Repeated(0u..1u, pe)

        override fun parseCore(ps: ParserState) = repeated.parse(ps).map(List<T>::firstOrNone)
    }

    internal class Repeated<T>(private val range: UIntRange, private val pe: EvalPE<T>) : NonTerminal<List<T>>() {

        init {
            require(!range.isEmpty()) { "Range is empty" }
        }


        override val logName: String by lazy { "Repeated(${pe.value().logName}) $range times" }

        override fun parseCore(ps: ParserState): Option<List<T>> {
            val initI = ps.i

            val list = mutableListOf<T>()
            while (list.size.toUInt() < range.last) {
                when (val res = pe.value().parse(ps)) {
                    is Some -> list += res.value
                    None -> break
                }
            }

            val result = list.takeIf { it.size.toUInt() in range }.toOption()

            return result.also {
                when (it) {
                    is Some -> {
                        ps.errs.clear()
                    }
                    None -> {
                        ps.i = initI
                        ps.addErr(wrong(logName) + ", but was repeated ${list.size} times")
                    }
                }
            }
        }
    }

    internal class Predicate(private val type: PredicateType, private val pe: EvalPE<*>) : NonTerminal<Unit>() {

        override val logName: String by lazy { "${type.name}(${pe.value().logName})" }


        override fun parseCore(ps: ParserState): Option<Unit> {
            val initI = ps.i

            return when (type) {
                And -> if (pe.value().parse(ps) != None) Some(Unit) else None
                Not -> if (pe.value().parse(ps) == None) Some(Unit) else None
            }.also {
                ps.i = initI
                when (it) {
                    None -> ps.addErr(wrong(logName))
                    is Some -> ps.errs.clear()
                }
            }
        }

        internal enum class PredicateType { And, Not }
    }

    internal class Sequence<T>(private val block: SequenceBuilderBlock<T>) : NonTerminal<T>() {

        init {
            check(build().first.isNotEmpty()) { "The sequence must contain 1 or more subexpressions" }
        }


        private fun build() = SequenceBuilder<T>().build(block)


        override val logName: String by lazy { "Sequence(${logNames.joinToString()})" }

        private val logNames by lazy { build().first.map(StoredPE<*>::peLogName) }


        override fun parseCore(ps: ParserState): Option<T> {
            val initI = ps.i
            val (subexpressions, valueBlock) = build()

            return if (subexpressions.all { it.parse(ps) != None }) {
                Some(ValueBuilder.valueBlock())
            } else {
                None.also {
                    ps.i = initI
                    ps.addErr(wrong(logName))
                }
            }
        }
    }

    internal class PrioritizedChoice<T>(
        firstPe: EvalPE<T>, secondPe: EvalPE<T>, vararg otherPes: EvalPE<T>,
    ) : NonTerminal<T>() {

        private val pes: List<EvalPE<T>> = listOf(firstPe, secondPe) + otherPes

        override val logName: String by lazy {
            "PrioritizedChoice(${pes.joinToString(separator = " / ") { it.value().logName }})"
        }

        override fun parseCore(ps: ParserState): Option<T> {
            val initI = ps.i

            var parsedValue: Option<T> = None

            pes.asSequence().map(EvalPE<T>::value).firstOrNull {
                ps.i = initI
                parsedValue = it.parse(ps)
                parsedValue != None
            }

            return parsedValue.also {
                when (it) {
                    is Some -> {
                        ps.errs.clear()
                    }
                    None -> {
                        ps.i = initI
                        ps.addErr(wrong(logName))
                    }
                }
            }
        }
    }

    internal class Map<T, R>(private val transform: MapBuilderBlock<T, R>, private val pe: EvalPE<T>) :
        NonTerminal<R>() {

        override val logName: String by lazy { pe.value().logName }

        override fun parseCore(ps: ParserState) = pe.value().parse(ps).map { MapBuilder.transform(it) }
    }
}


public class SequenceBuilder<T> internal constructor() : Operators() {

    // Subexpressions

    public operator fun <T> EvalPE<T>.unaryPlus(): StoredPE<T> = StoredPE(pe = this).also(subexpressions::add)

    private val subexpressions = mutableListOf<StoredPE<*>>()


    // Value

    @KPegDsl
    public object ValueBuilder {

        public val <T> StoredPE<T>.get: T get() = parsedPeValue
    }

    public fun value(b: ValueBuilderBlock<T>) {
        valueBlock = b
    }

    private lateinit var valueBlock: ValueBuilderBlock<T>


    // Build

    internal fun build(b: SequenceBuilderBlock<T>): Pair<List<StoredPE<*>>, ValueBuilderBlock<T>> {
        this.b()
        return subexpressions to valueBlock
    }
}

internal typealias SequenceBuilderBlock<T> = SequenceBuilder<T>.() -> Unit
internal typealias ValueBuilderBlock<T> = ValueBuilder.() -> T


@KPegDsl
public object MapBuilder

internal typealias MapBuilderBlock<T, R> = MapBuilder.(T) -> R