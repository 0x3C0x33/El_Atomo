package adrian;

import adrian.comandos.Polonio;
import adrian.datos.DeuxExMachina;
import discord4j.core.DiscordClient;

public class Main {
    public static void main(String[] args) {
        DiscordClient client = DiscordClient.create("");

        Polonio polonio = new Polonio(client);
        polonio.start();
        
        //System.out.println(DeuxExMachina.elementos.toString());

    }

}
