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
import kpeg.Option.Some
import kpeg.PegParser.ParserState
import kpeg.pe.ParsingExpression


public class StoredPE<T> internal constructor(private val pe: ParsingExpression<T>) {

    public var option: Option<T> = None
        private set

    public val value: T get() = option.unwrap()


    internal fun peek(ps: ParserState): Option<T> = pe.peek(ps).alsoIfSome { option = Some(it) }

    internal fun parse(ps: ParserState): Option<T> = pe.parse(ps).alsoIfSome { option = Some(it) }
}