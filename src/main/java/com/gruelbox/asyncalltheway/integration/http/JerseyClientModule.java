package com.gruelbox.asyncalltheway.integration.http;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import javax.ws.rs.client.Client;

public class JerseyClientModule extends AbstractModule {

  @Provides
  @Singleton
  Client client(JerseyClientConfiguration configuration, Environment environment) {
    return new JerseyClientBuilder(environment)
        .using(configuration)
        .build("demo");
  }
}
