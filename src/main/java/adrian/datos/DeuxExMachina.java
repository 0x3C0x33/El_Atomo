package adrian.datos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.rest.util.Color;

import java.io.File;
import java.io.IOException;

//Clase encargada de proporcionar las mismas variables a todas las clases.
public class DeuxExMachina {
    public static List<List<String>> elementos = new ArrayList<>();
    public static List<String> urls = new ArrayList<String>();
    public static List<Color> colores = new ArrayList<Color>();
    public static HashMap<String, Color> coloresMap = new HashMap<>();

    // Metodo para leer los elementos de un JSON
    static {
        try {
            String rutaElementos = "src/main/java/adrian/Datos/tabla.json";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(rutaElementos));
            JsonNode filaArrayNode = jsonNode.get("Tabla").get("Fila");
    
            for (JsonNode filaNode : filaArrayNode) {
                JsonNode celdaArrayNode = filaNode.get("Celda");
                List<String> celdas = new ArrayList<>();
    
                for (JsonNode celdaNode : celdaArrayNode) {
                    celdas.add(celdaNode.asText());
                }
    
                // Agregar el ArrayList de celdas a la lista de elementos
                elementos.add(celdas);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo para leer todas las urls del JSON urls
    static {
        try {
            String rutaUrls = "src/main/java/adrian/Datos/urls.json";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(rutaUrls));
            JsonNode urlArrayNode = jsonNode.get("urls");
            for (JsonNode urlNode : urlArrayNode) {
                String url = urlNode.asText();
                urls.add(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo para crear todos los colores para los tipo de elemenos
    static {
        coloresMap.put("Metal alcalino", Color.of(255,197,145));
        coloresMap.put("Alcalinotérreo", Color.of(255,223,145));
        coloresMap.put("Metal de post-transición", Color.of(255,249,145));
        coloresMap.put("Metal de transición", Color.of(237,255,146));
        coloresMap.put("Lantánido", Color.of(210,255,145));
        coloresMap.put("Actínido", Color.of(184, 255, 145));
        coloresMap.put("Metaloide", Color.of(146, 255, 158));
        coloresMap.put("No metal", Color.of(171, 145, 255));
        coloresMap.put("Halógeno", Color.of(249, 145, 255));
        coloresMap.put("Gas noble", Color.of(146, 223, 255));
    }

    static {
        colores.add(Color.of(255,197,145));
        colores.add(Color.of(255,223,145));
        colores.add(Color.of(255,249,145));
        colores.add(Color.of(237,255,146));
        colores.add(Color.of(210,255,145));
        colores.add(Color.of(184, 255, 145));
        colores.add(Color.of(146, 255, 158));
        colores.add(Color.of(171, 145, 255));
        colores.add(Color.of(249, 145, 255));
        colores.add(Color.of(146, 223, 255));
    }

}
