object Pom {

    const val GROUP_ID: String = "io.kpeg"
    const val ARTIFACT_ID: String = "kpeg"
    const val VERSION: String = "0.1.0"

    const val NAME: String = "kpeg"
    const val DESCRIPTION: String = "Kotlin PEG parser with Kotlin DSL"
    const val URL: String = "https://github.com/AzimMuradov/kpeg" /* "https://kpeg.io" */
    const val INCEPTION_YEAR: String = "2021"

    const val LICENSE_NAME: String = "The Apache License, Version 2.0"
    const val LICENSE_URL: String = "http://www.apache.org/licenses/LICENSE-2.0.txt"


    data class Developer(val id: String, val name: String, val email: String)

    // Developers, developers, developers, developers!
    val developers = listOf(
        Developer(
            id = "AzimMuradov",
            name = "Azim Muradov",
            email = "azim.muradov.dev@gmail.com",
        )
    )

    const val SCM_CONNECTION: String = "scm:git:https://github.com/AzimMuradov/kpeg.git"
    const val SCM_DEVELOPER_CONNECTION: String = "scm:git:https://github.com/AzimMuradov/kpeg.git"
    const val SCM_URL: String = "https://github.com/AzimMuradov/kpeg"
    const val SCM_TAG: String = "v$VERSION"

    const val ISSUES_URL: String = "https://github.com/AzimMuradov/kpeg/issues"
}