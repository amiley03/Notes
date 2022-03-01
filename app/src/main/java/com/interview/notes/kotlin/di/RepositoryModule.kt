package com.interview.notes.kotlin.di

import com.interview.notes.kotlin.model.repo.NotesRepository
import com.interview.notes.kotlin.model.repo.NotesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun getNotesRepository(
        impl: NotesRepositoryImpl
    ) : NotesRepository

}
