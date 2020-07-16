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

package online.senpai.owoard.view

import javafx.beans.binding.Bindings
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import online.senpai.owoard.controller.LibvlcController
import online.senpai.owoard.model.AudioDevice
import online.senpai.owoard.model.AudioSettingsModel
import tornadofx.*

class AudioSettingsEditorView : View("Audio Settings") {
    private val libvlcController: LibvlcController by inject(FX.defaultScope)
    private val model: AudioSettingsModel by inject()

    override val root: Form = form {
        maxWidth = 250.0 // TODO
        fieldset("Output audio device") {
            label(audioController.audioOutputDeviceProperty/*model.audioDevice.stringBinding {
                if (it != null) {
                    """
                        ${it.interfaceName}
                        ${it.deviceId}
                        ${it.deviceDescription}
                    """.trimIndent() // TODO Fix
                } else "Default system output device"
            }*/)
            field {
                combobox(values = audioController.outputAudioDevices, property = model.audioDevice)
            }
            field("Volume") {
                slider(range = 0..100, value = audioController.masterVolumeProperty.get().value) {
                    isShowTickMarks = true
                    isShowTickLabels = true
                    model.masterVolume.bindBidirectional(valueProperty())
                }
            }
            buttonbar {
                button("Cancel") {
                    isCancelButton = true
                    action {
                        cancel()
                    }
                }
                button("Save") {
                    isDefaultButton = true
                    enableWhen(model.dirty)
                    action {
                        save()
                    }
                }
            }
        }
    }

    private fun save() {
        model.commit {
            close()
        }
    }

    private fun cancel() {
        model.rollback()
        close()
    }
}
