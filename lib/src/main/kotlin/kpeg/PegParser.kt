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

package kpeg

import kpeg.Option.None
import kpeg.pe.Symbol


public class PegParser(private val grammar: Set<Symbol<*>>) {

    public fun <T> parse(start: Symbol<T>, s: String): Option<T> {
        check(start in grammar, ErrorMessages::WRONG_START)

        // TODO(memoization)

        val newPs = ParserState(s, i = 0).also { ps = it }

        val result = start.parse(newPs)

        return if (newPs.i == s.length) result else None
    }


    private var ps: ParserState? = null

    internal data class ParserState(val s: String, var i: Int)


    internal companion object {

        private object ErrorMessages {
            const val WRONG_START: String = "Start element must be in grammar"
        }
    }
}