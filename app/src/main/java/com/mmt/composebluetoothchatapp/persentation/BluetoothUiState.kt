package com.mmt.composebluetoothchatapp.persentation

import com.mmt.composebluetoothchatapp.data.chat.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList()
)
