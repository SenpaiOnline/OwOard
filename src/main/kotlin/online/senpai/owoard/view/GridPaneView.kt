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

import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.tools.FlowGridPane
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.stage.FileChooser
import online.senpai.owoard.controller.TileFactory
import online.senpai.owoard.model.AudioObjectModel
import tornadofx.*
import java.io.File
import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonWriter

typealias ColumnAndRowIndex = Pair<Int, Int>

class GridPaneView(tileSize: Double = 150.0) : View("My View") {
    private val tileFactory: TileFactory by inject()
    private val alignment: HPos = HPos.CENTER
    val tileSizeProperty = SimpleDoubleProperty(this, "tileSize", tileSize)
    var tileSize by tileSizeProperty

    override val root = FlowGridPane(0, 0).apply {
        hgap = 5.0
        vgap = 5.0
        paddingAll = 5
        background = Background(BackgroundFill(Tile.BACKGROUND.darker(), CornerRadii.EMPTY, Insets.EMPTY))
    }

    fun initGridPane(cols: Int, rows: Int) {
        if (root.noOfCols > 0 || root.noOfRows > 0) {
            // TODO Ask
            clearGrid()
        }
        changeGridPaneSizeTo(cols, rows)
        for (i: Int in 0 until cols) {
            for (j: Int in 0 until rows) {
//                val tile: Tile = tileFactory.createEmptyNode()
//                val tile = AudioTile()
                val model = AudioObjectModel("TestObj", "/home/maxim/Documents/VerminData/extraction/File0057.ogg")
                val tile: AudioTile = find(mapOf("model" to model))
                /*tile.apply {
                    prefWidthProperty().bind(tileSize)
                    prefHeightProperty().bind(tileSize)
                    maxWidthProperty().bind(tileSize)
                    maxHeightProperty().bind(tileSize)
                }*/
                FlowGridPane.setHalignment(tile.root, alignment)
                FlowGridPane.setConstraints(tile.root, i, j)
                root.add(tile)
            }
        }
        /*for (i in 0 until rows) {
            root.addColumn(i, *Array(cols) { tileFactory.createEmptyNode() })
        }*/
    }

    @Throws(IllegalArgumentException::class)
    fun changeGridPaneSizeTo(cols: Int, rows: Int) {
        require(cols > 0 && rows > 0)
        root.setNoOfColsAndNoOfRows(cols, rows)
    }

    fun clearGrid() {
        root.clear()
    }

    private fun getNodePosition(node: Node): ColumnAndRowIndex {
        return FlowGridPane.getColumnIndex(node) to FlowGridPane.getRowIndex(node)
    }

    fun saveNodes() {
        val file: File = chooseFile(
                title = "Save as...",
                mode = FileChooserMode.Save,
                filters = arrayOf(FileChooser.ExtensionFilter("Json files", "*.json"))
        ).firstOrNull() ?: return
        runAsync {
            val jsonArray: JsonArray = Json.createArrayBuilder().apply {
                root.children.forEach { tile: Node ->
                    add(Json.createObjectBuilder().apply {
                        val position: ColumnAndRowIndex = getNodePosition(tile)
                        add("col", position.first)
                        add("row", position.second)
                        val userData: Any? = tile.userData
                        add("soundObject", when (userData) {
                            is AudioObjectModel -> userData.toJSON()
                            else -> Json.createObjectBuilder().build()
                        })
                    })
                }
            }.build()
            val jsonObject = Json.createObjectBuilder().apply {
                add("cols", root.noOfCols)
                add("rows", root.noOfRows)
                add("tileSize", tileSize)
                add("tiles", jsonArray)
            }.build()
            val jsonWriter: JsonWriter = Json.createWriter(file.outputStream())
            jsonWriter.writeObject(jsonObject)
            jsonWriter.close() //  TODO
        }
    }

    fun replaceNodeWith(node: Node, col: Int, row: Int) {
    }

    fun addItem(item: AudioObjectModel) {
    }
}
