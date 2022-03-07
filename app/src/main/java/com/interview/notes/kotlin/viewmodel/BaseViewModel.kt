package com.interview.notes.kotlin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.interview.notes.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class BaseViewModel(val dispatcherIO: CoroutineDispatcher) : ViewModel() {

    val uiState: LiveData<UIState> get() = _uiState
    protected val _uiState: MutableLiveData<UIState> by lazy { MutableLiveData<UIState>() }

    protected fun loadAsyncAndUpdateUI(handler: suspend () -> Unit) {
        _uiState.postValue(UIState.Loading)
        CoroutineScope(dispatcherIO).launch {
            try {
                handler()
            } catch (exception: Exception) {
                Timber.e(exception, "An exception occurred accessing repository")
                _uiState.postValue(UIState.Error(R.string.error_data))
            }
        }
    }

    protected fun updateUIState(uiState: UIState) {
        Timber.d("Updating live data value with $uiState")
        _uiState.postValue(uiState)
    }
}

sealed class UIState {
    object Loading : UIState()
    object Loaded : UIState()
    object Exit : UIState()
    object NewNote : UIState()
    object NoteList : UIState()
    class EditNote(val noteItem: NoteItemViewModel) : UIState()
    class Updated(val noteItem: NoteItemViewModel) : UIState()
    class Error(val errorMessageId: Int) : UIState()
}
