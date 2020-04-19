package com.gruelbox.asyncalltheway.integration.stuff;

import com.google.inject.AbstractModule;
import com.gruelbox.asyncalltheway.domain.stuff.StuffRepository;

public class StuffImplModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(StuffRepository.class).to(StuffRepositoryImpl.class);
  }
}
