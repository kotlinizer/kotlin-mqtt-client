package com.github.kotlinizer.mqtt.client

import com.github.kotlinizer.mqtt.Process
import com.github.kotlinizer.mqtt.TmpFile
import com.github.kotlinizer.mqtt.blockThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

/**
 * Mosquitto must be installed on device.
 */
fun withBroker(username: Boolean = false, port: Int = 1883, block: suspend CoroutineScope.() -> Unit) {
    val file = TmpFile()
    var passwordFile: TmpFile? = null
    val process = Process(listOf("mosquitto", "-v", "-c", file.path))
    val userPwConfig: String
    if (username) {
        passwordFile = TmpFile()
        userPwConfig = "allow_anonymous false\n password_file ${passwordFile.path}"
    } else {
        userPwConfig = "allow_anonymous true"
    }
    try {
        passwordFile?.write(userPassword)
        file.write(
            """
                port $port
                $userPwConfig
            """.trimIndent()
        )
        process.start()
        blockThread {
            block(this)
        }
    } finally {
        process.stop()
        file.delete()
        passwordFile?.delete()
        blockThread {
            delay(100)
        }
    }
}

/**
 * Username: "user" password: "test"
 */
private val userPassword = "user:$6\$lNTBFqv33EiQiuVg\$oIMD9hyNhpcP65nnMB7CVDdeVZClZs7zJ" +
        "aoEM9VHQhEQCnhE54OTMXHQ/nVCnLUh6u4IdPty7ah6kwCoYsWfng=="
