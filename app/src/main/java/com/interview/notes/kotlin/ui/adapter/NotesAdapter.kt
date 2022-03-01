package com.interview.notes.kotlin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.interview.notes.BR
import com.interview.notes.R
import com.interview.notes.kotlin.viewmodel.NoteItemViewModel
import java.util.ArrayList

class NotesAdapter(
    private val onItemClickedListener: ((String) -> Unit)? = null
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val items: MutableList<NoteItemViewModel> = ArrayList()

    fun setNotes(freshNotes: List<NoteItemViewModel>) {
        items.clear()
        items.addAll(freshNotes)
        notifyDataSetChanged()
    }

    fun updateItem(item: NoteItemViewModel) {
        // remove if already exists
        val existingItem = items.find { it.noteId == item.noteId }
        val index = items.indexOf(existingItem)
        if (index > -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }

        // add new item to top of list
        items.add(0, item)
        notifyItemInserted(0)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.note_item,
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val viewModel = items[position]
        viewModel.clickListener = onItemClickedListener
        holder.bind(viewModel)
    }

    class NoteViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemViewModel: NoteItemViewModel) {
            binding.setVariable(BR.itemViewModel, itemViewModel)
        }
    }
}
