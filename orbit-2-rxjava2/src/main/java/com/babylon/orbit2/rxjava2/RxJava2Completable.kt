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

package com.babylon.orbit2.rxjava2

import com.babylon.orbit2.syntax.strict.Builder
import com.babylon.orbit2.syntax.Operator
import com.babylon.orbit2.syntax.Orbit2Dsl
import com.babylon.orbit2.syntax.strict.OrbitDslPlugins
import com.babylon.orbit2.syntax.strict.VolatileContext
import io.reactivex.Completable

internal class RxJava2Completable<S : Any, E : Any>(
    override val registerIdling: Boolean,
    val block: VolatileContext<S, E>.() -> Completable
) : Operator<S, E>

/**
 * The maybe transformer flat maps incoming [VolatileContext] for every event into a [Completable] of
 * another type.
 *
 * The transformer executes on an `IO` dispatcher by default.
 *
 * @param registerIdling When true tracks the block's idling state, default: true
 * @param block the lambda returning a new [Completable] given the current state and event
 */
@Orbit2Dsl
fun <S : Any, SE : Any, E : Any> Builder<S, SE, E>.transformRx2Completable(
    registerIdling: Boolean = true,
    block: VolatileContext<S, E>.() -> Completable
): Builder<S, SE, E> {
    OrbitDslPlugins.register(RxJava2DslPlugin)
    return Builder(stack + RxJava2Completable(registerIdling, block))
}
