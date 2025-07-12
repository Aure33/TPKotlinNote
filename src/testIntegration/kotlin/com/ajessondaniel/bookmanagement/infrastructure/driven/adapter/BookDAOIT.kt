package com.ajessondaniel.bookmanagement.infrastructure.driven.adapter

import com.ajessondaniel.bookmanagement.domain.model.Book
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT(
    private val bookDAO: BookDAO
) : FunSpec() {
    init {
        extension(SpringExtension)

        beforeTest {
            performQuery(
                // language=sql
                "DELETE FROM book"
            )
        }

        test("get all books from db") {
            // GIVEN
            performQuery(
                // language=sql
                """
               insert into book (title, author)
               values 
                   ('Hamlet', 'Shakespeare'),
                   ('Les fleurs du mal', 'Beaudelaire'),
                   ('Harry Potter', 'Rowling');
            """.trimIndent()
            )

            // WHEN
            val res = bookDAO.getAllBooks()

            // THEN
            res.shouldContainExactlyInAnyOrder(
                Book("Hamlet", "Shakespeare"), Book("Les fleurs du mal", "Beaudelaire"), Book("Harry Potter", "Rowling")
            )
        }

        test("create book in db") {
            // GIVEN
            // WHEN
            bookDAO.createBook(Book("Les misérables", "Victor Hugo"))

            // THEN
            val res = performQuery(
                // language=sql
                "SELECT * from book"
            )

            res shouldHaveSize 1
            assertSoftly(res.first()) {
                this["id"].shouldNotBeNull().shouldBeInstanceOf<Int>()
                this["title"].shouldBe("Les misérables")
                this["author"].shouldBe("Victor Hugo")
            }
        }

        test("reserve book in db should set is_reserved to true") {
            // GIVEN
            bookDAO.createBook(Book("Titre", "Auteur"))
            // WHEN
            val reserved = bookDAO.reserveBook("Titre", "Auteur")
            // THEN
            reserved shouldBe true
            val res = performQuery("SELECT * FROM book WHERE title = 'Titre' AND author = 'Auteur'")
            res.first()["is_reserved"] shouldBe true
        }

        test("reserve book in db should return false if already reserved") {
            // GIVEN
            bookDAO.createBook(Book("Titre", "Auteur"))
            bookDAO.reserveBook("Titre", "Auteur")
            // WHEN
            val reserved = bookDAO.reserveBook("Titre", "Auteur")
            // THEN
            reserved shouldBe false
        }

        test("createBook should allow only unique title/author") {
            // GIVEN
            bookDAO.createBook(Book("Titre", "Auteur"))
            // WHEN/THEN
            shouldThrowAny {
                bookDAO.createBook(Book("Titre", "Auteur"))
            }
        }

        afterSpec {
            container.stop()
        }
    }

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }

        private fun ResultSet.toList(): List<Map<String, Any>> {
            val md = this.metaData
            val columns = md.columnCount
            val rows: MutableList<Map<String, Any>> = ArrayList()
            while (this.next()) {
                val row: MutableMap<String, Any> = HashMap(columns)
                for (i in 1..columns) {
                    row[md.getColumnName(i)] = this.getObject(i)
                }
                rows.add(row)
            }
            return rows
        }

        fun performQuery(sql: String): List<Map<String, Any>> {
            val hikariConfig = HikariConfig()
            hikariConfig.setJdbcUrl(container.jdbcUrl)
            hikariConfig.username = container.username
            hikariConfig.password = container.password
            hikariConfig.setDriverClassName(container.driverClassName)

            val ds = HikariDataSource(hikariConfig)

            val statement = ds.connection.createStatement()
            statement.execute(sql)
            val resultSet = statement.resultSet
            return resultSet?.toList() ?: listOf()
        }
    }
}