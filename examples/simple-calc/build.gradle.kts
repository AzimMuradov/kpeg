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

    implementation(project(":lib"))
}

application {
    mainClass.set("kpeg.examples.simple_calc.MainKt")
}