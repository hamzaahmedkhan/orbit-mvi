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

package com.babylon.orbit2.coroutines

import com.appmattus.kotlinfixture.kotlinFixture
import com.babylon.orbit2.ContainerHost
import com.babylon.orbit2.assert
import com.babylon.orbit2.container
import com.babylon.orbit2.syntax.strict.OrbitDslPlugins
import com.babylon.orbit2.syntax.strict.orbit
import com.babylon.orbit2.syntax.strict.reduce
import com.babylon.orbit2.syntax.strict.sideEffect
import com.babylon.orbit2.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CoroutineDslPluginBehaviourTest {
    private val fixture = kotlinFixture()
    private val initialState = fixture<TestState>()

    @BeforeEach
    fun beforeEach() {
        OrbitDslPlugins.reset() // Test for proper registration
    }

    @Test
    fun `suspend transformation maps`() {
        val action = fixture<Int>()
        val middleware = Middleware().test(initialState)

        middleware.suspend(action)

        middleware.assert {
            states(
                { TestState(action + 5) }
            )
        }
    }

    @Test
    fun `flow transformation flatmaps`() {
        val action = fixture<Int>()
        val middleware = Middleware().test(initialState)

        middleware.flow(action)

        middleware.assert {
            states(
                { TestState(action) },
                { TestState(action + 1) },
                { TestState(action + 2) },
                { TestState(action + 3) }
            )
        }
    }

    @Test
    fun `hot flow transformation flatmaps`() {
        val action = fixture<Int>()
        val channel = Channel<Int>(100)
        val middleware = Middleware(channel.consumeAsFlow()).test(initialState = initialState, blocking = false)

        middleware.hotFlow()

        channel.sendBlocking(action)
        channel.sendBlocking(action + 1)
        channel.sendBlocking(action + 2)
        channel.sendBlocking(action + 3)

        middleware.assert {
            postedSideEffects(
                action.toString(),
                (action + 1).toString(),
                (action + 2).toString(),
                (action + 3).toString()
            )
        }
    }

    private data class TestState(val id: Int)

    private class Middleware(val hotFlow: Flow<Int> = emptyFlow()) : ContainerHost<TestState, String> {

        override val container = CoroutineScope(Dispatchers.Unconfined).container<TestState, String>(TestState(42))

        fun suspend(action: Int) = orbit {
            transformSuspend {
                delay(50)
                action + 5
            }
                .reduce {
                    state.copy(id = event)
                }
        }

        fun flow(action: Int) = orbit {
            transformFlow {
                flowOf(action, action + 1, action + 2, action + 3)
                    .onEach { delay(50) }
            }
                .reduce {
                    state.copy(id = event)
                }
        }

        fun hotFlow() = orbit {
            transformFlow {
                hotFlow
            }
                .sideEffect {
                    post(event.toString())
                }
        }
    }
}
