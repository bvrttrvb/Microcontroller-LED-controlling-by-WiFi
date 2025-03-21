package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                ControlPanel()
            }
        }
    }
}

@Composable
fun ControlPanel() {
    var currentAction by remember { mutableStateOf("Dioda nie świeci") }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(color = Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Aktualna czynność:", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = currentAction)
        Dioda(onActionChange = { action ->
            currentAction = action
            sendCommandToESP(action)  // Wysyłanie komendy
        })
        Spacer(modifier = Modifier.height(9.dp))
    }
}

@Composable
fun Dioda(onActionChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { onActionChange("Dioda włączona") }) {
                Text("Włącz Diodę", fontSize = 10.sp)
            }
            Button(onClick = { onActionChange("Dioda nie świeci") }) {
                Text("Wyłącz Diodę", fontSize = 10.sp)
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { onActionChange("Dioda miga") }) {
                Text("Migaj Diodą", fontSize = 10.sp)
            }
        }
    }
}

fun sendCommandToESP(command: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = when (command) {
                "Dioda włączona" -> "http://192.168.0.106/turn_on"  // Zmień na swoje IP ESP32
                "Dioda nie świeci" -> "http://192.168.0.106/turn_off"
                "Dioda miga" -> "http://192.168.0.106/blink"
                else -> null
            }
            url?.let {
                Log.d("HTTP", "Connecting to $it")
                val connection = URL(it).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("HTTP", "Response Code: $responseCode")

                connection.disconnect()
            }
        } catch (e: Exception) {
            Log.e("HTTP", "Error: $e")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControlPanelPreview() {
    MyApplicationTheme {
        ControlPanel()
    }
}