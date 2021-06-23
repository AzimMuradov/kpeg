package kpeg

import kpeg.Option.None
import kpeg.Option.Some


public sealed class Option<out T> {

    public data class Some<T>(public val value: T) : Option<T>()

    public object None : Option<Nothing>() {

        override fun toString(): String = "None"
    }
}


internal fun <T> T.takeAsOptionIf(condition: (T) -> Boolean): Option<T> = when (condition(this)) {
    true -> Some(this)
    false -> None
}

public inline fun <T> Option<T>.alsoIfSome(block: (T) -> Unit): Option<T> = when (this) {
    is Some -> Some(value.also(block))
    None -> this
}

public fun <T> Option<T>.unwrap(): T = when (this) {
    is Some -> value
    None -> throw IllegalStateException("option is None")
}