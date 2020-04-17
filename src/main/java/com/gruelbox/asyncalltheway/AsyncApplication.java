package com.gruelbox.asyncalltheway;

import com.gruelbox.asyncalltheway.persistence.PersistenceBundle;
import com.gruelbox.tools.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AsyncApplication extends Application<AsyncConfiguration> {

  @Override
  public String getName() {
    return "Async all the way down";
  }

  @Override
  public void initialize(Bootstrap<AsyncConfiguration> bootstrap) {
    super.initialize(bootstrap);
    bootstrap.addBundle(new PersistenceBundle<>());
    bootstrap.addBundle(new GuiceBundle<>(this, new AsyncModule()));
  }

  @Override
  public void run(AsyncConfiguration configuration, Environment environment) {
    // No-op
  }
}