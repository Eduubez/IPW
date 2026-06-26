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
    implementation(project(":repository-jdbi"))


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
    implementation("tools.jackson.core:jackson-databind:3.1.0")
    implementation("io.jsonwebtoken:jjwt-api:0.12.7")
    implementation("tools.jackson.module:jackson-module-kotlin:3.1.0")

    // for JDBI and Postgres
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.jdbi:jdbi3-postgres:3.49.6")
    implementation("org.postgresql:postgresql:42.7.4")

    // Tests
    testImplementation(kotlin("test"))
    testImplementation("org.springframework:spring-test:7.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test:4.0.5")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}