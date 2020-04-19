package com.gruelbox.asyncalltheway.integration.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gruelbox.asyncalltheway.integration.jooq.public_.tables.modules.DaoModule;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.asyncsql.PostgreSQLClient;
import java.net.URI;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

@Slf4j
public class PersistenceModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new DaoModule());
  }

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
    jooqConfig.set(new Settings().withExecuteWithOptimisticLocking(true));
    return jooqConfig;
  }

  @Provides
  @Singleton
  DSLContext dsl(Configuration configuration) {
    return DSL.using(configuration);
  }

  @Provides
  @Singleton
  AsyncSQLClient sqlClient(PersistenceConfiguration configuration, Vertx vertx) {
    URI uri = URI.create(configuration.getDatabase().getUrl().substring(5));
    JsonObject config = new JsonObject()
        .put("host", uri.getHost())
        .put("port", uri.getPort())
        .put("username", configuration.getDatabase().getUser())
        .put("password", configuration.getDatabase().getPassword())
        .put("database", uri.getPath().substring(1));
    return PostgreSQLClient.createShared(vertx, config);
  }

}
