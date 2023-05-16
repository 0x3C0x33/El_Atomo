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
        DiscordClient client = DiscordClient.create("MTEwNTg4NjI0MjYyMjY4MTE5OA.GO2bxU.mIhVCBNUKnm0eE6uIAF5JzsYUwRaHYqlb3SLkE");

        //Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> Mono.empty());

        new Thread(() -> {
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
        }).start();

        new Thread(() -> {
            Mono<Void> comandos = client.withGateway((GatewayDiscordClient gateway) -> {
                Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                    Mono.fromRunnable(() -> {
                      final User self = event.getSelf();
                      System.out.printf("Iniciando comandos");
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
