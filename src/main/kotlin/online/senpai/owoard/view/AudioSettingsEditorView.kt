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
    private val outputDevices: ObservableList<AudioDevice> = FXCollections.observableArrayList<AudioDevice>()

    init {
        model.audioInterface.onChange {
            it?.let {
                fillOutputDevicesList(it)
            }
        }
        model.audioInterface.value?.let {
            fillOutputDevicesList(it)
        }
    }

    override val root: Form = form {
        maxWidth = 550.0 // TODO
        prefWidth = 550.0
        fieldset("Output audio device") {
            field("LibVLC device") {
                label(libvlcController.libvlcAudioDevice)
            }
            field("Audio interface") {
                combobox(model.audioInterface, libvlcController.audioInterfaces())
            }
            field("Audio device") {
                combobox(model.audioDevice, outputDevices) {
                    enableWhen(Bindings.isNotNull(model.audioInterface as StringProperty))
                }
            }
        }
        fieldset("Master volume") {
            slider(0..100, model.masterVolume.value) {
                isShowTickMarks = true
                isShowTickLabels = true
                isSnapToTicks = true
                blockIncrement = 2.0
                majorTickUnit = 5.0
                minorTickCount = 4
                model.masterVolume.bindBidirectional(valueProperty())
                tooltip {
                    textProperty().bind(model.masterVolume.stringBinding { it?.toInt().toString() })
                }
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
                action(op = ::save)
            }
        }
    }

    private fun fillOutputDevicesList(interfaceName: String) {
        outputDevices.setAll(libvlcController.interfaceAudioDevices(interfaceName))
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
