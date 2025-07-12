package com.ajessondaniel.bookmanagement.domain.usecase

import com.ajessondaniel.bookmanagement.domain.model.Book
import com.ajessondaniel.bookmanagement.domain.port.BookPort

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }

    fun addBook(book: Book): Boolean {
        val exists = bookPort.getAllBooks().any { it.name == book.name && it.author == book.author }
        if (exists) return false
        bookPort.createBook(book)
        return true
    }

    fun reserveBook(name: String, author: String): Boolean {
        val book = bookPort.getAllBooks().find { it.name == name && it.author == author }
        if (book == null || book.isReserved) return false
        return bookPort.reserveBook(name, author)
    }
}