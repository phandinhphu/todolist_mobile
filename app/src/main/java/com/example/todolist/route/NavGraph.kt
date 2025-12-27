package com.example.todolist.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todolist.ui.screen.auth.LoginScreen
import com.example.todolist.ui.screen.auth.RegisterScreen
import com.example.todolist.ui.screen.task.AddEditTaskScreen
import com.example.todolist.ui.screen.task.TaskListScreen

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
    }
}