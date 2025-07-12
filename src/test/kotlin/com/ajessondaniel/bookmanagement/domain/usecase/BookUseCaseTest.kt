package com.ajessondaniel.bookmanagement.domain.usecase

import com.ajessondaniel.bookmanagement.domain.model.Book
import com.ajessondaniel.bookmanagement.domain.port.BookPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : FunSpec({

    val bookPort = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookPort)

    beforeTest {
        clearMocks(bookPort)
    }

    test("get all books should returns all books sorted by name") {
        every { bookPort.getAllBooks() } returns listOf(
            Book("Les Misérables", "Victor Hugo"),
            Book("Hamlet", "William Shakespeare")
        )

        val res = bookUseCase.getAllBooks()

        res.shouldContainExactly(
            Book("Hamlet", "William Shakespeare"),
            Book("Les Misérables", "Victor Hugo")
        )
    }

    test("add book") {
        every { bookPort.getAllBooks() } returns emptyList()
        justRun { bookPort.createBook(any()) }

        val book = Book("Les Misérables", "Victor Hugo")

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }

    test("addBook returns true when book does not exist") {
        every { bookPort.getAllBooks() } returns listOf()
        justRun { bookPort.createBook(any()) }
        val book = Book("Titre", "Auteur")
        val result = bookUseCase.addBook(book)
        result shouldBe true
        verify { bookPort.createBook(book) }
    }

    test("addBook returns false when book already exists") {
        every { bookPort.getAllBooks() } returns listOf(Book("Titre", "Auteur"))
        val book = Book("Titre", "Auteur")
        val result = bookUseCase.addBook(book)
        result shouldBe false
        verify(exactly = 0) { bookPort.createBook(any()) }
    }

    test("reserveBook returns true when book is available") {
        every { bookPort.getAllBooks() } returns listOf(Book("Titre", "Auteur"))
        every { bookPort.reserveBook("Titre", "Auteur") } returns true
        val result = bookUseCase.reserveBook("Titre", "Auteur")
        result shouldBe true
    }

    test("reserveBook returns false when book is already reserved") {
        every { bookPort.getAllBooks() } returns listOf(Book("Titre", "Auteur", isReserved = true))
        val result = bookUseCase.reserveBook("Titre", "Auteur")
        result shouldBe false
    }

    test("reserveBook returns false when book does not exist") {
        every { bookPort.getAllBooks() } returns listOf()
        val result = bookUseCase.reserveBook("Titre", "Auteur")
        result shouldBe false
    }

})