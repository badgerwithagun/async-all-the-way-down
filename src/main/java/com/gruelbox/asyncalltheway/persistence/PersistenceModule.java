package com.gruelbox.asyncalltheway.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gruelbox.asyncalltheway.persistence.jooq.tables.daos.StuffDao;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

@Slf4j
public class PersistenceModule extends AbstractModule {

  @Provides
  @Singleton
  DataSource dataSource(Environment environment, PersistenceConfiguration config) {
    log.info("Initialising data source");
    ManagedDataSource ds = config.getDatabase().build(environment.metrics(), "main-data-source");
    environment.lifecycle().manage(ds);
    return ds;
  }

  @Provides
  @Singleton
  Vertx vertx() {
    return Vertx.vertx();
  }

  @Provides
  @Singleton
  Configuration jooqConfiguration() {
    Configuration jooqConfig = new DefaultConfiguration();
    jooqConfig.set(SQLDialect.POSTGRES);
    return jooqConfig;
  }

  @Provides
  @Singleton
  DSLContext dsl(Configuration configuration) {
    return DSL.using(configuration);
  }

  @Provides
  @Singleton
  AsyncSQLClient sqlClient(PersistenceConfiguration configuration, Vertx vertx, Configuration jooqConfig)
      throws InterruptedException, ExecutionException {
    URI uri = URI.create(configuration.getDatabase().getUrl().substring(5));
    JsonObject config = new JsonObject()
        .put("host", uri.getHost())
        .put("port", uri.getPort())
        .put("username", configuration.getDatabase().getUser())
        .put("password", configuration.getDatabase().getPassword())
        .put("database", uri.getPath().substring(1));
    var client = PostgreSQLClient.createShared(vertx, config);
    log.info("Testing connection");
    new StuffDao(jooqConfig, vertx, client).findOneById("Foo").get();
    return client;
  }

}
