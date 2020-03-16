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

package online.senpai.owoard

import org.jnativehook.NativeInputEvent
import org.jnativehook.keyboard.NativeKeyEvent
import java.util.*
import javax.json.Json
import javax.json.JsonObject

enum class KeyLocation(val code: Int) {
    UNKNOWN(0),
    STANDARD(1),
    LEFT(2),
    RIGHT(3),
    NUMPAD(4)
}

class KeyCombination(val rawCode: Int, val keyCode: Int, val rawModifiers: Int, keyLocation: Int) : Jsonable {
    val keyLocation: KeyLocation = when (keyLocation) {
        1 -> KeyLocation.STANDARD
        2 -> KeyLocation.LEFT
        3 -> KeyLocation.RIGHT
        4 -> KeyLocation.NUMPAD
        else -> KeyLocation.UNKNOWN
    }

    val isControlDown: Boolean by lazy(mode = LazyThreadSafetyMode.NONE) {
        (rawModifiers and NativeInputEvent.CTRL_MASK) != 0
    }
    val isShiftDown: Boolean by lazy(mode = LazyThreadSafetyMode.NONE) {
        (rawModifiers and NativeInputEvent.SHIFT_MASK) != 0
    }
    val isAltDown: Boolean by lazy(mode = LazyThreadSafetyMode.NONE) {
        (rawModifiers and NativeInputEvent.ALT_MASK != 0)
    }
    val isMetaDown: Boolean by lazy(mode = LazyThreadSafetyMode.NONE) {
        (rawModifiers and NativeInputEvent.META_MASK != 0)
    }
    val keyName: String by lazy(mode = LazyThreadSafetyMode.NONE) {
        NativeKeyEvent.getKeyText(keyCode) ?: keyCode.toString(radix = 16)
    }
    val modifiers: String by lazy(mode = LazyThreadSafetyMode.NONE) {
        mutableListOf<String>()
                .apply {
                    if (isControlDown) add("Ctrl")
                    if (isShiftDown) add("Shift")
                    if (isAltDown) add("Alt")
                    if (isMetaDown) add("Meta")
                }
                .joinToString(separator = "+")
    }

    override fun hashCode(): Int {
        return Objects.hash(keyLocation.code, keyCode, rawModifiers)
    }

    override fun toJson(): JsonObject {
        return Json.createObjectBuilder()
                .apply {
                    add("rawCode", rawCode)
                    add("keyCode", keyCode)
                    add("rawModifiers", rawModifiers)
                    add("keyLocation", keyLocation.code)
                }
                .build()
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            this === other -> true
            is KeyCombination ->
                return keyCode == other.keyCode && keyLocation == other.keyLocation && rawModifiers == other.rawModifiers
            else -> false
        }
    }

    override fun toString(): String = "KeyCombination(keyName=$keyName, modifiers=$modifiers, hash=${hashCode()})"

    companion object {
        fun fromJson(json: JsonObject): KeyCombination {
            return KeyCombination(
                    rawCode = json.getInt("rawCode"),
                    keyCode = json.getInt("keyCode"),
                    rawModifiers = json.getInt("rawModifiers"),
                    keyLocation = json.getInt("keyLocation")
            )
        }
    }
}
