package com.gruelbox.asyncalltheway;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.gruelbox.asyncalltheway.persistence.PersistenceModule;
import com.gruelbox.tools.dropwizard.guice.Configured;
import com.gruelbox.tools.dropwizard.guice.resources.WebResource;

public class AsyncModule extends AbstractModule implements Configured<AsyncConfiguration> {

  private AsyncConfiguration configuration;

  @Override
  protected void configure() {
    install(new PersistenceModule());
    Multibinder.newSetBinder(binder(), WebResource.class).addBinding().to(DemoResource.class);
    configuration.bind(binder());
  }

  @Override
  public void setConfiguration(AsyncConfiguration configuration) {
    this.configuration = configuration;
  }
}
