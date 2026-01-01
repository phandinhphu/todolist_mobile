package com.example.todolist.data.manager

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import com.example.todolist.domain.manager.AudioPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidAudioPlayer @Inject constructor(@ApplicationContext private val context: Context) :
        AudioPlayer {

    private var ringtone: Ringtone? = null

    override fun play() {
        if (isPlaying()) return

        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        if (isPlaying()) {
            ringtone?.stop()
            ringtone = null
        }
    }

    override fun isPlaying(): Boolean {
        return ringtone?.isPlaying == true
    }
}
