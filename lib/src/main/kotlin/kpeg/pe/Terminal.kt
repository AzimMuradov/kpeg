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

import arrow.core.None
import arrow.core.Option
import arrow.core.some
import kpeg.KPegDsl
import kpeg.ParseErrorMessages.notEnoughTextFor
import kpeg.ParseErrorMessages.wrong
import kpeg.ParserState


/**
 * Object marked by [DslMarker] to have scope to define "character" [parsing expression][ParsingExpression].
 */
@KPegDsl
public object CharacterBuilder

/**
 * Object marked by [DslMarker] to have scope to define "literal" [parsing expression][ParsingExpression].
 */
@KPegDsl
public object LiteralBuilder

internal typealias CharacterBuilderBlock = CharacterBuilder.(Char) -> Boolean
internal typealias LiteralBuilderBlock = LiteralBuilder.(String) -> Boolean


internal sealed class Terminal<T>(packrat: Boolean = false, private val moveBy: Int) : ParsingExpression<T>(packrat) {

    final override fun parseCore(ps: ParserState) = with(ps) {
        if (i + moveBy <= s.length) {
            peek().also {
                if (it != None) {
                    i += moveBy
                    handleWS()
                }
            }
        } else {
            None.also { addErr(notEnoughTextFor(logName)) }
        }
    }

    protected abstract fun ParserState.peek(): Option<T>


    internal class Character(
        packrat: Boolean = false,
        private val block: CharacterBuilderBlock,
    ) : Terminal<Char>(packrat, moveBy = 1) {

        override fun ParserState.peek() = s[i]
            .takeIf { CharacterBuilder.block(it) }?.some()
            ?: None.also { addErr(wrong(logName)) }
    }

    internal class Literal(
        private val len: Int,
        private val block: LiteralBuilderBlock,
    ) : Terminal<String>(moveBy = len) {

        override fun ParserState.peek() = s.substring(i until i + len)
            .takeIf { LiteralBuilder.block(it) }?.some()
            ?: None.also { addErr(wrong(logName)) }
    }
}