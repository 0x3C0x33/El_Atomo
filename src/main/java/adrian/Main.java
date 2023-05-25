package adrian;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import adrian.comandos.Polonio;
import adrian.datos.DeuxExMachina;
import discord4j.core.DiscordClient;

public class Main {
    private static final String TOKEN = "";
    private static boolean finalizar = false;
    private static String prompt = "";
    private static int numHilos = 1;
    private static Scanner tc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        DiscordClient client = DiscordClient.create(TOKEN);

        CountDownLatch latch = new CountDownLatch(numHilos);

        Polonio polonio = new Polonio(latch,client);
        polonio.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Todo listo!");
        while (!finalizar) {
            System.out.print("admin$bot>");
            prompt = tc.nextLine();
            switch (prompt) {
                case "cambiarDificultad":cambiarDificultad();break;
                case "ayuda":ayuda();break;
                case "salir":System.out.println("Saliendo...");System.exit(0);break;
                default:System.out.println("Comando no reconocido, intentelo de nuevo. Escriba 'ayuda' para ver los comandos.");;break;
            }
        }
    }

    private static void cambiarDificultad() {
        try {
            System.out.println("Dificultades: 0 = sin dificultad, 1 = fácil, 2 = normal, 3 = difícil.");
            System.out.print("Nueva dificultad => ");
            int dif = tc.nextInt();
            tc.nextLine();
            if (dif < 4 && dif > -1) {
                DeuxExMachina.dificultad = dif;
                System.out.println("Dificultad cambiada con exito.");
            } else {
                System.out.println("Dificultad no valida...");
            }
        } catch (InputMismatchException e) {
            System.err.println("Error de tipo de dato.");
        } catch (Exception e) {
            System.out.println("Error inesperado. Contacte con su tecnico. Log: ");
            e.printStackTrace();
        }
    }

    private static void ayuda() {
        System.out.println("====== Comandos ======");
        System.out.println("");
        System.out.println("ayuda : Muestra los comandos disponibles.");
        System.out.println("salir : Apaga el bot.");
        System.out.println("cambiarDificultad : Cambia la dificultadad del juego entre 0=sin dificultad, 1=fácil, 2=normal o 3=difícil.");
    }

}
