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

import kotlinx.coroutines.MainCoroutineDispatcher

/**
 * Represents a stream of values.
 *
 * Observing happens on [MainCoroutineDispatcher] if one is installed, otherwise on the default dispatcher.
 *
 * The subscription can be closed using the returned [Closeable]. It is the user's responsibility
 * to manage the lifecycle of the subscription.
 */
@Deprecated("Stream is deprecated and will be removed in Orbit 1.2.0, use stateFlow instead")
interface Stream<T> {

    fun observe(lambda: (T) -> Unit): Closeable
}

interface Closeable {
    fun close()
}
