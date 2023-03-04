package com.mmt.composebluetoothchatapp.persentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.composebluetoothchatapp.domain.chat.BluetoothController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
): ViewModel() {

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
}