package com.gloot.push;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Message {
  String body;
}
