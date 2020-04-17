package com.gruelbox.asyncalltheway;

import com.gruelbox.asyncalltheway.persistence.jooq.tables.daos.StuffDao;
import com.gruelbox.asyncalltheway.persistence.jooq.tables.pojos.Stuff;
import com.gruelbox.tools.dropwizard.guice.resources.WebResource;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.vertx.reactivex.sqlclient.SqlClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;

@Path("/demo")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
@Slf4j
@Singleton
public class DemoResource implements WebResource {

  private final Configuration jooqConfig;
  private final SqlClient sqlClient;

  @Inject
  DemoResource(Configuration jooqConfig, SqlClient sqlClient) {
    this.jooqConfig = jooqConfig;
    this.sqlClient = sqlClient;
  }


  /**
   * When called, will start trying to send a sequence of 1-60 to any caller to /read. Neither the writer nor the reader block server threads; all
   * work is done reactively.
   */
  @GET
  @Path("/write")
  public void write(@Suspended final AsyncResponse asyncResponse,
      @Context HttpServletRequest servletRequest)
      throws IOException {

    if (!servletRequest.isAsyncStarted()) {
      asyncResponse.resume(new IllegalStateException("No async support"));
      return;
    }

    final AsyncContext asyncContext = servletRequest.getAsyncContext();
    final ServletOutputStream output = asyncContext.getResponse().getOutputStream();
    asyncResponse.setTimeout(120, TimeUnit.SECONDS);

    insert(
        new StuffDao(jooqConfig, sqlClient),
        i -> {
          output.write((i + " sent /n").getBytes(StandardCharsets.UTF_8));
          output.flush();
        },
        1,
        60)
    .subscribe(() -> {
      asyncContext.complete();
      asyncResponse.isCancelled();
    }, asyncResponse::resume);
  }

  Completable insert(StuffDao stuffDao, Consumer<Long> receiver, long i, long limit) {
    return Completable.create(emitter -> {
      stuffDao.insert(new Stuff(Long.toString(i), "Record " + i))
          .subscribe(count -> {
            if (count != 1) {
              emitter.onError(new IllegalStateException("Failed an insert"));
            }
            receiver.accept(i);
            if (i < limit) {
              Observable.timer(1, TimeUnit.SECONDS).subscribe(x ->
                  insert(stuffDao, receiver,i + 1, limit));
            } else {
              emitter.onComplete();
            }
          });
    });
  }

  @GET
  @Path("/read")
  public void read(@Suspended final AsyncResponse asyncResponse) {

  }

}