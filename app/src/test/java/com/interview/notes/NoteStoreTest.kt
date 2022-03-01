package com.interview.notes

import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.data.local.NotesStore
import com.interview.notes.kotlin.model.data.local.NotesStoreImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class NoteStoreTest {

    private lateinit var context: Context
    private lateinit var noteStore: NotesStore

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        noteStore = NotesStoreImpl(context)
    }

    @Test
    fun `test saving and retrieving of new note with id`() {
        val note = Note("New note", "content")
        noteStore.saveNote(note)

        val fetchedNote = noteStore.getNote(note.id)

        assert(fetchedNote != null)
        assertEquals(note.id, fetchedNote!!.id)
    }

    @Test
    fun `test updating and retrieving note with id`() {
        val note = Note("New note", "content")
        noteStore.saveNote(note)

        val updatedNote = Note(note.title, "updated content", note.id)

        noteStore.saveNote(updatedNote)

        val fetchedUpdatedNote = noteStore.getNote(note.id)

        assertNotNull(fetchedUpdatedNote)
        assertEquals(note.title, fetchedUpdatedNote!!.title)
        assertNotSame(note.content, fetchedUpdatedNote.content)
        assertEquals(updatedNote.content, fetchedUpdatedNote.content)
    }

}
