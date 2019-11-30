package kotlinx.mqtt

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.mqtt.internal.mqttDispatcher

class MqttResult<T> internal constructor(action: suspend () -> T) {

    private val async = GlobalScope.async(mqttDispatcher) { action() }

    override fun toString() = async.toString()

    suspend fun await(): T {
        return async.await()
    }
}