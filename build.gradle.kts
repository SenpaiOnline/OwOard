import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jnativehookVersion: String by project
val tornadofxVersion: String by project
val tilesfxVersion: String by project
val vlcjVersion: String by project
val fontawesomefxCommonVersion: String by project
val fontawesomefxicons525Version: String by project
val junitJupiterVersion: String by project

plugins {
    application
    java
    kotlin("jvm") version "1.3.50"
}

application {
    mainClassName = "online.senpai.owoard.OwoardApp"
}

group = "online.senpai"
version = "0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/jerady/maven") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.1stleg:jnativehook:$jnativehookVersion")
    implementation("uk.co.caprica:vlcj:$vlcjVersion")
    implementation("no.tornado:tornadofx:$tornadofxVersion")
    implementation("eu.hansolo:tilesfx:$tilesfxVersion")
    implementation("de.jensd:fontawesomefx-commons:$fontawesomefxCommonVersion")
    implementation("de.jensd:fontawesomefx-icons525:$fontawesomefxicons525Version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
kotlin.sourceSets["test"].kotlin.srcDirs("src/test/kotlin")

sourceSets["main"].resources.srcDirs("src/main/resources")
sourceSets["test"].resources.srcDirs("src/test/resources")
