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

package online.senpai.owoard.controller

import com.toxicbakery.kfinstatemachine.StateMachine
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.AbstractTransitionalCallback
import online.senpai.owoard.event.AudioPlaying
import online.senpai.owoard.event.AudioStopped
import online.senpai.owoard.model.AudioObject
import online.senpai.owoard.model.AudioSettings
import online.senpai.owoard.model.AudioSettingsModel
import online.senpai.owoard.view.AudioSettingsEditorView
import tornadofx.*
import online.senpai.owoard.controller.AudioControllerEvent as Event
import online.senpai.owoard.controller.AudioControllerState as State

private val logger: KLogger = KotlinLogging.logger {}

class AudioController : Controller() {
    private val stateMachine = AudioControllerStateMachine()
    private val _outputAudioDevices: ReadOnlyListWrapper<AudioDevice> =
            ReadOnlyListWrapper(FXCollections.observableArrayList())
    private val _masterVolumeProperty: ReadOnlyObjectWrapper<SimpleIntegerProperty> =
            ReadOnlyObjectWrapper(SimpleIntegerProperty(100))
    private val _audioOutputDeviceProperty: ReadOnlyObjectWrapper<SimpleStringProperty> =
            ReadOnlyObjectWrapper(SimpleStringProperty(null))
    private val mediaPlayerEventHandler: MediaPlayerEventAdapter = MediaPlayerEventHandler()
    val audioOutputDeviceProperty: ReadOnlyObjectProperty<SimpleStringProperty> = _audioOutputDeviceProperty.readOnlyProperty
    val masterVolumeProperty: ReadOnlyObjectProperty<SimpleIntegerProperty> = _masterVolumeProperty.readOnlyProperty
    val outputAudioDevices: ReadOnlyListProperty<AudioDevice> = _outputAudioDevices.readOnlyProperty

    init {
        stateMachine.registerCallback(object : AbstractTransitionalCallback<State, Event>(logger) {
            override fun enteredState(
                    stateMachine: StateMachine<State, Event>,
                    previousState: State,
                    transition: Event,
                    currentState: State
            ): Unit = when (transition) {
                is Event.Play -> onPlaying()
                is Event.Stop -> onIdling()
            }
        })
        subscribe<AudioPlaying> { hasStartedPlaying() }
        subscribe<AudioStopped> { hasStoppedPlaying() }
    }

    private fun hasStartedPlaying() {
        stateMachine.transition(Event.Play)
    }

    private fun hasStoppedPlaying() {
        stateMachine.transition(Event.Stop)
    }

    private fun onPlaying() {

    }

    private fun onIdling() {

    }

    fun openSettingsEditor() {
        val editorScope = Scope()
        setInScope(audioSettingsModel, editorScope)
        find<AudioSettingsEditorView>(editorScope).openModal(block = true, resizable = false)
    }

    fun play(audio: AudioObject) {
        play(audio.path.toString(), audio.volume)
    }

    fun play(path: String, volume: Int) {
        val relativeDelta: Int = audioSettings.masterVolume * 100 / volume
        libvlcController.play(path, relativeDelta)
    }
}

private enum class AudioControllerState {
    Idling,
    Playing
}

private sealed class AudioControllerEvent {
    object Play : Event()
    object Stop : Event()
}

private class AudioControllerStateMachine : StateMachine<State, Event>(
        State.Idling,
        transition(
                oldState = State.Idling,
                transition = Event.Play::class,
                newState = State.Playing
        ),
        transition(
                oldState = State.Playing,
                transition = Event.Stop::class,
                newState = State.Idling
        ),
        transition(
                oldState = State.Playing,
                transition = Event.Play::class,
                newState = State.Playing
        )
)
