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

package online.senpai.owoard.controller

import com.toxicbakery.kfinstatemachine.StateMachine
import javafx.application.Platform
import javafx.beans.property.ReadOnlyFloatProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import mu.KLogger
import mu.KotlinLogging
import online.senpai.owoard.AbstractTransitionalCallback
import online.senpai.owoard.event.AudioPaused
import online.senpai.owoard.event.AudioPlaying
import online.senpai.owoard.event.AudioPositionChanged
import online.senpai.owoard.event.AudioStopped
import online.senpai.owoard.model.AudioDevice
import tornadofx.*
import uk.co.caprica.vlcj.factory.AudioOutput
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import java.lang.ref.WeakReference
import online.senpai.owoard.controller.LibvlcControllerEvent as Event
import online.senpai.owoard.controller.LibvlcControllerState as State

private val logger: KLogger = KotlinLogging.logger {}

class LibvlcController : Controller() {
    private lateinit var mediaPlayerFactory: MediaPlayerFactory
    private lateinit var mediaPlayer: MediaPlayer
    private val stateMachine = LibvlcControllerStateMachine()
    private val mediaPlayerEventListeners: MutableList<WeakReference<MediaPlayerEventAdapter>> = mutableListOf()
    private val _libvlcAudioDevice: ReadOnlyObjectWrapper<SimpleStringProperty> =
            ReadOnlyObjectWrapper(SimpleStringProperty())
    private val _libvlcVolume: ReadOnlyObjectWrapper<SimpleFloatProperty> =
            ReadOnlyObjectWrapper(SimpleFloatProperty())
    val libvlcAudioDevice: ReadOnlyStringProperty by _libvlcAudioDevice.readOnlyProperty
    val libvlcVolume: ReadOnlyFloatProperty by _libvlcVolume.readOnlyProperty // TODO check

    init {
        stateMachine.registerCallback(object : AbstractTransitionalCallback<State, Event>(logger) {
            override fun enteredState(
                    stateMachine: StateMachine<State, Event>,
                    previousState: State,
                    transition: Event,
                    currentState: State
            ): Unit = when (transition) {
                is Event.Initialize -> onInitialize()
                is Event.Terminate -> onTerminate()
            }
        })
    }

    private fun onTerminate() {
        logger.debug { "Current event listeners: ${mediaPlayerEventListeners.joinToString { it.get().toString() }}" }
        mediaPlayerEventListeners.forEach { mediaPlayer.events().removeMediaPlayerEventListener(it.get()) }
        mediaPlayer.release()
        mediaPlayerFactory.release()
    }

    private fun onInitialize() {
        mediaPlayerFactory = MediaPlayerFactory()
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer()
        mediaPlayer.events().addMediaPlayerEventListener(MediaPlayerEventHandler())
        playAudioStub()
    }

    private fun playAudioStub() {
        val stubPath: String = resources.url("/silence.ogg").path
        if (stubPath.isEmpty()) {
            throw IllegalStateException("Cannot find the audio stub file!")
        }
        mediaPlayer.media().play(stubPath)
    }

    fun initialize() {
        stateMachine.transition(Event.Initialize)
    }

    fun terminate() {
        stateMachine.transition(Event.Terminate)
    }

    fun addMediaPlayerEventListener(listener: MediaPlayerEventAdapter) {
        mediaPlayerEventListeners.add(WeakReference(listener))
        mediaPlayer.events().addMediaPlayerEventListener(listener)
    }

    fun interfaceAudioDevices(interfaceName: String): ObservableList<AudioDevice> {
        val out: AudioOutput = mediaPlayerFactory
                .audio()
                .audioOutputs()
                .find { it.name == interfaceName }
                ?: throw IllegalArgumentException("Cannot find an interface with the given name!")
        return out
                .map { AudioDevice(deviceId = it.deviceId, deviceDescription = it.longName) }
                .toObservable()
    }

    fun audioInterfaces(): ObservableList<String> {
        return mediaPlayerFactory
                .audio()
                .audioOutputs()
                .filter { it.devices.size > 0 }
                .distinctBy { it.name }
                .map { it.name }
                .toObservable()
    }

    fun play(path: String, volume: Int) {
        logger.debug { "Volume: $volume" }
        // https://stackoverflow.com/questions/62307382/vlcj-how-to-change-the-volume-of-audio-before-playing-it
        mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun mediaPlayerReady(mediaPlayer: MediaPlayer) {
                mediaPlayer.submit {
                    mediaPlayer.audio().setVolume(volume)
                }
            }

            override fun stopped(mediaPlayer: MediaPlayer) {
                mediaPlayer.submit {
                    mediaPlayer.audio().setVolume(volume)
                    mediaPlayer.events().removeMediaPlayerEventListener(this)
                }
                mediaPlayer.submit {
                    mediaPlayer.media().play(path)
                }
            }
        })
        playAudioStub()
        // TODO requires testing on Windows
    }

    fun setAudioOutputDevice(audioInterface: String?, audioDevice: AudioDevice?) {
        logger.debug { "Changing an audio interface to $audioInterface and a device to $audioDevice" }
        mediaPlayer.submit {
            mediaPlayer.audio().setOutputDevice(audioInterface, audioDevice?.deviceId)
        }
    }

    private inner class MediaPlayerEventHandler : MediaPlayerEventAdapter() {
        override fun audioDeviceChanged(mediaPlayer: MediaPlayer, audioDevice: String?) {
            Platform.runLater {
                _libvlcAudioDevice.get().value = audioDevice
            }
        }

        override fun volumeChanged(mediaPlayer: MediaPlayer, volume: Float) {
            Platform.runLater {
                _libvlcVolume.get().value = volume
            }
        }

        override fun positionChanged(mediaPlayer: MediaPlayer, newPosition: Float) {
            // TODO stub
            Platform.runLater { // TODO Is it necessary? Or it can be fired from libvlc thread?
                fire(AudioPositionChanged(newPosition))
            }
        }

        override fun playing(mediaPlayer: MediaPlayer) {
            // TODO stub
            Platform.runLater {
                fire(AudioPlaying())
            }
        }

        override fun stopped(mediaPlayer: MediaPlayer) {
            // TODO stub
            Platform.runLater {
                fire(AudioStopped())
            }
        }

        override fun paused(mediaPlayer: MediaPlayer) {
            // TODO stub
            Platform.runLater {
                fire(AudioPaused())
            }
        }
    }
}

private enum class LibvlcControllerState {
    Terminated,
    Initialized,
}

private sealed class LibvlcControllerEvent {
    object Initialize : Event()
    object Terminate : Event()
}

private class LibvlcControllerStateMachine : StateMachine<State, Event>(
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
        )
)
