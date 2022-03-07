package com.interview.notes.kotlin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.interview.notes.BR
import com.interview.notes.R
import com.interview.notes.kotlin.viewmodel.BaseViewModel
import com.interview.notes.kotlin.viewmodel.UIState
import timber.log.Timber

abstract class BaseFragment<VM : BaseViewModel, VDB : ViewDataBinding> : Fragment() {

    abstract val layoutId: Int
    abstract val viewModelClass: Class<VM>

    open lateinit var binding: VDB
    open lateinit var viewModel: VM

    abstract fun setUpViews()

    private val uiObserver by lazy {
        Observer<UIState> { uiState ->
            when (uiState) {
                UIState.Loading -> handleLoading()
                is UIState.Error -> handleError(uiState.errorMessageId)
            }
            Timber.d("Observed UIState $uiState")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUI()
        setUpViews()
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = ViewModelProvider(requireActivity()).get(viewModelClass)
        binding.setVariable(BR.viewModel, viewModel)
    }

    private fun observeUI() {
        viewModel.uiState.observe(viewLifecycleOwner, uiObserver)
    }

    private fun handleError(errorMessageId: Int) {
        showMessage(errorMessageId)
    }

    private fun handleLoading() {
        showMessage(R.string.loading)
    }

    private fun showMessage(messageId: Int) {
        Toast.makeText(activity?.applicationContext, getString(messageId), Toast.LENGTH_SHORT).show()
    }
}
