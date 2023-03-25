package com.mmt.composebluetoothchatapp.domain.chat

import com.mmt.composebluetoothchatapp.data.chat.BluetoothMessage

sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    data class Error(val message: String): ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage): ConnectionResult
}