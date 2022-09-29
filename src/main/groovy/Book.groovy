import groovy.transform.Immutable

@Immutable
class Book {
    String isbn
    long quantity
    BigDecimal price
}
