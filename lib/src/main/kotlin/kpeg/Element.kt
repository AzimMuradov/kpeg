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


public data class Element<S : Symbol> internal constructor(
    public val begin: UInt,
    public val end: UInt,
    public val symbol: S,
) {

    public companion object {
        public fun <S : Symbol> cmp(): Comparator<Element<S>> = Comparator<Element<S>> { lhs, rhs ->
            lhs.begin.compareTo(rhs.begin)
        }.then { lhs, rhs ->
            lhs.end.compareTo(rhs.end)
        }
    }
}