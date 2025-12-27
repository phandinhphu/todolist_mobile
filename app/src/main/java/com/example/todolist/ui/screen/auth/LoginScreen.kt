package com.example.todolist.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.todolist.route.Routes

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.authUiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                navController.navigate(Routes.TaskList.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email") },
            isError = emailError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError != null) {
            Text(text = emailError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Password") },
            isError = passwordError != null,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError != null) {
            Text(text = passwordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is AuthUiState.Loading -> CircularProgressIndicator()
            is AuthUiState.Error -> {
                Text(text = (uiState as AuthUiState.Error).message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val (isValid, errors) = validate(email, password)
                    if (isValid) {
                        viewModel.login(email, password)
                    } else {
                        emailError = errors["email"]
                        passwordError = errors["password"]
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Login")
                }
            }
            else -> {
                Button(onClick = {
                    val (isValid, errors) = validate(email, password)
                    if (isValid) {
                        viewModel.login(email, password)
                    } else {
                        emailError = errors["email"]
                        passwordError = errors["password"]
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Login")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate(Routes.Register.route) }) {
            Text("Don't have an account? Register")
        }
    }
}

fun validate(email: String, password: String): Pair<Boolean, Map<String, String>> {
    val errors = mutableMapOf<String, String>()
    var isValid = true

    if (email.isBlank()) {
        errors["email"] = "Please enter an email"
        isValid = false
    }
    if (password.length < 6) {
        errors["password"] = "Password must be at least 6 characters"
        isValid = false
    }

    return Pair(isValid, errors)
}