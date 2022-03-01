package com.interview.notes.kotlin.ui

import androidx.lifecycle.Observer
import com.interview.notes.R
import com.interview.notes.databinding.ActivityMainBinding
import com.interview.notes.kotlin.viewmodel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.interview.notes.kotlin.ui.adapter.NotesAdapter
import com.interview.notes.kotlin.viewmodel.NoteItemViewModel
import com.interview.notes.kotlin.viewmodel.UIState

/**
 * Main {@link android.app.Activity} which displays a list of existing Notes.
 */
@AndroidEntryPoint
class MainActivity : BaseActivityVM<NotesViewModel, ActivityMainBinding>() {

    override val layoutId = R.layout.activity_main
    override val viewModelClass = NotesViewModel::class.java

    private val notesAdapter: NotesAdapter = NotesAdapter { id ->
        handleNoteItemCLicked(id)
    }

    private val observer by lazy {
        Observer<UIState> { uiState ->
            when(uiState) {
                is UIState.Updated -> insertNewItem(uiState.noteItem)
                else -> Unit
            }
        }
    }

    private fun insertNewItem(item: NoteItemViewModel) {
        notesAdapter.updateItem(item)
    }

    override fun setUpViews() {
        binding.notesList.adapter = notesAdapter
        binding.btnAddNote.setOnClickListener { openNoteDetails() }
        viewModel.uiState.observe(this, observer)
    }

    override fun updateUI() {
        notesAdapter.setNotes(viewModel.notes)
    }

    private fun openNoteDetails(noteId: String? = null) {
        startActivity(AddNoteActivity.build(this, noteId))
    }

    private fun handleNoteItemCLicked(noteId: String) {
        openNoteDetails(noteId)
    }

    override fun handleError(errorMessageId: Int) {
        showMessage(errorMessageId)
    }

    override fun handleLoading() {
       showMessage(R.string.loading)
    }

}
