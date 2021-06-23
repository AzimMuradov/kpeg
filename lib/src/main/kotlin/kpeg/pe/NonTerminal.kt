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
import kpeg.PegParser.ParserState
import kpeg.StoredPE
import kpeg.pe.GroupBuilder.ValueBuilder
import kpeg.pe.ParsingExpression as PE


@KPegDsl
public class GroupBuilder<T> : Operators() {

    // Subexpressions

    public operator fun <T> PE<T>.unaryPlus(): StoredPE<T> = StoredPE(pe = also { subexpressions += it })

    internal val subexpressions = mutableListOf<PE<*>>()


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

    internal class Optional<T>(private val pe: PE<T>) : NonTerminal<T>() {

        override fun peek(ps: ParserState): Option<T> = TODO()

        override fun parse(ps: ParserState): Option<T> = TODO()
    }

    internal class Repeated<T>(private val range: UIntRange, private val pe: PE<T>) : NonTerminal<List<T>>() {

        override fun peek(ps: ParserState): Option<List<T>> = TODO()

        override fun parse(ps: ParserState): Option<List<T>> = TODO()
    }

    internal class Predicate(private val type: PredicateType, private val pe: PE<*>) : NonTerminal<Unit>() {

        override fun peek(ps: ParserState): Option<Unit> = TODO()

        override fun parse(ps: ParserState): Option<Unit> = TODO()


        enum class PredicateType {
            And,
            Not,
        }
    }

    internal class Sequence<T>(private val b: GroupBuilderBlock<T>) : NonTerminal<T>() {

        override fun peek(ps: ParserState): Option<T> = TODO()

        override fun parse(ps: ParserState): Option<T> = TODO()
    }

    internal class PrioritizedChoice<T>(private val b: GroupBuilderBlock<T>) : NonTerminal<T>() {

        override fun peek(ps: ParserState): Option<T> = TODO()

        override fun parse(ps: ParserState): Option<T> = TODO()
    }
}