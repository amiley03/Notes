package com.interview.notes.kotlin.viewmodel

import androidx.lifecycle.viewModelScope
import com.interview.notes.kotlin.domain.mappers.NoteItemMapper
import com.interview.notes.kotlin.domain.usecases.OrderLatestNote
import com.interview.notes.kotlin.model.repo.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository, dispatcherIO: CoroutineDispatcher = Dispatchers.IO) : BaseViewModel(dispatcherIO) {

    val notes: List<NoteItemViewModel> get() = OrderLatestNote().invoke(noteMap.values.toList())
    private val noteMap = mutableMapOf<String, NoteItemViewModel>()

    var noteId: String? = null
    var title: String = ""
    var content: String = ""
    var screenNameId = R.string.new_note

    fun loadNotes() {
        loadAsyncAndUpdateUI {
            repository.getAllNotes()
               .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
                .map { notes ->
                    val mapped = NoteItemMapper().invoke(notes).toMutableList()

                    // Filter out any note we already have in out data set
                    noteMap.values.forEach { item ->
                        mapped.removeIf { it.timeStamp == item.timeStamp }
                    }
                    mapped.toList()
                }.onEach { notesMapped ->
                    notesMapped.forEach { note ->
                        Timber.d("Updating Note to list: ${note.title}")
                        noteMap[note.noteId] = note
                    }
                    if (notesMapped.size > 1) {
                        updateUIState(UIState.Loaded)
                    } else if (notesMapped.isNotEmpty()) {
                        updateUIState(UIState.Updated(notesMapped[0]))
                    }
                }
                .collect()
        }
    }

    fun loadSavedNote() {
        noteId?.let { id ->
            viewModelScope.launch {
                // Editing existing note
                repository.fetchNote(id)
                    .map { note ->
                        NoteItemMapper().invoke(note)
                    }.onEach { noteItem ->
                        screenNameId = R.string.edit_note
                        updateUIState(UIState.EditNote(noteItem))
                    }
                    .collect()
            }
        } ?: updateUIState(UIState.NewNote)
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

    private fun saveNote() {
        loadAsyncAndUpdateUI {
            val note = Note(title, content, noteId ?: UUID.randomUUID().toString())
            repository.saveNote(note)
            title = ""
            content = ""
            noteId = null
            _uiState.postValue(UIState.Exit)
        }
    }
}
