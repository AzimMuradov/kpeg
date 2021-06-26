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
import kpeg.PegParser.ParserState
import kpeg.alsoIfSome
import kpeg.takeAsOptionIf


@KPegDsl
public object CharacterBuilder

@KPegDsl
public object LiteralBuilder

public typealias CharacterBuilderBlock = CharacterBuilder.(Char) -> Boolean
public typealias LiteralBuilderBlock = LiteralBuilder.(String) -> Boolean


internal sealed class Terminal<T>(protected val moveBy: Int) : ParsingExpression<T>() {

    final override fun parse(ps: ParserState): Option<T> = peek(ps).alsoIfSome { ps.i += moveBy }


    internal class Character(val b: CharacterBuilderBlock) : Terminal<Char>(moveBy = 1) {

        override fun peek(ps: ParserState): Option<Char> = with(ps) {
            if (i + moveBy <= s.length) {
                s[i].takeAsOptionIf { CharacterBuilder.b(it) }
            } else {
                None
            }
        }
    }

    internal class Literal(private val len: Int, val b: LiteralBuilderBlock) : Terminal<String>(moveBy = len) {

        override fun peek(ps: ParserState): Option<String> = with(ps) {
            if (i + moveBy <= s.length) {
                s.substring(i until i + len).takeAsOptionIf { LiteralBuilder.b(it) }
            } else {
                None
            }
        }
    }
}