package adrian;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class Main {

    public static void main(String[] args) {
        DiscordClient client = DiscordClient.create("");

        //Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> Mono.empty());

        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                Mono.fromRunnable(() -> {
                  final User self = event.getSelf();
                  System.out.printf("Iniciado como %s#%s%n", self.getUsername(), self.getDiscriminator());
            })).then();
          
            Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
            
                if (message.getContent().equalsIgnoreCase("!ping")) {
                    return message.getChannel()
                        .flatMap(channel -> channel.createMessage("pong!"));
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
        
    }

}
