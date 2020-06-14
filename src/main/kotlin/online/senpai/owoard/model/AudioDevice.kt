/*
 * This file is part of the OwOard distribution (https://github.com/aiscy/OwOard).
 * Copyright (c) 2020 Maxim Valeryevich Pavlov.
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
import javax.json.JsonObject

class AudioDevice(
        interfaceName: String,
        deviceId: String,
        deviceDescription: String
) : JsonModel {
    val interfaceNameProperty = SimpleStringProperty(this, "interfaceName", interfaceName)
    var interfaceName: String by interfaceNameProperty
    val deviceIdProperty = SimpleStringProperty(this, "deviceId", deviceId)
    var deviceId: String by deviceIdProperty
    val deviceDescriptionProperty = SimpleStringProperty(this, "deviceDescription", deviceDescription)
    var deviceDescription: String by deviceDescriptionProperty

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("interfaceName", interfaceName)
            add("deviceId", deviceId)
            add("deviceDescription", deviceDescription)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            interfaceName = getString("interfaceName")
            deviceId = getString("deviceId")
            deviceDescription = getString("deviceDescription")
        }
    }

    override fun toString(): String {
        return "AudioDevice(interface=${interfaceName}, id=${deviceId}, description=${deviceDescription})"
    }

    companion object {
        fun fromJson(json: JsonObject): AudioDevice {
            return with(json) {
                AudioDevice(
                        interfaceName = getString("interfaceName"),
                        deviceId = getString("deviceId"),
                        deviceDescription = getString("deviceDescription")
                )
            }
        }
    }
}
