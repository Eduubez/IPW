plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    kotlin("jvm") version "2.2.21" apply false
    kotlin("plugin.spring") version "2.2.21" apply false
    id("org.springframework.boot") version "4.0.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

rootProject.name = "ipw"

// The host/application module
include("app")
project(":app").projectDir = file("modules-backend/app")

// The domain module
include("domain")
project(":domain").projectDir = file("modules-backend/domain")

// The HTTP module
include("http")
project(":http").projectDir = file("modules-backend/http")

// The services module
include("services")
project(":services").projectDir = file("modules-backend/services")

// The repository interfaces module
include("repository")
project(":repository").projectDir = file("modules-backend/repository")

// The JDBI repository implementation module
include("repository-jdbi")
project(":repository-jdbi").projectDir = file("modules-backend/repository-jdbi")