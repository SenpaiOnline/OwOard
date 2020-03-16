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

package online.senpai.owoard.controller

import com.toxicbakery.kfinstatemachine.StateMachine
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.AbstractTransitionalCallback
import online.senpai.owoard.KeyCombination
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.NativeInputEvent
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import tornadofx.*
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
import javafx.scene.input.KeyCombination as FxKeyCombination
import online.senpai.owoard.controller.NativeHookEvent as Event
import online.senpai.owoard.controller.NativeHookState as State

private val logger: KLogger = KotlinLogging.logger {}

class NativeHookController : Controller() {
    private val keyEventDispatcher: KeyEventDispatcher by inject()
    private val stateMachine: NativeHookStateMachine = NativeHookStateMachine()
    private val nativeHookEventHandler: NativeHookEventHandler = NativeHookEventHandler()
    val keyEventMustContainModifierProperty = SimpleBooleanProperty(true)
    var keyEventMustContainModifier: Boolean by keyEventMustContainModifierProperty

    init {
        Logger.getLogger(GlobalScreen::class.java.`package`.name).apply {
            level = Level.OFF
            useParentHandlers = false
        }

        stateMachine.registerCallback(object : AbstractTransitionalCallback<State, Event>(logger) {
            override fun enteredState(
                    stateMachine: StateMachine<State, Event>,
                    previousState: State,
                    transition: Event,
                    currentState: State
            ) {
                when (transition) {
                    is Event.Initialize -> onInitialize()
                    is Event.Terminate -> onTerminate()
                }
            }
        })
    }

    private fun onInitialize() {
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(nativeHookEventHandler)
        GlobalScreen.setEventDispatcher(EventDispatcher())
    }

    private fun onTerminate() {
        GlobalScreen.removeNativeKeyListener(nativeHookEventHandler)
        GlobalScreen.unregisterNativeHook()
    }

    @Throws(NativeHookException::class)
    fun initialize() {
        stateMachine.transition(Event.Initialize)
    }

    fun terminate() {
        stateMachine.transition(Event.Terminate)
    }

    private inner class NativeHookEventHandler : NativeKeyListener {
        override fun nativeKeyTyped(nativeKeyEvent: NativeKeyEvent) {
        }

        override fun nativeKeyReleased(nativeKeyEvent: NativeKeyEvent) {
        }

        override fun nativeKeyPressed(nativeKeyEvent: NativeKeyEvent) {
            if (keyEventMustContainModifier) {
                if (nativeKeyEvent.modifiers == 0) return
            }
            when (nativeKeyEvent.keyCode) {
                NativeKeyEvent.VC_CONTROL_L,
                NativeKeyEvent.VC_CONTROL_R,
                NativeKeyEvent.VC_SHIFT_L,
                NativeKeyEvent.VC_SHIFT_R,
                NativeKeyEvent.VC_ALT_L,
                NativeKeyEvent.VC_ALT_R,
                NativeKeyEvent.VC_META_L,
                NativeKeyEvent.VC_META_R
                -> return // Ignore the modifiers as an independent key
            }
            val keyCombination = KeyCombination(
                    nativeKeyEvent.rawCode,
                    nativeKeyEvent.keyCode,
                    nativeKeyEvent.modifiers,
                    nativeKeyEvent.keyLocation
            )
            logger.debug {
                "Native key: ${nativeKeyEvent.keyCode.toString(16)}, modifiers mask: ${nativeKeyEvent.modifiers}"
            }
            logger.debug { keyCombination }
            Platform.runLater {
                keyEventDispatcher.keyCombinationPressed(keyCombination)
            }
        }
    }
}

class EventDispatcher : AbstractExecutorService() {
    private var running = true

    override fun shutdown() {
        running = false
    }

    override fun shutdownNow(): List<Runnable> {
        running = false
        return emptyList()
    }

    override fun isShutdown(): Boolean = !running
    override fun isTerminated(): Boolean = !running
    override fun awaitTermination(amount: Long, units: TimeUnit): Boolean = true
    override fun execute(action: Runnable): Unit = action.run()
}

private enum class NativeHookState {
    Terminated,
    Initialized
}

private sealed class NativeHookEvent {
    object Initialize : Event()
    object Terminate : Event()
}

private class NativeHookStateMachine : StateMachine<State, Event>(
        State.Terminated,
        transition(
                oldState = State.Terminated,
                transition = Event.Initialize::class,
                newState = State.Initialized
        ),
        transition(
                oldState = State.Initialized,
                transition = Event.Terminate::class,
                newState = State.Terminated
        )
)

fun NativeKeyEvent.toFxKeyCombination(): KeyCodeCombination {
    val keyCode: KeyCode = when (this.keyCode) {
        NativeKeyEvent.VC_ESCAPE -> KeyCode.ESCAPE
        NativeKeyEvent.VC_F1 -> KeyCode.F1
        NativeKeyEvent.VC_F2 -> KeyCode.F2
        NativeKeyEvent.VC_F3 -> KeyCode.F3
        NativeKeyEvent.VC_F4 -> KeyCode.F4
        NativeKeyEvent.VC_F5 -> KeyCode.F5
        NativeKeyEvent.VC_F6 -> KeyCode.F6
        NativeKeyEvent.VC_F7 -> KeyCode.F7
        NativeKeyEvent.VC_F8 -> KeyCode.F8
        NativeKeyEvent.VC_F9 -> KeyCode.F9
        NativeKeyEvent.VC_F10 -> KeyCode.F10
        NativeKeyEvent.VC_F11 -> KeyCode.F11
        NativeKeyEvent.VC_F12 -> KeyCode.F12
        NativeKeyEvent.VC_F13 -> KeyCode.F13
        NativeKeyEvent.VC_F14 -> KeyCode.F14
        NativeKeyEvent.VC_F15 -> KeyCode.F15
        NativeKeyEvent.VC_F16 -> KeyCode.F16
        NativeKeyEvent.VC_F17 -> KeyCode.F17
        NativeKeyEvent.VC_F18 -> KeyCode.F18
        NativeKeyEvent.VC_F19 -> KeyCode.F19
        NativeKeyEvent.VC_F20 -> KeyCode.F20
        NativeKeyEvent.VC_F21 -> KeyCode.F21
        NativeKeyEvent.VC_F22 -> KeyCode.F22
        NativeKeyEvent.VC_F23 -> KeyCode.F23
        NativeKeyEvent.VC_F24 -> KeyCode.F24

        NativeKeyEvent.VC_BACKQUOTE -> KeyCode.BACK_QUOTE
        NativeKeyEvent.VC_0 -> KeyCode.DIGIT0
        NativeKeyEvent.VC_1 -> KeyCode.DIGIT1
        NativeKeyEvent.VC_2 -> KeyCode.DIGIT2
        NativeKeyEvent.VC_3 -> KeyCode.DIGIT3
        NativeKeyEvent.VC_4 -> KeyCode.DIGIT4
        NativeKeyEvent.VC_5 -> KeyCode.DIGIT5
        NativeKeyEvent.VC_6 -> KeyCode.DIGIT6
        NativeKeyEvent.VC_7 -> KeyCode.DIGIT7
        NativeKeyEvent.VC_8 -> KeyCode.DIGIT8
        NativeKeyEvent.VC_9 -> KeyCode.DIGIT9

        NativeKeyEvent.VC_MINUS -> KeyCode.MINUS
        NativeKeyEvent.VC_EQUALS -> KeyCode.EQUALS
        NativeKeyEvent.VC_BACKSPACE -> KeyCode.BACK_SPACE
        NativeKeyEvent.VC_TAB -> KeyCode.TAB
        NativeKeyEvent.VC_CAPS_LOCK -> KeyCode.CAPS

        NativeKeyEvent.VC_A -> KeyCode.A
        NativeKeyEvent.VC_B -> KeyCode.B
        NativeKeyEvent.VC_C -> KeyCode.C
        NativeKeyEvent.VC_D -> KeyCode.D
        NativeKeyEvent.VC_E -> KeyCode.E
        NativeKeyEvent.VC_F -> KeyCode.F
        NativeKeyEvent.VC_G -> KeyCode.G
        NativeKeyEvent.VC_H -> KeyCode.H
        NativeKeyEvent.VC_I -> KeyCode.I
        NativeKeyEvent.VC_J -> KeyCode.J
        NativeKeyEvent.VC_K -> KeyCode.K
        NativeKeyEvent.VC_L -> KeyCode.L
        NativeKeyEvent.VC_M -> KeyCode.M
        NativeKeyEvent.VC_N -> KeyCode.N
        NativeKeyEvent.VC_O -> KeyCode.O
        NativeKeyEvent.VC_P -> KeyCode.P
        NativeKeyEvent.VC_Q -> KeyCode.Q
        NativeKeyEvent.VC_R -> KeyCode.R
        NativeKeyEvent.VC_S -> KeyCode.S
        NativeKeyEvent.VC_T -> KeyCode.T
        NativeKeyEvent.VC_U -> KeyCode.U
        NativeKeyEvent.VC_V -> KeyCode.V
        NativeKeyEvent.VC_W -> KeyCode.W
        NativeKeyEvent.VC_X -> KeyCode.X
        NativeKeyEvent.VC_Y -> KeyCode.Y
        NativeKeyEvent.VC_Z -> KeyCode.Z
        NativeKeyEvent.VC_OPEN_BRACKET -> KeyCode.OPEN_BRACKET
        NativeKeyEvent.VC_CLOSE_BRACKET -> KeyCode.CLOSE_BRACKET
        NativeKeyEvent.VC_BACK_SLASH -> KeyCode.BACK_SLASH
        NativeKeyEvent.VC_SEMICOLON -> KeyCode.SEMICOLON
        NativeKeyEvent.VC_QUOTE -> KeyCode.QUOTE
        NativeKeyEvent.VC_ENTER -> KeyCode.ENTER
        NativeKeyEvent.VC_COMMA -> KeyCode.COMMA
        NativeKeyEvent.VC_PERIOD -> KeyCode.PERIOD
        NativeKeyEvent.VC_SLASH -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.DIVIDE else KeyCode.SLASH
        NativeKeyEvent.VC_SPACE -> KeyCode.SPACE
        NativeKeyEvent.VC_DELETE -> KeyCode.DELETE

        NativeKeyEvent.VC_SHIFT_L -> KeyCode.SHIFT
        NativeKeyEvent.VC_SHIFT_R -> KeyCode.SHIFT
        NativeKeyEvent.VC_CONTROL_L -> KeyCode.CONTROL
        NativeKeyEvent.VC_CONTROL_R -> KeyCode.CONTROL
        NativeKeyEvent.VC_ALT_L -> KeyCode.ALT
        NativeKeyEvent.VC_ALT_R -> KeyCode.ALT
        NativeKeyEvent.VC_META_L -> KeyCode.META
        NativeKeyEvent.VC_META_R -> KeyCode.META
        NativeKeyEvent.VC_CONTEXT_MENU -> KeyCode.CONTEXT_MENU

        NativeKeyEvent.VC_UP -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD8 else KeyCode.UP
        NativeKeyEvent.VC_DOWN -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD2 else KeyCode.DOWN
        NativeKeyEvent.VC_LEFT -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD4 else KeyCode.LEFT
        NativeKeyEvent.VC_RIGHT -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD6 else KeyCode.RIGHT
        NativeKeyEvent.VC_HOME -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD7 else KeyCode.HOME
        NativeKeyEvent.VC_END -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD1 else KeyCode.END
        0xE04C -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD5 else KeyCode.CLEAR
        NativeKeyEvent.VC_PAGE_UP -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD9 else KeyCode.PAGE_UP
        NativeKeyEvent.VC_PAGE_DOWN -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD3 else KeyCode.PAGE_DOWN
        NativeKeyEvent.VC_INSERT -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.NUMPAD0 else KeyCode.INSERT
        0xE4E -> KeyCode.ADD
        0xE4A -> KeyCode.SUBTRACT
        0xE36 -> KeyCode.MINUS
        NativeKeyEvent.VC_PRINTSCREEN -> if (this.keyLocation == NativeKeyEvent.KEY_LOCATION_NUMPAD) KeyCode.MULTIPLY else KeyCode.PRINTSCREEN

        NativeKeyEvent.VC_SCROLL_LOCK -> KeyCode.SCROLL_LOCK
        NativeKeyEvent.VC_PAUSE -> KeyCode.PAUSE

        NativeKeyEvent.VC_KP_SEPARATOR -> KeyCode.DECIMAL
        NativeKeyEvent.VC_NUM_LOCK -> KeyCode.NUM_LOCK

        else -> KeyCode.UNDEFINED
    }

    val modifiers: MutableList<FxKeyCombination.Modifier> = mutableListOf()
    if ((this.modifiers and NativeInputEvent.SHIFT_MASK) != 0) modifiers.add(FxKeyCombination.SHIFT_DOWN)
    if ((this.modifiers and NativeInputEvent.CTRL_MASK) != 0) modifiers.add(FxKeyCombination.CONTROL_DOWN)
    if ((this.modifiers and NativeInputEvent.ALT_MASK) != 0) modifiers.add(FxKeyCombination.ALT_DOWN)
    if ((this.modifiers and NativeInputEvent.META_MASK) != 0) modifiers.add(FxKeyCombination.META_DOWN)
    return KeyCodeCombination(keyCode, *modifiers.toTypedArray())
}
