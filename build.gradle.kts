plugins {
    kotlin("jvm") version "2.0.20"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))


    testImplementation(kotlin("test"))
    implementation("at.favre.lib:bcrypt:0.9.0")
    implementation("org.jline:jline:3.29.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0") // o versión compatible
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(20)
}