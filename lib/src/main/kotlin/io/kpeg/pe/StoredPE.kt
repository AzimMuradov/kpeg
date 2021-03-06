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

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import io.kpeg.ParserState


public class StoredPE<T> internal constructor(private val pe: EvalPE<T>) {

    internal val peLogName: String by lazy { pe.value().logName }


    internal val parsedPeValue: T by lazy { parsedPe.getOrElse { error("Option is empty") } }

    private var parsedPe: Option<T> = None


    internal fun parse(ps: ParserState) = pe.value().parse(ps).also { parsedPe = it }
}