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

import com.toxicbakery.kfinstatemachine.StateMachine
import com.toxicbakery.kfinstatemachine.TransitionCallback
import mu.KLogger
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties

abstract class AbstractTransitionalCallback<S, T : Any>(private val logger: KLogger) : TransitionCallback<S, T> {
    override fun enteringState(
            stateMachine: StateMachine<S, T>,
            currentState: S,
            transition: T,
            targetState: S
    ) {
        logger.debug {
            if (transition::class.isData) {
                val properties: String = transition::class.declaredMemberProperties
                        .filter { it.visibility == KVisibility.PUBLIC }
                        .joinToString { kProperty1: KProperty1<out T, Any?> ->
                            "name=${kProperty1.name}, value=${kProperty1.getter.call(transition).toString()}"
                        }
                "Changing state to $targetState from $currentState because of the event ${transition::class.simpleName}, properties: $properties."
            } else {
                "Changing state to $targetState from $currentState because of ${transition::class.simpleName}."
            }
        }
    }
}
