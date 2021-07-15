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

import arrow.core.Eval
import arrow.core.Eval.Later
import arrow.core.Eval.Now
import arrow.core.None
import arrow.core.Option
import io.kpeg.ParseErrorMessages.wrong
import io.kpeg.ParserState
import io.kpeg.pe.Symbol.Rule


public typealias EvalSymbol<T> = Eval<Symbol<T>>


public class Symbol<T> internal constructor(
    name: String,
    private val pe: EvalPE<T>,
    private val ignoreWS: Boolean,
) : ParsingExpression<T>(packrat = true) {

    override val logName: String = "Symbol($name)"

    override fun parseCore(ps: ParserState): Option<T> {
        val ignoreWSinParent = ps.ignoreWS
        ps.ignoreWS = ignoreWS

        ps.handleWS()
        val result = pe.value().parse(ps).also {
            if (it == None) {
                ps.addErr(wrong(logName))
            }
        }

        ps.ignoreWS = ignoreWSinParent

        return result
    }


    public companion object Rule : Operators() {

        public fun <T> rule(
            name: String,
            ignoreWS: Boolean = true,
            b: RuleBlock<T>,
        ): EvalSymbol<T> = Now(Symbol(name, pe = Rule.b(), ignoreWS))

        public fun <T> lazyRule(
            name: String,
            ignoreWS: Boolean = true,
            b: RuleBlock<T>,
        ): EvalSymbol<T> = Later { Symbol(name, pe = Rule.b(), ignoreWS) }
    }
}

internal typealias RuleBlock<T> = Rule.() -> EvalPE<T>