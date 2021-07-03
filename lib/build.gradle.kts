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

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
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

                // Use to include or exclude non public members
                includeNonPublic.set(false)

                // Do not output deprecated members. Applies globally, can be overridden by packageOptions
                skipDeprecated.set(true)

                // Emit warnings about not documented members. Applies globally, also can be overridden by packageOptions
                reportUndocumented.set(true)

                // Do not create index pages for empty packages
                skipEmptyPackages.set(true)

                // This name will be shown in the final output
                displayName.set("JVM")

                // Platform used for code analysis. See the "Platforms" section of this readme
                platform.set(org.jetbrains.dokka.Platform.jvm)

                // List of files with module and package documentation
                // https://kotlinlang.org/docs/reference/kotlin-doc.html#module-and-package-documentation
                // includes.from("packages.md", "extra.md")

                // List of files or directories containing sample code (referenced with @sample tags)
                // samples.from("samples/basic.kt", "samples/advanced.kt")

                // Specifies the location of the project source code on the Web.
                // If provided, Dokka generates "source" links for each declaration.
                // Repeat for multiple mappings
                sourceLink {
                    // Unix based directory relative path to the root of the project (where you execute gradle respectively).
                    localDirectory.set(file("src/main/kotlin"))

                    // URL showing where the source code can be accessed through the web browser
                    remoteUrl.set(URL("https://github.com/AzimMuradov/kpeg/blob/dev/lib/src/main/kotlin"))
                    // Suffix which is used to append the line number to the URL. Use #L for GitHub
                    remoteLineSuffix.set("#L")
                }

                // Used for linking to JDK documentation
                jdkVersion.set(15)

                // Disable linking to online kotlin-stdlib documentation
                noStdlibLink.set(false)

                // Disable linking to online JDK documentation
                noJdkLink.set(false)
            }
        }

        doLast {
            println("View docs at:")
            println("file://$buildDir/dokka/index.html")
        }
    }
}