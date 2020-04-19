package com.gruelbox.asyncalltheway.domain.stuff;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
public class Stuff {

  @NonNull
  @Getter
  private final String id;

  @NonNull
  @Getter
  @Setter
  private String data;

  @Getter
  @Setter
  @Builder.Default
  @JsonIgnore
  private long version = 0L;

}
