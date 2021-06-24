plugins {
    kotlin("jvm") version "1.5.20"

    `java-library`

    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))

    implementation(kotlin("stdlib-jdk8"))

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