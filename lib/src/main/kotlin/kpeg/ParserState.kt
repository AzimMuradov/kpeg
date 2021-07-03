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

import arrow.core.Some
import kpeg.pe.Ignorable.Whitespace
import kpeg.pe.ParsingExpression


internal class ParserState(
    internal val s: String,
    private val ws: Whitespace = Whitespace(WhitespaceChars.NO_WS),
) {

    // Current index in s

    internal var i: Int = 0


    // Whitespace handling

    internal var ignoreWS: Boolean = false

    internal fun handleWS() {
        if (ignoreWS) ws.parse(this)
    }


    // Memoization (for packrat)

    internal val memNone: List<MutableMap<ParsingExpression<*>, ArrayDeque<ParseError>>> =
        List(size = s.length + 1) { mutableMapOf() }

    internal val memSome: List<MutableMap<ParsingExpression<*>, Pair<Int, Some<*>>>> =
        List(size = s.length + 1) { mutableMapOf() }


    // Error stack

    internal var errs = ArrayDeque<ParseError>()

    internal fun addErr(errMsg: String) {
        errs.add(ParseError(i, errMsg))
    }
}