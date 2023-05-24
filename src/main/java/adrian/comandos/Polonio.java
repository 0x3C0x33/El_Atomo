package adrian.comandos;

import adrian.datos.DeuxExMachina;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class Polonio extends Thread{
    // Parametros de clase
    private int numeroDeAtomos = 112;

    // Parametros internos
    private DiscordClient client;
    private Random ran = new Random();

    public Polonio(DiscordClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {

            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
                final User self = event.getSelf();
                System.out.printf("Iniciado como %s#%s%n...\n", self.getUsername(), self.getDiscriminator());
            })).then();

            Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                if (message.getContent().equalsIgnoreCase("!atomo")) {
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage(creador()));
                }

                return Mono.empty();
            }).then();

            return printOnLogin.and(handlePingCommand);
        });

        login.block();
    }

    //Metodo encargado de crear el mensaje del atomo personalizado TODO:Terminar de hacer el embed
    private EmbedCreateSpec creador() {
        //El atomo aleatorio
        int numAtomo = ran.nextInt(numeroDeAtomos);
        List<String> atomo = new ArrayList<>();

        atomo.addAll(DeuxExMachina.elementos.get(numAtomo));

        //[[1, H, Hidrógeno, 1.0080, FFFFFF, 1s1, 2.2, 120, 13.598, 0.754, +1 -1, Gas, 13.81, 20.28, 0.00008988, No metal, 1766]]
        //  0, 1, 2        , 3     , 4     , 5  , 6  , 7  , 8     , 9    , 10   , 11 , 12   , 13   , 14        , 15      , 16

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
        .color(DeuxExMachina.coloresMap.get(atomo.get(15))) // DeuxExMachina.colores.get(ran.nextInt(DeuxExMachina.colores.size()))
        .title(atomo.get(2))
        .description("Número atómico: " + atomo.get(0))
        .addField("["+atomo.get(1)+"]", "\u200B", false)
        .addField("Masa atomica", atomo.get(3) + "u", true)
        .addField("Configuración electronica", atomo.get(5), true)
        .addField("Electronegatividad", atomo.get(6), true)
        .addField("Radio atómico", atomo.get(7), true)
        .addField("Energía de ionización", atomo.get(8), true)
        .addField("Afinidad electronica", atomo.get(9), true)
        .addField("Estados de Oxidación", atomo.get(10), true)
        .addField("Estado estándar", atomo.get(11), true)
        .addField("Punto de fusión", atomo.get(12), true)
        .addField("Punto de ebullición", atomo.get(13), true)
        .addField("Densidad", atomo.get(14), true)
        .addField("Bloque de grupo", atomo.get(15), true)
        .image(DeuxExMachina.urls.get(numAtomo))
        .timestamp(Instant.now())
        .footer("Descubierto en " + atomo.get(16), null)
        .build();

        return embed;
    }

    // EmbedCreateSpec embed = EmbedCreateSpec.builder()
    // .color(DeuxExMachina.coloresMap.get(atomo.get(16))) // DeuxExMachina.colores.get(ran.nextInt(DeuxExMachina.colores.size()))
    // .title(atomo.get(2))
    // .description("Número atómico: " + atomo.get(0))
    // .addField("["+atomo.get(1)+"]", "\u200B", false)
    //     .addField("\u200B", "\u200B", false)
    // .addField("Masa atomica", atomo.get(3), true)
    // .addField("CPKHexColor", atomo.get(4), true)
    // .addField("Configuración electronica", atomo.get(5), true)
    //     .addField("\u200B", "\u200B", false)
    // .addField("Electronegatividad", atomo.get(6), true)
    // .addField("Radio atómico", atomo.get(7), true)
    // .addField("Energía de ionización", atomo.get(8), true)
    //     .addField("\u200B", "\u200B", false)
    // .addField("Afinidad electronica", atomo.get(9), true)
    // .addField("Estados de Oxidación", atomo.get(10), true)
    // .addField("Estado estándar", atomo.get(11), true)
    //     .addField("\u200B", "\u200B", false)
    // .addField("Punto de fusión", atomo.get(12), true)
    // .addField("Punto de ebullición", atomo.get(13), true)
    // .addField("Densidad", atomo.get(14), true)
    //     .addField("\u200B", "\u200B", false)
    // .addField("Bloque de grupo", atomo.get(15), true)
    // .image(DeuxExMachina.urls.get(numAtomo))
    // .timestamp(Instant.now())
    // .footer("Descubierto en " + atomo.get(17), "")
    // .build();

//             "Número atómico",
//             "Símbolo",
//             "Nombre",
            //             "Masa atomica",
            //             "CPKHexColor",
            //             "Configuración electronica",
        //             "Electronegatividad",
        //             "Radio atómico",
        //             "Energía de ionización",
            //             "Afinidad electronica",
            //             "Estados de Oxidación",
            //             "Estado estándar",
        //             "Punto de fusion",
        //             "Punto de ebullición",
        //             "Densidad",
            //             "Bloque de grupo",   
}

// Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
//     Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
//         final User self = event.getSelf();
//         System.out.printf("Iniciado como %s#%s%n...\n", self.getUsername(), self.getDiscriminator());
//     })).then();

//     Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
//         Message message = event.getMessage();

//         if (message.getContent().equalsIgnoreCase("!ping")) {
//             return message.getChannel()
//                     .flatMap(channel -> channel.createMessage(creador()));
//         }

//         return Mono.empty();
//     }).then();

//     Mono<Void> holaMundo = gateway.on(MessageCreateEvent.class, event -> {
//         Message message = event.getMessage();

//         if (message.getContent().equalsIgnoreCase("!hola")) {
//             return message.getChannel()
//                     .flatMap(channel -> channel.createMessage("Hola mundo!"));
//         }

//         return Mono.empty();
//     }).then();

//     // combine them!
//     return printOnLogin.and(handlePingCommand).and(holaMundo);
// });

// login.block();
