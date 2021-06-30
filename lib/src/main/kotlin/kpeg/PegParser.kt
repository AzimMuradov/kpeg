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

import kpeg.WhitespaceChars.DEFAULT_WS
import kpeg.WhitespaceChars.NO_WS
import kpeg.pe.ParsingExpression
import kpeg.pe.Ignorable.Whitespace
import kpeg.pe.Symbol


/**
 * The starting point to run [peg parser][parse].
 */
public class PegParser {

    /**
     * Parse [text] with defined [whitespace] to get [symbol] using packrat parser.
     *
     * It returns [Some(value)][Option.Some] on success and [None][Option.None] otherwise.
     */
    public fun <T> parse(symbol: Symbol<T>, text: String, whitespace: List<Char> = DEFAULT_WS): Option<T> {
        val ps = ParserState(text, Whitespace(whitespace))

        val result = symbol.parse(ps)

        return if (ps.i == text.length) result else Option.None
    }

    internal class ParserState(internal val s: String, private val ws: Whitespace = Whitespace(NO_WS)) {

        // Current index in s

        internal var i: Int = 0


        // Whitespace handling

        internal var ignoreWS: Boolean = false

        internal fun handleWS() {
            if (ignoreWS) ws.parse(this)
        }


        // Memoization (for packrat)

        internal val mem: List<MutableMap<ParsingExpression<*>, Pair<Int, Option<*>>>> =
            List(size = s.length + 1) { mutableMapOf() }
    }
}


/**
 * Built-in whitespace variants.
 */
public object WhitespaceChars {

    /**
     * Use no whitespace characters.
     */
    public val NO_WS: List<Char> = listOf()

    /**
     * Use default whitespace characters (`' '`, `'\t'`, `'\r'`, `'\n'`).
     */
    public val DEFAULT_WS: List<Char> = listOf(' ', '\t', '\r', '\n')
}