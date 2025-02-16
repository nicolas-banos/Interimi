package com.interimi.interimi.screens

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.interimi.interimi.AppNavigation
import com.interimi.interimi.viewmodels.OpenAIViewModel
import com.interimi.interimi.R
import com.interimi.interimi.User
import com.interimi.interimi.viewmodels.UserPreferencesViewModel
import com.interimi.interimi.viewmodels.UserViewModel
import com.interimi.interimi.ui.theme.InterimiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterimiTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        val backgroundRes = if (selectedTab == 0) R.drawable.anciano_estoico else R.drawable.library

        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = "Fondo de pantalla",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                ) {
                    Text("Consejos", modifier = Modifier.padding(16.dp))
                }
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                ) {
                    Text("Metas", modifier = Modifier.padding(16.dp))
                }
            }

            when (selectedTab) {
                0 -> OpenAIScreen()
                1 -> UserScreen()
            }
        }
    }
}

// CONSEJOS

@Composable
fun OpenAIScreen(
    viewModel: OpenAIViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val response by viewModel.openAIResponse.collectAsState()
    var userInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Image(
            painter = painterResource(id = R.drawable.anciano_estoico),
            contentDescription = "Fondo de pantalla romano",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = response,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {


                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("Escribe tu pregunta") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White
                    ),

                    shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        viewModel.askAI(userInput, 1)
                        userInput = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Preguntar")
            }
        }


    }
}





// METAS

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val user by viewModel.userState.collectAsState()

    // Siempre intentar obtener el usuario con id 1 al inicio
    LaunchedEffect(Unit) {
        viewModel.getUserById(1)
    }

    // Si el usuario es nulo, lo creamos automáticamente
    if (user == null) {
        LaunchedEffect(Unit) {
            viewModel.insertUser(User(id = 1, name = "", age = 0, goals = "", history = "")) {
                viewModel.getUserById(1) // Recargar después de la creación
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (user != null) {
            GoalsScreen(user!!, viewModel)
        } else {
        //Si no se da creado mostrar indicador de progreso
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun GoalsScreen(
    user: User,
    viewModel: UserViewModel,
    preferencesViewModel: UserPreferencesViewModel = hiltViewModel()
) {
    var newGoal by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        preferencesViewModel.loadHistory()
    }

    // Separamos las metas usando "-" como delimitador
    val goalsList = user.goals
        ?.split("-")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                if (goalsList.isEmpty()) {
                    Text(
                        text = "No hay metas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                } else {
                    goalsList.forEach { goal ->
                        Text(
                            text = "- $goal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newGoal,
            onValueChange = { newGoal = it },
            label = { Text("Escribe una nueva meta") },
            modifier = Modifier.weight(0.1f).fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium // Fuente por defecto
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),

            ) {
            Button(
                onClick = {
                    if (newGoal.isNotBlank()) {
                        viewModel.updateUserGoals(user.id, newGoal)
                        preferencesViewModel.saveHistory("Nueva meta: $newGoal")
                        newGoal = ""
                    }
                }
            ) {
                Text("Añadir Meta", color = Color.White)
            }
            Button(
                onClick = { viewModel.deleteGoalsByUserId(user.id) }
            ) {
                Text("Borrar Metas", color = Color.White)
            }
        }
    }
}
