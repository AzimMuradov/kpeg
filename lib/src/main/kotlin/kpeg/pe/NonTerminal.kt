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
import kpeg.Option
import kpeg.Option.None
import kpeg.Option.Some
import kpeg.PegParser.ParserState
import kpeg.pe.GroupBuilder.ValueBuilder
import kpeg.pe.NonTerminal.Predicate.PredicateType.And
import kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import kpeg.unwrap
import kpeg.unwrapOrNull
import kpeg.pe.ParsingExpression as PE


internal sealed class NonTerminal<T> : PE<T>() {

    internal class Optional<T>(pe: PE<T>) : NonTerminal<Option<T>>() {

        private val repeated = Repeated(1u..1u, pe)

        override fun parse(ps: ParserState): Option<Option<T>> = Some(repeated.parse(ps).firstOrNone())

        private fun Option<List<T>>.firstOrNone(): Option<T> = when (val res = this) {
            is Some -> Some(res.value.first())
            None -> None
        }
    }

    internal class Repeated<T>(private val range: UIntRange, private val pe: PE<T>) : NonTerminal<List<T>>() {

        override fun parse(ps: ParserState): Option<List<T>> {
            val initI = ps.i

            val list = mutableListOf<T>()
            while (list.size.toUInt() < range.last) {
                when (val res = pe.parse(ps)) {
                    is Some -> list += res.value
                    None -> break
                }
            }
            return if (list.size.toUInt() in range) {
                Some(list)
            } else {
                ps.i = initI
                None
            }
        }
    }

    internal class Predicate(private val type: PredicateType, private val pe: PE<*>) : NonTerminal<Unit>() {

        override fun parse(ps: ParserState): Option<Unit> {
            val initI = ps.i

            return when (type) {
                And -> if (pe.parse(ps) != None) Some(Unit) else None
                Not -> if (pe.parse(ps) != None) None else Some(Unit)
            }.also { ps.i = initI }
        }

        internal enum class PredicateType { And, Not }
    }

    internal sealed class Group<T>(private val b: GroupBuilderBlock<T>) : NonTerminal<T>() {

        final override fun parse(ps: ParserState): Option<T> {
            val initI = ps.i
            val (subexpressions, valueBlock) = GroupBuilder<T>().build(b)

            return if (successCondition(subexpressions, ps)) {
                Some(ValueBuilder.valueBlock())
            } else {
                ps.i = initI
                None
            }
        }

        protected abstract fun successCondition(subexpressions: List<StoredPE<*>>, ps: ParserState): Boolean


        internal class Sequence<T>(b: GroupBuilderBlock<T>) : Group<T>(b) {

            override fun successCondition(subexpressions: List<StoredPE<*>>, ps: ParserState): Boolean {
                return subexpressions.all { it.parse(ps) != None }
            }
        }

        internal class PrioritizedChoice<T>(b: GroupBuilderBlock<T>) : Group<T>(b) {

            override fun successCondition(subexpressions: List<StoredPE<*>>, ps: ParserState): Boolean {
                if (subexpressions.isEmpty()) return true

                val initI = ps.i
                val indexOfFirstSuccess = subexpressions.indexOfFirst {
                    ps.i = initI
                    it.parse(ps) != None
                }
                return indexOfFirstSuccess != -1
            }
        }
    }

    internal class Map<T, R>(private val transform: MapBuilderBlock<T, R>, private val pe: PE<T>) : NonTerminal<R>() {

        override fun parse(ps: ParserState): Option<R> = when (val res = pe.parse(ps)) {
            is Some -> Some(MapBuilder.transform(res.value))
            None -> None
        }
    }
}


public class GroupBuilder<T> internal constructor() : Operators() {

    // Subexpressions

    public operator fun <T> PE<T>.unaryPlus(): StoredPE<T> = StoredPE(pe = this).also(subexpressions::add)

    private val subexpressions = mutableListOf<StoredPE<*>>()


    // Value

    @KPegDsl
    public object ValueBuilder {

        public val <T> StoredPE<T>.value: T get() = parsedValue.unwrap()

        public val <T> StoredPE<T>.nullable: T? get() = parsedValue.unwrapOrNull()

        public val <T> StoredPE<T>.option: Option<T> get() = parsedValue
    }

    public fun value(b: ValueBuilder.() -> T) {
        valueBlock = b
    }

    private lateinit var valueBlock: ValueBuilder.() -> T


    // Build

    internal fun build(b: GroupBuilderBlock<T>): Pair<List<StoredPE<*>>, ValueBuilder.() -> T> {
        this.b()
        return subexpressions to valueBlock
    }
}

internal typealias GroupBuilderBlock<T> = GroupBuilder<T>.() -> Unit


@KPegDsl
public object MapBuilder

internal typealias MapBuilderBlock<T, R> = MapBuilder.(T) -> R