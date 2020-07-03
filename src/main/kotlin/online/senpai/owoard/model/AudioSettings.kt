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
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import online.senpai.owoard.controller.AudioController
import tornadofx.*
import javax.json.JsonObject

private const val AUDIO_DEVICE = "audioDevice"
private const val MASTER_VOLUME = "masterVolume"

class AudioSettings(
        audioDevice: AudioDevice? = null,
        masterVolume: Int = 100
) : JsonModel {
    val audioDeviceProperty = SimpleObjectProperty<AudioDevice>(this, AUDIO_DEVICE, audioDevice)
    var audioDevice: AudioDevice? by audioDeviceProperty
    val masterVolumeProperty = SimpleIntegerProperty(this, MASTER_VOLUME, masterVolume)
    var masterVolume: Int by masterVolumeProperty

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add(AUDIO_DEVICE, audioDevice)
            add(MASTER_VOLUME, masterVolume)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            audioDevice = AudioDevice.fromJson(this)
            masterVolume = int(MASTER_VOLUME) ?: 100
        }
    }
}

class AudioSettingsModel : ItemViewModel<AudioSettings>() {
    private val audioController: AudioController by inject(FX.defaultScope)

    val audioDevice: Property<AudioDevice?> = bind(AudioSettings::audioDeviceProperty)
    val masterVolume: Property<Number> = bind(AudioSettings::masterVolumeProperty)

    private fun setMasterVolume(pair: Pair<Number, Number>) {
        setMasterVolume(pair.first.toInt())
    }

    private fun setMasterVolume(volume: Int) {
        audioController.setMasterVolume(volume)
    }

    private fun setAudioOutputDevice(pair: Pair<AudioDevice?, AudioDevice?>) {
        val device: AudioDevice = pair.first ?: return
        setAudioOutputDevice(device)
    }

    private fun setAudioOutputDevice(device: AudioDevice) {
        audioController.setAudioOutputDevice(device)
    }

    /*override fun onCommit(commits: List<Commit>) {
        commits.findChanged(audioDevice)?.let { setAudioOutputDevice(it) }
        commits.findChanged(masterVolume)?.let { setMasterVolume(it) }
    }*/

    override fun onCommit() {
        audioDevice.value?.let { setAudioOutputDevice(it) }
        setMasterVolume(masterVolume.value.toInt())
    }
}
