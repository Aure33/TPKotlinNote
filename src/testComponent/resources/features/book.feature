Feature: the user can create and retrieve the books
  Scenario: user creates two books and retrieve both of them
    When the user creates the book "Les Misérables" written by "Victor Hugo"
    And the user creates the book "L'avare" written by "Molière"
    And the user get all books
    Then the list should contains the following books in the same order
      | title | author | isBorrowed |
      | Les Misérables | Victor Hugo | false |
      | L'avare | Molière | false |

  Scenario: user borrows a book
    When the user creates the book "1984" written by "George Orwell"
    And the user borrows the book with id 1
    And the user get all books
    Then the list should contains the following books in the same order
      | title | author | isBorrowed |
      | 1984 | George Orwell | true |

  Scenario: user unborrows a book
    When the user creates the book "Brave New World" written by "Aldous Huxley"
    And the user borrows the book with id 1
    And the user unborrows the book with id 1
    And the user get all books
    Then the list should contains the following books in the same order
      | title | author | isBorrowed |
      | Brave New World | Aldous Huxley | false |

  Scenario: user tries to borrow a non-existent book
    When the user tries to borrow the book with id 999
    Then the user should get an error "Book with id '999' not found."

  Scenario: user tries to borrow an already borrowed book
    When the user creates the book "The Hobbit" written by "J.R.R. Tolkien"
    And the user borrows the book with id 1
    And the user tries to borrow the book with id 1
    Then the user should get an error "The book is already in the requested borrowed state."