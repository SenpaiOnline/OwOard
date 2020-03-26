import javafx.stage.Stage
import online.senpai.owoard.OwoardApp
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.*

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

@ExtendWith(ApplicationExtension::class)
class TestOfTest : ApplicationTest() {
    val app = OwoardApp()

    override fun init() {
        val primaryStage: Stage = FxToolkit.registerPrimaryStage()
        FX.registerApplication(FxToolkit.setupApplication {
            app
        }, primaryStage)
    }

    override fun stop() {
        FxToolkit.cleanupStages()
    }

    @Test
    fun test() {
        clickOn("")
    }
}
