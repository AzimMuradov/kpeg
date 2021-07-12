import org.jetbrains.dokka.Platform
import java.net.URL


plugins {
    kotlin("jvm")

    `java-library`

    jacoco

    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))

    implementation(kotlin("stdlib-jdk8"))

    api("io.arrow-kt:arrow-core:0.13.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:4.6.0")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.0.2")
    testImplementation("com.marcinmoskala:DiscreteMathToolkit:1.0.3")
}


// General configuration

kotlin {
    explicitApi()
}


// Test configuration

tasks {
    jacocoTestReport {
        dependsOn(test)

        reports {
            xml.required.set(false)
            csv.required.set(false)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
        doLast {
            println("View code coverage at:")
            println("file://$buildDir/jacocoHtml/index.html")
        }
    }
}

jacoco {
    toolVersion = "0.8.7"
}


// Docs configuration

tasks {
    dokkaHtml {
        outputDirectory.set(buildDir.resolve("dokka"))

        // Set module name displayed in the final output
        moduleName.set("kpeg library")

        dokkaSourceSets {
            configureEach {

                // Do not output deprecated members. Applies globally, can be overridden by packageOptions
                skipDeprecated.set(true)

                // Emit warnings about not documented members. Applies globally, also can be overridden by packageOptions
                reportUndocumented.set(true)

                // This name will be shown in the final output
                displayName.set("JVM")

                // Platform used for code analysis. See the "Platforms" section of this readme
                platform.set(Platform.jvm)

                // List of files with module and package documentation
                // https://kotlinlang.org/docs/reference/kotlin-doc.html#module-and-package-documentation
                // includes.from("packages.md", "extra.md")

                // List of files or directories containing sample code (referenced with @sample tags)
                samples.from(
                    file("/$projectDir/src/test/kotlin/io/kpeg/samples/").listFiles()!!.map { it.canonicalPath }
                )

                // Specifies the location of the project source code on the Web.
                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl.set(URL("https://github.com/AzimMuradov/kpeg/blob/master/lib/src/main/kotlin"))
                    remoteLineSuffix.set("#L")
                }

                // Λrrow Core library
                externalDocumentationLink {
                    url.set(URL("https://arrow-kt.io/docs/apidocs/arrow-core/"))
                    packageListUrl.set(URL("file://$rootDir/docs/resources/arrow-core-package-list"))
                }

                // Used for linking to JDK documentation
                jdkVersion.set(15)
            }
        }

        doLast {
            println("View docs at:")
            println("file://$buildDir/dokka/index.html")
        }
    }
}