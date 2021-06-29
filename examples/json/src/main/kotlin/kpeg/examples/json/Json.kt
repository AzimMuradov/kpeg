package kpeg.examples.json


data class Json(val value: JsonValue) {
    override fun toString(): String = "$value"
}

sealed class JsonValue {

    data class JsonObject(val value: List<Pair<String, JsonValue>>) : JsonValue() {
        override fun toString(): String = buildString {
            if (value.isEmpty()) {
                append("{}")
            } else {
                append("{")
                append("\"${value.first().first}\": ${value.first().second}")
                for (i in 1 until value.size) {
                    append(", \"${value[i].first}\": ${value[i].second}")
                }
                append("}")
            }
        }
    }

    data class JsonArray(val value: List<JsonValue>) : JsonValue() {
        override fun toString(): String = "$value"
    }

    data class JsonNumber(val value: Double) : JsonValue() {
        override fun toString(): String = "$value"
    }

    data class JsonString(val value: String) : JsonValue() {
        override fun toString(): String = "\"$value\""
    }

    data class JsonBoolean(val value: Boolean) : JsonValue() {
        override fun toString(): String = "$value"
    }

    object JsonNull : JsonValue() {
        override fun toString(): String = "null"
    }
}