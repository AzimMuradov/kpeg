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

import arrow.core.*


public typealias ParseResult<T> = Either<List<ParseError>, T>

public data class ParseError(val index: Int, val message: String)


internal object ParseErrorMessages {

    internal fun notEnoughTextFor(peLogName: String) = "Can't parse $peLogName - text is too short"

    internal const val TEXT_IS_TOO_LONG = "Text is too long"

    internal const val RANGE_IS_EMPTY = "Range is empty"

    internal fun wrong(peLogName: String) = "Wrong $peLogName"
}


// Option utils

internal fun <T> Option<T>.get() = getOrElse { error("Option is empty") }

internal inline fun <T> Option<T>.alsoIfSome(block: (T) -> Unit) = when (this) {
    None -> this
    is Some -> Some(value.also(block))
}