package com.mmt.composebluetoothchatapp.persentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.composebluetoothchatapp.data.chat.BluetoothDeviceDomain
import com.mmt.composebluetoothchatapp.domain.chat.BluetoothController
import com.mmt.composebluetoothchatapp.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
): ViewModel() {
    private var deviceConnectionFromJob: Job? = null
    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedBluetoothDevices,
        bluetoothController.pairedBluetoothDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(scannedDevices, pairedDevices)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update {
            it.copy(isConnecting = true)
        }
        deviceConnectionFromJob = bluetoothController.connectToDevice(device)
            .listen()
    }


    fun disconnectFromDevice() {
        deviceConnectionFromJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {
            it.copy(isConnecting = false, isConnected = false)
        }
    }

    fun waitForIncomingConnections() {
        _state.update {
            it.copy(
                isConnecting = true

            )
        }
        deviceConnectionFromJob = bluetoothController.startBluetoothServer()
            .listen()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }
            }
        }.catch { throwable ->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    isConnected = false,
                    isConnecting = false,
                    errorMessage = null
                )
            }
        }.launchIn(viewModelScope)
    }
}