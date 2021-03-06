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

import arrow.core.Option
import arrow.core.some
import io.kpeg.ParserState


internal sealed class Ignorable : ParsingExpression<Unit>(packrat = true) {

    internal class Whitespace(private val wsChars: List<Char>) : Ignorable() {

        override fun parseCore(ps: ParserState): Option<Unit> {
            while (ps.i < ps.s.length && ps.s[ps.i] in wsChars) {
                ps.i += 1
            }
            return Unit.some()
        }
    }
}