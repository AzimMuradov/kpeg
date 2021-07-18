import java.io.File
import java.util.*


fun getPropsFromFile(file: File) = Properties().apply { load(file.inputStream()) }

fun Properties.findPropOrEnvVar(
    propName: String,
    envVarName: String = propName.propNameToEnvVarName(),
): String? =
    get(propName) as String? ?: System.getenv(envVarName)


private fun String.propNameToEnvVarName() =
    splitAndRejoin(splitBy = ".", rejoinBy = "_") { it.camelToScreamingSnakeCase() }


// Variable name conversions

private fun String.splitAndRejoin(
    splitBy: String,
    rejoinBy: CharSequence,
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((String) -> CharSequence)? = null,
): String =
    split(splitBy).joinToString(rejoinBy, prefix, postfix, limit, truncated, transform)


private val camelRegex = """(?<=[a-zA-Z])[A-Z]""".toRegex()

private fun String.camelToScreamingSnakeCase(): String = camelRegex.replace(this) { "_${it.value}" }.toUpperCase()