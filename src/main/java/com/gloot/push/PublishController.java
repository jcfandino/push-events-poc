package com.gloot.push;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PublishController {

  private final KafkaTemplate<String, Message> kafkaTemplate;

  @GetMapping("/send/{message}")
  public String send(@PathVariable("message") String message) {
    kafkaTemplate.send("messages", new Message(message)).completable().join();
    return "ok";
  }
}
