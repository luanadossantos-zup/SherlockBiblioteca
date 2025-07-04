package com.zup.sherlockbiblioteca

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zup.sherlockbiblioteca.adapter.BookAdapter
import com.zup.sherlockbiblioteca.databinding.ActivityMainBinding
import com.zup.sherlockbiblioteca.repository.BookRepository
import com.zup.sherlockbiblioteca.viewmodel.MainViewModel
import com.zup.sherlockbiblioteca.viewmodel.MainViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Repository e a factory
        val repository = BookRepository(applicationContext)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

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
                if (lastVisibleItem >= totalItens - 2) {
                    viewModel.loadMore()
                }
            }
        })

        //6 -------------------
        //A Activity vê a lista atualizada e os troca da RecyclerView
        lifecycleScope.launch {
            viewModel.visibleBooks.collectLatest { list ->
                adapter.submitList(list)
            }
        }

        //1 -------------------------
        //A Activity percebe o que foi digitado,
        // chama updateQuery e entrega o texto digitado
        //
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