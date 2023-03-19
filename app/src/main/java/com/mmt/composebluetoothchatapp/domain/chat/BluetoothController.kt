package com.mmt.composebluetoothchatapp.domain.chat

import com.mmt.composebluetoothchatapp.data.chat.BluetoothDevice
import com.mmt.composebluetoothchatapp.data.chat.BluetoothDeviceDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedBluetoothDevices: StateFlow<List<BluetoothDevice>>
    val pairedBluetoothDevices: StateFlow<List<BluetoothDevice>>
    val isConnected: StateFlow<Boolean>
    val errors: SharedFlow<String>
    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult>
    fun closeConnection()

    fun startDiscovery()
    fun stopDiscovery()
    fun release()
}