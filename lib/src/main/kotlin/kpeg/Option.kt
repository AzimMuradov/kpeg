package kpeg

import kpeg.Option.None
import kpeg.Option.Some


// Soon would be replaced with the Option class of the Λrrow library
// See: https://arrow-kt.io/

/**
 * Option - it either [Some(value)][Some] or [None].
 */
public sealed class Option<out T> {

    /**
     * Some represents [Option] that holds [value] of type [T].
     */
    public data class Some<T>(public val value: T) : Option<T>()

    /**
     * None represents [Option] that holds nothing.
     */
    public object None : Option<Nothing>() {

        override fun toString(): String = "None"
    }
}


internal fun <T> Option<T>.unwrapOrNull(): T? = when (this) {
    is Some -> value
    None -> null
}

internal fun <T> Option<T>.unwrap(): T = when (this) {
    is Some -> value
    None -> throw IllegalStateException("Option is None")
}

internal fun <T> T.takeAsOptionIf(condition: (T) -> Boolean): Option<T> = when (condition(this)) {
    true -> Some(this)
    false -> None
}

internal inline fun <T> Option<T>.alsoIfSome(block: (T) -> Unit): Option<T> = when (this) {
    is Some -> Some(value.also(block))
    None -> this
}