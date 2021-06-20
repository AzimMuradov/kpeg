plugins {
    kotlin("jvm") version "1.5.10"

    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))

    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

kotlin {
    explicitApi()
}
