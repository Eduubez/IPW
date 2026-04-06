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
    // Module dependencies
    api(project(":domain"))
    implementation(project(":services"))

    // Spring MVC
    implementation("org.springframework:spring-webmvc:7.0.0")

    // Spring Security
    implementation("org.springframework.security:spring-security-config:7.0.0")
    implementation("org.springframework.security:spring-security-web:7.0.0")

    // Servlet API (provided pelo container)
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    // Annotations
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    // Kotlin + JSON
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.0")

    // Tests
    testImplementation(kotlin("test"))
    testImplementation("org.springframework:spring-test:7.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}