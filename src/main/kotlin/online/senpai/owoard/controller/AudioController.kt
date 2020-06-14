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
import javafx.application.Platform
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.AbstractTransitionalCallback
import online.senpai.owoard.Playable
import online.senpai.owoard.model.AudioDevice
import tornadofx.*
import uk.co.caprica.vlcj.factory.AudioOutput
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import online.senpai.owoard.controller.AudioControllerEvent as Event
import online.senpai.owoard.controller.AudioControllerState as State
import uk.co.caprica.vlcj.player.base.AudioDevice as VlcjAudioDevice

private val logger: KLogger = KotlinLogging.logger {}

class AudioController : Controller() {
    private lateinit var mediaPlayerFactory: MediaPlayerFactory
    private lateinit var mediaPlayer: MediaPlayer
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
            ) {
                when (transition) {
                    is Event.Initialize -> onInitialize()
                    is Event.Terminate -> onTerminate()
                    is Event.Play -> onPlay(transition.playable, transition.volume)
                    is Event.Stop -> onStop(transition.playable)
                }
            }
        })
    }

    private fun setupMediaPlayerEvents() {
        mediaPlayer.events().addMediaPlayerEventListener(mediaPlayerEventHandler)
    }

    private fun getListOfAudioOutputDevices(): List<AudioDevice> {
        val list: MutableList<AudioDevice> = mutableListOf()
        mediaPlayerFactory.audio().audioOutputs().forEach { audioOutput: AudioOutput ->
            audioOutput.devices.forEach { audioDevice: VlcjAudioDevice ->
                list.add(AudioDevice(
                        interfaceName = audioOutput.name,
                        deviceId = audioDevice.deviceId,
                        deviceDescription = audioDevice.longName
                ))
            }
        }
        return list
    }

    private fun onTerminate() {
        mediaPlayer.events().removeMediaPlayerEventListener(mediaPlayerEventHandler)
        mediaPlayer.release()
        mediaPlayerFactory.release()
        _outputAudioDevices.clear()
    }

    private fun onInitialize() {
        mediaPlayerFactory = MediaPlayerFactory()
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer()
        _outputAudioDevices.setAll(getListOfAudioOutputDevices())
        setupMediaPlayerEvents()
        stateMachine.transition(Event.BeReady)
    }

    private fun onPlay(playable: Playable, volume: Int) {
        /*mediaPlayer.media().prepare(playable.source)*/
        /*if (nextMasterVolume != null) {
            nextMasterVolume?.let { masterVolume: Int ->
                val relativeDelta: Int = volume * 100 / masterVolume
                changeVolume(relativeDelta)
            }
            nextMasterVolume = null
        } else {
            changeVolume(volume)
        }*/
        /*mediaPlayer.controls().start()*/
        mediaPlayer.media().play(playable.source)
        /*val masterVolume: Int = _masterVolumeProperty.get().value
        if (mediaPlayer.audio().volume() != masterVolume) {
            changeVolume(masterVolume)
        }*/
    }

    private fun onStop(playable: Playable?) {
        mediaPlayer.controls().stop()
        /*mediaPlayer.media().*/ // TODO release the current media
    }

    private fun changeVolume(volume: Int) {
        logger.debug { "Changing audio volume from ${mediaPlayer.audio().volume()} to $volume" }
        mediaPlayer.audio().setVolume(volume)
    }

    fun setMasterVolume(volume: Int) {
        when (stateMachine.state) {
            State.Playing -> _masterVolumeProperty.get().value = volume // TODO Custom tile volume
            State.Ready -> _masterVolumeProperty.get().value = volume
            else -> logger.error { "Inappropriate state for changing volume! Current state: ${stateMachine.state}" }
        }
    }

    fun setAudioOutputDevice(audioDevice: AudioDevice) {
        logger.debug { "Changing audio device to $audioDevice" }
        mediaPlayer.audio().setOutputDevice(audioDevice.interfaceName, audioDevice.deviceId)
    }

    fun play(playable: Playable, volume: Int = 100) {
        stateMachine.transition(Event.Play(playable, volume))
    }

    fun terminate() {
        stateMachine.transition(Event.Terminate)
    }

    fun initialize() {
        stateMachine.transition(Event.Initialize)
    }

    private inner class MediaPlayerEventHandler : MediaPlayerEventAdapter() {
        override fun playing(mediaPlayer: MediaPlayer) {
            Platform.runLater {
                if (mediaPlayer.audio().volume() == -1) mediaPlayer.audio().setVolume(0)
                logger.debug { mediaPlayer.audio().volume() }
                val masterVolume: Int = _masterVolumeProperty.get().value
                if (mediaPlayer.audio().volume() != masterVolume) {
                    changeVolume(masterVolume)
                }
            }
        }

        override fun finished(mediaPlayer: MediaPlayer) {
            Platform.runLater {
                stateMachine.transition(Event.BeReady)
            }
        }

        override fun audioDeviceChanged(mediaPlayer: MediaPlayer, audioDevice: String?) {
            Platform.runLater {
                _audioOutputDeviceProperty.get().value = audioDevice
            }
        }
    }
}

private enum class AudioControllerState {
    Terminated,
    Initialized,
    Ready,
    Playing
}

private sealed class AudioControllerEvent {
    object Initialize : Event()
    object Terminate : Event()
    object BeReady : Event()
    data class Play(val playable: Playable, val volume: Int) : Event()
    data class Stop(val playable: Playable?) : Event()
}

private class AudioControllerStateMachine : StateMachine<State, Event>(
        State.Terminated,
        transition(
                oldState = State.Terminated,
                transition = Event.Initialize::class,
                newState = State.Initialized
        ),
        transition(
                oldState = State.Initialized,
                transition = Event.Terminate::class,
                newState = State.Terminated
        ),
        transition(
                oldState = State.Initialized,
                transition = Event.BeReady::class,
                newState = State.Ready
        ),
        transition(
                oldState = State.Ready,
                transition = Event.Play::class,
                newState = State.Playing
        ),
        transition(
                oldState = State.Playing,
                transition = Event.Stop::class,
                newState = State.Ready
        ),
        transition(
                oldState = State.Playing,
                transition = Event.BeReady::class,
                newState = State.Ready
        ),
        transition(
                oldState = State.Playing,
                transition = Event.Play::class,
                newState = State.Playing
        ),
        transition(
                oldState = State.Playing,
                transition = Event.Terminate::class,
                newState = State.Terminated
        ),
        transition(
                oldState = State.Ready,
                transition = Event.Terminate::class,
                newState = State.Terminated
        )
)
