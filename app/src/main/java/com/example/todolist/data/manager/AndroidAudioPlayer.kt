package com.example.todolist.data.manager

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import com.example.todolist.domain.manager.AudioPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidAudioPlayer @Inject constructor(@ApplicationContext private val context: Context) :
    AudioPlayer {

    private var mediaPlayer: android.media.MediaPlayer? = null

    override fun play() {
        if (isPlaying()) return

        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) 
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            
            mediaPlayer = android.media.MediaPlayer().apply {
                setDataSource(context, uri)
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }

    override fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }
}
