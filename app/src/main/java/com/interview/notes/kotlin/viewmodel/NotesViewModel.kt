package com.interview.notes.kotlin.viewmodel

import androidx.lifecycle.viewModelScope
import com.interview.notes.R
import com.interview.notes.kotlin.domain.mappers.NoteItemMapper
import com.interview.notes.kotlin.domain.usecases.OrderLatestNote
import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.repo.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.single

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository, dispatcherIO: CoroutineDispatcher = Dispatchers.IO) : BaseViewModel(dispatcherIO) {

    val notes: List<NoteItemViewModel> get() = OrderLatestNote().invoke(noteMap.values.toList())
    private val noteMap = mutableMapOf<String, NoteItemViewModel>()

    var noteId: String? = null
    var title: String = ""
    var content: String = ""
    var screenNameId = R.string.new_note

    init {
        loadAsyncAndUpdateUI {
            // Observe notes and update the UI when we find new notes not part of the noteMap data set
            repository.getAllNotes()
                .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
                .map { notes ->
                    // Filter out any note we already have in our data set - Runtime O(n) iterating over O(n) notes and using hash map lookup O(1)
                    val newNotes = notes.filter { !noteMap.containsKey(it.id) || noteMap[it.id]?.timeStamp != it.timeStamp }
                    Timber.d("New notes ${newNotes.size}")
                    NoteItemMapper().invoke(newNotes)
                }.onEach { newNotes ->
                    newNotes.forEach { note ->
                        Timber.d("Updating Note to list: ${note.title}")
                        noteMap[note.noteId] = note
                    }
                    if (newNotes.size > 1) {
                        updateUIState(UIState.Loaded)
                    } else if (newNotes.isNotEmpty()) {
                        updateUIState(UIState.Updated(newNotes[0]))
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
