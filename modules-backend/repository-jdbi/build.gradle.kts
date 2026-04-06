plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":repository"))

    implementation("org.jdbi:jdbi3-core:3.49.6")
    implementation("org.jdbi:jdbi3-kotlin:3.49.6")
    implementation("org.jdbi:jdbi3-postgres:3.49.6")
    implementation("org.postgresql:postgresql:42.7.4")

    implementation("org.springframework:spring-context:7.0.0")
    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}