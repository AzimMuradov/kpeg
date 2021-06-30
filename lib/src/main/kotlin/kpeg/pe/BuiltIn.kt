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

@file:Suppress("ClassName")

package kpeg.pe

import kpeg.Option
import kpeg.PegParser.ParserState
import kpeg.pe.Terminal.Character


internal sealed class BuiltIn(b: CharacterBuilderBlock) : Memoized<Char>() {

    private val pe = Character(b)

    final override fun parseBody(ps: ParserState): Option<Char> = pe.parse(ps)


    internal object ANY : BuiltIn({ true })

    internal object DIGIT : BuiltIn({ it.isDigit() })

    internal object LETTER : BuiltIn({ it.isLetter() })

    internal object HEX_DIGIT : BuiltIn({ it.isDigit() || it in 'a'..'f' || it in 'A'..'F' })
}