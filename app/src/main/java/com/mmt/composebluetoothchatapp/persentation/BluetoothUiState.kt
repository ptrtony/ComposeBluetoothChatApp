package com.mmt.composebluetoothchatapp.persentation

import com.mmt.composebluetoothchatapp.data.chat.BluetoothDevice
import com.mmt.composebluetoothchatapp.data.chat.BluetoothMessage

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList()
)
