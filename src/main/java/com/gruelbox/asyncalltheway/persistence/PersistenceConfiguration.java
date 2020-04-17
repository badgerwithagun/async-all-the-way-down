package com.gruelbox.asyncalltheway.persistence;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

@Slf4j
@Getter
public class PersistenceConfiguration {

  @NotNull
  private DataSourceFactory database;

  @NotNull
  private FlywayFactory flyway;

  public void migrate() {
    log.info("Running Flyway migrations");
    try {
      int migrations = flyway.build(
          database.getUrl(),
          database.getUser(),
          database.getPassword()).migrate();
      log.info("{} migrations applied", migrations);
    } catch (Exception e) {
      log.error("Flyway migrations failed ", e);
      throw e;
    }
  }
}
