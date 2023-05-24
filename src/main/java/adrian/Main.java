package adrian;

import java.time.Instant;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class Main {
    public static void main(String[] args) {
        DiscordClient client = DiscordClient.create("TOKEN");

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(Color.BLUE)
                .title("Title")
                .url("https://discord4j.com")
                .author("Some Name", "https://discord4j.com", "https://i.imgur.com/F9BhEoz.png")
                .description("a description")
                .thumbnail("https://i.imgur.com/F9BhEoz.png")
                .addField("field title", "value", false)
                .addField("\u200B", "\u200B", false)
                .addField("inline field", "value", true)
                .addField("inline field", "value", true)
                .addField("inline field", "value", true)
                .image("https://i.imgur.com/TWlCrUs.png")
                .timestamp(Instant.now())
                .footer("footer", "https://i.imgur.com/F9BhEoz.png")
                .build();

        new Thread(() -> {
            Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
                Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
                    final User self = event.getSelf();
                    System.out.printf("Iniciado como %s#%s%n...\n", self.getUsername(), self.getDiscriminator());
                })).then();

                Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                    Message message = event.getMessage();

                    if (message.getContent().equalsIgnoreCase("!ping")) {
                        return message.getChannel()
                                .flatMap(channel -> channel.createMessage(embed));
                    }

                    return Mono.empty();
                }).then();

                Mono<Void> holaMundo = gateway.on(MessageCreateEvent.class, event -> {
                    Message message = event.getMessage();

                    if (message.getContent().equalsIgnoreCase("!hola")) {
                        return message.getChannel()
                                .flatMap(channel -> channel.createMessage("Hola mundo!"));
                    }

                    return Mono.empty();
                }).then();

                // combine them!
                return printOnLogin.and(handlePingCommand).and(holaMundo);
            });

            login.block();
        }).start();

        new Thread(() -> {
            Mono<Void> comandos = client.withGateway((GatewayDiscordClient gateway) -> {
                Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
                    final User self = event.getSelf();
                    System.out.println("Iniciando comandos...");
                })).then();

                Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                    Message message = event.getMessage();

                    if (message.getContent().equalsIgnoreCase("!ping2")) {
                        return message.getChannel()
                                .flatMap(channel -> channel.createMessage("pong2!"));
                    }

                    return Mono.empty();
                }).then();

                Mono<Void> holaMundo = gateway.on(MessageCreateEvent.class, event -> {
                    Message message = event.getMessage();

                    if (message.getContent().equalsIgnoreCase("!adios")) {
                        return message.getChannel()
                                .flatMap(channel -> channel.createMessage("Adios mundo!"));
                    }

                    return Mono.empty();
                }).then();

                // combine them!
                return printOnLogin.and(handlePingCommand).and(holaMundo);
            });

            comandos.block();
        }).start();
    }

}
