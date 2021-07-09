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

import arrow.core.Eval
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kpeg.ParserState


public typealias EvalPE<T> = Eval<ParsingExpression<T>>

public sealed class ParsingExpression<out T>(private val packrat: Boolean) {

    internal open val logName: String by lazy { "${this::class.simpleName}" }


    internal fun parse(ps: ParserState): Option<T> =
        if (packrat) {
            when (this) {
                in ps.memNone[ps.i] -> {
                    val memErrs = ps.memNone[ps.i].getValue(this)

                    None.also { ps.errs = memErrs }
                }
                in ps.memSome[ps.i] -> {
                    val (nextI, result) = ps.memSome[ps.i].getValue(this)

                    @Suppress("UNCHECKED_CAST")
                    (result as Some<T>).also { ps.i = nextI }
                }
                else -> {
                    val initI = ps.i

                    parseCore(ps).also {
                        when (it) {
                            None -> ps.memNone[initI][this] = ArrayDeque(ps.errs)
                            is Some -> ps.memSome[initI][this] = ps.i to it
                        }
                    }
                }
            }
        } else {
            parseCore(ps)
        }

    internal abstract fun parseCore(ps: ParserState): Option<T>
}