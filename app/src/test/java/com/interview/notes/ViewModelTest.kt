package com.interview.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.data.local.NoteDao
import com.interview.notes.kotlin.model.repo.NotesRepository
import com.interview.notes.kotlin.model.repo.NotesRepositoryImpl
import com.interview.notes.kotlin.viewmodel.NotesViewModel
import com.interview.notes.kotlin.viewmodel.UIState
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
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
import javax.inject.Singleton

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

    private val testNotes: List<Note> by lazy {
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

        // test initial load
        notesViewModel.loadNotes()

        // verify the repo method is called
        verify(mockRepository).getAllNotes()

        // emit test data
        listFlow.emit(testNotes)

        // verify data is present
        assert(notesViewModel.notes.isNotEmpty())

        // verify order of the result list is correct
        val sorted = testNotes.sortedByDescending { it.timeStamp }
        sorted.forEachIndexed { index, note ->
            assertTrue(note.id == notesViewModel.notes[index].noteId)
        }

        // verify the live data state
        assertEquals(UIState.Loaded, notesViewModel.uiState.value)

        // test single note update
        val newNote = Note("New Note", "test content")
        listFlow.emit(listOf(newNote))

        // verify the live data state
        assertTrue(notesViewModel.uiState.value is UIState.Updated)

        // verify the new note is at the beginning of the list
        assertEquals(newNote.id, notesViewModel.notes[0].noteId)
    }

    @Test
    fun `verify NotesViewModel UIState Error`() = runBlockingTest {
        val notesViewModel = NotesViewModel(mockRepository, TestCoroutineDispatcher())
       `when`(mockRepository.getAllNotes()).then { throw Exception("test exception") }
        notesViewModel.loadNotes()
        assertEquals(UIState.Error::class.java, notesViewModel.uiState.value?.javaClass)
    }

    @Test
    fun `verify note detail view model test UIState Loaded new note`() = runBlockingTest {
        val id = randomUUID().toString()
        val flow = MutableSharedFlow<Note>()

        val notesViewModel = NotesViewModel(mockRepository, TestCoroutineDispatcher())

        // return test data when repo method is called
        `when`(mockRepository.fetchNote(id)).thenReturn(flow)

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
        val flow = MutableSharedFlow<Note>()

        val notesViewModel = NotesViewModel(mockRepository, TestCoroutineDispatcher())

        // return test data when repo method is called
        `when`(mockRepository.fetchNote(testNote.id)).thenReturn(flow)

        notesViewModel.noteId = testNote.id
        notesViewModel.loadSavedNote()
        flow.emit(testNote)

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

@ExperimentalCoroutinesApi
@Singleton
@Provides
fun provideDispatchers(): CoroutineDispatcher = TestCoroutineDispatcher()

