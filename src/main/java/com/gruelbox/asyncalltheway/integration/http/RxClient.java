package com.gruelbox.asyncalltheway.integration.http;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import java.io.InputStream;
import java.util.function.Function;
import javax.inject.Inject;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.InvocationCallback;

public class RxClient {

  private final Client client;
  private final ObjectMapper objectMapper;

  @Inject
  RxClient(Client client, ObjectMapper objectMapper) {
    this.client = client;
    this.objectMapper = objectMapper;
  }

  public <T> Flowable<T> getJsonArray(Function<Client, AsyncInvoker> invocationBuilder, Class<T> arrayElementType) {
    return Flowable.create(emitter ->
        invocationBuilder.apply(client)
          .get(new InvocationCallback<InputStream>() {
            @Override
            public void completed(InputStream input) {
              try (var parser = objectMapper.getFactory().createParser(input)) {
                if (parser.nextToken() != JsonToken.START_ARRAY) {
                  emitter.onError(new IllegalArgumentException("Not an array"));
                }
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                  var stuff = objectMapper.readValue(parser, arrayElementType);
                  emitter.onNext(stuff);
                }
                emitter.onComplete();
              } catch (Exception e) {
                emitter.onError(new IllegalArgumentException("JSON parse error", e));
              }
            }
            @Override
            public void failed(Throwable e) {
              emitter.onError(new IllegalArgumentException("Request failed", e));
            }
          }),
        BackpressureStrategy.BUFFER);
  }

}
