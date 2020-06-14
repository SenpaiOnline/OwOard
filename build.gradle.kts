import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    java
    kotlin("jvm") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("io.gitlab.arturbosch.detekt") version "1.6.0"
    id("org.openjfx.javafxplugin") version "0.0.8"
}

application {
    mainClassName = "online.senpai.owoard.OwoardApp"
}

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/jerady/maven") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    jcenter {
        content {
            includeGroup("org.jetbrains.kotlinx") // detekt needs 'kotlinx-html' for the html report
        }
    }
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
    testImplementation(TestLibraries.testFxJunit5)
    testImplementation(TestLibraries.testFxMonocle)
    testImplementation(TestLibraries.hamcrest)
    testImplementation(TestLibraries.mockk)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "11"
    }
}

javafx {
    version = "11"
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin", "src/main/java")
        resources.srcDirs("src/main/resources")
    }
    test {
        java.srcDirs("src/test/kotlin")
        resources.srcDirs("src/test/resources")
    }
}

detekt {
    config = files("detekt.yml")
    reports {
        html {
            enabled = true
            destination = file("build/reports/detekt.html")
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

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

tasks.withType<ShadowJar> {
    exclude("icon.png") // the unused icon from Tornadofx
}
