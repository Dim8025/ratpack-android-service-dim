import ratpack.groovy.handling.GroovyChainAction

import javax.inject.Inject

import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode
import ratpack.handling.RequestLogger

class AccountEndpoint extends GroovyChainAction {

    final Logger logger = LoggerFactory.getLogger(ratpack.class);
    private final AccountService accountService

    @Inject
    AccountEndpoint(AccountService accountService) {
        this.accountService = accountService
    }

    @Override
    void execute() throws Exception {
        RequestLogger.ncsa(logger)
        logger.info "> execute AccountEndpoint GroovyChainAction"

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
