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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.Closeable
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalCoroutinesApi
@Suppress("DEPRECATION")
internal fun <T> Flow<T>.asStream(): Stream<T> {
    return object : Stream<T> {
        override fun observe(lambda: (T) -> Unit): Closeable {

            val job = CoroutineScope(streamCollectionDispatcher).launch {
                this@asStream.collect {
                    lambda(it)
                }
            }

            return Closeable { job.cancel() }
        }
    }
}

private val streamCollectionDispatcher
    get() = try {
        Dispatchers.Main.also {
            it.isDispatchNeeded(EmptyCoroutineContext) // Try to perform an operation on the dispatcher
        }
    } catch (ias: IllegalStateException) {
        Dispatchers.Default
    }
