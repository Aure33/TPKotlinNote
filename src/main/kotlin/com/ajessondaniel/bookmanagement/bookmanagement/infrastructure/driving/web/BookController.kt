package com.ajessondaniel.bookmanagement.infrastructure.driving.web

import com.ajessondaniel.bookmanagement.domain.usecase.BookUseCase
import com.ajessondaniel.bookmanagement.infrastructure.driving.web.dto.BookDTO
import com.ajessondaniel.bookmanagement.infrastructure.driving.web.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(
    private val bookUseCase: BookUseCase
) {
    @CrossOrigin
    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks()
            .map { it.toDto() }
    }

    @CrossOrigin
    @PostMapping
    fun addBook(@RequestBody bookDTO: BookDTO): org.springframework.http.ResponseEntity<String> {
        val created = bookUseCase.addBook(bookDTO.toDomain())
        return if (created) org.springframework.http.ResponseEntity.status(HttpStatus.CREATED).body("Livre créé avec succès")
        else org.springframework.http.ResponseEntity.status(HttpStatus.CONFLICT).body("Un livre avec ce titre et cet auteur existe déjà")
    }

    @CrossOrigin
    @PostMapping("/{name}/{author}/reserve")
    fun reserveBook(
        @PathVariable name: String,
        @PathVariable author: String
    ): org.springframework.http.ResponseEntity<String> {
        val reserved = bookUseCase.reserveBook(name, author)
        return if (reserved) org.springframework.http.ResponseEntity.ok("Réservation effectuée")
        else org.springframework.http.ResponseEntity.status(HttpStatus.CONFLICT).body("Livre déjà réservé ou inexistant")
    }

}