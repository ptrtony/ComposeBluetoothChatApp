package com.mmt.composebluetoothchatapp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.composebluetoothchatapp.data.chat.BluetoothMessage
import com.mmt.composebluetoothchatapp.ui.theme.ComposeBluetoothChatAppTheme
import com.mmt.composebluetoothchatapp.ui.theme.OldRose
import com.mmt.composebluetoothchatapp.ui.theme.Vanilla

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (message.isFromLocalUser) 15.dp else 0.dp,
                    topEnd = 15.dp,
                    bottomStart = if (message.isFromLocalUser) 15.dp else 0.dp,
                    bottomEnd = 15.dp
                )
            )
            .background(
                if (message.isFromLocalUser) OldRose else Vanilla
            )
    ) {
        Text(
            text = message.senderMessage,
            fontSize = 10.sp,
            color = Color.Black
        )

        Text(
            text = message.message,
            color = Color.Black,
            modifier = Modifier.widthIn(max = 250.dp)
        )
    }
}



@Composable
@Preview
fun ChatMessagePreview() {
    ComposeBluetoothChatAppTheme() {
        ChatMessage(
            message = BluetoothMessage(
                message = "Hello world!",
                senderMessage = "HUAWEI METADATA",
                isFromLocalUser = true
            )
        )
    }
}