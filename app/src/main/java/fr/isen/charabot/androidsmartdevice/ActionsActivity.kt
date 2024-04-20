package fr.isen.charabot.androidsmartdevice

import android.bluetooth.*
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.unit.sp
import fr.isen.charabot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import java.util.*

class ActionsActivity : ComponentActivity() {
    private lateinit var bluetoothGatt: BluetoothGatt
    private var ledBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private var counterBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private var controlBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private var incrementCount: Int by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDeviceTheme {
                var ledStates by remember { mutableStateOf(List(3) { false }) }
                var isSubscribed by remember { mutableStateOf(false) }

                Column(Modifier.padding(16.dp)) {
                    LedControlSection(ledStates) { index, state ->
                        toggleLed(index, state)
                        ledStates = ledStates.toMutableList().apply { this[index] = !state }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SubscriptionSection(isSubscribed) { subscribe ->
                        subscribeToNotifications(subscribe)
                        isSubscribed = subscribe
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ButtonClickCount(incrementCount)
                }
            }
        }
    }

    private fun toggleLed(index: Int, state: Boolean) {
        if (ledBluetoothGattCharacteristic != null) {
            val value = if (state) 0x00 else (index + 1).toByte()
            ledBluetoothGattCharacteristic?.value = byteArrayOf(value)
            bluetoothGatt.writeCharacteristic(ledBluetoothGattCharacteristic)
        } else {
            Toast.makeText(this, "Caractéristique LED indisponible", Toast.LENGTH_LONG).show()
        }
    }

    private fun subscribeToNotifications(subscribe: Boolean) {
        if (bluetoothGatt != null && counterBluetoothGattCharacteristic != null) {
            val descriptor = counterBluetoothGattCharacteristic?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            descriptor?.value = if (subscribe) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            bluetoothGatt.writeDescriptor(descriptor)
        } else {
            Toast.makeText(this, "BluetoothGatt ou caractéristique indisponible", Toast.LENGTH_LONG).show()
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            val bleServices = gatt?.services
            ledBluetoothGattCharacteristic = bleServices?.get(2)?.characteristics?.get(0)
            counterBluetoothGattCharacteristic = bleServices?.get(2)?.characteristics?.get(1)
            controlBluetoothGattCharacteristic = bleServices?.get(3)?.characteristics?.get(0)

            // Abonnez-vous aux notifications lorsque les services sont découverts
            subscribeToNotifications(true)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            val hex = value.joinToString("") { byte -> "%02x".format(byte)}
            Log.d("hex", hex)
            runOnUiThread {
                if (characteristic.uuid == counterBluetoothGattCharacteristic?.uuid) {
                    // Mise à jour de l'interface avec le nombre de clics
                    incrementCount = hex.toInt()
                } else if (characteristic.uuid == controlBluetoothGattCharacteristic?.uuid) {
                    // Mise à jour de l'interface avec le contrôle
                    // binding.controlCounter.text = hex
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt.close()
    }
}

@Composable
fun SubscriptionSection(isSubscribed: Boolean, onSubscribeChange: (Boolean) -> Unit) {
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
fun ButtonClickCount(incrementCount: Int) {
    Text("Nombre de clics sur le bouton principal : $incrementCount", fontSize = 16.sp)
}
