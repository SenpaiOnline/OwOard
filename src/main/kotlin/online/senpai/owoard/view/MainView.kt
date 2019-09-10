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

import javafx.event.Event
import javafx.scene.control.ScrollPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import tornadofx.*

class MainView : View("Yet Another Soundboard? Please Stop!") {
    val gridPaneView: GridPaneView by inject()
    var scrollpane: ScrollPane by singleAssign()
    override val root = borderpane {
        setPrefSize(600.0, 800.0)
        top {
            vbox {
                menubar {
                    menu("File") {
                        item("Create new").action { gridPaneView.initGridPane(12, 6) }
                        item("Save")
                        separator()
                        item("Quit")
                    }
                    menu("Create") {
                        item("Add handler").action {
                            primaryStage.scene.addEventHandler(KeyEvent.KEY_PRESSED) {
                                println("Scene handler -> $it")
                            }
                        }
                        item("Dump").action { gridPaneView.saveNodes() }
                        item("Test").action {
                            val ke = KeyEvent(KeyEvent.KEY_PRESSED,
                                    "", "",
                                    KeyCode.P, false, false, true, false)
                            Event.fireEvent(this@borderpane, ke)
                        }
                    }
                }
            }
        }
        center {
            scrollpane = scrollpane(fitToWidth = true, fitToHeight = true) {
                add(gridPaneView)
            }
        }
    }
}
