package fr.isen.charabot.androidsmartdevice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Modifier la disposition verticale à Centré
            ) {
                // Titre de l'application (plus gros, centré et bleu clair)
                Text(
                    text = "Bienvenue dans votre application Smart Device",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Blue
                )
                Text(
                    text = "Pour démarrer vos interactions avec les appareils BLE environnants cliquer sur commencer",
                    modifier = Modifier.padding(vertical = 6.dp),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Blue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.logoble),
                    contentDescription = "Bluetooth Icon",
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val intent = Intent(this@MainActivity, ScanActivity::class.java)
                        startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(Color.Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "COMMENCER", color = Color.White)
                }
            }
        }
    }
}
