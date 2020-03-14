import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jnativehookVersion: String by project
val tornadofxVersion: String by project
val tilesfxVersion: String by project
val vlcjVersion: String by project
val fontawesomefxCommonVersion: String by project
val fontawesomefxicons525Version: String by project
val junitJupiterVersion: String by project
val testfxCoreVersion: String by project
val hamcrestVersion: String by project
val openjfxMonocleVersion: String by project
val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val jnaVersion: String by project

plugins {
    application
    java
    kotlin("jvm") version "1.3.70"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.github.ben-manes.versions") version "0.28.0"
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
    implementation(Libraries.Fx.tornadofx)
    implementation(Libraries.Fx.tilesfx) { exclude(group = "junit") }
    implementation(Libraries.Fx.fontawesomefxCommon)
    implementation(Libraries.Fx.fontawesomefxIcons525)

    implementation(Libraries.jnativehook)
    implementation(Libraries.vlcj)
    implementation(Libraries.kfinStateMachine)

    implementation(Libraries.kotlinLogging)
    implementation(Libraries.logbackClassic)

    testImplementation(TestLibraries.junitJupiter)
    testImplementation(TestLibraries.testFxCore)
    testImplementation(TestLibraries.testFxMonocle)
    testImplementation(TestLibraries.hamcrest)
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

tasks.register<JavaExec>("fontTest") {
    group = "playground"
    main = "online.senpai.owoard.playground.FontTest"
    classpath = sourceSets.test.get().runtimeClasspath
}

tasks.register<JavaExec>("jNativeHook") {
    group = "playground"
    main = "online.senpai.owoard.playground.JNativeHookKt"
    classpath = sourceSets.test.get().runtimeClasspath
}
