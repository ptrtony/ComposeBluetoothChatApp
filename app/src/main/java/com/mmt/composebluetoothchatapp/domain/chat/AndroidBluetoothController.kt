package com.mmt.composebluetoothchatapp.domain.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.mmt.composebluetoothchatapp.data.chat.BluetoothDevice
import com.mmt.composebluetoothchatapp.data.chat.BluetoothDeviceDomain
import com.mmt.composebluetoothchatapp.data.chat.toBluetoothDeviceDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
): BluetoothController{

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    private val _isConnected = MutableStateFlow(false)
    private val _error = MutableSharedFlow<String>()
    private val _fondDevReceiver = FoundDevReceiver { device ->
        val newDevice = device.toBluetoothDeviceDomain()
        _scannedDevices.update { devices ->
            if (newDevice in devices) {
                devices
            } else {
                devices + newDevice
            }
        }
    }
    override val scannedBluetoothDevices: StateFlow<List<BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    override val pairedBluetoothDevices: StateFlow<List<BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()

    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    override val errors: SharedFlow<String>
        get() = _error.asSharedFlow()

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _error.emit("can`t connect to a non-paired")
            }
        }
    }
    init {
        updatePairedDevices()
        context.registerReceiver(bluetoothStateReceiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
            addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED)
        })
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow<ConnectionResult> {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord("chat-server", UUID.fromString(uuid))
            var shouldLoop = true
            while (shouldLoop) {
                try {
                    currentClientSocket = currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                }
                currentClientSocket?.let {
                    currentServerSocket?.close()
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH-CONNECT permission")
            }
            currentClientSocket = bluetoothAdapter?.getRemoteDevice(bluetoothAdapter?.address)
                ?.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
            stopDiscovery()
            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)
                } catch (e : IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        context.registerReceiver(_fondDevReceiver,
        IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND))
        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(_fondDevReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        closeConnection()
    }

    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        bluetoothAdapter?.bondedDevices
            ?.map { device ->
                device.toBluetoothDeviceDomain()
            }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private val uuid = "faefieha2iiu2i10-4o2u-1fjhb"

}