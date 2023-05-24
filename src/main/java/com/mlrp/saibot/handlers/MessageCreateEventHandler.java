package com.mlrp.saibot.handlers;

import static java.util.function.Predicate.not;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MessageCreateEventHandler implements EventHandler<MessageCreateEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageCreateEventHandler.class);

  public MessageCreateEventHandler() {}

  @Override
  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  @Override
  public Mono<Void> handle(MessageCreateEvent event) {
    return Mono.justOrEmpty(event.getMember().filter(not(User::isBot)))
        .doOnNext(
            member ->
                LOGGER.info(
                    "Message '{}' created by '{}'",
                    event.getMessage().getContent(),
                    member.getDisplayName()))
        .doOnError(t -> LOGGER.error(t.getMessage(), t))
        .onErrorComplete()
        .then();
  }
}
