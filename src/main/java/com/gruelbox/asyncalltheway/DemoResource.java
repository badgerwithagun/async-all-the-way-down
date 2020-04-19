package com.gruelbox.asyncalltheway;

import com.gruelbox.asyncalltheway.domain.stuff.Stuff;
import com.gruelbox.asyncalltheway.domain.stuff.StuffRepository;
import com.gruelbox.asyncalltheway.integration.http.RxClient;
import com.gruelbox.tools.dropwizard.guice.resources.WebResource;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.stream.ChunkedInput;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import lombok.extern.slf4j.Slf4j;
import net.winterly.rxjersey.server.rxjava2.Streamable;
import com.gruelbox.asyncalltheway.integration.serialization.JacksonStreamWriter;

@Path("/demo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
@Singleton
public class DemoResource implements WebResource {

  private final StuffRepository stuffRepository;
  private final RxClient rxClient;
  private final AsyncConfiguration configuration;

  @Inject
  DemoResource(StuffRepository stuffRepository, RxClient rxClient, AsyncConfiguration configuration) {
    this.stuffRepository = stuffRepository;
    this.rxClient = rxClient;
    this.configuration = configuration;
  }

  /**
   * Asynchronously resets the test.
   *
   * @return The number of records.
   */
  @GET
  @Path("/reset")
  public Single<Integer> reset() {
    return stuffRepository.clear();
  }

  /**
   * Asynchronously fetches the number of records without blocking the server thread.
   *
   * @return The number of records.
   */
  @GET
  @Path("/count")
  public Single<Integer> count() {
    return stuffRepository.count();
  }

  /**
   * Asynchronously writes a hundred records without blocking the server thread and returns them.
   *
   * @return The records, as JSON.
   */
  @GET
  @Path("/write")
  @Streamable(writer = JacksonStreamWriter.class)
  public Flowable<Stuff> write(@Context HttpServletResponse response) {
    var result = Flowable.range(1, 100)
        .map(i -> Stuff.builder()
            .id(Long.toString(i))
            .data("Record " + i)
            .build())
        // Slow things down so we can see streaming happening
        .zipWith(Flowable.interval(100, TimeUnit.MILLISECONDS), (item, interval) -> item)
        .flatMap(record ->
            stuffRepository.insert(record)
                .toFlowable()
                .map(x -> record))
        .doOnNext(record -> log.info("Inserted " + record));
    log.info("Returning thread to Jetty");
    return result;
  }

  /**
   * Asynchronously reads the records in the DB without blocking the server thread.
   *
   * @return The records as JSON.
   */
  @GET
  @Path("/read")
  @Streamable(writer = JacksonStreamWriter.class)
  public Flowable<Stuff> read() {
    var result = stuffRepository.selectAll()
        // Slow things down so we can see streaming happening
        .zipWith(Flowable.interval(100, TimeUnit.MILLISECONDS), (item, interval) -> item);
    log.info("Returning thread to Jetty");
    return result;
  }

  /**
   * Asynchronously reads the records in the DB via a non-blocking HTTP client request
   * to the endpoint above.
   *
   * @return The records as JSON.
   */
  @GET
  @Path("/readhttp")
  @Streamable(writer = JacksonStreamWriter.class)
  public Flowable<Stuff> readViaHttp() {
    return rxClient.getJsonArray(client ->
        client.target("http://localhost:8081/demo/read").request().async(),
        Stuff.class);
  }

}