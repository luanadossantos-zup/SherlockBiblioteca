package com.zup.sherlockbiblioteca.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.zup.sherlockbiblioteca.model.Livro
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

    private val _livrosVisiveis = MutableStateFlow<List<Livro>>(emptyList())
    val livrosVisiveis: StateFlow<List<Livro>> = _livrosVisiveis.asStateFlow()

    private var todosOsLivros: List<Livro> = emptyList()
    private var resultadosFiltrados: List<Livro> = emptyList()

    private var tipoSelecionado: String? = null // pode ser "Conto", "Romance" ou null (sem filtro)

    private val pageSize = 10
    private var paginaAtual = 0

    init {
        viewModelScope.launch {
            todosOsLivros = carregarLivrosDoJSON(application)
            resultadosFiltrados = todosOsLivros
            carregarPagina()
        }

        viewModelScope.launch {
            query
                .debounce(300)
                .distinctUntilChanged()
                .collect { texto ->
                    paginaAtual = 0

                    // Aplica o filtro por texto + tipo, se houver
                    resultadosFiltrados = todosOsLivros.filter { livro ->
                        val casaComBusca = texto.length < 3 || livro.titulo.contains(texto, ignoreCase = true)
                        val casaComTipo = tipoSelecionado == null || livro.tipo == tipoSelecionado
                        casaComBusca && casaComTipo
                    }

                    carregarPagina()
                }

        }

    }

    fun atualizarQuery(nova: String) {
        _query.value = nova //Main Activity chama a função
    }

    fun carregarMais() {
        if ((paginaAtual + 1) * pageSize < resultadosFiltrados.size) {
            paginaAtual++
            carregarPagina()
        }
    }

    private fun carregarPagina() {
        val fim = ((paginaAtual + 1) * pageSize).coerceAtMost(resultadosFiltrados.size)
        val novaLista = resultadosFiltrados.take(fim)
        _livrosVisiveis.value = novaLista
    }

    private fun carregarLivrosDoJSON(context: Context): List<Livro> {
        val json = context.assets.open("livros.json")
            .bufferedReader()
            .use { it.readText() }

        val tipo = object : TypeToken<List<Livro>>() {}.type
        return Gson().fromJson(json, tipo)
    }

    fun filtrarContos() {
        tipoSelecionado = "Conto"
        aplicarFiltro()
    }

    fun filtrarRomances() {
        tipoSelecionado = "Romance"
        aplicarFiltro()
    }

    fun limparFiltroTipo() {
        tipoSelecionado = null
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        paginaAtual = 0

        resultadosFiltrados = todosOsLivros.filter { livro ->
            val texto = query.value
            val casaComBusca = texto.length < 3 || livro.titulo.contains(texto, ignoreCase = true)
            val casaComTipo = tipoSelecionado == null || livro.tipo == tipoSelecionado
            casaComBusca && casaComTipo
        }

        carregarPagina()
    }


}

