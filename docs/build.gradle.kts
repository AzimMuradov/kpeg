plugins {
    id("ru.vyarus.mkdocs") version V.P_MKDOCS
}


mkdocs {

    // mkdocs sources
    sourcesDir = "."

    // strict build (fail on build errors)
    strict = false

    // publication sub-folder (by default project version)
    publish.docPath = ""
}

python {
    pip("mkdocs-macros-plugin:${V.MKDOCS_MACROS}")
}


// Add kdoc to the site build

gitPublish.contents {
    val libProject = project.projects.lib.dependencyProject

    from("${libProject.buildDir}/dokka") {
        into("kdoc")
    }
}

tasks.getByPath(":docs:gitPublishCopy").dependsOn(":lib:dokkaHtml")


// Clean task

val clean by tasks.registering(Delete::class) {
    delete("$buildDir")
}