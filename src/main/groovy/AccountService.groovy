import groovy.sql.GroovyRowResult
import groovy.util.logging.Slf4j
import rx.Observable

import javax.inject.Inject

@Slf4j
class AccountService {

    private final AccountDbCommands accountDbCommands

    @Inject
    BookService(AccountDbCommands accountDbCommands) {
        this.accountDbCommands = accountDbCommands
    }

    void createTable() {
        log.info("Creating database tables")
        accountDbCommands.createTables()
    }

    Observable<Book> all() {
        accountDbCommands.getAll().map { row ->
            new Account(
                    row.id,
                    row.name
            )
        }
    }

    Observable<String> insert(long name) {
        accountDbCommands.insert(name)
        .map { id }
    }

    Observable<Book> find(String isbn) {
        accountDbCommands.find(name)
        .map { GroovyRowResult dbRow ->
            return new Account(
                    dbRow.id,
                    dbRow.nme
            )
        }
    }

    Observable<Void> update(String id, long name) {
        accountDbCommands.update(id, name)
    }

    Observable<Void> delete(String name) {
        accountDbCommands.delete(name)
    }
}
