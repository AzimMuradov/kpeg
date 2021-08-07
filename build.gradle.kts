plugins {
    kotlin("jvm") version V.P_KOTLIN apply false

    id("org.jetbrains.dokka") version V.P_DOKKA apply false

    id("io.github.gradle-nexus.publish-plugin") version V.P_NEXUS_PUBLISH
}


// Nexus publishing configuration

// Find credentials

val localPropsFile = rootDir.listFiles()!!.firstOrNull { it.name == "local.properties" }

val localProps = getPropsFromFile(localPropsFile)

val ossrhUsername: String = localProps.findPropOrEnvVar(propName = "ossrhUsername") ?: ""
val ossrhPassword: String = localProps.findPropOrEnvVar(propName = "ossrhPassword") ?: ""
val sonatypeStagingProfileId: String = localProps.findPropOrEnvVar(propName = "sonatypeStagingProfileId") ?: ""

// Set up Sonatype repository

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

            stagingProfileId.set(sonatypeStagingProfileId)
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        }
    }

    useStaging.set(System.getenv("PUBLISH_SNAPSHOT") == null)
}