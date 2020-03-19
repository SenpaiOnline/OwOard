import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    java
    kotlin("jvm") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("io.gitlab.arturbosch.detekt") version "1.6.0"
}

application {
    mainClassName = "online.senpai.owoard.OwoardApp"
    applicationDefaultJvmArgs = listOf("-DVLCJ_INITX=no") // TODO Does it affect Windows?
}

group = "online.senpai"

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/jerady/maven") }
    maven { url = uri("https://jitpack.io") }
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
    testImplementation(TestLibraries.testFxMonocle)
    testImplementation(TestLibraries.hamcrest)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
kotlin.sourceSets["test"].kotlin.srcDirs("src/test/kotlin")

sourceSets["main"].resources.srcDirs("src/main/resources")
sourceSets["test"].resources.srcDirs("src/test/resources")

detekt {
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

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
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
