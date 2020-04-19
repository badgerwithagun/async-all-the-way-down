package com.gruelbox.asyncalltheway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.gruelbox.asyncalltheway.integration.http.JerseyClientModule;
import com.gruelbox.asyncalltheway.integration.persistence.PersistenceModule;
import com.gruelbox.asyncalltheway.integration.stuff.StuffImplModule;
import com.gruelbox.tools.dropwizard.guice.Configured;
import com.gruelbox.tools.dropwizard.guice.resources.WebResource;
import io.dropwizard.setup.Environment;

public class AsyncModule extends AbstractModule implements Configured<AsyncConfiguration> {

  private AsyncConfiguration configuration;

  @Override
  protected void configure() {
    configuration.bind(binder());
    install(new PersistenceModule());
    install(new JerseyClientModule());
    install(new StuffImplModule());
    Multibinder.newSetBinder(binder(), WebResource.class).addBinding().to(DemoResource.class);
  }

  @Override
  public void setConfiguration(AsyncConfiguration configuration) {
    this.configuration = configuration;
  }

  @Provides
  ObjectMapper objectMapper(Environment environment) {
    return environment.getObjectMapper();
  }
}
