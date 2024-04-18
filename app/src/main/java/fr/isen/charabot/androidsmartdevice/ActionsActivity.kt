package fr.isen.charabot.androidsmartdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.charabot.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ActionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var ledStatus by remember { mutableStateOf(BooleanArray(3) { false }) }
    var clickCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Affichage des différentes LED", style = MaterialTheme.typography.headlineMedium)

        // Display LEDs
        LedIndicator(ledStatus)

        // Click counter button
        Button(onClick = { clickCount++ }) {
            Text("Cliquez ici")
        }

        // Display click count
        Text("Nombre de clics : $clickCount")

        // Subscription for notifications (Placeholder)
        Button(onClick = { /* TODO: Add subscription logic */ }) {
            Text("Abonnez-vous pour recevoir les notifications")
        }
    }
}

@Composable
fun LedIndicator(status: BooleanArray) {
    Row {
        status.forEach { isOn ->
            Image(
                painter = painterResource(id = if (isOn) R.drawable.ledon else R.drawable.ledoff),
                contentDescription = "LED",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidSmartDeviceTheme {
        MainScreen()
    }
}
