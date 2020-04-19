package com.gruelbox.asyncalltheway.integration.jooq.public_.tables.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.github.jklingsporn.vertx.jooq.rx.VertxDAO;

public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<VertxDAO<com.gruelbox.asyncalltheway.integration.jooq.public_.tables.records.StuffRecord, com.gruelbox.asyncalltheway.integration.jooq.public_.tables.pojos.Stuff, java.lang.String>>() {}).to(com.gruelbox.asyncalltheway.integration.jooq.public_.tables.daos.StuffDao.class).asEagerSingleton();
    }
}
