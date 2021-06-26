plugins {
    kotlin("jvm") version "1.5.10"

    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))

    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":lib"))
}

application {
    mainClass.set("kpeg.examples.MainKt")
}
