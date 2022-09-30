import com.google.inject.Inject
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixObservableCommand
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import ratpack.exec.Blocking

import static ratpack.rx.RxRatpack.observe
import static ratpack.rx.RxRatpack.observeEach

class AccountDbCommands {

    private final Sql sql
    private static final HystrixCommandGroupKey hystrixCommandGroupKey = HystrixCommandGroupKey.Factory.asKey("sql-accountdb")

    @Inject
    public AccountDbCommands(Sql sql) {
        this.sql = sql
    }

    void createTables() {
        sql.execute("drop table if exists account")
        sql.execute("create table account (id varchar(13) primary key, name varchar(80))")
    }

    rx.Observable<GroovyRowResult> getAll() {
        return new HystrixObservableCommand<GroovyRowResult>(
                HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("getAll"))) {

            @Override
            protected rx.Observable<GroovyRowResult> construct() {
                observeEach(Blocking.get {
                    sql.rows("select id, name from salesforcestage.account order by name")
                })
            }

            @Override
            protected String getCacheKey() {
                return "db-accountdb-all"
            }
        }.toObservable()
    }

    rx.Observable<String> insert(final String name) {
        return new HystrixObservableCommand<String>(
                HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("insert"))) {

            @Override
            protected rx.Observable<List<Object>> construct() {
                observe(Blocking.get {
                    sql.executeInsert("insert into account (name) values ($name)")
                })
            }
        }.toObservable()
    }

    rx.Observable<GroovyRowResult> find(final String name) {
        return new HystrixObservableCommand<GroovyRowResult>(
                HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("find"))) {

            @Override
            protected rx.Observable<GroovyRowResult> construct() {
                observe(Blocking.get {
                    sql.firstRow("select name from account where name like *$name*")
                })
            }

            @Override
            protected String getCacheKey() {
                return "db-accountdb-find-$name"
            }
        }.toObservable()
    }

    rx.Observable<Void> update(final String id, final String name) {
        return new HystrixObservableCommand<Void>(
                HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("update"))) {

            @Override
            protected rx.Observable<Integer> construct() {
                observe(Blocking.get {
                    sql.executeUpdate("update account set name = $name where id = $id")
                })
            }
        }.toObservable()
    }

    rx.Observable<Void> delete(final String name) {
        return new HystrixObservableCommand<Void>(
                HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("delete"))) {

            @Override
            protected rx.Observable<Integer> construct() {
                observe(Blocking.get {
                    sql.executeUpdate("delete from account where name = $name")
                })
            }
        }.toObservable()
    }

}