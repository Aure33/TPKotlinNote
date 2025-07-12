package com.ajessondaniel.bookmanagement.domain.port

import com.ajessondaniel.bookmanagement.domain.model.Book

interface BookPort {
    fun getAllBooks(): List<Book>
    fun createBook(book: Book)
    fun reserveBook(name: String, author: String): Boolean
}