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
import online.senpai.owoard.controller.NativeHookController
import online.senpai.owoard.view.MainView
import tornadofx.*
import kotlin.system.exitProcess

class OwoardApp : App(MainView::class, Styles::class) {
    private val audioController: AudioController by inject()
    private val nativeHookController: NativeHookController by inject()

    override fun start(stage: Stage) {
        audioController.initialize()
        nativeHookController.initialize()
        stage.setOnCloseRequest {
            audioController.terminate()
            nativeHookController.terminate()
            Platform.exit()
            exitProcess(0)
        }
        super.start(stage)
    }
}
