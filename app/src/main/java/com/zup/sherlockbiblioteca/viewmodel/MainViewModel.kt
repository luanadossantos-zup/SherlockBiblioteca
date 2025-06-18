package com.zup.sherlockbiblioteca.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.zup.sherlockbiblioteca.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.distinctUntilChanged

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _visibleBooks = MutableStateFlow<List<Book>>(emptyList())
    val visibleBooks: StateFlow<List<Book>> = _visibleBooks.asStateFlow()

    protected var allBooks: List<Book> = emptyList()
    protected var filteredResults: List<Book> = emptyList()

    private var selectedFormatOfStories: String? = null

    private val pageSize = 10
    private var currentPage = 0

    init {
        viewModelScope.launch {
            allBooks = loadBooksFromJSON(application)
            filteredResults = allBooks
            loadPage()
        }

        viewModelScope.launch {
            query
                .debounce(300)
                .distinctUntilChanged()
                .collect { text ->
                    filterBooks(text)
                }
        }
    }

    fun updateQuery(new: String) {
        _query.value = new //Main Activity chama a função
    }

    fun loadMore() {
        if ((currentPage + 1) * pageSize < filteredResults.size) {
            currentPage++
            loadPage()
        }
    }

    protected fun loadPage() {
        val end = ((currentPage + 1) * pageSize).coerceAtMost(filteredResults.size)
        val newList = filteredResults.take(end)
        _visibleBooks.value = newList
    }

    private fun loadBooksFromJSON(context: Context): List<Book> {
        val json = context.assets.open("livros.json")
            .bufferedReader()
            .use { it.readText() }

        val formatOfStories = object : TypeToken<List<Book>>() {}.type
        return Gson().fromJson(json, formatOfStories)
    }

    private fun filterBooks(text: String = query.value) {
        currentPage = 0
        filteredResults = allBooks.filter { book ->
            val matchSearch = text.length < 3 || book.title.contains(text, ignoreCase = true)
            val matchFormat = selectedFormatOfStories == null || book.format == selectedFormatOfStories
            matchSearch && matchFormat
        }
        loadPage()
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

