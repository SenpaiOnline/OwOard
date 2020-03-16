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
import javafx.collections.ObservableList
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.stage.FileChooser
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.controller.AudioController
import tornadofx.*
import java.io.File
import javax.json.Json
import javax.json.stream.JsonParser

private val logger: KLogger = KotlinLogging.logger {}

class GridPaneView(tileSize: Double = 150.0) : View("My View") {
    private val audioController: AudioController by inject()
    val numberOfColumns: Int get() = root.noOfCols
    val numberOfRows: Int get() = root.noOfRows
    val nodes: ObservableList<Node> get() = root.children
    val tileSizeProperty = SimpleDoubleProperty(this, "tileSize", tileSize)
    var tileSize by tileSizeProperty

    override val root: FlowGridPane = FlowGridPane(0, 0).apply {
        hgap = 5.0
        vgap = 5.0
        paddingAll = 5
        background = Background(BackgroundFill(Tile.BACKGROUND.darker(), CornerRadii.EMPTY, Insets.EMPTY))
    }

    fun addNode(node: Node, col: Int, row: Int): Unit = root.add(node, col, row)

    fun setHalignment(node: Node, haligment: HPos): Unit = FlowGridPane.setHalignment(node, haligment)

    fun getNodeColumnIndex(node: Node): Int = FlowGridPane.getColumnIndex(node)

    fun getNodeRowIndex(node: Node): Int = FlowGridPane.getRowIndex(node)

    fun setNodeColumnIndex(node: Node, index: Int): Unit = FlowGridPane.setColumnIndex(node, index)

    fun setNodeRowIndex(node: Node, index: Int): Unit = FlowGridPane.setRowIndex(node, index)

    fun setGridPaneSize(cols: Int, rows: Int) {
        require(cols > 0 && rows > 0)
        root.setNoOfColsAndNoOfRows(cols, rows)
    }

    fun clearGrid() {
        root.clear()
    }

    fun loadTiles() {
        val file: File = chooseFile(
                title = "Save as...",
                mode = FileChooserMode.Single,
                filters = arrayOf(FileChooser.ExtensionFilter("Owoard files", "*.owo"))
        )
                .firstOrNull() ?: return
        val parser: JsonParser = Json.createParser(file.inputStream())
//        parser.`object`.
    }

    /*fun swapNodes(n1: Tile, n2: Tile) {
        val n1Clone = n1.clone()
        var temp: Int = getNodeRowIndex(n1)
        setNodeRowIndex(n1, getNodeRowIndex(n2))
        setNodeRowIndex(n2, temp)

        temp = getNodeColumnIndex(n1)
        setNodeColumnIndex(n1, getNodeColumnIndex(n2))
        setNodeColumnIndex(n2, temp)
    }*/

    fun replaceNode(n1: Node, n2: Node) {
        val col = getNodeColumnIndex(n1)
        val row = getNodeRowIndex(n1)
        n1.replaceWith(n2, ViewTransition.Metro(0.5.seconds))
        setNodeColumnIndex(n1, col)
        setNodeRowIndex(n1, row)
    }
}
