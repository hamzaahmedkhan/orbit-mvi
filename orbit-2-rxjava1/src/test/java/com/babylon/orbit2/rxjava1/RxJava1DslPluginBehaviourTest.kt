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

package com.babylon.orbit2.rxjava1

import com.appmattus.kotlinfixture.kotlinFixture
import com.babylon.orbit2.ContainerHost
import com.babylon.orbit2.OrbitDslPlugins
import com.babylon.orbit2.assert
import com.babylon.orbit2.container
import com.babylon.orbit2.reduce
import com.babylon.orbit2.sideEffect
import com.babylon.orbit2.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import rx.Completable
import rx.Observable
import rx.Single

internal class RxJava1DslPluginBehaviourTest {
    private val fixture = kotlinFixture()
    private val initialState = fixture<TestState>()

    @BeforeEach
    fun beforeEach() {
        OrbitDslPlugins.reset() // Test for proper registration
    }

    @Test
    fun `single transformation flatmaps`() {
        val action = fixture<Int>()
        val middleware = Middleware().test(initialState)

        middleware.single(action)

        middleware.assert {
            states(
                { TestState(action + 5) }
            )
        }
    }

    @Test
    fun `completable transformation flatmaps`() {
        val action = fixture<Int>()
        val middleware = Middleware().test(initialState)

        middleware.completable(action)

        middleware.assert {
            states(
                { TestState(action) }
            )
        }
    }

    @Test
    fun `observable transformation flatmaps`() {
        val action = fixture<Int>()
        val middleware = Middleware().test(initialState)

        middleware.observable(action)

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

    private class Middleware : ContainerHost<TestState, String> {

        override val container = CoroutineScope(Dispatchers.Unconfined).container<TestState, String>(TestState(42))

        fun single(action: Int) = orbit {
            transformRx1Single {
                Single.just(action + 5)
            }
                .reduce {
                    state.copy(id = event)
                }
        }

        fun completable(action: Int) = orbit {
            transformRx1Completable {
                Completable.complete()
            }
                .reduce {
                    state.copy(id = action)
                }
        }

        fun observable(action: Int) = orbit {
            transformRx1Observable {
                Observable.just(action, action + 1, action + 2, action + 3)
            }
                .sideEffect {
                    post(event.toString())
                }
        }
    }
}
