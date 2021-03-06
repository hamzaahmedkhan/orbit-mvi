/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.babylon.orbit2

import com.babylon.orbit2.internal.RealContainer
import com.babylon.orbit2.syntax.strict.OrbitDslPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

class TestContainer<STATE : Any, SIDE_EFFECT : Any>(
    initialState: STATE,
    private val isolateFlow: Boolean,
    private val blocking: Boolean
) : RealContainer<STATE, SIDE_EFFECT>(
    initialState = initialState,
    parentScope = CoroutineScope(Dispatchers.Unconfined),
    settings = Container.Settings(
        orbitDispatcher =
        @Suppress("EXPERIMENTAL_API_USAGE") if (blocking) Dispatchers.Unconfined else newSingleThreadContext("orbit"),
        backgroundDispatcher = Dispatchers.Unconfined
    )
) {
    private val dispatched = AtomicBoolean(false)

    override fun orbit(orbitFlow: suspend OrbitDslPlugin.ContainerContext<STATE, SIDE_EFFECT>.() -> Unit) {
        if (!isolateFlow || dispatched.compareAndSet(false, true)) {
            if (blocking) {
                runBlocking {
                    orbitFlow(pluginContext)
                }
            } else {
                super.orbit(orbitFlow)
            }
        }
    }
}
