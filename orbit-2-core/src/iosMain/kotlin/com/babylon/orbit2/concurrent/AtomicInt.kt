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

package com.babylon.orbit2.concurrent

import kotlin.native.concurrent.AtomicInt

actual class AtomicInt actual constructor(initial: Int) {
    private val actualAtomic = AtomicInt(initial)

    actual val value: Int
        get() = actualAtomic.value

    actual fun compareAndSet(expected: Int, target: Int): Boolean =
        actualAtomic.compareAndSet(expected, target)

    actual fun decrement(): Unit = actualAtomic.decrement()
}
