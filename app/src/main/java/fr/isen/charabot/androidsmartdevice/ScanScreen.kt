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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
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
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clickable { scanInteraction.playAction() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = if (scanInteraction.isScanning) R.drawable.pause else R.drawable.play),
                contentDescription = if (scanInteraction.isScanning) "Pause" else "Lancer le scan",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (scanInteraction.isScanning) "Scan en cours" else "Lancer le scan",
                style = MaterialTheme.typography.headlineMedium// Utilisation d'un texte plus grand
            )
        }

        if (scanInteraction.isScanning) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
        }

        if (scanInteraction.isScanning) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(scanInteraction.devices) { scanResult ->
                    DeviceItem(
                        scanResult = scanResult,
                        onItemClick = { device ->
                            val intent = Intent(context, DeviceActivity::class.java).apply {
                                putExtra("deviceName", device.name ?: "Unknown Device")
                                putExtra("deviceAddress", device.address ?: "Unknown Address")
                                putExtra("deviceRSSI", scanResult.rssi)
                            }
                            context.startActivity(intent)
                        }
                    )
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(scanResult: ScanResult, onItemClick: (BluetoothDevice) -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClick(scanResult.device)
            })
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RSSIIndicator(scanResult.rssi) // Affichage du signal RSSI sous forme de rond de couleur
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "Name: ${scanResult.device.name ?: "Unknown Device"}",
                style = MaterialTheme.typography.labelLarge, // Même taille de police que l'adresse MAC
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Address: ${scanResult.device.address ?: "Unknown Address"}",
                style = MaterialTheme.typography.labelLarge, // Même taille de police que le nom de l'appareil
            )
        }
    }
}

@Composable
fun RSSIIndicator(rssi: Int) {
    // Convertir le signal RSSI en couleur
    val color = when {
        rssi <= -70 -> Color.Red
        rssi <= -50 -> Color.Yellow
        else -> Color.Green
    }

    Box(
        modifier = Modifier
            .size(36.dp) // Taille plus grande pour le rond de couleur du signal
            .background(color, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rssi.toString(), // Afficher le signal RSSI en noir dans le rond de couleur
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge
        )
    }
}


class ScanInteraction(
    var isScanning: Boolean,
    val devices: MutableList<ScanResult>,
    val playAction: () -> Unit,
    val onDeviceClick: (BluetoothDevice) -> Unit
)