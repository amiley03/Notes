package com.interview.notes.kotlin.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.interview.notes.R
import com.interview.notes.databinding.ActivityMainBinding
import com.interview.notes.kotlin.ui.NoteDetailsFragment.Companion.DETAILS_FRAGMENT_TAG
import com.interview.notes.kotlin.ui.NotesListFragment.Companion.LIST_FRAGMENT_TAG
import com.interview.notes.kotlin.viewmodel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.interview.notes.kotlin.ui.adapter.NotesAdapter
import com.interview.notes.kotlin.viewmodel.NoteItemViewModel
import com.interview.notes.kotlin.viewmodel.UIState

/**
 * Main {@link android.app.Activity} which displays a list of existing Notes.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: NotesViewModel by viewModels()

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, NotesListFragment.newInstance(), LIST_FRAGMENT_TAG)
            .commit()
    }

    override fun onBackPressed() {
        viewModel.noteId = null
        super.onBackPressed()
    }

}
