package mbmk.mqtt.internal.connection.packet.sent

import mbmk.mqtt.MqttPacket
import mbmk.mqtt.internal.connection.packet.received.MqttReceivedPacket
import mbmk.mqtt.reverseTypes
import mbmk.mqtt.internal.util.shl
import mbmk.mqtt.internal.util.toEncodedBytes
import kotlin.experimental.and

internal abstract class MqttSentPacket : MqttPacket {

    protected abstract val fixedHeader: Byte

    protected abstract val variableHeader: List<Byte>

    protected abstract val payload: List<Byte>

    private val remainingLength: List<Byte> by lazy {
        (variableHeader.size + payload.size).toEncodedBytes()
    }

    private val packetType: Byte by lazy {
        reverseTypes[this::class] ?: throw IllegalStateException("Class not found.")
    }

    fun pack(): List<Byte> {
        val bytes = mutableListOf(((packetType shl 4) + (fixedHeader and 0b00001111)).toByte())
        bytes.addAll(remainingLength)
        bytes.addAll(variableHeader)
        bytes.addAll(payload)
        return bytes
    }

    abstract fun isResponse(receivedPacket: MqttReceivedPacket): Boolean
}