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
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyMapWrapper
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.AbstractTransitionalCallback
import online.senpai.owoard.KeyCombination
import online.senpai.owoard.KeyEventSubscriber
import tornadofx.*
import online.senpai.owoard.controller.KeyDispatcherEvent as Event
import online.senpai.owoard.controller.KeyDispatcherState as State

private val logger: KLogger = KotlinLogging.logger {}

class KeyEventDispatcher : Controller() {
    private val _subscribersToSpecificHotkey: ReadOnlyMapWrapper<KeyCombination, KeyEventSubscriber> =
            ReadOnlyMapWrapper(FXCollections.observableHashMap<KeyCombination, KeyEventSubscriber>())
    private val _lastPressedKeyCombinationProperty: ReadOnlyObjectWrapper<SimpleObjectProperty<KeyCombination>> =
            ReadOnlyObjectWrapper(SimpleObjectProperty())
    private val stateMachine: KeyDispatcherStateMachine = KeyDispatcherStateMachine()
    private var _lastPressedKeyCombination: KeyCombination? by _lastPressedKeyCombinationProperty.get()
    val subscribersToSpecificHotkey: ReadOnlyMapProperty<KeyCombination, KeyEventSubscriber> =
            _subscribersToSpecificHotkey.readOnlyProperty
    val lastPressedKeyCombination: ReadOnlyObjectProperty<SimpleObjectProperty<KeyCombination>> =
            _lastPressedKeyCombinationProperty.readOnlyProperty

    init {
        stateMachine.registerCallback(object : AbstractTransitionalCallback<State, Event>(logger) {
            override fun enteredState(
                    stateMachine: StateMachine<State, Event>,
                    previousState: State,
                    transition: Event,
                    currentState: State
            ) {
                when (transition) {
                    is Event.SwitchToNormalMode -> onNormalMode()
                    is Event.SwitchToAwaitMode -> onAwaitMode()
                }
            }
        })
    }

    private fun onNormalMode() {
        _lastPressedKeyCombination = null
    }

    private fun onAwaitMode() {
    }

    fun normalMode() {
        stateMachine.transition(Event.SwitchToNormalMode)
    }

    fun awaitNewHotkey() {
        stateMachine.transition(Event.SwitchToAwaitMode)
    }

    @Throws(IllegalArgumentException::class)
    fun registerSubscriberByHotkey(keyCombination: KeyCombination, subscriber: KeyEventSubscriber) {
        require(!_subscribersToSpecificHotkey.contains(keyCombination)) { "Hotkey is already in use!" }
        logger.debug { "New subscriber: ${keyCombination.keyName}, ${keyCombination.modifiers}" }
        _subscribersToSpecificHotkey[keyCombination] = subscriber
    }

    fun removeSubscriberByHotkey(keyCombination: KeyCombination) {
        logger.debug { "Removing the subscriber with hotkey ${keyCombination.keyName}, ${keyCombination.modifiers}" }
        _subscribersToSpecificHotkey.remove(keyCombination)
    }

    fun keyCombinationPressed(keyCombination: KeyCombination) {
        when (stateMachine.state) {
            State.NormalMode -> _subscribersToSpecificHotkey[keyCombination]?.handleKeyEvent()
            State.AwaitMode -> _lastPressedKeyCombination = keyCombination
        }
    }
}

private enum class KeyDispatcherState {
    NormalMode,
    AwaitMode
}

private sealed class KeyDispatcherEvent {
    object SwitchToNormalMode : Event()
    object SwitchToAwaitMode : Event()
}

private class KeyDispatcherStateMachine : StateMachine<State, Event>(
        State.NormalMode,
        transition(
                oldState = State.NormalMode,
                newState = State.AwaitMode,
                transition = Event.SwitchToAwaitMode::class
        ),
        transition(
                oldState = State.AwaitMode,
                newState = State.NormalMode,
                transition = Event.SwitchToNormalMode::class
        )
)
