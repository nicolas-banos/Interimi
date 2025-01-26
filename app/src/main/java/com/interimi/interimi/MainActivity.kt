package com.interimi.interimi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.interimi.interimi.ui.theme.InterimiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Clase principal de la actividad.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterimiTheme {
                TabView() // Configura las pestañas.
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabView() {
    var selectedTab by remember { mutableStateOf(0) } // Pestaña seleccionada.
    val tabs = listOf("SQLite", "Room") // Lista de nombres de pestañas.

    Scaffold(
        topBar = { TopAppBar(title = { Text("Persistencia de Datos") }) },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TabRow(selectedTabIndex = selectedTab) { // Control de las pestañas.
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
                when (selectedTab) { // Muestra la pantalla correspondiente según la pestaña seleccionada.
                    0 -> SQLiteScreen()
                    1 -> RoomScreen()
                }
            }
        }
    )
}

// Pantalla para manejo de SQLite.
@Composable
fun SQLiteScreen() {
    val context = LocalContext.current
    val sqliteHelper = remember { MySQLiteHelper(context) } // Helper de SQLite.
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    var history by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") } // ID del usuario para buscar o editar.
    var users by remember { mutableStateOf(listOf<Map<String, Any>>()) } // Lista de usuarios.
    var singleUser by remember { mutableStateOf<Map<String, Any>?>(null) } // Usuario específico.
    val coroutineScope = rememberCoroutineScope()

    // Valida si el ID proporcionado es válido.
    val isIdValid = userId.toIntOrNull() != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Habilita el scroll vertical.
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Formulario para entrada de datos.
        TextField(value = userId, onValueChange = { userId = it }, label = { Text("ID Usuario") }, modifier = Modifier.fillMaxWidth())
        TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        TextField(value = age, onValueChange = { age = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        TextField(value = goals, onValueChange = { goals = it }, label = { Text("Metas") }, modifier = Modifier.fillMaxWidth())
        TextField(value = history, onValueChange = { history = it }, label = { Text("Historial") }, modifier = Modifier.fillMaxWidth())

        // Botón para insertar un nuevo usuario.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        sqliteHelper.insertUser(name, age.toIntOrNull(), goals, history)
                        users = sqliteHelper.getAllUsers()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Insertar")
            }
        }

        // Botones para editar, borrar y buscar usuarios.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        userId.toIntOrNull()?.let {
                            sqliteHelper.updateUser(it, name, age.toIntOrNull(), goals, history)
                            users = sqliteHelper.getAllUsers()
                        }
                    }
                },
                enabled = isIdValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Editar")
            }
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        userId.toIntOrNull()?.let {
                            sqliteHelper.deleteUserById(it)
                            users = sqliteHelper.getAllUsers()
                        }
                    }
                },
                enabled = isIdValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Borrar")
            }
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        userId.toIntOrNull()?.let {
                            singleUser = sqliteHelper.getUserById(it)
                        }
                    }
                },
                enabled = isIdValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Buscar")
            }
        }

        // Muestra todos los usuarios.
        Spacer(modifier = Modifier.height(16.dp))
        Text("Usuarios:", style = MaterialTheme.typography.titleMedium)
        users.forEach { user ->
            Text("- ID: ${user["id"]}, Nombre: ${user["name"]}, Metas: ${user["goals"]}, Historial: ${user["history"]}")
        }

        // Muestra un único usuario si fue encontrado.
        singleUser?.let { user ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Usuario Encontrado:", style = MaterialTheme.typography.titleMedium)
            Text("- ID: ${user["id"]}, Nombre: ${user["name"]}, Metas: ${user["goals"]}, Historial: ${user["history"]}")
        }
    }
}

// Pantalla para manejo de Room.
@Composable
fun RoomScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) } // Instancia de Room.
    val userDao = db.userDao()
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    var history by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") } // ID del usuario para buscar o editar.
    var users by remember { mutableStateOf(listOf<User>()) } // Lista de usuarios.
    var singleUser by remember { mutableStateOf<User?>(null) } // Usuario específico.
    val coroutineScope = rememberCoroutineScope()

    // Valida si el ID proporcionado es válido.
    val isIdValid = userId.toIntOrNull() != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Habilita el scroll vertical.
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Formulario para entrada de datos.
        TextField(value = userId, onValueChange = { userId = it }, label = { Text("ID Usuario") }, modifier = Modifier.fillMaxWidth())
        TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        TextField(value = age, onValueChange = { age = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        TextField(value = goals, onValueChange = { goals = it }, label = { Text("Metas") }, modifier = Modifier.fillMaxWidth())
        TextField(value = history, onValueChange = { history = it }, label = { Text("Historial") }, modifier = Modifier.fillMaxWidth())

        // Botón para insertar un nuevo usuario.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val user = User(name = name, age = age.toIntOrNull(), goals = goals, history = history)
                        userDao.insertUser(user)
                        users = userDao.getAllUsers()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Insertar")
            }
        }

        // Botones para editar, borrar y buscar usuarios.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        userId.toIntOrNull()?.let {
                            val updatedUser = User(id = it, name = name, age = age.toIntOrNull(), goals = goals, history = history)
                            userDao.updateUser(updatedUser)
                            users = userDao.getAllUsers()
                        }
                    }
                },
                enabled = isIdValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Editar")
            }
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        userId.toIntOrNull()?.let {
                            userDao.deleteUserById(it)
                            users = userDao.getAllUsers()
                        }
                    }
                },
                enabled = isIdValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Borrar")
            }
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        userId.toIntOrNull()?.let {
                            singleUser = userDao.getUserById(it)
                        }
                    }
                },
                enabled = isIdValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Buscar")
            }
        }

        // Muestra todos los usuarios.
        Spacer(modifier = Modifier.height(16.dp))
        Text("Usuarios:", style = MaterialTheme.typography.titleMedium)
        users.forEach { user ->
            Text("- ID: ${user.id}, Nombre: ${user.name}, Metas: ${user.goals}, Historial: ${user.history}")
        }

        // Muestra un único usuario si fue encontrado.
        singleUser?.let { user ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Usuario Encontrado:", style = MaterialTheme.typography.titleMedium)
            Text("- ID: ${user.id}, Nombre: ${user.name}, Metas: ${user.goals}, Historial: ${user.history}")
        }
    }
}
