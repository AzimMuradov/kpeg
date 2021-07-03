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

import arrow.core.None
import arrow.core.Option
import kpeg.ParseErrorMessages.TEXT_IS_TOO_SHORT
import kpeg.PegParser.parse
import kpeg.WhitespaceChars.DEFAULT_WS
import kpeg.pe.Ignorable.Whitespace
import kpeg.pe.Symbol


/**
 * The starting point to run [peg parser][parse].
 */
public object PegParser {

    /**
     * Parse [text] with defined [whitespace] to get [symbol] using packrat parser.
     *
     * It returns [Some(value)][Option.Some] on success and [None][Option.None] otherwise.
     */
    public fun <T> parse(
        symbol: Symbol<T>,
        text: String,
        whitespace: List<Char> = DEFAULT_WS,
    ): ParseResult<T> {

        val ps = ParserState(text, Whitespace(whitespace))

        val parsedSymbol = symbol
            .parse(ps)
            .takeIf { ps.i == text.length }
            ?: None.also { ps.addErr(TEXT_IS_TOO_SHORT) }

        return parsedSymbol.toEither(ps::errs)
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