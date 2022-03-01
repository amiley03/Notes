package com.interview.notes.kotlin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interview.notes.R
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    val uiState: LiveData<UIState> get() = _uiState
    protected val _uiState: MutableLiveData<UIState> by lazy { MutableLiveData<UIState>() }

    abstract fun loadData()

    protected fun loadAsyncAndUpdatUI(handler: suspend ()-> Unit) {
        _uiState.postValue(UIState.Loading)
        viewModelScope.launch {
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
    class Updated(val noteItem: NoteItemViewModel) : UIState()
    class Error(val errorMessageId: Int) : UIState()
}
