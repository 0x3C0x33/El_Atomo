package adrian.comandos;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import adrian.datos.DeuxExMachina;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

public class Polonio extends Thread {
    // Parametros de clase
    private int numeroDeAtomos = 112;

    // Parametros internos
    private DiscordClient client; // Sesion del bot
    private GatewayDiscordClient gateway;
    private Random ran = new Random(); // Para generar numeros aleatorios
    private int numAtomo; // Numero del atomo escogido aleatoriamente en ejecucion
    private List<String> atomo; // Atomo escogido aleatoriamente en ejecucion
    private String atomoCorrecto; // Nombre del atomo escodigo aleatoriamente que sera el correcto
    private CountDownLatch latch; // Avisa al hilo principal que su inicio ha terminado
    private boolean jugando = false; // Boleano para saber si hay un atomo en ejecucion
    private TimerTask task; // Task para empezar el contador de 60 segundos antes de que el bot no deje
                            // tomar el atomo
    private Timer timer; // El contador que cuenta los 60 segundos
    private int contadorMensajes = 0;
    private Message canalJugando;
    private Usuarios user;

    public Polonio(CountDownLatch latch, DiscordClient client) {
        this.latch = latch;
        this.client = client;
    }

    @Override
    public void run() {
        Mono<Void> polonio = client.withGateway((GatewayDiscordClient gateway) -> {

            // Se invoca al iniciar Polonio
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
                System.out.printf("Polonio iniciado...\n"); // self.getUsername(), self.getDiscriminator()
                latch.countDown(); // Manda la señal de que el hilo fue iniciado.
            })).then();

            // Se fuerza la creacion de un atomo.
            Mono<Void> creadorAtomo = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!atomo") && !jugando) {
                    canalJugando = message;
                    timer();
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage(creadorAtomo()));
                } else if (message.getContent().equalsIgnoreCase("!atomo") && jugando) {
                    return message.getChannel().flatMap(channel -> channel.createMessage("¡Ya se esta jugando!"));
                }
                return Mono.empty();
            }).then();

            // Se invoca para cancelar el atomo en ejecucion
            Mono<Void> aleatorizadorAtomo = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                contadorMensajes++;
                if (contadorMensajes > DeuxExMachina.mensajesParaRadiar
                        && DeuxExMachina.probabilidadParaRadiar - 100 >= ran.nextInt(100) - 100 && !jugando) {
                    timer();
                    contadorMensajes = 0;
                    canalJugando = message;
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage(creadorAtomo()));
                }
                return Mono.empty();
            }).then();

            // Se invoca despues de acertar el atomo
            Mono<Void> respuesta = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                Optional<User> usuario = message.getAuthor();
                if (message.getContent().equalsIgnoreCase("!" + atomoCorrecto.toLowerCase()) && jugando) {
                    cancelarTemporizador();
                    user.atomoCorrecto(usuario.get().getId().asLong());
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("¡Átomo correcto!"));
                }
                return Mono.empty();
            }).then();

            // Se invoca para cancelar el atomo en ejecucion
            Mono<Void> cancelar = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!cancelar") && jugando) {
                    cancelarTemporizador();
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Átomo cancelado."));
                }
                return Mono.empty();
            }).then();

            return printOnLogin.and(creadorAtomo).and(aleatorizadorAtomo).and(respuesta).and(cancelar);
        });
        polonio.block();
    }

    private void timer() { // channel.createMessage("¡Tiempo agotado para responder, la respuesta correctaera " + atomoCorrecto)
        timer = new Timer();
        jugando = true;
        task = new TimerTask() {
            @Override
            public void run() {
                Snowflake channelId = canalJugando.getChannelId(); //Obtener el ID del canal
                client.login()
                    .flatMap(gateway -> gateway.getChannelById(channelId)) //Obtener el canal por ID
                    .ofType(MessageChannel.class) //Filtrar solo instancias de MessageChannel
                    .flatMap(channel -> channel.createMessage("¡Tiempo agotado para responder! La respuesta correcta era " + atomoCorrecto)) //Crear y enviar el mensaje
                    //.doOnSuccess(message -> System.out.println("Mensaje enviado: " + message.getContent())) //Realizar alguna acción cuando el mensaje se envíe con éxito
                    .doOnError(Throwable::printStackTrace) //Manejar cualquier error que ocurra
                    .doFinally(signalType -> gateway.logout()) //Cerrar la conexión cuando se haya completado la operación
                    .subscribe(); //Suscribirse al flujo de eventos
                jugando = false;
            }
        };
        timer.schedule(task, DeuxExMachina.tiempoCancelacion); // Programa el temporizador
    }

    // Metodo encargado de crear el mensaje del atomo personalizado
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
            case 3:
                for (int i = 0; i < 5; i++) {
                    atomo.set(ran.nextInt(3, 15), generarCadenaAleatoria(10));
                }
            case 2:
                atomo.set(1, generarCadenaAleatoria(10));
            case 1:
                atomo.set(2, generarCadenaAleatoria(10));
            default:/* Sin dificultad. */
        }

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

    private void cancelarTemporizador() {
        jugando = false;
        if (timer != null && task != null) {
            task.cancel(); // Cancela la tarea
            timer.cancel(); // Cancela el temporizador
        }
    }
}
