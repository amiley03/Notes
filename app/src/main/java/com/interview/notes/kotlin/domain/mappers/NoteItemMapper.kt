package com.interview.notes.kotlin.domain.mappers

import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.viewmodel.NoteItemViewModel

class NoteItemMapper {

    operator fun invoke(note: Note): NoteItemViewModel =
        NoteItemViewModel(
            title = note.title,
            content = note.content,
            noteId = note.id,
            timeStamp = note.timeStamp
        )

    operator fun invoke(notes: List<Note>): List<NoteItemViewModel> =
        notes.map { note ->
            NoteItemViewModel(
                title = note.title,
                content = note.content,
                noteId = note.id,
                timeStamp = note.timeStamp
            )
        }

}
