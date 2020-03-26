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

package online.senpai.owoard.playground

import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Orientation
import javafx.scene.layout.VBox
import javafx.stage.Stage
import mu.KLogger
import mu.KotlinLogging
import tornadofx.*
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import java.util.concurrent.atomic.AtomicInteger


private val logger: KLogger = KotlinLogging.logger {}

class VlcjTestApp : App(VlcjTestView::class) {
    val audio by inject<VlcjTestAudio>()

    override fun start(stage: Stage) {
        stage.setOnCloseRequest {
            audio.player.release()
        }
        super.start(stage)
    }
}

class VlcjTestView : View() {
    val audio by inject<VlcjTestAudio>()
    val volume = SimpleIntegerProperty(100)
    val volumeAtomic = AtomicInteger(100)

    init {
        audio.player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun finished(mediaPlayer: MediaPlayer) {

            }

            override fun error(mediaPlayer: MediaPlayer) {

            }

            override fun playing(mediaPlayer: MediaPlayer) {
                /*logger.debug { volumeAtomic }
                mediaPlayer.submit {
                    mediaPlayer.audio().setVolume(volumeAtomic.get())
                }*/
            }

            override fun volumeChanged(mediaPlayer: MediaPlayer, volume: Float) {
                logger.debug { "Volume: $volume" }
            }

            override fun opening(mediaPlayer: MediaPlayer) {
                mediaPlayer.submit {
                    logger.info { mediaPlayer.audio().setVolume(volumeAtomic.get()) }
                }
            }


            /*override fun mediaChanged(mediaPlayer: MediaPlayer, media: MediaRef) {
                mediaPlayer.submit {
                    mediaPlayer.audio().setVolume(volumeAtomic.get())
                }
            }

            override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) {
                if (mediaPlayer.audio().volume() != volumeAtomic.get()) {
                    mediaPlayer.submit {
                        mediaPlayer.audio().setVolume(volumeAtomic.get())
                    }
                }
            }*/


        })
    }

    override val root: VBox = vbox {
        setPrefSize(200.0, 200.0)
        button("Play").action {
            audio.player.media().prepare(resources.url("/508733__djgriffin__cinematic-grand-piano-in-hall.ogg").path)
            logger.debug { "Trying to set volume to ${volumeAtomic.get()}" }
            val result = audio.player.audio().setVolume(volumeAtomic.get())
            logger.debug { "Attempt was $result" }
            audio.player.controls().play()
            audio.player.submit {
                val result1 = audio.player.audio().setVolume(volumeAtomic.get())
                logger.debug { "Attempt was $result1" }
            }
            /*audio.player.media().play(resources.url("/508733__djgriffin__cinematic-grand-piano-in-hall.ogg").path)*/
        }
        slider(min = 0, max = 100, value = 100, orientation = Orientation.HORIZONTAL) {
            valueProperty().onChange {
                volumeAtomic.set(it.toInt())
            }
        }
        checkbox("Mute") {
            selectedProperty().onChange {
                audio.player.audio().mute()
            }
        }
    }
}

class VlcjTestAudio : Controller() {
    val mediaFactory = MediaPlayerFactory("-vvv")
    val player = mediaFactory.mediaPlayers().newEmbeddedMediaPlayer()
}
