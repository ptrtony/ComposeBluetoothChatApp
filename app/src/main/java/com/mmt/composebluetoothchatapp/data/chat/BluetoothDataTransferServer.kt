package com.mmt.composebluetoothchatapp.data.chat

import android.bluetooth.BluetoothSocket
import com.mmt.composebluetoothchatapp.domain.chat.ConnectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferServer(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected){
                return@flow
            }
            val buffer = ByteArray(1024)
            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    throw TransferErrorException()
                }
                emit(buffer.decodeToString(endIndex = byteCount).toBluetoothMessage(false))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(byteArray: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(byteArray)
            } catch (e: IOException) {
                return@withContext false
            }
            true
        }
    }
}