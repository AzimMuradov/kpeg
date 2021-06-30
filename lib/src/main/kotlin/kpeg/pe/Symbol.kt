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

import kpeg.Option
import kpeg.PegParser.ParserState
import kpeg.pe.Symbol.Rule
import kpeg.pe.ParsingExpression as PE


public class Symbol<T> internal constructor(pe: PE<T>, private val ignoreWS: Boolean) : PE<T>() {

    private val memoizedPE = MemoizedPE(pe)

    override fun parse(ps: ParserState): Option<T> {
        val ignoreWSinParent = ps.ignoreWS
        ps.ignoreWS = ignoreWS

        ps.handleWS()
        val result = memoizedPE.parseMemoized(ps)

        ps.ignoreWS = ignoreWSinParent

        return result
    }


    public companion object Rule : Operators() {

        public fun <T> rule(ignoreWS: Boolean = true, b: RuleBlock<T>): Symbol<T> =
            Symbol(pe = Rule.b(), ignoreWS)
    }
}

internal typealias RuleBlock<T> = Rule.() -> PE<T>