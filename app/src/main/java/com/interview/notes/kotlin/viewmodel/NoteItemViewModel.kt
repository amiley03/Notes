package com.interview.notes.kotlin.viewmodel

import androidx.lifecycle.ViewModel

class NoteItemViewModel (
    val title: String,
    val content: String,
    val noteId: String,
    val timeStamp: String,
    var clickListener:((String) -> Unit)? = null
) :  ViewModel() {

    fun onItemClicked() {
        clickListener?.invoke(noteId)
    }
}

