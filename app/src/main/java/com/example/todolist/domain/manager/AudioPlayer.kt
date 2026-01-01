package com.example.todolist.domain.manager

interface AudioPlayer {
    fun play()
    fun stop()
    fun isPlaying(): Boolean
}
