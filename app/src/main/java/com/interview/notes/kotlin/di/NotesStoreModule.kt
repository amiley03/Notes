package com.interview.notes.kotlin.di

import com.interview.notes.kotlin.model.data.local.NotesStore
import com.interview.notes.kotlin.model.data.local.NotesStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotesStoreModule {

    @Binds
    abstract fun bindNoteStore(
        noteStore: NotesStoreImpl
    ): NotesStore
}

