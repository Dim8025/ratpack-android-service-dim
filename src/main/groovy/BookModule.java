import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class BookModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(BookService.class).in(Scopes.SINGLETON);
    bind(BookDbCommands.class).in(Scopes.SINGLETON);
    bind(BookEndpoint.class).in(Scopes.SINGLETON);
  }

}