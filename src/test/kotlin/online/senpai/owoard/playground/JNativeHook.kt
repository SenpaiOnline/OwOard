/*
 * This file is part of the OwOard distribution (https://github.com/aiscy/OwOard).
 * Copyright (c) 2019 Maxim Valeryevich Pavlov.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package online.senpai.owoard.playground

import mu.KLogger
import mu.KotlinLogging
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.Logger

private val logger: KLogger = KotlinLogging.logger {}

fun main() {
    Logger.getLogger(GlobalScreen::class.java.`package`.name).apply {
        level = Level.OFF
        useParentHandlers = false
    }
    GlobalScreen.registerNativeHook()
    GlobalScreen.addNativeKeyListener(object : NativeKeyListener {
        override fun nativeKeyPressed(e: NativeKeyEvent) {
            logger.info {
                "Key Pressed: ${NativeKeyEvent.getKeyText(e.keyCode)}, Raw code: ${e.rawCode.toString(16)}, Location: ${e.keyLocation}"
            }
        }

        override fun nativeKeyReleased(e: NativeKeyEvent) {
            /*logger.info { "Key Released: " + NativeKeyEvent.getKeyText(e.keyCode) }*/
        }

        override fun nativeKeyTyped(e: NativeKeyEvent) {
            /*logger.info {  "Key Typed: " + NativeKeyEvent.getKeyText(e.keyCode) }*/
        }
    })
    logger.info { "Ready!" }
}
