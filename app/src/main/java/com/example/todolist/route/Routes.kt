package com.example.todolist.route

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
    object TaskList : Routes("task_list")
    object AddTask : Routes("add_task")
    object EditTask : Routes("edit_task/{taskId}") {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
    object TagManagement : Routes("tag_management")
}