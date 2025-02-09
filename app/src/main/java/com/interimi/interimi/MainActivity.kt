package com.interimi.interimi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.interimi.interimi.ui.theme.InterimiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterimiTheme {
                OpenAIScreen(viewModel = UserViewModel()) // Carga solo OpenAI
            }
        }
    }
}

@Composable
fun OpenAIScreen(viewModel: UserViewModel) {
    val response by viewModel.openAIResponse.collectAsState() // Observa la respuesta de OpenAI
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Preguntar a OpenAI", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Escribe tu pregunta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.askOpenAI(userInput) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Respuesta de OpenAI:", fontWeight = FontWeight.Bold)
        Text(
            text = response,
            modifier = Modifier.padding(8.dp)
        )
    }
}
