plugins {
    id("ru.vyarus.mkdocs") version V.P_MKDOCS
}


mkdocs {

    // mkdocs sources
    sourcesDir = "."

    // strict build (fail on build errors)
    strict = false

    // target build directory (publication root)
    buildDir = "build/mkdocs"

    // automatically update site_url in mkdocs.yml before mkdocsBuild
    updateSiteUrl = true

    // optional variables declaration (to bypass gradle data to docs)
    extras = emptyMap()


    publish.apply {

        // publication sub-folder (by default project version)
        docPath = ""

        // generate index.html' for root redirection to the last published version
        rootRedirect = true

        // publish repository uri (by default the same as current repository)
        repoUri = null

        // publication branch
        branch = "gh-pages"

        // publication comment
        comment = "Publish $docPath documentation"

        // directory publication repository checkout, update and push
        repoDir = ".gradle/gh-pages"
    }
}

python {
    pip("mkdocs-macros-plugin:${V.MKDOCS_MACROS}")
}


// TODO(this is probably not the best solution to the problem)

// Clean task

val clean by tasks.registering(Delete::class) {
    delete("$buildDir", "$projectDir/.gradle")
}

// Add kdoc to the site build

val copyKDocToRepoDir by tasks.registering(Copy::class) {
    val libProject = project.projects.lib.dependencyProject
    from("${libProject.buildDir}/dokka")
    into("${mkdocs.buildDir}/kdoc")

    dependsOn(":lib:dokkaHtml")
}

tasks.getByPath(":docs:mkdocsBuild").apply {
    dependsOn(clean)
    finalizedBy(copyKDocToRepoDir)
}