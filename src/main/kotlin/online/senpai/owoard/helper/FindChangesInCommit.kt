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

import javafx.beans.property.Property
import tornadofx.*

/**
 * @return a [Pair] contains a new and an old property value, but only if the property was changed between commits,
 * otherwise returns null.
 */
@Suppress("UNCHECKED_CAST")
fun <T> List<Commit>.findChanged(ref: Property<T>): Pair<T, T>? {
    val commit: Commit? = find { it.property == ref && it.changed }
    return commit?.let { (it.newValue as T) to (it.oldValue as T) }
}
