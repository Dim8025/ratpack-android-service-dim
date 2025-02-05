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
        logger.info ("> execute AccountEndpoint GroovyChainAction 1")
        
        all {
            byMethod {
                get {
                    logger.info ("> all get")

                    accountService
                    .all()
                    .toList()
                    .subscribe { List<Account> accounts ->
                        render json(accounts)
                    }
                }
            }
        }

        logger.info ("> execute AccountEndpoint GroovyChainAction 2")

        post("new") {
            logger.info ("> post new")

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

        logger.info ("> execute AccountEndpoint GroovyChainAction 3")

        path(":name") {
            logger.info ("> path name")

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
    }
}
