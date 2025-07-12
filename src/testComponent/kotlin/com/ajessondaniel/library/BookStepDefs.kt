package com.ajessondaniel.library

import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.beans.factory.annotation.Autowired

class BookStepDefs {
    @LocalServerPort
    private var port: Int? = 0

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        // Clean database before each scenario
        jdbcTemplate.execute("DELETE FROM book")
    }

    @When("the user creates the book {string} written by {string}")
    fun createBook(title: String, author: String) {
        given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "title": "$title",
                      "author": "$author"
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("the user get all books")
    fun getAllBooks() {
        lastBookResult = given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }

    @When("the user borrows the book with id {int}")
    fun borrowBook(id: Int) {
        lastBookResult = given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "isBorrowed": true
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books/$id/borrow")
            .then()
            .statusCode(200)
    }

    @When("the user unborrows the book with id {int}")
    fun unborrowBook(id: Int) {
        lastBookResult = given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "isBorrowed": false
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books/$id/borrow")
            .then()
            .statusCode(200)
    }

    @When("the user tries to borrow the book with id {int}")
    fun tryToBorrowBook(id: Int) {
        lastBookResult = given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "isBorrowed": true
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books/$id/borrow")
            .then()
            .statusCode(400)
    }

    @Then("the list should contains the following books in the same order")
    fun shouldHaveListOfBooks(payload: List<Map<String, String>>) {
        val actualBooks = lastBookResult.extract().body().jsonPath().getList("", Map::class.java)

        actualBooks.size shouldBe payload.size

        payload.forEachIndexed { index, expectedLine ->
            val actualBook = actualBooks[index] as Map<*, *>
            // Vérifier que l'ID existe et est un nombre
            actualBook["id"] shouldBe (index + 1)
            actualBook["title"] shouldBe expectedLine["title"]
            actualBook["author"] shouldBe expectedLine["author"]
            actualBook["isBorrowed"] shouldBe expectedLine["isBorrowed"]!!.toBoolean()
        }
    }

    @Then("the user should get an error {string}")
    fun shouldGetError(expectedMessage: String) {
        val response = lastBookResult.extract().body().asString()
        response shouldBe expectedMessage
    }

    companion object {
        lateinit var lastBookResult: ValidatableResponse
    }
}