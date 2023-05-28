package adrian;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import adrian.comandos.Polonio;
import adrian.comandos.Usuarios;
import adrian.datos.DeuxExMachina;
import discord4j.core.DiscordClient;

public class Main {
    private static final String TOKEN = "";
    private static boolean finalizar = false;
    private static String prompt = "";
    private static int numHilos = 2;
    private static Scanner tc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        DiscordClient client = DiscordClient.create(TOKEN);

        CountDownLatch latch = new CountDownLatch(numHilos);
        Usuarios user = new Usuarios(latch,client);
        user.start();
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
                case "mensajesParaRadiar":mensajesParaRadiar();break;
                case "probabilidadParaRadiar":probabilidadParaRadiar();break;
                case "tiempoCancelacion":tiempoCancelacion();break;
                case "ayuda":ayuda();break;
                case "salir":System.out.println("Saliendo...");user.salir();System.exit(0);break;
                default:System.out.println("Comando no reconocido, intentelo de nuevo. Escriba 'ayuda' para ver los comandos.");;break;
            }
        }
    }

    private static void ayuda() {
        System.out.println("====== Comandos ======");
        System.out.println("");
        System.out.println(" · ayuda : Muestra los comandos disponibles.");
        System.out.println(" · salir : Apaga el bot.");
        System.out.println(" · cambiarDificultad : Cambia la dificultadad del juego entre 0=sin dificultad, 1=fácil, 2=normal o 3=difícil.");
        System.out.println(" · mensajesParaRadiar : Cambia el número mínimo de mensajes para que empiece la probabilidad de que aparezcan átomos. (P.Defecto: 20msj)");
        System.out.println(" · probabilidadParaRadiar : Cambia la probabilidad de que aparezcan átomos a partir del número indicado en 'mensajes para radiar, prob 0% al 100%. (P.Defecto: 10%)");
        System.out.println(" · tiempoCancelacion : Tiempo que tarda en que un átomo ya no pueda ser capturado (P.Defecto: 60s).");
        System.out.println(" · salir : Apaga el bot.");
        System.out.println(" · salir : Apaga el bot.");
        System.out.println(" · salir : Apaga el bot.");
        System.out.println(" · salir : Apaga el bot.");
        System.out.println(" · salir : Apaga el bot.");
    }

    private static void tiempoCancelacion() {
        try {
            System.out.println("Escriba el nuevo tiempo de cancelación en segundos.");
            System.out.print("Nuevo tiempo de cancelación => ");
            long temp = tc.nextInt();
            tc.nextLine();
            DeuxExMachina.tiempoCancelacion = temp * 1000;
            System.out.println("Tiempo cambiado con exito.");
        } catch (InputMismatchException e) {
            System.err.println("Error de tipo de dato.");
        } catch (Exception e) {
            System.out.println("Error inesperado. Contacte con su técnico. Log: ");
            e.printStackTrace();
        }
    }

    private static void probabilidadParaRadiar() {
        try {
            System.out.println("Escriba la nueva probabilidad entre el 0% y el 100% incluidos.");
            System.out.print("Nueva probabilidad => ");
            int prob = tc.nextInt();
            tc.nextLine();
            if (prob <= 100 && prob >= 0) {
                DeuxExMachina.dificultad = prob;
                System.out.println("Dificultad cambiada con exito.");
            } else {
                System.out.println("Dificultad no valida...");
            }
        } catch (InputMismatchException e) {
            System.err.println("Error de tipo de dato.");
        } catch (Exception e) {
            System.out.println("Error inesperado. Contacte con su técnico. Log: ");
            e.printStackTrace();
        }
    }

    private static void mensajesParaRadiar() {
        try {
            System.out.println("Número de mensajes antes de que inicie la probabilidad de radiado.");
            System.out.print("Nueva cantidad => ");
            int cant = tc.nextInt();
            tc.nextLine();
            DeuxExMachina.mensajesParaRadiar = cant;
            System.out.println("Número de mensajes cambiada con exito.");
        } catch (InputMismatchException e) {
            System.err.println("Error de tipo de dato.");
        } catch (Exception e) {
            System.out.println("Error inesperado. Contacte con su técnico. Log: ");
            e.printStackTrace();
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
            System.out.println("Error inesperado. Contacte con su técnico. Log: ");
            e.printStackTrace();
        }
    }
}
