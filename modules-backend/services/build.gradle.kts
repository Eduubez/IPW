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

    implementation("org.springframework:spring-context:7.0.0")
    implementation("org.springframework.security:spring-security-crypto:7.0.4")

    implementation("io.jsonwebtoken:jjwt-api:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.7")

    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
repositories {
    mavenCentral()
}