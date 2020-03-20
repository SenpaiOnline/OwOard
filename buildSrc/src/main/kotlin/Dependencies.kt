const val kotlinVersion = "1.3.70"

object Libraries {
    private object Versions {
        const val jnativehook = "2.0.2"
        const val vlcj = "4.4.0"
        const val kotlinLogging = "1.7.8"
        const val logbackClassic = "1.2.3"
        const val kfinStateMachine = "4.3.50"
    }

    const val jnativehook = "com.1stleg:jnativehook:${Versions.jnativehook}"
    const val vlcj = "uk.co.caprica:vlcj:${Versions.vlcj}"
    const val kotlinLogging = "io.github.microutils:kotlin-logging:${Versions.kotlinLogging}"
    const val logbackClassic = "ch.qos.logback:logback-classic:${Versions.logbackClassic}"
    const val kfinStateMachine = "com.ToxicBakery.kfinstatemachine:kfin-jvm:${Versions.kfinStateMachine}"

    object Fx {
        private object Versions {
            const val tornadofx = "1.7.20"
            const val tilesfx = "1.6.8"
            const val fontawesomefxCommon = "8.15"
            const val fontawesomefxIcons525 = "3.0.0-4"
        }

        const val tornadofx = "no.tornado:tornadofx:${Versions.tornadofx}"
        const val tilesfx = "eu.hansolo:tilesfx:${Versions.tilesfx}"
        const val fontawesomefxCommon = "de.jensd:fontawesomefx-commons:${Versions.fontawesomefxCommon}"
        const val fontawesomefxIcons525 = "de.jensd:fontawesomefx-icons525:${Versions.fontawesomefxIcons525}"
    }
}

object TestLibraries {
    private object Versions {
        const val junitJupiter = "5.6.0"
        const val testfx = "4.0.16-alpha"
        const val hamcrest = "2.2"
        const val openjfxMonocle = "8u76-b04"
    }

    const val junitJupiter = "org.junit.jupiter:junit-jupiter:${Versions.junitJupiter}"
    const val testFxCore = "org.testfx:testfx-core:${Versions.testfx}"
    const val testFxJunit5 = "org.testfx:testfx-junit5:${Versions.testfx}"
    const val testFxMonocle = "org.testfx:openjfx-monocle:${Versions.openjfxMonocle}"
    const val hamcrest = "org.hamcrest:hamcrest:${Versions.hamcrest}"
}
