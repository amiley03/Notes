package com.interview.notes.kotlin.ui

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.interview.notes.BR
import com.interview.notes.kotlin.viewmodel.BaseViewModel
import com.interview.notes.kotlin.viewmodel.UIState
import timber.log.Timber

abstract class BaseActivityVM<VM : BaseViewModel, VDB : ViewDataBinding> : AppCompatActivity() {

    abstract val layoutId: Int
    abstract val viewModelClass: Class<VM>

    open lateinit var binding: VDB
    open lateinit var viewModel: VM

    abstract fun handleLoading()

    abstract fun updateUI()

    abstract fun setUpViews()

    abstract fun handleError(errorMessageId: Int)

    private val uiObserver by lazy {
        Observer<UIState> { uiState ->
            when (uiState) {
                UIState.Loading -> handleLoading()
                UIState.Loaded -> updateUI()
                is UIState.Error -> handleError(uiState.errorMessageId)
                else -> {
                    Timber.e("Observed unhandled UIState $uiState")
                }
            }
            Timber.d("Observed UIState $uiState")
        }
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(viewModelClass)
        initBinding()
        setContentView(binding.root)
        setUpViews()
        observeUI()
        loadData()
    }

    private fun initBinding() {
        binding = DataBindingUtil.inflate(layoutInflater, layoutId, null, false)
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
    }

    private fun observeUI() {
        viewModel.uiState.observe(this, uiObserver)
    }

    private fun loadData() {
        viewModel.loadData()
    }

    protected fun showMessage(messageId: Int) {
        Toast.makeText(this@BaseActivityVM, getString(messageId), Toast.LENGTH_SHORT).show()
    }
}
