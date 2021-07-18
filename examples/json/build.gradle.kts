plugins {
    kotlin("jvm")

    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))

    implementation(kotlin("stdlib-jdk8"))

    implementation(projects.lib)
}

application {
    mainClass.set("io.kpeg.examples.json.MainKt")
}