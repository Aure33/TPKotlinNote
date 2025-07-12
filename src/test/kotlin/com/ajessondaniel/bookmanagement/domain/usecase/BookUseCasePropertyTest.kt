package com.ajessondaniel.bookmanagement.domain.usecase

import com.ajessondaniel.bookmanagement.domain.model.Book
import com.ajessondaniel.bookmanagement.domain.port.BookPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll

class InMemoryBookPort : BookPort {
    private val books = mutableListOf<Book>()

    override fun getAllBooks(): List<Book> = books

    override fun createBook(book: Book) {
        books.add(book)
    }

    override fun reserveBook(name: String, author: String): Boolean {
        val book = books.find { it.name == name && it.author == author }
        return book != null
    }

    fun clear() {
        books.clear()
    }
}

class BookUseCasePropertyTest : FunSpec({

    val bookPort = InMemoryBookPort()
    val bookUseCase = BookUseCase(bookPort)

    test("should return all elements in the alphabetical order") {
        checkAll(Arb.int(1..100)) { nbItems ->
            bookPort.clear()

            val arb = Arb.stringPattern("""[a-z]{1,10}""")

            val titles = mutableListOf<String>()

            for (i in 1..nbItems) {
                val title = arb.next()
                // Ajout uniquement si pas déjà présent (unicité)
                if (bookUseCase.addBook(Book(title, "Victor Hugo"))) {
                    titles.add(title)
                }
            }

            val res = bookUseCase.getAllBooks()

            res.map { it.name } shouldContainExactly titles.sorted()
        }
    }
})