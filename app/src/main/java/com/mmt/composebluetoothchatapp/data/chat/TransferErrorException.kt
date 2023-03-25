package com.mmt.composebluetoothchatapp.data.chat

import java.io.IOException

class TransferErrorException: IOException("Transferring incoming data failed") {
}