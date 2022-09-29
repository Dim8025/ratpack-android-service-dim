import ratpack.groovy.handling.GroovyChainAction

import javax.inject.Inject

import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode

class BookEndpoint extends GroovyChainAction {

  private final BookService bookService

  @Inject
  BookEndpoint(BookService bookService) {
    this.bookService = bookService
  }

  @Override
  void execute() throws Exception {
    post("new") {
      parse(jsonNode()).
              observe().
              flatMap { input ->
                bookService.insert(
                        input.get("isbn").asText(),
                        0l,
                        0d
                )
              }.
              single().
              flatMap { isbn ->
                bookService.find(isbn)
              }.
              single().
              subscribe { Book createdBook ->
                render json(createdBook)
              }
    }

    path(":isbn") {
      def isbn = pathTokens["isbn"]

      byMethod {
        get {
          bookService.find(isbn).
              single().
              subscribe { Book book ->
            if (book == null) {
              clientError 404
            } else {
              render json(book)
            }
          }
        }
        put {
          parse(jsonNode()).
              observe().
              flatMap { input ->
              bookService.update(
                  isbn,
                  0l,
                  0d
              )
          }.
          flatMap {
            bookService.find(isbn)
          }.
          single().
              subscribe { Book book ->
            render json(book)
          }
        }
        delete {
          bookService.delete(isbn).
              subscribe {
            response.send()
          }
        }
      }
    }

    all {
      byMethod {
        get {
          bookService.all().
                  toList().
                  subscribe { List<Book> books ->
                    render json(books)
                  }
        }
      }
    }

  }
}
