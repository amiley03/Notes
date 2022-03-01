package com.interview.notes.kotlin.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.interview.notes.R
import com.interview.notes.databinding.ActivityAddNoteBinding
import com.interview.notes.kotlin.viewmodel.NoteDetailsViewModel
import com.interview.notes.kotlin.viewmodel.UIState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * {@link android.app.Activity} to create a new Note or edit an existing one.
 * The layout has two EditTexts, one for title and another for content
 */
@AndroidEntryPoint
class AddNoteActivity :  BaseActivityVM<NoteDetailsViewModel, ActivityAddNoteBinding>() {

    override val layoutId = R.layout.activity_add_note
    override val viewModelClass = NoteDetailsViewModel::class.java

    private val noteId: String? by lazy {
        intent?.getStringExtra(INTENT_NOTE_ID)
    }

    private val uiObserver by lazy {
        Observer<UIState> { uiState ->
            when(uiState) {
                UIState.Exit -> closeScreen()
                else -> Unit
            }
        }
    }

    override fun setUpViews() {
        viewModel.noteId = noteId
        viewModel.uiState.observe(this, uiObserver)
    }

    override fun handleError(errorMessageId: Int) {
        showMessage(errorMessageId)
    }

    override fun handleLoading() {
        showMessage(R.string.loading)
    }

    override fun updateUI() {
        binding.notifyChange()
        setTitle(viewModel.screenNameId)
    }

    private fun closeScreen() {
        finish()
    }

    companion object {
        private const val INTENT_NOTE_ID = "note_id"

        fun build(context: Context, noteId: String? = null): Intent {
            val intent = Intent(context, AddNoteActivity::class.java)
            return intent.putExtra(INTENT_NOTE_ID, noteId)
        }
    }

}
