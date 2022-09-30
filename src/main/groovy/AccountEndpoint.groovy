import ratpack.groovy.handling.GroovyChainAction

import javax.inject.Inject

import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode

class AccountEndpoint extends GroovyChainAction {

    private final AccountService accountService

    @Inject
    AccountEndpoint(AccountService accountService) {
        this.accountService = accountService
    }

    @Override
    void execute() throws Exception {
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
