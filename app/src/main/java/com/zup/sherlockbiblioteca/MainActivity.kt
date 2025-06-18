package com.zup.sherlockbiblioteca

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zup.sherlockbiblioteca.adapter.BookAdapter
import com.zup.sherlockbiblioteca.databinding.ActivityMainBinding
import com.zup.sherlockbiblioteca.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o RecyclerView
        adapter = BookAdapter()
        binding.recyclerBooks.layoutManager = LinearLayoutManager(this)
        binding.recyclerBooks.adapter = adapter

        // Detecta scroll infinito
        binding.recyclerBooks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItens = adapter.itemCount
                if (lastVisibleItem >= totalItens - 2) { // margem de segurança
                    viewModel.loadMore()
                }
            }
        })

        // Observa os livros visíveis
        lifecycleScope.launch {
            viewModel.visibleBooks.collectLatest { list ->
                adapter.submitList(list)
            }
        }

        // Atualiza o campo de busca
        binding.campoBusca.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateQuery(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        with(binding) {
            botaoContos.setOnClickListener {
                viewModel.filterShortStories()
            }

            botaoRomances.setOnClickListener {
                viewModel.filterNovels()
            }

            botaoMostrarTudo.setOnClickListener {
                viewModel.cleanFilterFormat()
            }
        }
    }
}