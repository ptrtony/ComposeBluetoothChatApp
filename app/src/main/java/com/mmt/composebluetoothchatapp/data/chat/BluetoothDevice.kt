package com.mmt.composebluetoothchatapp.data.chat

typealias BluetoothDeviceDomain = BluetoothDevice
data class BluetoothDevice(
    val name: String?,
    val address: String
)