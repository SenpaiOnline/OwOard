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

import de.jensd.fx.glyphs.GlyphIcon
import tornadofx.*
import java.io.IOException

class LinBiolinumKView(
        icon: LinBiolinumK,
        iconSize: Dimension<Dimension.LinearUnits> = 1.em
) : GlyphIcon<LinBiolinumK>(LinBiolinumK::class.java) {

    init {
        setIcon(icon)
        style = "-fx-font-family: ${icon.fontFamily()}; -fx-font-size: $iconSize;"
    }

    override fun getDefaultGlyph(): LinBiolinumK {
        return LinBiolinumK.ENTER
    }

    companion object {
        init {
            try {
                loadFont("font/LinBiolinum_Kah.ttf", 10.0)
            } catch (ex: IOException) {
                throw IllegalStateException("Unable to load font LinBiolinum")
            }
        }
    }
}
