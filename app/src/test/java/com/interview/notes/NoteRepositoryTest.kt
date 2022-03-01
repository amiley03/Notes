package com.interview.notes

import android.os.Build
import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.data.local.NotesStore
import com.interview.notes.kotlin.model.repo.NotesRepository
import com.interview.notes.kotlin.model.repo.NotesRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.robolectric.annotation.Config
import java.util.UUID

@ExperimentalCoroutinesApi
class NoteRepositoryTest {

    private lateinit var mockNoteStore: NotesStore
    private lateinit var noteRepository: NotesRepository

    @Before
    fun setup() {
        mockNoteStore = mock()
        noteRepository = NotesRepositoryImpl(mockNoteStore)
    }

    @Test
    fun `verify repository test get notes`() = runBlockingTest {
        noteRepository.loadNotes()
        Mockito.verify(mockNoteStore).getNotes()
    }

    @Test
    fun `verify repository get note from id`() = runBlockingTest {
        val id = UUID.randomUUID().toString()
        noteRepository.fetchNote(id)
        Mockito.verify(mockNoteStore).getNote(id)
    }

    @Test
    fun `verify repo makes noteAPI save note call`() = runBlockingTest {
        val testNote = Note("testing", "uber")
        noteRepository.saveNote(testNote)
        Mockito.verify(mockNoteStore).saveNote(testNote)
    }
}
