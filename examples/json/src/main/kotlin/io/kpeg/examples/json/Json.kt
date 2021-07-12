package io.kpeg.examples.json


data class Json(val value: JsonValue) {

    override fun toString(): String = "$value"

    fun toPrettyString(): String = value.toPrettyString()
}

sealed class JsonValue {

    data class JsonObject(val value: List<Pair<String, JsonValue>>) : JsonValue() {

        override fun toString(): String =
            if (value.isNotEmpty()) {
                value.joinToString(separator = ", ", prefix = "{ ", postfix = " }") { (k, v) -> "\"$k\": $v" }
            } else {
                "{}"
            }

        override fun toPrettyString(currIndent: Int): String =
            if (value.isNotEmpty()) {
                value.joinToString(
                    separator = ",\n",
                    prefix = "{\n", postfix = "\n${" ".repeat(currIndent)}}"
                ) { (k, v) ->
                    """${" ".repeat(currIndent + 4)}"$k": ${v.toPrettyString(currIndent + 4)}"""
                }
            } else {
                "{}"
            }
    }

    data class JsonArray(val value: List<JsonValue>) : JsonValue() {

        override fun toString(): String = "$value"

        override fun toPrettyString(currIndent: Int): String =
            if (value.isNotEmpty()) {
                value.joinToString(
                    separator = ",\n",
                    prefix = "[\n", postfix = "\n${" ".repeat(currIndent)}]"
                ) {
                    """${" ".repeat(currIndent + 4)}${it.toPrettyString(currIndent + 4)}"""
                }
            } else {
                "[]"
            }
    }

    data class JsonNumber(val value: Double) : JsonValue() {

        override fun toString(): String = "$value"

        override fun toPrettyString(currIndent: Int): String = toString()
    }

    data class JsonString(val value: String) : JsonValue() {

        override fun toString(): String = "\"$value\""

        override fun toPrettyString(currIndent: Int): String = toString()
    }

    data class JsonBoolean(val value: Boolean) : JsonValue() {

        override fun toString(): String = "$value"

        override fun toPrettyString(currIndent: Int): String = toString()
    }

    object JsonNull : JsonValue() {

        override fun toString(): String = "null"

        override fun toPrettyString(currIndent: Int): String = toString()
    }


    fun toPrettyString(): String = toPrettyString(currIndent = 0)

    protected abstract fun toPrettyString(currIndent: Int): String
}