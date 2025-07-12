package com.ajessondaniel.bookmanagement.infrastructure.driving.web

import com.ajessondaniel.bookmanagement.domain.model.Book
import com.ajessondaniel.bookmanagement.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest
class BookControllerIT(
    @MockkBean private val bookUseCase: BookUseCase,
    private val mockMvc: MockMvc
) : FunSpec({
    extension(SpringExtension)

    test("rest route get books") {
        // GIVEN
        every { bookUseCase.getAllBooks() } returns listOf(Book("A", "B"))

        // WHEN
        mockMvc.get("/books")
            //THEN
            .andExpect {
                status { isOk() }
                content { content { APPLICATION_JSON } }
                content {
                    json(
                        // language=json
                        """
                        [
                          {
                            "name": "A",
                            "author": "B"
                          }
                        ]
                        """.trimIndent()
                    )
                }
            }
    }

    test("rest route post book") {
        every { bookUseCase.addBook(any()) } returns true

        mockMvc.post("/books") {
            // language=json
            content = """
                {
                  "name": "Les misérables",
                  "author": "Victor Hugo"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }

        val expected = Book(
            name = "Les misérables",
            author = "Victor Hugo"
        )

        verify(exactly = 1) { bookUseCase.addBook(expected) }
    }

    test("rest route post book should return 400 when body is not good") {
        every { bookUseCase.addBook(any()) } returns true

        mockMvc.post("/books") {
            // language=json
            content = """
                {
                  "title": "Les misérables",
                  "author": "Victor Hugo"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 0) { bookUseCase.addBook(any()) }
    }

    test("rest route post book should return 201 and message when created") {
        every { bookUseCase.addBook(any()) } returns true
        mockMvc.post("/books") {
            content = """
                { "name": "Titre", "author": "Auteur", "isReserved": false }
            """.trimIndent()
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content { string("Livre créé avec succès") }
        }
    }

    test("rest route post book should return 409 and message when duplicate") {
        every { bookUseCase.addBook(any()) } returns false
        mockMvc.post("/books") {
            content = """
                { "name": "Titre", "author": "Auteur", "isReserved": false }
            """.trimIndent()
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isConflict() }
            content { string("Un livre avec ce titre et cet auteur existe déjà") }
        }
    }

    test("rest route post reserve should return 200 when ok") {
        every { bookUseCase.reserveBook("Titre", "Auteur") } returns true
        mockMvc.post("/books/Titre/Auteur/reserve")
            .andExpect {
                status { isOk() }
                content { string("Réservation effectuée") }
            }
    }

    test("rest route post reserve should return 409 when already reserved") {
        every { bookUseCase.reserveBook("Titre", "Auteur") } returns false
        mockMvc.post("/books/Titre/Auteur/reserve")
            .andExpect {
                status { isConflict() }
                content { string("Livre déjà réservé ou inexistant") }
            }
    }
})