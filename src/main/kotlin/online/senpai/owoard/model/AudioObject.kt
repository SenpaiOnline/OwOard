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
import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import online.senpai.owoard.KeyCombination
import online.senpai.owoard.KeyEventSubscriber
import online.senpai.owoard.controller.KeyEventDispatcher
import online.senpai.owoard.helper.findChanged
import online.senpai.owoard.helper.toStringAsHex
import tornadofx.*
import java.nio.file.Path
import javax.json.JsonObject

class AudioObject( // TODO Should I split it?
        path: Path,
        name: String = path.fileName.toString().substringBeforeLast("."),
        hotkey: KeyCombination? = null,
        backgroundColor: Color = Color.web("#2a2a2a"),
        borderColor: Color = Color.TRANSPARENT,
        volume: Int = 100
) : JsonModel {
    lateinit var subscriber: KeyEventSubscriber // TODO Mediator?

    val nameProperty = SimpleStringProperty(this, "name", name)
    var name: String by nameProperty

    val pathProperty = SimpleObjectProperty<Path>(this, "path", path)
    var path: Path by pathProperty

    val hotkeyProperty = SimpleObjectProperty<KeyCombination>(this, "hotkey", hotkey)
    var hotkey: KeyCombination? by hotkeyProperty

    val backgroundColorProperty = SimpleObjectProperty<Color>(this, "backgroundColor", backgroundColor)
    var backgroundColor: Color by backgroundColorProperty

    val borderColorProperty = SimpleObjectProperty<Color>(this, "borderColor", borderColor)
    var borderColor: Color by borderColorProperty

    val volumeProperty = SimpleIntegerProperty(this, "volume", volume)
    var volume: Int by volumeProperty

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("path", path.toString())
            add("hotkey", hotkey?.toJson())
            add("backgroundColor", backgroundColor.toStringAsHex())
            add("borderColor", borderColor.toStringAsHex())
            add("volume", volume)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            val hotkeyObject: JsonObject? = getJsonObject("hotkey")
            name = getString("name")
            path = get("path") as Path
            hotkey = if (hotkeyObject != null) {
                KeyCombination.fromJson(hotkeyObject)
            } else {
                null
            }
            backgroundColor = Color.web(getString("backgroundColor"))
            borderColor = Color.web(getString("borderColor"))
            volume = getInt("volume")
        }
    }
}

class AudioObjectModel(val audioModel: AudioObject) : ItemViewModel<AudioObject>(audioModel) {
    private val keyDispatcher: KeyEventDispatcher by inject(FX.defaultScope)

    val name: Property<String> = bind(AudioObject::nameProperty)
    val path: Property<Path> = bind(AudioObject::pathProperty)
    val hotkey: Property<KeyCombination?> = bind(AudioObject::hotkeyProperty)
    val backgroundColor: Property<Color> = bind(AudioObject::backgroundColorProperty)
    val borderColor: Property<Color> = bind(AudioObject::borderColorProperty)
    val volume: Property<Number> = bind(AudioObject::volumeProperty)

    override fun onCommit(commits: List<Commit>) {
        commits.findChanged(hotkey)?.let { changeSubscriptionForHotkey(it) }
    }

    private fun changeSubscriptionForHotkey(pair: Pair<KeyCombination?, KeyCombination?>) {
        val (newValue: KeyCombination?, oldValue: KeyCombination?) = pair
        if (newValue != null) {
            if (oldValue != null) {
                keyDispatcher.removeSubscriberByHotkey(oldValue)
            }
            keyDispatcher.registerSubscriberByHotkey(newValue, audioModel.subscriber)
        } else {
            if (oldValue != null) {
                keyDispatcher.removeSubscriberByHotkey(oldValue)
            }
        }
    }
}
