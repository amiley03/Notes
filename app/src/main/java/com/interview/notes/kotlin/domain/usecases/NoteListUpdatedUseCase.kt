package com.interview.notes.kotlin.domain.usecases

import com.interview.notes.kotlin.domain.mappers.NoteItemMapper
import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.viewmodel.NoteItemViewModel
import timber.log.Timber

class NoteListUpdated {

    operator fun invoke(currentData: MutableMap<String, NoteItemViewModel>, updatedNotes: List<Note>): UpdateStatus {
        if (updatedNotes.size < currentData.size) {
            // Items deleted
            val ids = mutableSetOf<String>()
            updatedNotes.forEach {
                ids.add(it.id)
            }

            val deletedItems = currentData.values.filter { !ids.toSet().contains(it.noteId) }
            updateDataSet(currentData, updatedNotes)

            return UpdateStatus.Deleted(deletedItems.first())
        } else {
            // Items either updated or added
            // Filter out any note we already have in our data set - Runtime O(n) iterating over (n) notes and using hash map lookup O(1)
            val newNotes = updatedNotes.filter { !currentData.containsKey(it.id) || currentData[it.id]?.timeStamp != it.timeStamp }
            Timber.d("New notes ${newNotes.size}")

            updateDataSet(currentData, updatedNotes)

            return if (newNotes.size == 1) {
                UpdateStatus.Added(NoteItemMapper().invoke(newNotes.first()))
            } else {
                UpdateStatus.Refresh
            }
        }
    }

    private fun updateDataSet(currentData: MutableMap<String, NoteItemViewModel>, updatedNotes: List<Note>) {
        // update current data
        currentData.clear()
        updatedNotes.forEach { note ->
            Timber.d("Updating Note to list: ${note.title}")
            currentData[note.id] = NoteItemMapper().invoke(note)
        }
    }
}

sealed class UpdateStatus {
    class Deleted(val item: NoteItemViewModel) : UpdateStatus()
    class Added(val item: NoteItemViewModel) : UpdateStatus()
    object Refresh : UpdateStatus()
}
