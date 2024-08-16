package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.swing.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;


public class Tareabot extends TelegramLongPollingBot {
    private Set<Long> welcomedUsers = new HashSet<>();
    @Override
    public String getBotUsername() {
        return "@NuevomiBot";
    }

    @Override
    public String getBotToken() {
        return "7300857281:AAEzlKv9Ip3quT3POqoP6-chBZoVHnj7dDU";
    }

    //El método onUpdateReceived(Update update) de la clase Bot se usa para manejar todas las actualizaciones que el
    // bot recibe.
    // Dependiendo del tipo de actualización, se toman diferentes acciones.

    @Override
    public void onUpdateReceived(Update update) {


        //Se verifica si la actualización contiene un mensaje y si ese mensaje tiene texto.
        //Luego se procesa el contenido del mensaje y se responde según el caso.
        String nombre = update.getMessage().getFrom().getFirstName();
        String apellido = update.getMessage().getFrom().getLastName();
//      String nickname = update.getMessage().getFrom().getUserName();



        if (update.hasMessage() && update.getMessage().hasText()) {

            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            int messageDate = update.getMessage().getDate(); // Fecha en segundos desde la época Unix

            // Convertir la marca de tiempo del mensaje a Instant
            Instant instant = Instant.ofEpochSecond(messageDate);

            // Convertir Instant a LocalDateTime usando la zona horaria del sistema
            LocalDateTime receivedDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Definir el formato deseado
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Formatear la fecha y hora
            String formattedDateTime = receivedDateTime.format(formatter);

            System.out.println("User id: " + chat_id + " Message: " + message_text);
            System.out.println(nombre + " " + apellido + " Send: " + message_text);



            if (message_text.equalsIgnoreCase("hola")){
                sendText(chat_id, "hi");
            }


            if (message_text.equalsIgnoreCase("/info")) {
                sendText(chat_id, "Bryan Sandoval, Carnet: 10305, Cursando el 4to semestre");
            }

            if ( message_text.equalsIgnoreCase("/progra")) {
                sendText(chat_id, "No me gusta la clase de programacion cuando tenemos que copiar el texto y ya, pero comprendo de que si no es asi, casi no aprenderiamos nada en la clase");
            }


            if (message_text.equalsIgnoreCase("/hola")) {

                sendText(chat_id, "Hola" + " " + nombre + " " + apellido + " Fecha y hora: "+ formattedDateTime);
            }


            if (message_text.startsWith("/cambio")) {
                // Extraer la cantidad de Quetzales del mensaje
                String[] parts = message_text.split(" ");
                if (parts.length == 2) {

                    try {
                        double quetzales = Double.parseDouble(parts[1]);
                        double euros = quetzales * 0.12;
                        String result = String.format("Son %.2f euros.", euros);
                        sendText(chat_id, result);
                    }catch (NumberFormatException e){

                    }
                }
            }

        }

    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }
}
