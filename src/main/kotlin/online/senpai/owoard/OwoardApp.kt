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

import javafx.application.Platform
import javafx.stage.Stage
import online.senpai.owoard.controller.AudioController
import online.senpai.owoard.controller.HotkeyController
import online.senpai.owoard.view.MainView
import tornadofx.*
import kotlin.system.exitProcess

class OwoardApp: App(MainView::class) {
    private val audioController: AudioController = AudioController()
    private val hotkeyController: HotkeyController by inject()

    override fun start(stage: Stage) {
        hotkeyController.initialize()
        stage.setOnCloseRequest {
            audioController.destroy()
            hotkeyController.destroy()
            Platform.exit()
            exitProcess(0)
        }
        super.start(stage)
    }
}
