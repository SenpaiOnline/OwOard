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

package online.senpai.owoard.model

import eu.hansolo.tilesfx.Tile
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class GridModel(cols: Int, rows: Int, tiles: Array<Tile>) : JsonModel{
    val colsProperty = SimpleIntegerProperty(this, "cols", cols)
    var cols: Int by colsProperty
    val rowsProperty = SimpleIntegerProperty(this, "rows", rows)
    var rows by rowsProperty
    val tiles: ObservableList<Tile> = FXCollections.observableArrayList(*tiles)

    /*override fun updateModel(json: JsonObject) {
        with(json) {
            cols = int("cols")
            firstName = string("firstName")
            lastName = string("lastName")
            phones.setAll(getJsonArray("phones").toModel())
        }
    }*/

   /* override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("cols", cols)
            add("rows", rows)
            add("tiles", tiles.toJSON())
        }
    }*/
}
