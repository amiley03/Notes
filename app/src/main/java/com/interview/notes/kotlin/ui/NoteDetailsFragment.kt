package com.interview.notes.kotlin.ui

import androidx.lifecycle.Observer
import com.interview.notes.R
import com.interview.notes.databinding.FragmentNoteDetailsBinding
import com.interview.notes.kotlin.viewmodel.NoteItemViewModel
import com.interview.notes.kotlin.viewmodel.NotesViewModel
import com.interview.notes.kotlin.viewmodel.UIState

class NoteDetailsFragment : BaseFragment<NotesViewModel, FragmentNoteDetailsBinding>() {

    override val layoutId = R.layout.fragment_note_details
    override val viewModelClass = NotesViewModel::class.java

    private val uiObserver by lazy {
        Observer<UIState> { uiState ->
            when (uiState) {
                UIState.Exit -> closeScreen()
                UIState.NewNote -> handleNewNoteState()
                is UIState.EditNote -> handleEditNoteState(uiState.noteItem)
                else -> Unit
            }
        }
    }

    override fun setUpViews() {
        viewModel.uiState.observe(viewLifecycleOwner, uiObserver)
        viewModel.loadSavedNote()
    }

    private fun handleEditNoteState(noteItem: NoteItemViewModel) {
        binding.titleEdit.setText(noteItem.title)
        binding.contentEdit.setText(noteItem.content)
        activity?.setTitle(R.string.edit_note)
    }

    private fun handleNewNoteState() {
        binding.titleEdit.setText("")
        binding.contentEdit.setText("")
        activity?.setTitle(R.string.new_note)
    }

    private fun closeScreen() {
        parentFragmentManager.popBackStack()
    }

    companion object {
        const val DETAILS_FRAGMENT_TAG = "details_fragment"
        fun newInstance() = NoteDetailsFragment()
    }

}
