package com.gruelbox.asyncalltheway;

import com.gruelbox.asyncalltheway.integration.persistence.PersistenceBundle;
import com.gruelbox.asyncalltheway.integration.jooq.public_.tables.daos.StuffDao;
import com.gruelbox.tools.dropwizard.guice.GuiceBundle;
import net.winterly.rxjersey.dropwizard.RxJerseyBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ServerProperties;

@Slf4j
public class AsyncApplication extends Application<AsyncConfiguration> {

  @Inject
  private StuffDao stuffDao;

  @Override
  public String getName() {
    return "Async all the way down";
  }

  @Override
  public void initialize(Bootstrap<AsyncConfiguration> bootstrap) {
    super.initialize(bootstrap);
    bootstrap.addBundle(new PersistenceBundle<>());
    bootstrap.addBundle(new GuiceBundle<>(this, new AsyncModule()));
    bootstrap.addBundle(new RxJerseyBundle<>());
  }

  @Override
  public void run(AsyncConfiguration configuration, Environment environment) {
    log.info("Testing connection");
    stuffDao.findOneById("Foo").blockingGet();
    environment.jersey().property(ServerProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, 0);
  }
}