import ratpack.groovy.handling.GroovyChainAction

import javax.inject.Inject

import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode
import java.util.logging.Logger

class AccountEndpoint extends GroovyChainAction {

    private final AccountService accountService
    private final Logger logger

    @Inject
    AccountEndpoint(AccountService accountService) {
        this.accountService = accountService
        this.logger = Logger.getLogger("")
    }

    @Override
    void execute() throws Exception {
        logger.info ("> execute AccountEndpoint GroovyChainAction")

        post("new") {
            parse(jsonNode())
            .observe()
            .flatMap { input ->
                accountService.insert(input.get("name").asText())
            }
            .single()
            .flatMap { name ->
                accountService.find(name)
            }
            .single()
            .subscribe { Account createdAccount ->
                render json(createdAccount)
            }
        }

        path(":name") {
            def name = pathTokens["name"]

            byMethod {
                get {
                    accountService
                    .find(name)
                    .single()
                    .subscribe { Account account ->
                        if (account == null) {
                            clientError 404
                        } else {
                            render json(account)
                        }
                    }
                }
            }
        }

        all {
            byMethod {
                get {
                    logger.info "AccountService - all - get"

                    accountService
                    .all()
                    .toList()
                    .subscribe { List<Account> accounts ->
                        render json(accounts)
                    }
                }
            }
        }
    }
}
