package com.interimi.interimi

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.interimi.interimi.ui.theme.InterimiTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.Period

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterimiTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Consejos", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Metas", modifier = Modifier.padding(16.dp))
            }
        }

        when (selectedTab) {
            0 -> OpenAIScreen()
            1 -> UserScreen()
        }
    }
}

// CONSEJOS
@Composable
fun OpenAIScreen(viewModel: OpenAIViewModel = hiltViewModel()) {
    val response by viewModel.openAIResponse.collectAsState()
    var userInput by remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Escribe tu pregunta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.askOpenAI(userInput,1)
                userInput = "" // Limpiar el input después de enviar
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pedir consejo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Consejo:", style = MaterialTheme.typography.titleMedium)
            Text(text = response, modifier = Modifier.padding(8.dp))
        }
    }
}



// METAS

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val user by viewModel.userState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getUserById(1) // Se asume que solo hay un usuario por dispositivo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        if (user == null) {
            CreateUserForm { newUser ->
                viewModel.insertUser(newUser) {
                    viewModel.getUserById(1) // Recargar después de la creación
                }
            }
        } else {
            EditGoalsScreen(user!!, viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(birthDate: String): Int? {
    return try {
        val birth = LocalDate.parse(birthDate)
        val today = LocalDate.now()
        Period.between(birth, today).years
    } catch (e: Exception) {
        null
    }
}

@Composable
fun CreateUserForm(onUserCreated: (User) -> Unit) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Crear Usuario", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Fecha de nacimiento (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val age = calculateAge(birthDate)
                if (age == null) {
                    Toast.makeText(context, "Fecha de nacimiento no válida", Toast.LENGTH_SHORT).show()
                } else {
                    val newUser = User(id = 1, name = name, age = age, goals = "", history = "")
                    onUserCreated(newUser)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Usuario")
        }
    }
}

@Composable
fun EditGoalsScreen(user: User, viewModel: UserViewModel) {
    var newGoal by remember { mutableStateOf("") }
    var goalsList by remember(user.goals) {
        mutableStateOf(user.goals?.split("\n- ")?.filter { it.isNotBlank() }?.distinct() ?: emptyList())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Input para agregar nuevas metas
        OutlinedTextField(
            value = newGoal,
            onValueChange = { newGoal = it },
            label = { Text("Añadir nueva meta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newGoal.isNotBlank() && newGoal !in goalsList) {
                    val updatedGoals = (goalsList + newGoal).joinToString("\n- ")
                    viewModel.updateUserGoals(user.id, updatedGoals)

                    // Actualizar lista de metas.
                    goalsList = updatedGoals.split("\n- ").filter { it.isNotBlank() }.distinct()

                    newGoal = "" // Limpiar input después de añadir la meta
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Meta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Metas actuales:", fontWeight = FontWeight.Bold)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(goalsList) { meta ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "- $meta", modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        // Filtrar la meta seleccionada de la lista
                        val updatedGoalsList = goalsList.filter { it != meta }
                        val updatedGoals = updatedGoalsList.joinToString("\n- ")

                        // Actualizar en la BD primero y luego actualizar la UI
                        viewModel.updateUserGoals(user.id, updatedGoals)
                            // Refrescar la lista en la UI después de la actualización en la BD
                            goalsList = updatedGoals.split("\n- ").filter { it.isNotBlank() }

                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar meta")
                    }
                }
            }
        }

    }
}




