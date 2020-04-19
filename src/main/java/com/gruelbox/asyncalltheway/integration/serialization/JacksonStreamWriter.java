package com.gruelbox.asyncalltheway.integration.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import net.winterly.rxjersey.server.rxjava2.StreamWriter;

@Slf4j
public final class JacksonStreamWriter extends StreamWriter<Object, byte[]> {

  private final byte[] startArray = "[".getBytes();
  private final byte[] endArray = "]".getBytes();
  private final byte[] comma = ",".getBytes();
  private final ObjectMapper objectMapper;

  public JacksonStreamWriter() {
    super(Object.class, byte[].class, "");
    this.objectMapper = new ObjectMapper();
  }

  @Override
  protected void writeObject(Object input, boolean first) throws IOException {
    log.info("Writing chunk for {}", input);
    if (first) {
      super.writeChunk(startArray);
    } else {
      super.writeChunk(comma);
    }
    super.writeChunk(objectMapper.writeValueAsBytes(input));
  }

  @Override
  protected void beforeClose(boolean empty) throws IOException {
    if (empty) {
      super.writeChunk(startArray);
    }
    super.writeChunk(endArray);
    log.debug("Closing stream");
    super.beforeClose(empty);
  }
}
