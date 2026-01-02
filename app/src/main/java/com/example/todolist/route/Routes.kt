package com.example.todolist.route

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object ResetPassword : Routes("reset_password")
    object Home : Routes("home")
    object TaskList : Routes("task_list")
    object AddTask : Routes("add_task?date={date}") {
        fun createRoute(date: Long? = null) = "add_task?date=${date ?: -1L}"
    }
    object EditTask : Routes("edit_task/{taskId}") {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
    object TagManagement : Routes("tag_management")
    object Account : Routes("account")
    object Calendar : Routes("calendar")
}