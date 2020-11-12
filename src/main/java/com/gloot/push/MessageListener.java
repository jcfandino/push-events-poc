package com.gloot.push;

import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MessageListener {

  private final AmqpTemplate rabbitTemplate;

  @KafkaListener(topics = "messages")
  public void receive(Message message) {
    rabbitTemplate.convertAndSend(
        "amq.topic", "messages", String.format("{\"message\":\"%s\"}", message.getBody()));
  }
}
