package com.interview.notes.kotlin.domain.usecases

import com.interview.notes.kotlin.viewmodel.NoteItemViewModel

class OrderLatestNote {
    operator fun invoke(notes: List<NoteItemViewModel>): List<NoteItemViewModel> {
        return notes.sortedByDescending { note ->
            note.timeStamp
        }
    }
}
