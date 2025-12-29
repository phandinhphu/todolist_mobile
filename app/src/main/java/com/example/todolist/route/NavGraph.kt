package com.example.todolist.route

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.todolist.ui.components.BottomNavBar
import com.example.todolist.ui.screen.auth.LoginScreen
import com.example.todolist.ui.screen.auth.RegisterScreen
import com.example.todolist.ui.screen.home.HomeScreen
import com.example.todolist.ui.screen.tag.TagScreen
import com.example.todolist.ui.screen.task.AddEditTaskScreen
import com.example.todolist.ui.screen.task.TaskListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Screens that should show bottom navigation
    val screensWithBottomNav = listOf(Routes.Home.route, Routes.TaskList.route, Routes.TagManagement.route)
    val showBottomBar = currentRoute in screensWithBottomNav

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login.route,
            modifier = if (showBottomBar) Modifier.padding(paddingValues) else Modifier
        ) {
            composable(Routes.Login.route) {
                LoginScreen(navController = navController)
            }
            composable(Routes.Register.route) {
                RegisterScreen(navController = navController)
            }
            composable(Routes.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Routes.TaskList.route) {
                TaskListScreen(navController = navController)
            }
            composable(Routes.AddTask.route) {
                AddEditTaskScreen(
                    navController = navController,
                    taskId = null
                )
            }
            composable(
                route = Routes.EditTask.route,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId")
                AddEditTaskScreen(
                    navController = navController,
                    taskId = taskId
                )
            }
            composable(Routes.TagManagement.route) {
                TagScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}