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

import online.senpai.owoard.event.TilePlayEvent
import tornadofx.*
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import java.io.File

class AudioController : Controller() {
    private val audioComponent: AudioPlayerComponent = AudioPlayerComponent()
//    private var activeTile: Tile? = null

    init {
        /*audioComponent.mediaPlayer().events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer) {
                Platform.runLater {
                    (mediaPlayer.userData() as Tile).borderColor = Color.AQUA
                }
            }

            override fun stopped(mediaPlayer: MediaPlayer) {
                Platform.runLater {
                    (mediaPlayer.userData() as Tile).borderColor = Color.TRANSPARENT
                }
            }

            override fun finished(mediaPlayer: MediaPlayer) {
                Platform.runLater {
                    (mediaPlayer.userData() as Tile).borderColor = Color.TRANSPARENT
                }
            }

            override fun mediaChanged(mediaPlayer: MediaPlayer, media: MediaRef) {
                Platform.runLater {
                    (mediaPlayer.userData() as Tile).borderColor = Color.TRANSPARENT
                }
            }
        })*/

        subscribe { event: TilePlayEvent ->
            play(event.file)
        }
    }

    fun isInitialized() : Boolean {
        return true
//        audioComponent.mediaPlayer().
    }

    fun destroy() {
//        val media = RandomAccessFileMedia(File(""))
//        audioComponent.mediaPlayer().media().prepare(media)
//        audioComponent.mediaPlayer().media().newMediaRef().
        audioComponent.release()
    }

    fun play(file: File) {
//        audioComponent.mediaPlayer().userData(tile)
        audioComponent.mediaPlayer().media().play(file.absolutePath)
    }
}
