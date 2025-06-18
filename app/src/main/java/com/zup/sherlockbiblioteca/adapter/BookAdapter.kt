package com.zup.sherlockbiblioteca.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zup.sherlockbiblioteca.databinding.ItemBookBinding
import com.zup.sherlockbiblioteca.model.Book

class BookAdapter : ListAdapter<Book, BookAdapter.LivroViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LivroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LivroViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tituloLivro.text = book.title
            binding.anoLivro.text = "Ano: ${book.year}"
            binding.descricaoLivro.text = book.description
            binding.tipoLivro.text = book.format
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
