import com.zaxxer.hikari.HikariConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ratpack.groovy.sql.SqlModule
import ratpack.groovy.template.MarkupTemplateModule
import ratpack.handling.RequestLogger
import ratpack.hikari.HikariModule
import ratpack.hystrix.HystrixModule
import ratpack.rx.RxRatpack
import ratpack.service.Service
import ratpack.service.StartEvent

import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack

final Logger logger = LoggerFactory.getLogger(ratpack.class);

ratpack {
	bindings {
		module MarkupTemplateModule
		module HikariModule, { HikariConfig c ->
			c.addDataSourceProperty("url", System.getenv("JDBC_DATABASE_URL"))
			c.setDataSourceClassName("org.postgresql.ds.PGPoolingDataSource")
		}
		module SqlModule
		module BookModule
		module AccountModule
		module new HystrixModule().sse()

		bindInstance Service, new Service() {
			@Override
			void onStart(StartEvent event) throws Exception {
				logger.info "Initializing RX"
				RxRatpack.initialize()
			}
		}
	}

	handlers {
		all {
			RequestLogger.ncsa(logger)
			logger.info "== Request Received =="
            
			next()
        }

		get {
			render groovyMarkupTemplate("index.gtpl", title: "My Ratpack App")
		}

		get("hello") {
			response.send "Hello from Heroku!"
		}

		prefix("accounts") {
			all {
                logger.info "> Get All Accounts"
                chain(registry.get(AccountEndpoint))
            }
		}

		files { dir "public" }
	}
}
