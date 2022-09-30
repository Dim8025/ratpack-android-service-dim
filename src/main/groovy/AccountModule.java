import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AccountModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountService.class).in(Scopes.SINGLETON);
        bind(AccountDbCommands.class).in(Scopes.SINGLETON);
        bind(AccountEndpoint.class).in(Scopes.SINGLETON);
    }
}
