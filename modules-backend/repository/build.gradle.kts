plugins {
    kotlin("jvm") version "2.2.21"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":domain"))

    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}