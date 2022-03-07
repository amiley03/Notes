package com.interview.notes

import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.data.local.NoteDao
import com.interview.notes.kotlin.model.repo.NotesRepository
import com.interview.notes.kotlin.model.repo.NotesRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import java.util.UUID

@ExperimentalCoroutinesApi
class NoteRepositoryTest {

    private lateinit var mcokNoteDao: NoteDao
    private lateinit var noteRepository: NotesRepository

    @Before
    fun setup() {
        mcokNoteDao = mock()
        noteRepository = NotesRepositoryImpl(mcokNoteDao)
    }

    @Test
    fun `verify repository test get notes`() = runBlockingTest {
        noteRepository.getAllNotes()
        Mockito.verify(mcokNoteDao).getAllNotes()
    }

    @Test
    fun `verify repository get note from id`() = runBlockingTest {
        val id = UUID.randomUUID().toString()
        noteRepository.fetchNote(id)
        Mockito.verify(mcokNoteDao).findById(id)
    }

    @Test
    fun `verify repo makes noteAPI save note call`() = runBlockingTest {
        val testNote = Note("testing", "uber")
        noteRepository.saveNote(testNote)
        Mockito.verify(mcokNoteDao).insertNote(testNote)
    }
}
