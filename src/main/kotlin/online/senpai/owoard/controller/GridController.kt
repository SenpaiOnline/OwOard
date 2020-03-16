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
import javafx.geometry.HPos
import javafx.scene.Node
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.model.AudioObject
import online.senpai.owoard.model.AudioObjectModel
import online.senpai.owoard.view.AudioGridTile
import online.senpai.owoard.view.EmptyGridTile
import online.senpai.owoard.view.GridPaneView
import online.senpai.owoard.view.GridTile
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.util.*
import javax.json.Json
import javax.json.JsonArray

private val logger: KLogger = KotlinLogging.logger {}

class GridController : Controller() {
    private val references = WeakHashMap<Tile, GridTile>()
    private val gridPane: GridPaneView by inject()

    fun initGridPaneWithEmptyTiles(cols: Int, rows: Int, haligment: HPos = HPos.CENTER) {
        if (gridPane.numberOfColumns > 0 || gridPane.numberOfRows > 0) {
            // TODO Ask
            clearGrid()
        }
        gridPane.setGridPaneSize(cols, rows)
        for (col: Int in 0 until cols) {
            for (row: Int in 0 until rows) {
                val tile: EmptyGridTile = find()
                gridPane.setHalignment(tile.root, haligment)
                gridPane.addNode(tile.root, col, row)
                references[tile.root] = tile
            }
        }
    }

    fun addTileToGrid(tile: GridTile, col: Int, row: Int, haligment: HPos = HPos.CENTER) {
        gridPane.setHalignment(tile.root, haligment)
        gridPane.addNode(tile.root, col, row)
        references[tile.root] = tile
    }

    fun swapTiles(t1: GridTile, t2: GridTile) {
        t1.replaceWith(t2)
        references[t2.root] = t2
    }

    fun swapTiles(t1: Tile, t2: GridTile) {
        swapTiles(references[t1]!!, t2)
    }

    fun swapTiles(t1: GridTile, t2: Tile) {
        swapTiles(t1, references[t2]!!)
    }

    fun replaceTileWithAudioTile(tile: GridTile, file: Path) {
        val audioTileScope = Scope()
        val soundObject = AudioObject(path = file)
        setInScope(AudioObjectModel(soundObject), audioTileScope)
        val gridTile: AudioGridTile = find(scope = audioTileScope)
        soundObject.subscriber = gridTile
        gridPane.replaceNode(tile.root, gridTile.root)
        references[gridTile.root] = gridTile
    }

    fun replaceTileWithAudioTile(tile: GridTile, file: File) {
        replaceTileWithAudioTile(tile, file.toPath())
    }

    fun replaceTileWithEmptyTile(tile: GridTile) {
        val emptyTile: EmptyGridTile = find()
        gridPane.replaceNode(tile.root, emptyTile.root)
        references[emptyTile.root] = emptyTile
    }

    fun resizeGrid(cols: Int, rows: Int): Unit = gridPane.setGridPaneSize(cols, rows)

    fun clearGrid(): Unit = gridPane.clearGrid()

    fun getNodeColumnIndex(node: Node): Int = gridPane.getNodeColumnIndex(node)

    fun getNodeRowIndex(node: Node): Int = gridPane.getNodeRowIndex(node)

    fun tilesAsJsonArray(): JsonArray {
        return Json
                .createArrayBuilder()
                .apply {
                    gridPane.nodes.forEach { node: Node ->
                        val gridTile: GridTile? = references[node]
                        if (gridTile != null) {
                            add(gridTile.toJson())
                        } else {
                            logger.error { "$gridTile is null!" }
                            throw IllegalStateException()
                        }
                    }
                }
                .build()
    }
}
