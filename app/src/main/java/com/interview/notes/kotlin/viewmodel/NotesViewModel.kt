package com.interview.notes.kotlin.viewmodel

import androidx.lifecycle.viewModelScope
import com.interview.notes.R
import com.interview.notes.kotlin.di.DispatcherIO
import com.interview.notes.kotlin.domain.mappers.NoteItemMapper
import com.interview.notes.kotlin.domain.usecases.NoteListUpdated
import com.interview.notes.kotlin.domain.usecases.UpdateStatus
import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.repo.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository, @DispatcherIO dispatcherIO: CoroutineDispatcher) : BaseViewModel(dispatcherIO) {

    val notes: List<NoteItemViewModel> get() = noteMap.values.toList()
    private val noteMap = mutableMapOf<String, NoteItemViewModel>()

    var noteId: String? = null
    var title: String = ""
    var content: String = ""
    var screenNameId = R.string.new_note

    init {
        loadAsyncAndUpdateUI {
            // Observe notes and update the UI when we find new notes not part of the noteMap data set or deleted items
            repository.getAllNotes()
                .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
                .map { notes ->
                    NoteListUpdated().invoke(noteMap, notes)
                }.onEach { updateStatus ->
                    when (updateStatus) {
                        is UpdateStatus.Added -> updateUIState(UIState.Updated(updateStatus.item))
                        is UpdateStatus.Deleted -> updateUIState(UIState.Deleted(updateStatus.item))
                        UpdateStatus.Refresh -> updateUIState(UIState.Loaded)
                    }
                }
                .collect()
        }
    }

    fun loadSavedNote() {
        noteId?.let { id ->
            loadAsyncAndUpdateUI {
                // Editing existing note
                val note = repository.fetchNote(id)
                val noteVM = NoteItemMapper().invoke(note)
                screenNameId = R.string.edit_note
                updateUIState(UIState.EditNote(noteVM))
            }
        } ?: updateUIState(UIState.NewNote)
    }

    fun deleteNote(note: Note) {
        loadAsyncAndUpdateUI {
            repository.deleteNote(note)
        }
    }

    fun handleSaveNoteClicked() {
        when {
            title.isEmpty() -> updateUIState(UIState.Error(R.string.error_title))
            content.isEmpty() -> updateUIState(UIState.Error(R.string.error_content))
            else -> saveNote()
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
