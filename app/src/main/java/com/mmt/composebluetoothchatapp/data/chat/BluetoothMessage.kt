package com.mmt.composebluetoothchatapp.data.chat

data class BluetoothMessage(
    val message: String,
    val senderMessage: String,
    val isFromLocalUser: Boolean
)