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
import kpeg.Option.None
import kpeg.Option.Some
import kpeg.PegParser.ParserState


internal sealed class Special<T> : Memoized<T>() {

    internal class Whitespace(private val wsChars: List<Char>) : Special<List<Char>>() {

        override fun parseBody(ps: ParserState): Option<List<Char>> {
            val list = mutableListOf<Char>()
            while (true) {
                if (ps.i < ps.s.length && ps.s[ps.i] in wsChars) {
                    list += ps.s[ps.i]
                    ps.i += 1
                } else {
                    break
                }
            }
            return if (list.isNotEmpty()) Some(list) else None
        }
    }

    // TODO(class Comment : Special<String>())
}