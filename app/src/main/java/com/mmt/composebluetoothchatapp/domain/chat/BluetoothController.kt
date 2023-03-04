package com.mmt.composebluetoothchatapp.domain.chat

import com.mmt.composebluetoothchatapp.data.chat.BluetoothDevice
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedBluetoothDevices: StateFlow<List<BluetoothDevice>>
    val pairedBluetoothDevices: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun release()
}