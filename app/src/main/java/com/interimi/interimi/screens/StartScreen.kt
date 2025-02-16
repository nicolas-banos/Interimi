package com.interimi.interimi.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.interimi.interimi.R
import com.interimi.interimi.ui.theme.InterimiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterimiTheme {
                StartScreen()
            }
        }
    }
}

@Composable
fun StartScreen(navigateToMain: () -> Unit) {

    val fraseBienvenida = "BIENVENIDO A INTERIMI"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {

        Image(
            painter = painterResource(id = R.drawable.startscreen),
            contentDescription = "Fondo de pantalla romano",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )


        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .size(120.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.large)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de Interimi",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(10.dp)
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Text(
                text = fraseBienvenida,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = navigateToMain,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Text("ENTRAR")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
