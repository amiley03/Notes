package com.interview.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.internal.util.ReflectionUtil
import com.interview.notes.kotlin.domain.mappers.NoteItemMapper
import com.interview.notes.kotlin.model.Note
import com.interview.notes.kotlin.model.data.local.NotesStore
import com.interview.notes.kotlin.model.repo.NotesRepository
import com.interview.notes.kotlin.model.repo.NotesRepositoryImpl
import com.interview.notes.kotlin.viewmodel.NoteDetailsViewModel
import com.interview.notes.kotlin.viewmodel.NoteItemViewModel
import com.interview.notes.kotlin.viewmodel.NotesViewModel
import com.interview.notes.kotlin.viewmodel.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.internal.util.reflection.ReflectionMemberAccessor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.robolectric.util.ReflectionHelpers.setField
import java.util.UUID.randomUUID

@ExperimentalCoroutinesApi
class NotesTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var mockRepository: NotesRepository
    private lateinit var mockNoteStore: NotesStore

    private lateinit var noteRepository: NotesRepository
    private lateinit var noteDetailViewModel: NoteDetailsViewModel

    @Before
    fun setup() {
        mockRepository = mock()
        mockNoteStore = mock()

        noteRepository = NotesRepositoryImpl(mockNoteStore)
        noteDetailViewModel = NoteDetailsViewModel(mockRepository)

    }

    private fun setPrivateData(notesViewModel: NotesViewModel) {
        val field = NotesViewModel::class.java.getDeclaredField("noteMap")
        field.isAccessible = true
        val testMap = mutableMapOf<String, NoteItemViewModel>()
        testNotes.forEach {
            testMap[it.id] = NoteItemMapper().invoke(it)
        }

        field.set(notesViewModel, testMap)
    }

    private val testNotes: List<Note> by lazy {
        val list = mutableListOf<Note>()
        for (i in 0..6) {
            list.add(Note(i.toString(), "test content"))
        }
        list
    }

    private val flow = MutableSharedFlow<Note>()

    @Test
    fun `verify NotesViewModel UIState Loaded`() = runBlockingTest {
        val notesViewModel = NotesViewModel(mockRepository)

        // return test data when repo method is called
        setPrivateData(notesViewModel)
        `when`(mockRepository.notes).thenReturn(flow)

        notesViewModel.loadData()

        // verify the repo method is called
        verify(mockRepository).loadNotes()

        // emit test data
        testNotes.forEach {
            flow.emit(it)
        }

        // verify data is present
        assert(notesViewModel.notes.isNotEmpty())

        // verify order of the result list is correct
        val sorted = testNotes.sortedByDescending { it.timeStamp }
        sorted.forEachIndexed { index, note ->
            assertTrue(note.id == notesViewModel.notes[index].noteId)
        }
    }

    @Test
    fun `verify NotesViewModel UIState Error`() = runBlockingTest {
        val notesViewModel = NotesViewModel(mockRepository)
       `when`(mockRepository.loadNotes()).then { throw Exception("test exception") }
        notesViewModel.loadData()
        assert(notesViewModel.uiState.value is UIState.Error)
    }

    @Test
    fun `verify note detail view model test UIState Loaded new note`() = runBlockingTest {
        val id = randomUUID().toString()
        val testNote = Note("", "")
        // return test data when repo method is called
        `when`(mockRepository.fetchNote(id)).thenReturn(testNote)

        noteDetailViewModel.loadData()

        // verify for new note fetch is not called
        verify(mockRepository, times(0)).fetchNote(id)

        // verify the live data value is correct
        assert(noteDetailViewModel.uiState.value is UIState.Loaded)

        // verify correct screen title
        assert(noteDetailViewModel.screenNameId == R.string.new_note)
    }

    @Test
    fun `note detail view model test UIState Loaded edit note`() = runBlockingTest {
        val id = randomUUID().toString()
        val testNote = Note("","")
        // return test data when repo method is called
        `when`(mockRepository.fetchNote(id)).thenReturn(testNote)

        noteDetailViewModel.noteId = id
        noteDetailViewModel.loadData()

        // verify the repo method is called for editing note
        verify(mockRepository, times(1)).fetchNote(id)

        // verify the live data value is correct
        assert(noteDetailViewModel.uiState.value is UIState.Loaded)

        // verify correct screen title
        assert(noteDetailViewModel.screenNameId == R.string.edit_note)
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

