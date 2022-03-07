package com.interview.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.data.local.NoteDao
import com.interview.notes.kotlin.model.repo.NotesRepository
import com.interview.notes.kotlin.model.repo.NotesRepositoryImpl
import com.interview.notes.kotlin.viewmodel.NotesViewModel
import com.interview.notes.kotlin.viewmodel.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import java.util.UUID.randomUUID

@ExperimentalCoroutinesApi
class NotesTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var mockRepository: NotesRepository

    private lateinit var noteRepository: NotesRepository

    private lateinit var mockDao: NoteDao

    @Before
    fun setup() {
        mockRepository = mock()
        mockDao = mock()
        noteRepository = NotesRepositoryImpl(mockDao)
    }

    private val testNotes: MutableList<Note> by lazy {
        val list = mutableListOf<Note>()
        for (i in 0..6) {
            list.add(Note(i.toString(), "test content"))
        }
        list
    }

    @Test
    fun `verify NotesViewModel UIState Loaded`() = runBlockingTest {
        val listFlow = MutableSharedFlow<List<Note>>()

        // return test data when repo method is called
        `when`(mockRepository.getAllNotes()).thenReturn(listFlow)

        val notesViewModel = NotesViewModel(mockRepository, TestCoroutineDispatcher())

        // verify the repo method is called
        verify(mockRepository).getAllNotes()

        // emit test data
        listFlow.emit(testNotes)

        // verify data is present
        assert(notesViewModel.notes.isNotEmpty())

        // verify the live data state
        assertEquals(UIState.Loaded, notesViewModel.uiState.value)

        // test single note update
        val newNote = Note("New Note", "test content")
        testNotes.add(0, newNote)
        listFlow.emit(testNotes)

        // verify the live data state
        assertEquals(UIState.Updated::class.java, notesViewModel.uiState.value?.javaClass)

        // verify the new note is at the beginning of the list
        assertEquals(newNote.id, notesViewModel.notes[0].noteId)
    }

    @Test
    fun `verify NotesViewModel UIState Error`() = runBlockingTest {
        `when`(mockRepository.getAllNotes()).then { throw Exception("test exception") }
        val notesViewModel = NotesViewModel(mockRepository, TestCoroutineDispatcher())
        assertEquals(UIState.Error::class.java, notesViewModel.uiState.value?.javaClass)
    }

    @Test
    fun `verify note detail view model test UIState Loaded new note`() = runBlockingTest {
        val id = randomUUID().toString()

        val notesViewModel = NotesViewModel(mockRepository, TestCoroutineDispatcher())

        notesViewModel.loadSavedNote()

        // verify for new note fetch is not called
        verify(mockRepository, times(0)).fetchNote(id)

        // verify the live data value is correct
        assertEquals(UIState.NewNote, notesViewModel.uiState.value)

        // verify correct screen title
        assertEquals(R.string.new_note, notesViewModel.screenNameId)
    }

    @Test
    fun `note detail view model test UIState Loaded edit note`() = runBlockingTest {
        val testNote = Note("","")

        val notesViewModel = NotesViewModel(mockRepository, TestCoroutineDispatcher())

        // return test data when repo method is called
        `when`(mockRepository.fetchNote(testNote.id)).thenReturn(testNote)

        notesViewModel.noteId = testNote.id
        notesViewModel.loadSavedNote()

        // verify the repo method is called for editing note
        verify(mockRepository, times(1)).fetchNote(testNote.id)

        // verify the live data value is correct
        assertTrue(notesViewModel.uiState.value is UIState.EditNote)

        // verify correct screen title
        assertEquals(R.string.edit_note, notesViewModel.screenNameId)
    }

}

@ExperimentalCoroutinesApi
class CoroutineTestRule(private val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
