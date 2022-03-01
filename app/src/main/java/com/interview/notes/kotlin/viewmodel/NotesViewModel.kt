package com.interview.notes.kotlin.viewmodel

import androidx.lifecycle.viewModelScope
import com.interview.notes.kotlin.domain.mappers.NoteItemMapper
import com.interview.notes.kotlin.domain.usecases.OrderLatestNote
import com.interview.notes.kotlin.model.repo.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository) : BaseViewModel() {

    val notes: List<NoteItemViewModel> get() = OrderLatestNote().invoke(noteMap.values.toList())
    private val noteMap = mutableMapOf<String, NoteItemViewModel>()

    init {
        Timber.d("Initializing observers")
        observeNoteUpdates()
        observeNoteListUpdates()
    }

    private fun observeNoteListUpdates() {
        viewModelScope.launch {
            repository.noteList
                .map { notes ->
                    NoteItemMapper().invoke(notes)
                }
                .collect { notesMapped ->
                    notesMapped.forEach { note ->
                        Timber.d("Updating Note to list: ${note.title}")
                        noteMap[note.noteId] = note
                    }
                    updateUIState(UIState.Loaded)
                }
        }
    }

    private fun observeNoteUpdates() {
        viewModelScope.launch {
            repository.notes
                .map { note ->
                    NoteItemMapper().invoke(note)
                }
                .collect { noteVM ->
                    Timber.d("Updating Note to list: ${noteVM.title}")
                    noteMap[noteVM.noteId] = noteVM
                    updateUIState(UIState.Updated(noteVM))
                }
        }
    }

    override fun loadData() {
        loadAsyncAndUpdatUI {
            repository.loadNotes()
        }
    }
}
