package com.interimi.interimi

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import com.interimi.interimi.ui.theme.InterimiTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    private val TAG = "LifecycleEvent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterimiTheme {
                InterimiApp()
            }
        }
        Log.d(TAG, "onCreate")

        //Room
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            val userDao = db.userDao()

            //Insertar usuarior
            withContext(Dispatchers.IO) {
                val userId = userDao.insertUser(User(name = "Marcus Aurelius", email = "marcus@stoic.com"))
                Log.d(TAG, "User inserted with ID: $userId")
            }

            //Obtener usuarios
            withContext(Dispatchers.IO) {
                val users = userDao.getAllUsers()
                users.forEach {
                    Log.d(TAG, "User: ${it.name}, Email: ${it.email}")
                }
            }
        }


    }


    //Métodos tarea2
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG, "onTrimMemory - Memory level: $level")
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
                //Texto que describe una funcionalidad
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

