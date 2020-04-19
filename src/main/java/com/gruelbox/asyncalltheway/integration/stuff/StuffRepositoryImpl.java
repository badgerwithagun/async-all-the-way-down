package com.gruelbox.asyncalltheway.integration.stuff;

import static com.gruelbox.asyncalltheway.integration.jooq.public_.tables.Stuff.STUFF;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.asyncalltheway.domain.stuff.Stuff;
import com.gruelbox.asyncalltheway.domain.stuff.StuffRepository;
import com.gruelbox.asyncalltheway.integration.jooq.public_.tables.daos.StuffDao;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import org.jooq.DSLContext;
import org.jooq.SelectFinalStep;
import org.jooq.impl.DSL;

class StuffRepositoryImpl implements StuffRepository {

  private final AsyncSQLClient sqlClient;
  private final DSLContext dsl;
  private final StuffDao stuffDao;
  private final Client client;
  private final ObjectMapper objectMapper;

  @Inject
  StuffRepositoryImpl(AsyncSQLClient sqlClient,
      DSLContext dslContext, StuffDao stuffDao,
      Client client, ObjectMapper objectMapper) {
    this.sqlClient = sqlClient;
    this.dsl = dslContext;
    this.stuffDao = stuffDao;
    this.client = client;
    this.objectMapper = objectMapper;
  }

  @Override
  public Single<Integer> clear() {
    return stuffDao.deleteByCondition(STUFF.ID.ne("Boo"));
  }

  @Override
  public Single<Integer> count() {
    return queryStream(dsl.select(DSL.count()).from(STUFF))
        .map((JsonArray a) -> a.getInteger(0))
        .single(0);
  }

  @Override
  public Single<Integer> insert(Stuff stuff) {
    return stuffDao.insert(map(stuff));
  }

  @Override
  public Single<Integer> update(Stuff stuff) {
    // TODO locking is bugged. See https://github.com/jklingsporn/vertx-jooq/issues/145
    return stuffDao.update(map(stuff));
  }

  @Override
  public Flowable<Stuff> selectAll() {
    return queryStuff(dsl.selectFrom(STUFF)).map(this::map);
  }

  private com.gruelbox.asyncalltheway.integration.jooq.public_.tables.pojos.Stuff map(Stuff stuff) {
    return new com.gruelbox.asyncalltheway.integration.jooq.public_.tables.pojos.Stuff(stuff.getId(), stuff.getData(), stuff.getVersion());
  }

  private Stuff map(com.gruelbox.asyncalltheway.integration.jooq.public_.tables.pojos.Stuff stuff) {
    return Stuff.builder()
        .id(stuff.getId())
        .data(stuff.getData())
        .version(stuff.getVersion())
        .build();
  }

  /**
   * TODO this should be generalised for a POJO and submitted to https://github.com/jklingsporn
   */
  private Flowable<com.gruelbox.asyncalltheway.integration.jooq.public_.tables.pojos.Stuff> queryStuff(
      SelectFinalStep<?> sql) {
    return queryStream(sql)
        .map(jsonArray -> new com.gruelbox.asyncalltheway.integration.jooq.public_.tables.pojos.Stuff(
            jsonArray.getString(0),
            jsonArray.getString(1),
            jsonArray.getLong(2)));
  }

  /**
   * TODO this should be generalised for a POJO and submitted to https://github.com/jklingsporn
   */
  private Flowable<JsonArray> queryStream(SelectFinalStep<?> sql) {
    return Flowable.create(emitter ->
        sqlClient.queryStream(sql.toString(), result -> {
          if (result.failed()) {
            emitter.onError(result.cause());
            return;
          }
          emitter.setCancellable(result.result()::rxClose);
          result.result()
              .exceptionHandler(emitter::onError)
              .endHandler(x -> emitter.onComplete())
              .handler(emitter::onNext);
        }),
        BackpressureStrategy.BUFFER);
  }

}
