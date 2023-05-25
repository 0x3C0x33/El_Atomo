package adrian.comandos;

import adrian.datos.DeuxExMachina;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

public class Polonio extends Thread {
    // Parametros de clase
    private int numeroDeAtomos = 112;

    // Parametros internos
    private DiscordClient client; // Sesion del bot
    private Random ran = new Random(); // Para generar numeros aleatorios
    private int numAtomo; // Numero del atomo escogido aleatoriamente en ejecucion
    private List<String> atomo; // Atomo escogido aleatoriamente en ejecucion
    private String atomoCorrecto; // Nombre del atomo escodigo aleatoriamente que sera el correcto
    private CountDownLatch latch; // Avisa al hilo principal que su inicio ha terminado
    private boolean jugando; // Boleano para saber si hay un atomo en ejecucion

    public Polonio(CountDownLatch latch, DiscordClient client) {
        this.latch = latch;
        this.client = client;
    }

    @Override
    public void run() {
        Mono<Void> polonio = client.withGateway((GatewayDiscordClient gateway) -> {

            // Se invoca al iniciar Polonio
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
                // final User self = event.getSelf();
                System.out.printf("Polonio iniciado...\n"); // self.getUsername(), self.getDiscriminator()
                latch.countDown(); // Manda la señal de que el hilo fue iniciado.
            })).then();

            // Se fuerza la creacion de un atomo.
            Mono<Void> creadorAtomo = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                // Optional<User> usuario = message.getAuthor();

                if (message.getContent().equalsIgnoreCase("!atomo")) {
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage(creadorAtomo()));
                }

                return Mono.empty();
            }).then();

            // Se invoca despues de cierto numero de mensajes TODO:Terminar
            Mono<Void> aleatorizadorAtomo = gateway.on(MessageCreateEvent.class, event -> {

                return Mono.empty();
            }).then();

            // Se invoca despues de cierto numero de mensajes
            Mono<Void> respuesta = gateway.on(MessageCreateEvent.class, event -> {
                Timer timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Coloca aquí el código que se ejecutará después de 60 segundos
                        System.out.println("El temporizador ha terminado.");
                        // Aquí puedes llamar al método que deseas que finalice después de 60 segundos
                        metodoQueFinaliza();
                    }
                }, 6000); // Especifica el tiempo en milisegundos (60 segundos = 60000 ms)

                return Mono.empty();
            }).then();

            // Se invoca para cancelar el atomo en ejecucion TODO:Terminar
            Mono<Void> cancelar = gateway.on(MessageCreateEvent.class, event -> {
                
                return Mono.empty();
            }).then();

            return printOnLogin.and(creadorAtomo).and(aleatorizadorAtomo).and(respuesta).and(cancelar);
        });
        polonio.block();
    }

    private void metodoQueFinaliza() {
        // Coloca aquí el código que deseas que se ejecute después de 60 segundos
        System.out.println("El método ha finalizado.");
    }

    // Metodo encargado de crear el mensaje del atomo personalizado TODO:Terminar de
    // hacer el embed
    private EmbedCreateSpec creadorAtomo() {
        // El atomo aleatorio
        numAtomo = ran.nextInt(numeroDeAtomos);
        atomo = new ArrayList<>();

        atomo.addAll(DeuxExMachina.elementos.get(numAtomo));
        atomoCorrecto = atomo.get(2);

        for (int i = 0; i < atomo.size(); i++) {
            if (atomo.get(i).isEmpty()) {
                atomo.set(i, "---");
            }
        }

        switch (DeuxExMachina.dificultad) {
            case 0:
                /* Sin dificultad. */ break;
            case 3:
                for (int i = 0; i < 5; i++) {
                    atomo.set(ran.nextInt(3, 15), generarCadenaAleatoria(10));
                }
            case 2:
                atomo.set(1, generarCadenaAleatoria(10));
            case 1:
                atomo.set(2, generarCadenaAleatoria(10));
            default:
                break;
        }

        // [[1, H, Hidrógeno, 1.0080, FFFFFF, 1s1, 2.2, 120, 13.598, 0.754, +1 -1, Gas,
        // 13.81, 20.28, 0.00008988, No metal, 1766]]
        // 0, 1, 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 , 11 , 12 , 13 , 14 , 15 , 16

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(DeuxExMachina.coloresMap.get(atomo.get(15))) // DeuxExMachina.colores.get(ran.nextInt(DeuxExMachina.colores.size()))
                .title(atomo.get(2) + " :atom: " + "Dificultad: " + DeuxExMachina.dificultad)
                .description(":one: Número atómico: " + atomo.get(0))
                .addField("[" + atomo.get(1) + "]", "\u200B", false)
                .addField("Masa atomica :scales:", atomo.get(3) + "u", true)
                .addField("Configuración electronica", atomo.get(5), true)
                .addField("Electronegatividad :cloud_lightning:", atomo.get(6) + " (Escala Pauling)", true)
                .addField("Radio atómico :earth_africa:", atomo.get(7) + "pm", true)
                .addField("Energía de ionización :zap:", atomo.get(8) + "eV", true)
                .addField("Afinidad electronica :bulb:", atomo.get(9) + "eV", true)
                .addField("Estados de Oxidación :pick:", atomo.get(10), true)
                .addField("Estado estándar :cloud:", atomo.get(11), true)
                .addField("Punto de fusión :thermometer:", atomo.get(12) + "K", true)
                .addField("Punto de ebullición :thermometer:", atomo.get(13) + "K", true)
                .addField("Densidad :bricks:", atomo.get(14) + "g/cm3", true)
                .addField("Bloque de grupo :symbols:", atomo.get(15), true)
                .image(DeuxExMachina.urls.get(numAtomo))
                .timestamp(Instant.now())
                .footer("Descubierto en " + atomo.get(16), null)
                .build();

        return embed;
    }

    public String generarCadenaAleatoria(int longitud) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            char letra = (char) (ran.nextInt(26) + 'a');
            if (ran.nextInt(2) == 0) {
                sb.append(Character.toUpperCase(letra));
            } else {
                sb.append(letra);
            }

        }

        return sb.toString();
    }

    // "Número atómico",
    // "Símbolo",
    // "Nombre",
    // "Masa atomica",
    // "CPKHexColor",
    // "Configuración electronica",
    // "Electronegatividad",
    // "Radio atómico",
    // "Energía de ionización",
    // "Afinidad electronica",
    // "Estados de Oxidación",
    // "Estado estándar",
    // "Punto de fusion",
    // "Punto de ebullición",
    // "Densidad",
    // "Bloque de grupo",
}

// Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
// Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
// Mono.fromRunnable(() -> {
// final User self = event.getSelf();
// System.out.printf("Iniciado como %s#%s%n...\n", self.getUsername(),
// self.getDiscriminator());
// })).then();

// Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event ->
// {
// Message message = event.getMessage();

// if (message.getContent().equalsIgnoreCase("!ping")) {
// return message.getChannel()
// .flatMap(channel -> channel.createMessage(creador()));
// }

// return Mono.empty();
// }).then();

// Mono<Void> holaMundo = gateway.on(MessageCreateEvent.class, event -> {
// Message message = event.getMessage();

// if (message.getContent().equalsIgnoreCase("!hola")) {
// return message.getChannel()
// .flatMap(channel -> channel.createMessage("Hola mundo!"));
// }

// return Mono.empty();
// }).then();

// // combine them!
// return printOnLogin.and(handlePingCommand).and(holaMundo);
// });

// login.block();
