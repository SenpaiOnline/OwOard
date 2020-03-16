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

import online.senpai.owoard.controller.GridController
import online.senpai.owoard.controller.StateController
import online.senpai.owoard.model.AudioSettingsModel
import tornadofx.*

class MainView : View("OwOard") {
    /*private val audioSettingsEditorView: AudioSettingsEditorView by inject()*/
    private val gridPaneView: GridPaneView by inject()
    private val gridController: GridController by inject()
    private val stateController: StateController by inject()
    private val audioModel = AudioSettingsModel()

    override val root = borderpane {
        setPrefSize(600.0, 800.0) // TODO
        top {
            vbox {
                menubar {
                    menu("File") {
                        item("Create new...").action { gridController.initGridPaneWithEmptyTiles(12, 10) }
                        item("Save...").action { stateController.save() }
                        item("Load...").action { stateController.load() }
                        separator()
                        item("Quit")
                    }
                    menu("Settings") {
                        item("Audio settings...").action { openSettingsEditor() }
                    }
                }
            }
        }
        center {
            scrollpane(fitToWidth = true, fitToHeight = true) {
                add(gridPaneView)
            }
        }
    }

    private fun openSettingsEditor() {
        val editorScope = Scope()
        setInScope(audioModel, editorScope)
        find(AudioSettingsEditorView::class, editorScope).openWindow()
    }
}
