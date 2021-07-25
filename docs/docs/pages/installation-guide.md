# Installation guide


## Gradle

Use [Maven Central](https://search.maven.org/) repository:

```kotlin
repositories {
    mavenCentral()
}
```

=== "Kotlin DSL"
    ```kotlin
    dependencies {
        implementation("io.kpeg:kpeg:{{ project.version }}")
    }
    ```

=== "Groovy DSL"
    ```groovy
    dependencies {
        implementation 'io.kpeg:kpeg:{{ project.version }}'
    }
    ```


## Maven

```xml
<dependency>
    <groupId>io.kpeg</groupId>
    <artifactId>kpeg</artifactId>
    <version>{{ project.version }}</version>
</dependency>
```
