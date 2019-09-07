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

import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.TileBuilder
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import online.senpai.owoard.model.AudioObjectModel
import online.senpai.owoard.view.Player
import tornadofx.*
import java.io.File

//private const val TILE_SIZE = 150.0

class TileFactory : Controller() {

    fun createEmptyNode(text: String? = null, title: String = "Empty"): Tile {
        val tile: Tile = TileBuilder.create().apply {
//            prefSize(TILE_SIZE, TILE_SIZE)
//            maxSize(TILE_SIZE, TILE_SIZE)
            skinType(Tile.SkinType.CUSTOM)
            textSize(Tile.TextSize.BIGGER)
            if (!text.isNullOrEmpty()) text(text)
            title(title)
            roundedCorners(true)
        }.build()
        /*tile.setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY && it.clickCount == 2) {
                tile.replaceWith(createEmptyNode(text = "HALP", title = "I'm ADOPTED!"), ViewTransition.Explode(0.5.seconds))
            }
        }*/
        tile.setOnDragOver { event: DragEvent ->
            if (event.gestureSource != tile && event.dragboard.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY)
            }
            event.consume()
        }
        tile.setOnDragDropped { event: DragEvent ->
            val dragboard: Dragboard = event.dragboard
            var success = false
            if (dragboard.hasFiles()) {
                val file: File = dragboard.files.first() // TODO check file
                val soundObject = AudioObjectModel(name = file.name, path = file.absolutePath)
                success = true
                tile.replaceWith(createSoundObjectNode(soundObject = soundObject), ViewTransition.Explode(0.5.seconds))
            }
            event.isDropCompleted = success
            event.consume()
        }
        return tile
    }

    fun createSoundObjectNode(soundObject: AudioObjectModel): Tile {
        val tile: Tile = TileBuilder.create().apply {
//            prefSize(TILE_SIZE, TILE_SIZE)
//            maxSize(TILE_SIZE, TILE_SIZE)
            skinType(Tile.SkinType.CUSTOM)
            textSize(Tile.TextSize.BIGGER)
            roundedCorners(true)
        }.build()
        tile.apply {
            userData = soundObject
            titleProperty().bind(soundObject.nameProperty)
            textProperty().bind(soundObject.pathProperty)
        }
        /*tile.contextmenu {
            item("Play!").action {
                if (tile.userData != null) {
                    audioController.play(tile.userData as AudioObjectModel)
                }
            }
        }*/
        tile.graphic = Player(tile, File(soundObject.path)).root
        return tile
    }
}
