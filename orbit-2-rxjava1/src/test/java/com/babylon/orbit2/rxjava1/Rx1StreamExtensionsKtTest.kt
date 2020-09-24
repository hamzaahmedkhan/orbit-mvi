@file:Suppress("DEPRECATION")

package com.babylon.orbit2.rxjava1

import com.appmattus.kotlinfixture.kotlinFixture
import com.babylon.orbit2.Closeable
import com.babylon.orbit2.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class Rx1StreamExtensionsKtTest {

    private val fixture = kotlinFixture()
    private val stream = TestStream<Int>()
    private val observable = stream.asRx1Observable().test()

    @RepeatedTest(10)
    fun `Observable receives posted values`() {
        // given a random set of values
        val values = fixture<List<Int>> {
            repeatCount { random.nextInt(1, 10) }
        }

        // when we post them to the stream
        values.forEach {
            stream.post(it)
        }

        // then the observable receives them
        assertThat(observable.onNextEvents).containsAll(values)
    }

    @Test
    fun `Observable is attached to stream`() {
        // then observable is attached to the stream
        @Suppress("UsePropertyAccessSyntax")
        assertThat(stream.hasObservers()).isTrue()
    }

    @Test
    fun `Unsubscribing disconnects it from the stream`() {
        // when we unsubscribe the observable
        observable.unsubscribe()

        // then the observable is unattached from the stream
        @Suppress("UsePropertyAccessSyntax")
        assertThat(stream.hasObservers()).isFalse()
    }

    class TestStream<T> : Stream<T> {
        private val observers = mutableSetOf<(T) -> Unit>()

        override fun observe(lambda: (T) -> Unit): Closeable {
            observers += lambda
            return object : Closeable {
                override fun close() {
                    observers.remove(lambda)
                }
            }
        }

        fun post(value: T) = observers.forEach { it(value) }

        fun hasObservers() = observers.size > 0
    }
}
