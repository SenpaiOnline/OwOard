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
import eu.hansolo.tilesfx.Tile
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import javafx.scene.input.KeyCombination
import javafx.scene.paint.Color
import tornadofx.*
import java.io.File

class Player(private val tile: Tile, private val file: File) : View("Player") {
//    private val streamPlayer: StreamPlayer = StreamPlayer()
    val status = SimpleStringProperty(this, "status", "")
//    val statusObject = SimpleObjectProperty<Status>()
    var playbutton: Button by singleAssign()


    override val root = vbox {
        label {
//            textProperty().bind(statusObject.asString())
            textFill = Color.WHITE
        }
        label {
//            text = "Duration: ${streamPlayer.durationInSeconds}"
            textFill = Color.WHITE
        }
        spacer()
        playbutton = button(graphic = Icons525View(Icons525.PLAY)) {
            autosize()
            shortcut(KeyCombination.keyCombination("a"))
            action {
//                fire(TilePlayEvent(tile))
            }
        }
        playbutton.shortcut("")
    }
}
