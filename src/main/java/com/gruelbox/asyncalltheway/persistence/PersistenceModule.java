package com.gruelbox.asyncalltheway.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.reactivex.sqlclient.SqlClient;
import io.vertx.sqlclient.PoolOptions;
import java.net.URI;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
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
  Configuration jooqConfiguration() {
    Configuration jooqConfig = new DefaultConfiguration();
    jooqConfig.set(SQLDialect.POSTGRES);
    return jooqConfig;
  }

  @Provides
  @Singleton
  SqlClient sqlClient(PersistenceConfiguration configuration) {
    URI uri = URI.create(configuration.getDatabase().getUrl().substring(5));
    PgConnectOptions connectOptions = new PgConnectOptions()
        .setHost(uri.getHost())
        .setPort(uri.getPort())
        .setDatabase(uri.getPath());
    PoolOptions poolOptions = new PoolOptions()
        .setMaxSize(configuration.getDatabase().getMaxSize());
    return PgPool.pool(Vertx.vertx(), poolOptions);
  }

}
