package com.gruelbox.asyncalltheway;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(DropwizardExtensionsSupport.class)
public class DemoTest {

  @Container
  private static PostgreSQLContainer database = new PostgreSQLContainer()
      .withDatabaseName("test")
      .withUsername("test")
      .withPassword("test");

  DropwizardAppExtension<AsyncConfiguration> app = new DropwizardAppExtension<>(
      AsyncApplication.class,
      "config.yml",
      ConfigOverride.config("persistence.database.user", database.getUsername()),
      ConfigOverride.config("persistence.database.password", database.getPassword()),
      ConfigOverride.config("persistence.database.url", database.getJdbcUrl()));

  @Test
  void test() throws InterruptedException {
    System.out.println(app.getLocalPort());
    Thread.sleep(100000);
  }
}
