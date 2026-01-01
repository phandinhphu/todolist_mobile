package com.example.todolist.ui.screen.alarm

import androidx.lifecycle.ViewModel
import com.example.todolist.domain.manager.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val audioPlayer: AudioPlayer) : ViewModel() {

    fun startAlarm() {
        audioPlayer.play()
    }

    fun stopAlarm() {
        audioPlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        stopAlarm()
    }
}
