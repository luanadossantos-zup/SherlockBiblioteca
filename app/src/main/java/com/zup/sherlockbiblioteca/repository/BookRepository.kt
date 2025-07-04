package com.zup.sherlockbiblioteca.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zup.sherlockbiblioteca.model.Book

class BookRepository (
    private val context: Context
) {
    fun loadBooksFromJSON(): List<Book> {
        return try {
            val json = context.assets.open("livros.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<Book>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}