package com.mmt.composebluetoothchatapp.data.chat

fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
    val message = substringAfter("#")
    val name = substringBeforeLast("#")
    return BluetoothMessage(message = message, senderMessage = name, isFromLocalUser = isFromLocalUser)
}

fun BluetoothMessage.toByteArray(): ByteArray {
    return "$senderMessage#$message".encodeToByteArray()
}