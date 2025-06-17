package com.zup.sherlockbiblioteca.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zup.sherlockbiblioteca.databinding.ItemLivroBinding
import com.zup.sherlockbiblioteca.model.Livro

class LivroAdapter : ListAdapter<Livro, LivroAdapter.LivroViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val binding = ItemLivroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LivroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LivroViewHolder(private val binding: ItemLivroBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(livro: Livro) {
            binding.tituloLivro.text = livro.titulo
            binding.anoLivro.text = "Ano: ${livro.ano}"
            binding.descricaoLivro.text = livro.descricao
            binding.tipoLivro.text = livro.tipo
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Livro>() {
        override fun areItemsTheSame(oldItem: Livro, newItem: Livro): Boolean {
            return oldItem.titulo == newItem.titulo
        }

        override fun areContentsTheSame(oldItem: Livro, newItem: Livro): Boolean {
            return oldItem == newItem
        }
    }
}
