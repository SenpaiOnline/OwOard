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

package online.senpai.owoard.helper

import javafx.scene.paint.Color
import kotlin.math.roundToInt

fun Color.toStringAsHex(): String {
    val red: Int = (red * 255.0).roundToInt()
    val green: Int = (green * 255.0).roundToInt()
    val blue: Int = (blue * 255.0).roundToInt()
    val opacity: Int = (opacity * 255.0).roundToInt()
    return String.format("#%02x%02x%02x%02x", red, green, blue, opacity)
}
