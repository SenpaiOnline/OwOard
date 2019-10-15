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

package online.senpai.owoard.playground

import javafx.scene.control.Label
import javafx.scene.control.ListView
import online.senpai.owoard.LinBiolinumK
import online.senpai.owoard.LinBiolinumKView
import tornadofx.*

class FontTest : App(FontTestView::class)

class FontTestView : View("LinBiolinumK Viewer") {
    override val root: ListView<Label> = listview {
        LinBiolinumK.values().forEach {
            items.add(label(text = it.name, graphic = LinBiolinumKView(LinBiolinumK.valueOf(it.name), 3.em)))
        }
        items.add(label(graphic = hbox {
            add(LinBiolinumKView(LinBiolinumK.CTRL, 1.5.em))
            add(LinBiolinumKView(LinBiolinumK.ALT, 1.5.em))
            add(LinBiolinumKView(LinBiolinumK.DEL, 1.5.em))
        }))
    }
}
