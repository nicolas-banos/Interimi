package com.interimi.interimi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.interimi.interimi.ui.theme.InterimiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterimiTheme {
                InterimiApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterimiApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interimi", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Texto que describe una funcionalidad
                Text(
                    text = "Tu asistente personal basado en estoicismo.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.estoicismo),
                    contentDescription = "Imagen decorativa",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(16.dp)
                )


                //Lista de datos simulados
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    val sampleData = listOf(
                        "Reflexión 1: Controla lo que puedes",
                        "Motivación 2: Actúa con propósito",
                        "Consejo 3: Haz tu mejor esfuerzo cada día"
                    )
                    items(sampleData) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun InterimiAppPreview() {
    InterimiTheme {
        InterimiApp()
    }
}
