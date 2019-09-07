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

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class AudioObjectModel(name: String, path: String) : JsonModel {
    val nameProperty = SimpleStringProperty(this, "name", name)
    var name by nameProperty

    val pathProperty = SimpleStringProperty(this, "path", path)
    var path by pathProperty

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("path", path)
        }
    }
}

class AudioObjectViewModel : ItemViewModel<AudioObjectModel>() {
    val name = bind(AudioObjectModel::nameProperty)
    val path = bind(AudioObjectModel::pathProperty)
}

