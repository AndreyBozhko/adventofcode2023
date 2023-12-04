plugins {
    kotlin("jvm") version "1.9.20"
}

group = "me.abozhko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}
