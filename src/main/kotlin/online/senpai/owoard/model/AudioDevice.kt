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

import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import javax.json.JsonObject

private const val INTERFACE_NAME = "interfaceName"
private const val DEVICE_ID = "deviceId"
private const val DEVICE_DESCRIPTION = "deviceDescription"

class AudioDevice(
        interfaceName: String,
        deviceId: String,
        deviceDescription: String
) : JsonModel {
    val interfaceNameProperty = SimpleStringProperty(this, INTERFACE_NAME, interfaceName)
    var interfaceName: String by interfaceNameProperty
    val deviceIdProperty = SimpleStringProperty(this, DEVICE_ID, deviceId)
    var deviceId: String by deviceIdProperty
    val deviceDescriptionProperty = SimpleStringProperty(this, DEVICE_DESCRIPTION, deviceDescription)
    var deviceDescription: String by deviceDescriptionProperty

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add(INTERFACE_NAME, interfaceName)
            add(DEVICE_ID, deviceId)
            add(DEVICE_DESCRIPTION, deviceDescription)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            interfaceName = getString(INTERFACE_NAME)
            deviceId = getString(DEVICE_ID)
            deviceDescription = getString(DEVICE_DESCRIPTION)
        }
    }

    override fun toString(): String {
        return "AudioDevice(interface=${interfaceName}, id=${deviceId}, description=${deviceDescription})"
    }

    companion object {
        fun fromJson(json: JsonObject): AudioDevice {
            return with(json) {
                AudioDevice(
                        interfaceName = getString(INTERFACE_NAME),
                        deviceId = getString(DEVICE_ID),
                        deviceDescription = getString(DEVICE_DESCRIPTION)
                )
            }
        }
    }
}

class AudioDeviceModel : ItemViewModel<AudioDevice>() {
    val interfaceName: Property<String> = bind(AudioDevice::interfaceNameProperty)
    val deviceId: Property<String> = bind(AudioDevice::deviceIdProperty)
    val deviceDescription: Property<String> = bind(AudioDevice::deviceDescriptionProperty)
}
