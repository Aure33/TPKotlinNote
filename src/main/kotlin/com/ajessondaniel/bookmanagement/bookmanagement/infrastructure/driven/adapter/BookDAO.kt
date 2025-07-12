package com.ajessondaniel.bookmanagement.infrastructure.driven.adapter

import com.ajessondaniel.bookmanagement.domain.model.Book
import com.ajessondaniel.bookmanagement.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): BookPort {
    override fun getAllBooks(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK", MapSqlParameterSource()) { rs, _ ->
                Book(
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    isReserved = rs.getBoolean("is_reserved")
                )
            }
    }

    override fun createBook(book: Book) {
        val params = MapSqlParameterSource()
            .addValue("title", book.name)
            .addValue("author", book.author)
        val exists = namedParameterJdbcTemplate.query(
            "SELECT COUNT(*) FROM BOOK WHERE title = :title AND author = :author",
            params
        ) { rs, _ -> rs.getInt(1) }.firstOrNull() ?: 0
        if (exists > 0) {
            throw IllegalArgumentException("Book with same title and author already exists")
        }
        namedParameterJdbcTemplate
            .update("INSERT INTO BOOK (title, author) values (:title, :author)", mapOf(
                "title" to book.name,
                "author" to book.author
            ))
    }

    override fun reserveBook(name: String, author: String): Boolean {
        val params = MapSqlParameterSource()
            .addValue("title", name)
            .addValue("author", author)
        val updated = namedParameterJdbcTemplate.update(
            "UPDATE BOOK SET is_reserved = true WHERE title = :title AND author = :author AND is_reserved = false",
            params
        )
        return updated > 0
    }
}