package fr.isen.charabot.androidsmartdevice

import android.bluetooth.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.charabot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import java.util.UUID

class ActionsActivity : ComponentActivity() {
    private lateinit var bluetoothGatt: BluetoothGatt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDeviceTheme {
                var ledStates by remember { mutableStateOf(List(3) { false }) }
                var incrementCount by remember { mutableStateOf(0) }
                var isSubscribed by remember { mutableStateOf(false) }

                Column(Modifier.padding(16.dp)) {
                    LedControlSection(ledStates) { index, state ->
                        toggleLed(index, state)
                        ledStates = ledStates.toMutableList().apply { this[index] = !state }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SubscriptionSection(isSubscribed, incrementCount) { subscribe ->
                        subscribeToNotifications(subscribe)
                        isSubscribed = subscribe
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ButtonClickCount(incrementCount)
                }
            }
        }
    }

    private fun toggleLed(index: Int, isOn: Boolean) {
        val serviceUuid = UUID.fromString("your-service-uuid")
        val characteristicUuid = UUID.fromString("your-characteristic-uuid-for-leds")
        val service = bluetoothGatt.getService(serviceUuid)
        val characteristic = service.getCharacteristic(characteristicUuid)
        characteristic.value = byteArrayOf((index + 1).toByte(), if (isOn) 0x00.toByte() else 0x01.toByte())
        bluetoothGatt.writeCharacteristic(characteristic)
    }

    private fun subscribeToNotifications(subscribe: Boolean) {
        val serviceUuid = UUID.fromString("your-service-uuid-for-notifications")
        val characteristicUuid = UUID.fromString("your-characteristic-uuid-for-subscriptions")
        val service = bluetoothGatt.getService(serviceUuid)
        val characteristic = service.getCharacteristic(characteristicUuid)
        bluetoothGatt.setCharacteristicNotification(characteristic, subscribe)
    }
}

@Composable
fun LedControlSection(ledStates: List<Boolean>, onLedClick: (Int, Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        ledStates.forEachIndexed { index, isLedOn ->
            val imageRes = if (isLedOn) R.drawable.ledon else R.drawable.ledoff
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "LED $index",
                modifier = Modifier.size(48.dp).clickable { onLedClick(index, isLedOn) }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun SubscriptionSection(isSubscribed: Boolean, incrementCount: Int, onSubscribeChange: (Boolean) -> Unit) {
    Text(
        text = "Abonnez-vous pour recevoir le nombre d'incrémentations",
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isSubscribed,
            onCheckedChange = onSubscribeChange,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = "Recevoir", fontSize = 14.sp)
    }
    Text("Nombre d'incrémentations : $incrementCount", fontSize = 16.sp)
}

@Composable
fun ButtonClickCount(incrementCount: Int) {
    Text("Nombre de clics sur le bouton principal : $incrementCount", fontSize = 16.sp)
}
