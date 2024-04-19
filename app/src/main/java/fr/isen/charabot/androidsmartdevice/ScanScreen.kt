package fr.isen.charabot.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@SuppressLint("MissingPermission")
@Composable
fun ScanScreen(
    scanInteraction: ScanInteraction,
) {
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { scanInteraction.playAction() }, // Correction ici
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Row(
            modifier = Modifier
                .padding(16.dp)
                .clickable { scanInteraction.playAction() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = if (scanInteraction.isScanning) R.drawable.pause else R.drawable.play),
                contentDescription = if (scanInteraction.isScanning) "Pause" else "Lancer le scan",
                modifier = Modifier.size(24.dp) // Ici, on définit la taille de l'icône
            )
            Text(
                text = if (scanInteraction.isScanning) "Scan en cours" else "Lancer le scan",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Barre de progression qui apparaît uniquement lors du scan
        if (scanInteraction.isScanning) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // Reste de l'interface utilisateur pour afficher la liste des appareils
        if (scanInteraction.isScanning) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(scanInteraction.devices) { scanResult ->
                    DeviceItem(
                        scanResult = scanResult,
                        onItemClick = { device ->
                            // Lancer DeviceActivity avec les informations du périphérique Bluetooth sélectionné
                            val intent = Intent(context, DeviceActivity::class.java).apply {
                                putExtra("deviceName", device.name ?: "Unknown Device")
                                putExtra("deviceAddress", device.address ?: "Unknown Address")
                                putExtra("deviceRSSI", scanResult.rssi)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(scanResult: ScanResult, onItemClick: (BluetoothDevice) -> Unit) {
    val context = LocalContext.current
    var connectingToDevice by remember { mutableStateOf(false) } // Ajout de la variable connectingToDevice

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClick(scanResult.device)
                connectingToDevice = true // Indiquer que la connexion est en cours
            })
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Name: ${scanResult.device.name}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Address: ${scanResult.device.address}",
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "RSSI: ${scanResult.rssi}",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}


class ScanInteraction(
    var isScanning: Boolean,
    val devices: MutableList<ScanResult>,
    val playAction: () -> Unit,
    val onDeviceClick: (BluetoothDevice) -> Unit
)