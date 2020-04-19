package com.gruelbox.asyncalltheway;

import com.google.inject.Binder;
import com.gruelbox.asyncalltheway.integration.persistence.HasPersistenceConfiguration;
import com.gruelbox.asyncalltheway.integration.persistence.PersistenceConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import lombok.Getter;

@Getter
class AsyncConfiguration extends Configuration implements HasPersistenceConfiguration {

  private PersistenceConfiguration persistence;
  private JerseyClientConfiguration jerseyClient;

  void bind(Binder binder) {
    binder.bind(PersistenceConfiguration.class).toInstance(persistence);
    binder.bind(JerseyClientConfiguration.class).toInstance(jerseyClient);
  }

}
