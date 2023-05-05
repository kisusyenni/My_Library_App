package com.dicoding.mylibrary

data class Book(
    var title: String = "",
    var author: String = "",
    var synopsis: String = "",
    var image: Int = 0,
    var rating: Double = 0.0,
    var publisher: String = "",
    var pages: Int = 0,
    var isbn: String = "",
    var language: String = "",
    var genres: String = "",
    var status: Int = 0,
    var isFavorite: Boolean = false
)