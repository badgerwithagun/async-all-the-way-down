package com.gruelbox.asyncalltheway;

import com.google.inject.Binder;
import com.gruelbox.asyncalltheway.integration.persistence.HasPersistenceConfiguration;
import com.gruelbox.asyncalltheway.integration.persistence.PersistenceConfiguration;
import io.dropwizard.Configuration;
import lombok.Getter;

@Getter
public class AsyncConfiguration extends Configuration implements HasPersistenceConfiguration {

  private PersistenceConfiguration persistence;

  void bind(Binder binder) {
    binder.bind(PersistenceConfiguration.class).toInstance(persistence);
  }

}
