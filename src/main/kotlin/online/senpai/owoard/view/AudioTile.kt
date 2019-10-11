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

package online.senpai.owoard.view

import de.jensd.fx.glyphs.icons525.Icons525
import de.jensd.fx.glyphs.icons525.Icons525View
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.paint.Color
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.KeyEventSubscriber
import online.senpai.owoard.KeyEventType
import online.senpai.owoard.controller.KeyEventDispatcher
import online.senpai.owoard.event.TilePlayEvent
import online.senpai.owoard.model.AudioObjectModel
import tornadofx.*
import java.io.File

private val logger: KLogger = KotlinLogging.logger {}

class AudioTile : Fragment("Audio Tile"), KeyEventSubscriber {
    private val keyEventDispatcher: KeyEventDispatcher by inject()
    var playButton: Button by singleAssign()
    var stopButton: Button by singleAssign()

    val nameProperty = SimpleStringProperty(this, "name", "")
    var name: String by nameProperty

    val model: AudioObjectModel = params["model"] as AudioObjectModel

    override val root = stackpane {
        maxWidth = 150.0
        maxWidth = 150.0
        style {
            borderWidth += box(3.px)
            borderColor += box(
                    top = Color.RED,
                    right = Color.DARKGREEN,
                    left = Color.ORANGE,
                    bottom = Color.PURPLE
            )
            borderRadius += box(5.px)
        }
        vbox {
            label(model.nameProperty).textFill = Color.WHITE
            label("Status").textFill = Color.WHITE
            label(model.hotkeyProperty.stringBinding { if (it == null) "No hotkey" else it.displayText }) {
                textFill = Color.WHITE
            }
            spacer()
            hbox {
                alignment = Pos.BOTTOM_CENTER
                playButton = button(graphic = Icons525View(Icons525.PLAY)) {
                    action {
                        fire(TilePlayEvent(File(model.path)))
                    }
                }
                stopButton = button(graphic = Icons525View(Icons525.STOP)) {
                    setOnKeyTyped {
                        logger.debug { "Stop button pressed" }
                    }
                }
                button(graphic = Icons525View(Icons525.LAUNCHPAD)) {
                    action {
                        setupHotkey()
                    }
                }
            }
            hbox {
                alignment = Pos.BOTTOM_CENTER
                button(graphic = Icons525View(Icons525.STOP))
                button(graphic = Icons525View(Icons525.STOP))
                button(graphic = Icons525View(Icons525.STOP))
            }
        }
    }

    override fun onDelete() {
        removeHotkey()
        super.onDelete()
    }

    private fun setupHotkey() {
        val hotkey = KeyCodeCombination(KeyCode.P, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN)
        try {
            keyEventDispatcher.subscribeToSpecificHotkey(hotkey,this@AudioTile)
            model.hotkey = hotkey
        }
        catch (e: IllegalArgumentException) {
            error("Already in use!", "Hotkey ${hotkey.displayText} is already in use!", ButtonType.OK)
        }
    }

    private fun removeHotkey() {
        if (model.hotkey != null) {
            keyEventDispatcher.removeSubscriberByHotkey(model.hotkey)
        }
    }

    override fun handleKeyEvent(keyEventType: KeyEventType) {
        when (keyEventType) {
            KeyEventType.PLAY -> playButton.fire()
            KeyEventType.STOP -> stopButton.fire()
        }
    }
}
