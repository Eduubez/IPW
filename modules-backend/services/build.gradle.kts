plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":repository"))
    implementation(project(":repository-jdbi"))

    implementation("org.springframework:spring-context:7.0.0")
    implementation("org.springframework.security:spring-security-crypto:7.0.4")

    implementation("io.jsonwebtoken:jjwt-api:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.7")

    testImplementation(kotlin("test"))

    implementation("org.jdbi:jdbi3-core:3.49.6")
    implementation("org.jdbi:jdbi3-kotlin:3.49.6")
    implementation("org.jdbi:jdbi3-postgres:3.49.6")
    implementation("org.postgresql:postgresql:42.7.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
repositories {
    mavenCentral()
}