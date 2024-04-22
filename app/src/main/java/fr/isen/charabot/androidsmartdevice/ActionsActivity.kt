package fr.isen.charabot.androidsmartdevice

import android.bluetooth.*
import android.os.Bundle
import android.widget.Toast
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
import fr.isen.charabot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import java.util.*

class ActionsActivity : ComponentActivity() {
    private lateinit var bluetoothGatt: BluetoothGatt
    private var ledBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private var button1BluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private var button3BluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private var subscribedToNotifications: Boolean by mutableStateOf(false)
    private var incrementCountButton1: Int by mutableStateOf(0)
    private var incrementCountButton3: Int by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDeviceTheme {
                // UI State
                var ledStates by remember { mutableStateOf(List(3) { false }) }

                Column(Modifier.padding(16.dp)) {
                    LedControlSection(ledStates) { index, state ->
                        toggleLed(index, state)
                        ledStates = ledStates.toMutableList().apply { this[index] = !state }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SubscriptionSection(subscribedToNotifications) { subscribed ->
                        onSubscribeChanged(subscribed)
                    }
                }
            }
        }

        // Initialize Bluetooth services and characteristics here
    }

    private fun onSubscribeChanged(subscribed: Boolean) {
        subscribedToNotifications = subscribed
        // Implement logic to subscribe or unsubscribe to characteristics notifications here
    }

    private fun toggleLed(index: Int, state: Boolean) {
        // Logic to turn on/off LEDs using Bluetooth characteristics
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Handle disconnection
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            // Discover services and characteristics here
            val bleServices = gatt?.services

            // LED characteristics
            ledBluetoothGattCharacteristic = bleServices?.get(2)?.characteristics?.get(0)

            button1BluetoothGattCharacteristic = bleServices?.get(3)?.characteristics?.get(1)
            button3BluetoothGattCharacteristic = bleServices?.get(3)?.characteristics?.get(2)


            // Subscribe to notifications for buttons
            subscribeToButtonNotifications(button1BluetoothGattCharacteristic) { incrementCountButton1++ }
            subscribeToButtonNotifications(button3BluetoothGattCharacteristic) { incrementCountButton3++ }

        }



        private fun subscribeToButtonNotifications(characteristic: BluetoothGattCharacteristic?, incrementCount: () -> Unit) {
            if (bluetoothGatt != null && characteristic != null) {
                val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                bluetoothGatt.writeDescriptor(descriptor)
                bluetoothGatt.setCharacteristicNotification(characteristic, true)
            } else {
                Toast.makeText(applicationContext, "BluetoothGatt or characteristic is unavailable", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun SubscriptionSection(subscribed: Boolean, onSubscribeChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = subscribed,
            onCheckedChange = onSubscribeChange
        )
        Text("S'abonner pour recevoir le nombre d'incrémentations")
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
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onLedClick(index, isLedOn) }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
