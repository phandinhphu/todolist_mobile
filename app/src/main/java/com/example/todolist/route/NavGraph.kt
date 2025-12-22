package com.example.todolist.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todolist.ui.screen.auth.LoginScreen
import com.example.todolist.ui.screen.auth.RegisterScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Routes.Register.route) {
            RegisterScreen(navController = navController)
        }
        // Add more routes as needed
    }
}