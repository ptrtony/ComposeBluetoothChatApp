@file:OptIn(ExperimentalComposeUiApi::class)

package com.mmt.composebluetoothchatapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.mmt.composebluetoothchatapp.persentation.BluetoothUiState

@Composable
fun ChatScreen(
    state:BluetoothUiState,
    onDisconnect:() -> Unit,
    onSendMessage:(String) -> Unit
) {
    val message = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(16.dp)
        ) {
            Text(
                text = "Messages",
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                onDisconnect()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect"
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)

        ) {
            items(state.messages.size) { index ->
                val pos = index - 1
                val message = state.messages[pos]
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChatMessage(
                        message = message,
                        modifier = Modifier.align(
                            if (message.isFromLocalUser) Alignment.End else Alignment.Start
                        )
                    )
                }
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextField(
                value = message.value, onValueChange = {
                    message.value = it
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "message")
                }
            )
            IconButton(onClick = {
                onSendMessage(message.value)
                keyboardController?.hide()
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send message")
            }
        }

    }
}