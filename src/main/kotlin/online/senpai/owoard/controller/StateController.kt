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

import javafx.scene.paint.Color
import javafx.stage.FileChooser
import online.senpai.owoard.KeyCombination
import online.senpai.owoard.model.AudioObject
import online.senpai.owoard.model.AudioObjectModel
import online.senpai.owoard.view.AudioGridTile
import online.senpai.owoard.view.EmptyGridTile
import online.senpai.owoard.view.GridPaneView
import tornadofx.*
import java.io.File
import java.nio.file.Paths
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonValue
import javax.json.JsonWriter
import javax.json.stream.JsonParser
import javax.json.stream.JsonParser.Event.START_OBJECT

class StateController : Controller() {
    private val audioController: AudioController by inject()
    private val gridController: GridController by inject()
    private val gridPaneView: GridPaneView by inject()

    fun save() {
        val file: File = FileChooser()
                .apply {
                    initialFileName = "*.owo"
                    extensionFilters.add(FileChooser.ExtensionFilter("Owoard files", "*.owo"))
                }
                .showSaveDialog(primaryStage)
                ?: return
        runAsync {
            val jsonObject: JsonObject = Json
                    .createObjectBuilder()
                    .apply {
                        add("cols", gridPaneView.numberOfColumns)
                        add("rows", gridPaneView.numberOfRows)
                        add("tileSize", gridPaneView.tileSize)
                        /*add("masterVolume", audioController.masterVolume)*/ // TODO
                        add("tiles", gridController.tilesAsJsonArray())
                    }
                    .build()
            val jsonWriter: JsonWriter = Json.createWriter(file.outputStream())
            jsonWriter.writeObject(jsonObject)
            jsonWriter.close() //  TODO
        }
    }

    fun load() {
        val file: File = chooseFile(
                mode = FileChooserMode.Single,
                filters = arrayOf(
                        FileChooser.ExtensionFilter(
                                "OwO File",
                                "*.owo"
                        )
                )
        )
                .firstOrNull()
                ?: return
        /*val gridList: MutableList<GridTile> = mutableListOf()*/
        val parser: JsonParser = Json.createParser(file.inputStream())
        while (parser.hasNext()) {
            val event: JsonParser.Event = parser.next()
            if (event == START_OBJECT) {
                val jsonObject: JsonObject = parser.`object`
                /*audioController.masterVolume = jsonObject.getInt("masterVolume", 100)*/ // TODO
                gridController.clearGrid()
                gridController.resizeGrid(jsonObject.getInt("cols"), jsonObject.getInt("rows"))
                jsonObject.getJsonArray("tiles").forEach { tile: JsonValue ->
                    val tileObject: JsonObject = tile.asJsonObject()
                    when (tileObject.getString("type")) {
                        "empty" -> {
                            addEmptyTile(tileObject)
                        }
                        "audio" -> {
                            addAudioTile(tileObject)
                        }
                    }
                }
            }
        }
        parser.close()
    }

    private fun addEmptyTile(tileObject: JsonObject) {
        gridController.addTileToGrid(
                tile = find<EmptyGridTile>(),
                col = tileObject.getInt("col"),
                row = tileObject.getInt("row")
        )
    }

    private fun addAudioTile(tileObject: JsonObject) {
        val modelObject: JsonObject = tileObject.getJsonObject("model")
        val audioTileScope = Scope()
        val hotkeyObject: JsonObject? = modelObject.getJsonObject("hotkey")
        val model = AudioObject( // TODO
                name = modelObject.getString("name"),
                path = Paths.get(modelObject.getString("path")),
                hotkey = null,
                volume = modelObject.getInt("volume"),
                backgroundColor = Color.web(modelObject.getString("backgroundColor")),
                borderColor = Color.web(modelObject.getString("borderColor"))
        )
        val vm = AudioObjectModel(model)
        setInScope(vm, audioTileScope)
        val gridTile: AudioGridTile = find(scope = audioTileScope)
        model.subscriber = gridTile
        if (hotkeyObject != null) {
            vm.hotkey.value = KeyCombination.fromJson(hotkeyObject)
        }
        gridController.addTileToGrid(
                tile = gridTile,
                col = tileObject.getInt("col"),
                row = tileObject.getInt("row")
        )
        vm.commit(force = true)
    }
}
