package fr.isen.charabot.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class DeviceActivity : ComponentActivity() {

    private lateinit var btManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothGatt: BluetoothGatt
    private var isConnected by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceName = intent.getStringExtra("deviceName") ?: "Unknown Device"
        val deviceAddress = intent.getStringExtra("deviceAddress") ?: "Unknown Address"
        val deviceRSSI = intent.getIntExtra("deviceRSSI", 0)

        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = btManager.adapter
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)

        setContent {
            DeviceScreen(deviceName, deviceAddress, deviceRSSI)
        }

        if (!isConnected) {
            connectToDevice()
        }
    }

    private fun connectToDevice() {
        bluetoothGatt = bluetoothDevice.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.i("BluetoothGatt", "Connected to GATT server.")
                        runOnUiThread {
                            isConnected = true
                            val intent = Intent(this@DeviceActivity, ActionsActivity::class.java)
                            startActivity(intent) // Démarrez l'activité après avoir ajouté l'extra
                            finish()
                        }
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.i("BluetoothGatt", "Disconnected from GATT server.")
                        runOnUiThread {
                            Toast.makeText(
                                this@DeviceActivity,
                                "Disconnected from GATT server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }



    @Composable
    fun DeviceScreen(
        deviceName: String,
        deviceAddress: String,
        deviceRSSI: Int,
    ) {
        var connecting by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$deviceName",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$deviceAddress",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$deviceRSSI",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    connecting = true
                    connectToDevice()
                },
                enabled = !connecting
            ) {
                Text(if (connecting) "Connexion en cours" else "Connexion")
            }
        }
    }
}
