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

package online.senpai.owoard

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val gridTile: CssRule by cssclass()
        val idle: CssRule by csspseudoclass()
    }

    init {
        gridTile {
            borderWidth += box(15.px)
        }
        gridTile and idle {
            /*borderColor += box(Color.ORANGE)*/
        }
    }
}
