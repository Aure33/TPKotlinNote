package com.ajessondaniel.bookmanagement.domain.model

data class Book(
    val name: String,
    val author: String,
    val isReserved: Boolean = false
) {
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        require(author.isNotBlank()) { "Name must not be blank" }
    }
}
