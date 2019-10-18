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

import javafx.beans.Observable
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.input.KeyCodeCombination
import online.senpai.owoard.KeyEventSubscriber
import online.senpai.owoard.KeyEventType
import tornadofx.*

class KeyEventDispatcher : Controller() {
    val subscribersToSpecificHotkey: ObservableMap<KeyCodeCombination, KeyEventSubscriber> = FXCollections.observableHashMap()
    val recordLastPressedKeyCombinationProperty = SimpleBooleanProperty(false)
    var recordLastPressedKeyCombination: Boolean by recordLastPressedKeyCombinationProperty
    val lastPressedKeyCombinationProperty = SimpleObjectProperty<KeyCodeCombination>()
    var lastPressedKeyCombination: KeyCodeCombination? by lastPressedKeyCombinationProperty

    init {
        recordLastPressedKeyCombinationProperty.addListener { _: Observable, oldValue: Boolean, newValue: Boolean ->
            if (oldValue && !newValue) {
                lastPressedKeyCombination = null
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    fun subscribeToSpecificHotkey(keyCodeCombination: KeyCodeCombination, subscriber: KeyEventSubscriber) {
        require(!subscribersToSpecificHotkey.contains(keyCodeCombination)) { "Hotkey is already in use!" }
        subscribersToSpecificHotkey[keyCodeCombination] = subscriber
    }

    fun removeSubscriberByHotkey(keyCodeCombination: KeyCodeCombination) {
        subscribersToSpecificHotkey.remove(keyCodeCombination)
    }

    fun keyCombinationPressed(keyCombination: KeyCodeCombination) {
        subscribersToSpecificHotkey[keyCombination]?.handleKeyEvent(KeyEventType.PLAY) // TODO stop
        if (recordLastPressedKeyCombination) {
            lastPressedKeyCombination = keyCombination
        }
    }
}
