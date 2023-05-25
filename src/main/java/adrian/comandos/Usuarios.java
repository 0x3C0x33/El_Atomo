package adrian.comandos;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class Usuarios {
    public static void main(String[] args) {
        ConnectionString connString = new ConnectionString(
                "mongodb+srv://<username>:<password>@cluster0.mongodb.net/test?retryWrites=true&w=majority");

        MongoClient mongoClient = MongoClients.create(connString);
        MongoDatabase database = mongoClient.getDatabase("test");

        MongoCollection<Document> collection = database.getCollection("myCollection");

        Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new Document("x", 203).append("y", 102));

        collection.insertOne(doc);
    }
}
