package com.gruelbox.asyncalltheway;

import static com.ea.async.Async.await;
import static com.gruelbox.asyncalltheway.persistence.jooq.tables.Stuff.STUFF;
import static java.util.concurrent.CompletableFuture.completedFuture;

import com.gruelbox.asyncalltheway.persistence.jooq.tables.daos.StuffDao;
import com.gruelbox.asyncalltheway.persistence.jooq.tables.pojos.Stuff;
import com.gruelbox.tools.dropwizard.guice.resources.WebResource;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
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
import org.jooq.DSLContext;

@Path("/demo")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
@Slf4j
@Singleton
public class DemoResource implements WebResource {

  private final Configuration jooqConfig;
  private final AsyncSQLClient sqlClient;
  private final Vertx vertx;
  private final DSLContext dsl;

  @Inject
  DemoResource(Configuration jooqConfig, AsyncSQLClient sqlClient, Vertx vertx, DSLContext dslContext) {
    this.jooqConfig = jooqConfig;
    this.sqlClient = sqlClient;
    this.vertx = vertx;
    this.dsl = dslContext;
  }

  @GET
  @Path("/write")
  public void write(@Suspended AsyncResponse asyncResponse, @Context HttpServletRequest servletRequest) throws IOException {
    asyncResponse.setTimeout(120, TimeUnit.SECONDS);

    // Returns immediately
    writeAsync(asyncResponse, servletRequest);

    // Write some progress out just to prove it
    logNow(servletRequest);

  }

  @GET
  @Path("/read")
  public void read(@Suspended AsyncResponse asyncResponse, @Context HttpServletRequest servletRequest) throws IOException {
    asyncResponse.setTimeout(120, TimeUnit.SECONDS);

    // Returns immediately
    readAsync(asyncResponse, servletRequest);

    // Write some progress out just to prove it
    logNow(servletRequest);
  }

  CompletableFuture<Void> writeAsync(AsyncResponse asyncResponse, HttpServletRequest servletRequest) throws IOException {
    var asyncContext = servletRequest.getAsyncContext();
    var output = asyncContext.getResponse().getOutputStream();
    try {
      await(recursiveInsert(new StuffDao(jooqConfig, vertx, sqlClient), output, 1, 60));
      asyncContext.complete();
      asyncResponse.isCancelled();
    } catch (Exception e) {
      asyncResponse.resume(e);
    }
    return completedFuture(null);
  }

  CompletableFuture<Integer> recursiveInsert(StuffDao stuffDao, OutputStream receiver, long i, long limit) {
    var record = new Stuff(Long.toString(i), "Record " + i);
    if (await(stuffDao.insert(record)) != 1) {
      throw new IllegalStateException("Failed an insert");
    }
    try {
      receiver.write((i + " sent\n").getBytes(StandardCharsets.UTF_8));
      receiver.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (i < limit) {
      var recursedResult = await(recursiveInsert(stuffDao, receiver, i + 1, limit));
      return completedFuture(recursedResult + 1);
    }
    return completedFuture(1);
  }

  CompletableFuture<Void> readAsync(AsyncResponse asyncResponse, HttpServletRequest servletRequest) throws IOException {
    var asyncContext = servletRequest.getAsyncContext();
    var output = asyncContext.getResponse().getOutputStream();
    try {
      await(queryStream(dsl.selectFrom(STUFF).toString(), jsonArray -> {
        try {
          output.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
          output.flush();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }));
      asyncContext.complete();
      asyncResponse.isCancelled();
    } catch (Exception e) {
      asyncResponse.resume(e);
    }
    return completedFuture(null);
  }


  CompletableFuture<Void> queryStream(String sql, Consumer<JsonArray> consumer) {
    var cf = new CompletableFuture<Void>();
    sqlClient.queryStream(sql, result -> {
      if (result.failed()) {
        cf.completeExceptionally(result.cause());
        return;
      }
      result.result()
          .exceptionHandler(cf::completeExceptionally)
          .endHandler(x -> cf.complete(null))
          .handler(consumer::accept);
    });
    return cf;
  }

  private void logNow(@Context HttpServletRequest servletRequest)
      throws IOException {
    ServletOutputStream outputStream = servletRequest.getAsyncContext().getResponse()
        .getOutputStream();
    outputStream.write("Doing work asynchronously\n".getBytes(StandardCharsets.UTF_8));
    outputStream.flush();
  }

}