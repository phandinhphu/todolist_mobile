package com.example.todolist.data.mapper

import com.example.todolist.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain(): User =
    User(
        uid = uid,
        email = email
    )
