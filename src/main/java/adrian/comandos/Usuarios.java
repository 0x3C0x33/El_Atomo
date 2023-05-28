package adrian.comandos;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

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

public class Usuarios extends Thread {
    private DiscordClient client; // Sesion del bot
    private GatewayDiscordClient gateway;
    private CountDownLatch latch; // Avisa al hilo principal que su inicio ha terminado
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private Random ran = new Random(); // Para generar numeros aleatorios
    private Message canal;

    public Usuarios(CountDownLatch latch, DiscordClient client) {
        this.latch = latch;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("ElAtomo");
            collection = database.getCollection("Usuarios");
        } catch (Exception e) {
            System.out.println("Error al iniciar la base de datos o al insertar el documento.");
            e.printStackTrace();
        }
        
        Mono<Void> usuarios = client.withGateway((GatewayDiscordClient gateway) -> {

            // Se invoca al iniciar Polonio
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
                System.out.printf("Eventos de usuarios iniciado...\n"); // self.getUsername(), self.getDiscriminator()
                latch.countDown(); // Manda la señal de que el hilo fue iniciado.
            })).then();

            // Se crea un usuario.
            Mono<Void> crearUsuario = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                Optional<User> usuario = message.getAuthor();
                if (message.getContent().equalsIgnoreCase("!crearUsuario")) {
                    try {
                        long userId = usuario.get().getId().asLong();
                        Bson filter = Filters.eq("ID", userId);
                        Document existingUser = collection.find(filter).first();
                        if (existingUser != null) {
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("El usuario ya existe :confounded:"));
                        } else {
                            Document doc = new Document("Usuario", usuario.get().getUsername().toString())
                                    .append("Frase", "Lo bueno de la ciencia es que es cierta independientemente de si crees o no en ella")
                                    .append("AtomoFav", 1)
                                    .append("ID", usuario.get().getId().asLong())
                                    .append("Atomos capturados", new Document("Fácil", 0)
                                            .append("Normal", 0)
                                            .append("Difícil", 0));
    
                            collection.insertOne(doc);
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("Usuario creado :disguised_face:"));
                        }
                    } catch (Exception e) {
                        System.out.println("Error inesperado, contacto con su técnico. Log:");
                        e.printStackTrace();
                    } 
                }
                return Mono.empty();
            }).then();

            Mono<Void> infoUsuario = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                Optional<User> usuario = message.getAuthor();
                if (message.getContent().equalsIgnoreCase("!perfil")) {
                    canal = message;
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage(perfilUsuario(usuario.get().getId().asLong())));
                }
                return Mono.empty();
            }).then();

            return printOnLogin.and(crearUsuario).and(infoUsuario);
        });
        usuarios.block();
    }

    // Metodo encargado de crear el mensaje del atomo personalizado
    // hacer el embed
    private EmbedCreateSpec perfilUsuario(long id) {
        Document query = new Document("ID", id);
        Document result = collection.find(query).first();
        if (result != null) {
            Document atomosCapturados = result.get("Atomos capturados", Document.class);
            EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(DeuxExMachina.colores.get(ran.nextInt(DeuxExMachina.colores.size()))) // DeuxExMachina.colores.get(ran.nextInt(DeuxExMachina.colores.size()))
                .title(result.get("Usuario").toString())
                .description("'" + result.get("Frase").toString() + "'")
                .addField("Número de átomos capturados", "", false)
                .addField("Fácil", atomosCapturados.get("Fácil").toString(), true)
                .addField("Normal", atomosCapturados.get("Normal").toString(), true)
                .addField("Difícil", atomosCapturados.get("Difícil").toString(), true)
                .addField("Átomo favorito", "", false)
                .image(DeuxExMachina.urls.get(Integer.parseInt(result.get("AtomoFav").toString())))
                .timestamp(Instant.now())
                .footer("Proporcionado por El Átomo", "https://cdn.discordapp.com/app-icons/1105886242622681198/f386d217f931af95f5b8be5e8359182f.png?size=64")
                .build();
            return embed;
        } else {
            Snowflake channelId = canal.getChannelId(); //Obtener el ID del canal
                client.login()
                    .flatMap(gateway -> gateway.getChannelById(channelId)) //Obtener el canal por ID
                    .ofType(MessageChannel.class) //Filtrar solo instancias de MessageChannel
                    .flatMap(channel -> channel.createMessage("Tu usuario no existe :face_in_clouds:")) //Crear y enviar el mensaje
                    //.doOnSuccess(message -> System.out.println("Mensaje enviado: " + message.getContent())) //Realizar alguna acción cuando el mensaje se envíe con éxito
                    .doOnError(Throwable::printStackTrace) //Manejar cualquier error que ocurra
                    .doFinally(signalType -> gateway.logout()) //Cerrar la conexión cuando se haya completado la operación
                    .subscribe(); //Suscribirse al flujo de eventos
        }
        return null;
    }

    public String atomoCorrecto(long usuarioID) {
        String dificultad = "Fácil";
        switch (DeuxExMachina.dificultad) {
            case 1:dificultad = "Fácil";break;
            case 2:dificultad = "Normal";break;
            case 3:dificultad = "Difícil";break;
        }
        Document query = new Document("ID", usuarioID);
        Document result = collection.find(query).first();
        if (result != null) {
            Document atomosCapturados = result.get("Atomos capturados", Document.class);
            atomosCapturados.get("dificultad");
            Document filtro = new Document("Atomos capturados", new Document(dificultad, ""));
            
            return null;
        }
        return null;
    }

    public void salir() {
        mongoClient.close();
    }
}
