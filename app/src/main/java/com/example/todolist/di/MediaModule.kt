package com.example.todolist.di

import com.example.todolist.data.manager.AndroidAudioPlayer
import com.example.todolist.domain.manager.AudioPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {

    @Binds
    @Singleton
    abstract fun bindAudioPlayer(androidAudioPlayer: AndroidAudioPlayer): AudioPlayer
}
