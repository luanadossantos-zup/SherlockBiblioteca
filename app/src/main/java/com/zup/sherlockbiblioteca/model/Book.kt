package com.zup.sherlockbiblioteca.model

import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("titulo")
    val title: String,

    @SerializedName("ano")
    val year: Int,

    @SerializedName("descricao")
    val description: String,

    @SerializedName("tipo")
    val format: String
)
