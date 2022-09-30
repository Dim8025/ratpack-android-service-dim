import groovy.sql.GroovyRowResult
import groovy.util.logging.Slf4j
import rx.Observable

import javax.inject.Inject

@Slf4j
class AccountService {

    private AccountDbCommands accountDbCommands

    @Inject
    BookService(AccountDbCommands accountDbCommands) {
        this.accountDbCommands = accountDbCommands
    }

    void createTable() {
        log.info("Creating database tables")
        accountDbCommands.createTables()
    }

    Observable<Account> all() {
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

    Observable<Account> find(String isbn) {
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
