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

import arrow.core.*
import kpeg.KPegDsl
import kpeg.ParseErrorMessages.wrong
import kpeg.ParserState
import kpeg.get
import kpeg.pe.GroupBuilder.ValueBuilder
import kpeg.pe.NonTerminal.Predicate.PredicateType.And
import kpeg.pe.NonTerminal.Predicate.PredicateType.Not


internal sealed class NonTerminal<T> : ParsingExpression<T>(packrat = false) {

    internal class Optional<T>(private val pe: EvalPE<T>) : NonTerminal<Option<T>>() {

        override val logName: String get() = "Optional(${pe.value().logName})"

        private val repeated = Repeated(0u..1u, pe)

        override fun parseCore(ps: ParserState) = repeated.parse(ps).map(List<T>::firstOrNone)
    }

    internal class Repeated<T>(private val range: UIntRange, private val pe: EvalPE<T>) : NonTerminal<List<T>>() {

        init {
            require(!range.isEmpty()) { "Range is empty" }
        }


        override val logName: String get() = "Repeated(${pe.value().logName}) $range times"

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

        override val logName: String get() = "${type.name}(${pe.value().logName})"


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

    internal sealed class Group<T>(private val b: GroupBuilderBlock<T>) : NonTerminal<T>() {

        protected val logNames = GroupBuilder<T>().build(b).first.map(StoredPE<*>::peLogName)


        final override fun parseCore(ps: ParserState): Option<T> {
            val initI = ps.i
            val (subexpressions, valueBlock) = GroupBuilder<T>().build(b)

            return if (successCondition(subexpressions, ps)) {
                Some(ValueBuilder.valueBlock())
            } else {
                None.also {
                    ps.i = initI
                    ps.addErr(wrong(logName))
                }
            }
        }

        protected abstract fun successCondition(subexpressions: List<StoredPE<*>>, ps: ParserState): Boolean


        internal class Sequence<T>(b: GroupBuilderBlock<T>) : Group<T>(b) {

            override val logName: String = "Sequence(${logNames.joinToString()})"

            override fun successCondition(subexpressions: List<StoredPE<*>>, ps: ParserState): Boolean {
                return subexpressions.all { it.parse(ps) != None }
            }
        }

        internal class PrioritizedChoice<T>(b: GroupBuilderBlock<T>) : Group<T>(b) {

            override val logName: String = "PrioritizedChoice(${logNames.joinToString(separator = " / ")})"

            override fun successCondition(subexpressions: List<StoredPE<*>>, ps: ParserState): Boolean {
                if (subexpressions.isEmpty()) return true

                val initI = ps.i
                val indexOfFirstSuccess = subexpressions.indexOfFirst {
                    ps.i = initI
                    it.parse(ps) != None
                }

                if (indexOfFirstSuccess != -1) {
                    ps.errs.clear()
                }

                return indexOfFirstSuccess != -1
            }
        }
    }

    internal class Map<T, R>(private val transform: MapBuilderBlock<T, R>, private val pe: EvalPE<T>) :
        NonTerminal<R>() {

        override val logName: String get() = pe.value().logName

        override fun parseCore(ps: ParserState) = pe.value().parse(ps).map { MapBuilder.transform(it) }
    }
}


public class GroupBuilder<T> internal constructor() : Operators() {

    // Subexpressions

    public operator fun <T> EvalPE<T>.unaryPlus(): StoredPE<T> = StoredPE(pe = this).also(subexpressions::add)

    private val subexpressions = mutableListOf<StoredPE<*>>()


    // Value

    @KPegDsl
    public object ValueBuilder {

        public val <T> StoredPE<T>.get: T get() = parsedValue.get()

        public val <T> StoredPE<T>.nullable: T? get() = parsedValue.orNull()

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