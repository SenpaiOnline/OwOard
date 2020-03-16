/*
 * This file is part of the OwOard distribution (https://github.com/aiscy/OwOard).
 * Copyright (c) 2020 Maxim Valeryevich Pavlov.
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

package online.senpai.owoard.view

import com.toxicbakery.kfinstatemachine.StateMachine
import com.toxicbakery.kfinstatemachine.TransitionCallback
import de.jensd.fx.glyphs.icons525.Icons525
import de.jensd.fx.glyphs.icons525.Icons525View
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ButtonType
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.KeyCombination
import online.senpai.owoard.controller.KeyEventDispatcher
import online.senpai.owoard.helper.selectAudioFile
import online.senpai.owoard.model.AudioObjectModel
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import online.senpai.owoard.view.TileEditorEvent as Event
import online.senpai.owoard.view.TileEditorState as State

private val logger: KLogger = KotlinLogging.logger {}

private enum class TileEditorState {
    Normal,
    Binded
}

private sealed class TileEditorEvent {
    object SwitchToNormal : Event()
    object SwitchToBinded : Event()
}

private class TileEditorStateMachine(
        normal: () -> Unit,
        binded: () -> Unit
) : StateMachine<State, Event>(
        State.Normal,
        transition(
                oldState = State.Normal,
                newState = State.Binded,
                transition = Event.SwitchToBinded::class
        ),
        transition(
                oldState = State.Binded,
                newState = State.Normal,
                transition = Event.SwitchToNormal::class
        )
) {
    init {
        registerCallback(object : TransitionCallback<State, Event> {
            override fun enteredState(
                    stateMachine: StateMachine<State, Event>,
                    previousState: State,
                    transition: Event,
                    currentState: State
            ) {
                when (transition) {
                    is Event.SwitchToNormal -> normal()
                    is Event.SwitchToBinded -> binded()
                }
            }

            override fun enteringState(
                    stateMachine: StateMachine<State, Event>,
                    currentState: State,
                    transition: Event,
                    targetState: State
            ) {
                logger.debug { "Changing state to $targetState from $currentState because of an event ${transition::class.simpleName}." }
            }
        })
    }
}

class GridTileEditor : View("Tile Editor") {
    private val model: AudioObjectModel by inject()
    private val keyDispatcher: KeyEventDispatcher by inject(FX.defaultScope)
    private var buttonBar: HBox by singleAssign()

    override val root: Form = form {
        fieldset("Tile settings", labelPosition = Orientation.VERTICAL) {
            field("Tile name") {
                textfield(model.name) {
                    validator {
                        if (model.name.value.isNullOrBlank()) error("Name shouldn't be blank") else null
                    }
                }
            }
            field("Path to audio", orientation = Orientation.VERTICAL) {
                label(model.path) {
                    tooltip {
                        textProperty().bind(model.path.stringBinding { it.toString() })
                    }
                }
                button("Browse...") {
                    action {
                        selectFile()
                    }
                }
            }
            field("Hotkey", orientation = Orientation.VERTICAL) {
                hbox(alignment = Pos.CENTER_LEFT) {
                    buttonBar = this
                    button(
                            graphic = Icons525View(Icons525.WP_COG),
                            text = model.hotkey.stringBinding {
                                if (it == null) "None..." else "${it.modifiers} + ${it.keyName}"
                            }
                    ) {
                        hboxConstraints {
                            marginRight = 20.0
                            hGrow = Priority.ALWAYS
                        }
                        action {
                            setHotkey()
                        }
                    }
                    button(graphic = Icons525View(Icons525.EJECT_CIRCLE)) {
                        action {
                            clearHotkey()
                        }
                    }
                }
            }
            field("Background color") {
                colorpicker(model.backgroundColor as ObjectProperty<Color>)
            }
            field("Border color") {
                colorpicker(model.borderColor as ObjectProperty<Color>)
            }
            buttonbar {
                button("Cancel") {
                    isCancelButton = true
                    action {
                        cancel()
                    }
                }
                button("Save") {
                    isDefaultButton = true
                    enableWhen(Bindings.and(model.dirty, model.valid))
                    action {
                        save()
                    }
                }
            }
        }
    }

    init {
        model.validationContext.addValidator(buttonBar, model.hotkey) {
            if (
                    keyDispatcher.subscribersToSpecificHotkey.containsKey(it) &&
                    keyDispatcher.subscribersToSpecificHotkey[it] !== model.audioModel.subscriber
            ) {
                error("Hotkey is already in use!")
            } else {
                null
            }
        }
    }

    private fun save() {
        if (model.validate()) {
            model.commit {
                close()
            }
        }

    }

    private fun cancel() {
        model.rollback()
        close()
    }

    private fun clearHotkey() {
        val hotkey: KeyCombination? = model.hotkey.value
        if (hotkey != null) {
            model.hotkey.value = null
        }
    }

    private fun setHotkey() {
        keyDispatcher.lastPressedKeyCombination.get().onChangeOnce {
            if (it == null) return@onChangeOnce
            model.hotkey.value = it
            model.validate(fields = *arrayOf(model.hotkey))
        }
    }

    private fun selectFile() {
        val file: File? = selectAudioFile()
        if (file != null) {
            val path: Path = file.toPath()
            if (Files.isReadable(path)) {
                model.path.value = path
            } else {
                error("", "Error while loading selected file!", ButtonType.OK)
            }
        }
    }

    override fun onBeforeShow() {
        with(root) {
            prefWidth = 250.0
            maxWidth = 250.0
            minWidth = 250.0
        }
    }

    override fun onDock() {
        keyDispatcher.awaitNewHotkey()
    }

    override fun onUndock() {
        keyDispatcher.normalMode()
        if (model.isDirty) {
            model.rollback()
        }
    }
}
