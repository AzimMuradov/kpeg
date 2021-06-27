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


internal class MemoizedPE<T>(private val pe: ParsingExpression<T>) {

    internal fun parseMemoized(ps: ParserState): Option<T> =
        if (pe in ps.mem[ps.i]) {
            val (nextI, parsedValue) = ps.mem[ps.i].getValue(pe)

            ps.i = nextI

            @Suppress("UNCHECKED_CAST")
            parsedValue as Option<T>
        } else {
            val initI = ps.i
            pe.parse(ps).also { ps.mem[initI][pe] = ps.i to it }
        }
}