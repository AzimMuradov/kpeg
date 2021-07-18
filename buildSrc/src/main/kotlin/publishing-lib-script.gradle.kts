plugins {
    `maven-publish`
    signing
}


// Find credentials

val localProps = getPropsFromFile(project.rootProject.file("local.properties"))

val signingKeyId: String = localProps.findPropOrEnvVar(propName = "signing.keyId") ?: ""
val signingKey: String = localProps.findPropOrEnvVar(propName = "signing.key") ?: ""
val signingPassword: String = localProps.findPropOrEnvVar(propName = "signing.password") ?: ""


// Set up publications

publishing {
    with(Pom) {
        publications {
            create<MavenPublication>("mavenJvm") {
                groupId = GROUP_ID
                artifactId = ARTIFACT_ID
                version = VERSION

                afterEvaluate {
                    artifact(sourcesJar)
                    artifact(javadocJar)
                }

                from(components["kotlin"])

                pom {
                    name.set(NAME)
                    description.set(DESCRIPTION)
                    url.set(URL)
                    inceptionYear.set(INCEPTION_YEAR)

                    licenses {
                        license {
                            name.set(LICENSE_NAME)
                            url.set(LICENSE_URL)
                        }
                    }

                    developers {
                        for (dev in developers) {
                            developer {
                                id.set(dev.id)
                                name.set(dev.name)
                                email.set(dev.email)
                            }
                        }
                    }

                    scm {
                        connection.set(SCM_CONNECTION)
                        developerConnection.set(SCM_DEVELOPER_CONNECTION)
                        url.set(SCM_URL)
                        tag.set(SCM_TAG)
                    }

                    issueManagement {
                        url.set(ISSUES_URL)
                    }
                }
            }
        }

        repositories {
            maven {
                // change URLs to point to your repos, e.g. http://my.org/repo
                val releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases"))
                val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots"))
                url = if ("$version".endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            }
        }
    }
}


// Set up signing

signing {
    useInMemoryPgpKeys(
        signingKeyId,
        signingKey,
        signingPassword,
    )
    sign(publishing.publications)
}


// Artifact tasks

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(file("src/main/kotlin"))
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(file("build/dokka"))

    dependsOn("dokkaHtml")
}