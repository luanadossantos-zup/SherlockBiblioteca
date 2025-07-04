package com.zup.sherlockbiblioteca.viewmodel

import androidx.lifecycle.ViewModel
import com.zup.sherlockbiblioteca.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.zup.sherlockbiblioteca.repository.BookRepository
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged

class MainViewModel (private val repository: BookRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val query: StateFlow<String> = _query.asStateFlow()

    private val _visibleBooks = MutableStateFlow<List<Book>>(emptyList())
    val visibleBooks: StateFlow<List<Book>> = _visibleBooks.asStateFlow()

    private var allBooks: List<Book> = emptyList()
    private var filteredResults: List<Book> = emptyList()

    private var selectedFormatOfStories: String? = null

    private val pageSize = 10
    private var currentPage = 0

    init {
        viewModelScope.launch {
            allBooks = repository.loadBooksFromJSON()
            filteredResults = allBooks
            emitVisiblePage()
        }

        //3 -----------------
        //O Flow query dispara uma reação
        //Esse bloco do init do ViewModel escuta o Flow:
        viewModelScope.launch {
            query
                .debounce(300)
                .distinctUntilChanged()
                .collect { text ->
                    filterBooks(text)
                }
        }
    }

    //2 --------------------
    // A ViewModel atualiza o flow query, no caso ela emite um novo valor
    //no StateFlow chamado query
    //
    fun updateQuery(new: String) {
        _query.value = new //Main Activity chama a função
    }

    fun loadMore() {
        if ((currentPage + 1) * pageSize < filteredResults.size) {
            currentPage++
            emitVisiblePage()
        }
    }

    // 5 ----------------
    // A ViewModel emite a nova lista de livros visíveis via StateFlow.
    private fun emitVisiblePage() {
        val end = ((currentPage + 1) * pageSize).coerceAtMost(filteredResults.size)
        val newList = filteredResults.take(end)
        _visibleBooks.value = newList
    }

    // 4 -------------
    //O texto digitado é passado para filterBooks(text)
    //A lista filteredResults é atualizada e a primeira “página” (10 livros) é enviada
    private fun filterBooks(text: String = query.value) {
        currentPage = 0
        filteredResults = allBooks.filter { book ->
            val matchSearch = text.length < 3 || book.title.contains(text, ignoreCase = true)
            val matchFormat = selectedFormatOfStories == null || book.format == selectedFormatOfStories
            matchSearch && matchFormat
        }
        emitVisiblePage()
    }

    fun filterShortStories() {
        selectedFormatOfStories = "Conto"
        filterBooks()
    }

    fun filterNovels() {
        selectedFormatOfStories = "Romance"
        filterBooks()
    }

    fun cleanFilterFormat() {
        selectedFormatOfStories = null
        filterBooks()
    }
}

