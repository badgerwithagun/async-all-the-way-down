package com.gruelbox.asyncalltheway.integration.persistence;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistenceBundle<T extends Configuration & HasPersistenceConfiguration> implements
    ConfiguredBundle<T> {

  @SuppressWarnings("rawtypes")
  private FlywayBundle flywayBundle;

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    flywayBundle = new FlywayBundle<T>() {
      @Override
      public DataSourceFactory getDataSourceFactory(T configuration) {
        return configuration.getPersistence().getDatabase();
      }

      @Override
      public FlywayFactory getFlywayFactory(T configuration) {
        return configuration.getPersistence().getFlyway();
      }
    };
    bootstrap.addBundle(flywayBundle);
  }

  @Override
  public void run(T configuration, Environment environment) {
    configuration.getPersistence().migrate();
  }
}
