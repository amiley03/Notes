package com.interview.notes.kotlin.ui

import androidx.lifecycle.Observer
import com.interview.notes.R
import com.interview.notes.databinding.FragmentNotesListBinding
import com.interview.notes.kotlin.ui.NoteDetailsFragment.Companion.DETAILS_FRAGMENT_TAG
import com.interview.notes.kotlin.ui.adapter.NotesAdapter
import com.interview.notes.kotlin.viewmodel.NoteItemViewModel
import com.interview.notes.kotlin.viewmodel.NotesViewModel
import com.interview.notes.kotlin.viewmodel.UIState

class NotesListFragment : BaseFragment<NotesViewModel, FragmentNotesListBinding>() {

    override val layoutId = R.layout.fragment_notes_list
    override val viewModelClass = NotesViewModel::class.java

    private val notesAdapter: NotesAdapter = NotesAdapter { id ->
        handleNoteItemCLicked(id)
    }

    private val observer by lazy {
        Observer<UIState> { uiState ->
            when (uiState) {
                is UIState.Updated -> insertNewItem(uiState.noteItem)
                UIState.Loaded -> updateUI()
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
        viewModel.loadNotes()
    }

    private fun updateUI() {
        notesAdapter.setNotes(viewModel.notes)
        activity?.setTitle(R.string.app_name)
    }

    private fun openNoteDetails(noteId: String? = null) {
        viewModel.noteId = noteId
        parentFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, NoteDetailsFragment.newInstance(), DETAILS_FRAGMENT_TAG)
            .addToBackStack(DETAILS_FRAGMENT_TAG)
            .commit()
    }

    private fun handleNoteItemCLicked(noteId: String) {
        openNoteDetails(noteId)
    }

    companion object {
        const val LIST_FRAGMENT_TAG = "list_fragment"
        fun newInstance() = NotesListFragment()
    }
}
