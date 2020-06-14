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

import de.jensd.fx.glyphs.icons525.Icons525
import de.jensd.fx.glyphs.icons525.Icons525View
import eu.hansolo.tilesfx.Tile
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.Jsonable
import online.senpai.owoard.KeyEventSubscriber
import online.senpai.owoard.Playable
import online.senpai.owoard.controller.AudioController
import online.senpai.owoard.controller.GridController
import online.senpai.owoard.helper.selectAudioFile
import online.senpai.owoard.model.AudioObjectModel
import tornadofx.*
import java.io.File
import java.util.concurrent.Callable
import javax.json.Json
import javax.json.JsonObject

private val logger: KLogger = KotlinLogging.logger {}

sealed class GridTile : Fragment(), Cloneable, Jsonable {
    final override val root: Tile = Tile(Tile.SkinType.CUSTOM)
    internal val gridController: GridController by inject(FX.defaultScope)
    val col: Int get() = gridController.getNodeColumnIndex(root)
    val row: Int get() = gridController.getNodeRowIndex(root)

    init {
        with(root) {
            userData = this@GridTile
            roundedCorners = false
            isTextVisible = true
            textSize = Tile.TextSize.BIGGER
        }
    }
}

class EmptyGridTile : GridTile() {
    init {
        with(root) {
            text = "Empty"
            setOnDragOver(::dragOver)
            setOnDragDropped(::dragDropped)
            contextmenu {
                item("Select audio file...").action {
                    val file: File? = selectAudioFile()
                    if (file != null) {
                        gridController.replaceTileWithAudioTile(this@EmptyGridTile, file)
                    }
                }
            }

            addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED) {
                logger.debug { "MouseDragEvent.MOUSE_DRAG_RELEASED" }
                val otherTile: Tile = it.gestureSource as Tile
                if (this !== otherTile) {
                    val clone: EmptyGridTile = this@EmptyGridTile.clone()
                    gridController.swapTiles(otherTile, clone)
                    gridController.swapTiles(this@EmptyGridTile, otherTile)
                }
                it.consume()
            }
        }
    }

    private fun dragOver(event: DragEvent) {
        if (event.gestureSource !== this && event.dragboard.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY)
        }
        event.consume()
    }

    private fun dragDropped(event: DragEvent) {
        val dragboard: Dragboard = event.dragboard
        var success = false
        if (dragboard.hasFiles()) {
            val file: File = dragboard.files.first() // TODO check the file
            success = true
            gridController.replaceTileWithAudioTile(this, file)
        }
        event.isDropCompleted = success
        event.consume()
    }

    override fun clone(): EmptyGridTile = find(scope = this.scope)

    override fun toString(): String = "EmptyGridTile(col=$col, row=$row)"

    override fun toJson(): JsonObject {
        return Json
                .createObjectBuilder()
                .apply {
                    add("type", "empty")
                    add("col", col)
                    add("row", row)
                }
                .build()
    }
}

class AudioGridTile : GridTile(), KeyEventSubscriber, Playable {
    private val audioController: AudioController by inject(FX.defaultScope)
    private val editor: GridTileEditor by inject()
    private val model: AudioObjectModel by inject()
    override val source: String = model.path.value.toString()

    init {
        with(root) {
            backgroundColorProperty().bind(model.backgroundColor)
            borderColorProperty().bind(model.borderColor)
            borderWidth = 5.0
            isTextVisible = false

            graphic = vbox {
                label(model.name) {
                    textFill = Color.WHITE
                    isWrapText = true
                }
                spacer()
                label(model.hotkey.stringBinding { it?.modifiers ?: "" }) {
                    textFill = Color.WHITE
                }
                label(model.hotkey.stringBinding { it?.keyName ?: "No hotkey" }) {
                    textFill = Color.WHITE
                }
            }

            contextmenu {
                item("Play", graphic = Icons525View(Icons525.PLAY)).action {
                    play()
                }
                item("Settings...", graphic = Icons525View(Icons525.WP_COG)).action {
                    editTile()
                }
                menu("Other") {
                    item("Delete tile").action {
                        deleteTile()
                    }
                }
            }

            onDoubleClick {
                play()
            }


            addEventFilter(MouseDragEvent.DRAG_DETECTED) {
                logger.debug { "MouseDragEvent.DRAG_DETECTED" }
                startFullDrag()
                it.consume()
            }

            addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED) {
                logger.debug { "MouseDragEvent.MOUSE_DRAG_RELEASED" }
                val otherTile: Tile = it.gestureSource as Tile
                if (this !== otherTile) {
                    val clone: AudioGridTile = this@AudioGridTile.clone()
                    gridController.swapTiles(otherTile, clone)
                    gridController.swapTiles(this@AudioGridTile, otherTile)
                }
                it.consume()
            }
        }
    }

    private fun deleteTile() {
        if (model.hotkey.value != null) {
            model.hotkey.value = null
            model.commit(model.hotkey)
        }
        gridController.replaceTileWithEmptyTile(this)
    }

    private fun play() {
        if (model.path.value != null) {
            audioController.play(this, model.volume.value.toInt())
        }
    }

    private fun editTile() {
        editor.openModal(
                resizable = false,
                owner = currentStage,
                stageStyle = StageStyle.UNDECORATED
        )
    }

    override fun onDock() {
        root.backgroundProperty().bind(Bindings.createObjectBinding(Callable<Background> {
            Background(BackgroundFill(model.backgroundColor.value, CornerRadii.EMPTY, Insets.EMPTY))
        }, model.backgroundColor))
    }

    override fun handleKeyEvent() {
        play()
    }

    override fun playing() {
        TODO("not implemented")
    }

    override fun stopped() {
        TODO("not implemented")
    }

    override fun clone(): AudioGridTile = find(scope = this.scope)

    override fun toJson(): JsonObject {
        return Json
                .createObjectBuilder()
                .apply {
                    add("type", "audio")
                    add("col", col)
                    add("row", row)
                    add("model", model.audioModel.toJSON())
                }
                .build()
    }

    override fun toString(): String {
        return "AudioGridTile(col=$col, row=$row, name=${model.name.value}, hashCode=${model.hotkey.value.hashCode()})"
    }
}

