import groovy.sql.GroovyRowResult
import groovy.util.logging.Slf4j
import rx.Observable

import javax.inject.Inject

@Slf4j
class BookService {

    private final BookDbCommands bookDbCommands

    @Inject
    BookService(BookDbCommands bookDbCommands) {
        this.bookDbCommands = bookDbCommands
    }

    void createTable() {
        log.info("Creating database tables")
        bookDbCommands.createTables()
    }

    Observable<Book> all() {
        bookDbCommands.getAll().map { row ->
            new Book(
                    row.isbn,
                    row.quantity,
                    row.price
            )
        }
    }

    Observable<String> insert(String isbn, long quantity, BigDecimal price) {
        bookDbCommands.insert(isbn, quantity, price).
                map {
                    isbn
                }
    }

    Observable<Book> find(String isbn) {
        bookDbCommands.find(isbn).map { GroovyRowResult dbRow ->
            return new Book(
                    isbn,
                    dbRow.quantity,
                    dbRow.price
            )
        }
    }

    Observable<Void> update(String isbn, long quantity, BigDecimal price) {
        bookDbCommands.update(isbn, quantity, price)
    }

    Observable<Void> delete(String isbn) {
        bookDbCommands.delete(isbn)
    }
}