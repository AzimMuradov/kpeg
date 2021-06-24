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
import kpeg.StoredPE
import kpeg.pe.GroupBuilder.ValueBuilder
import kpeg.pe.NonTerminal.Predicate.PredicateType.And
import kpeg.pe.NonTerminal.Predicate.PredicateType.Not
import kpeg.pe.ParsingExpression as PE


@KPegDsl
public class GroupBuilder<T> internal constructor(private val ps: ParserState) : Operators() {

    // Subexpressions

    public operator fun <T> PE<T>.unaryPlus(): StoredPE<T> = StoredPE(pe = this).also { subexpressions += it }

    internal val subexpressions = mutableListOf<StoredPE<*>>()


    // Value

    @KPegDsl
    public object ValueBuilder

    public fun value(b: ValueBuilderBlock<T>) {
        valueBlock = b
    }

    internal lateinit var valueBlock: ValueBuilderBlock<T>
}

public typealias ValueBuilderBlock<T> = ValueBuilder.() -> T
public typealias GroupBuilderBlock<T> = GroupBuilder<T>.() -> Unit


public sealed class NonTerminal<T> : PE<T>() {

    internal class Optional<T>(pe: PE<T>) : NonTerminal<T>() {

        private val repeated = Repeated(1u..1u, pe)


        override fun peek(ps: ParserState): Option<T> = repeated.peek(ps).first()

        override fun parse(ps: ParserState): Option<T> = repeated.parse(ps).first()


        private fun Option<List<T>>.first(): Option<T> = when (val res = this) {
            is Some -> Some(res.value.first())
            None -> None
        }
    }

    internal class Repeated<T>(private val range: UIntRange, private val pe: PE<T>) : NonTerminal<List<T>>() {

        override fun peek(ps: ParserState): Option<List<T>> {
            val initI = ps.i
            val res = body(ps)
            ps.i = initI
            return res
        }

        override fun parse(ps: ParserState): Option<List<T>> {
            val initI = ps.i
            val res = body(ps)
            if (res == None) ps.i = initI
            return res
        }


        private fun body(ps: ParserState): Option<List<T>> {
            val list = mutableListOf<T>()

            while (list.size.toUInt() < range.last) {
                when (val res = pe.parse(ps)) {
                    is Some -> list += res.value
                    None -> break
                }
            }

            return if (list.size.toUInt() in range) Some(list) else None
        }
    }

    internal class Predicate(private val type: PredicateType, private val pe: PE<*>) : NonTerminal<Unit>() {

        override fun peek(ps: ParserState): Option<Unit> = when (type) {
            And -> if (pe.peek(ps) != None) Some(Unit) else None
            Not -> if (pe.peek(ps) != None) None else Some(Unit)
        }

        override fun parse(ps: ParserState): Option<Unit> = peek(ps)


        enum class PredicateType {
            And,
            Not,
        }
    }

    internal class Sequence<T>(private val b: GroupBuilderBlock<T>) : NonTerminal<T>() {

        override fun peek(ps: ParserState): Option<T> = with(GroupBuilder<T>(ps).also(b)) {
            if (subexpressions.all { it.peek(ps) != None }) {
                Some(ValueBuilder.valueBlock())
            } else {
                None
            }
        }

        override fun parse(ps: ParserState): Option<T> = with(GroupBuilder<T>(ps).also(b)) {
            val initI = ps.i
            if (subexpressions.all { it.parse(ps) != None }) {
                Some(ValueBuilder.valueBlock())
            } else {
                ps.i = initI
                None
            }
        }
    }

    internal class PrioritizedChoice<T>(private val b: GroupBuilderBlock<T>) : NonTerminal<T>() {

        override fun peek(ps: ParserState): Option<T> = TODO()

        override fun parse(ps: ParserState): Option<T> = TODO()
    }
}