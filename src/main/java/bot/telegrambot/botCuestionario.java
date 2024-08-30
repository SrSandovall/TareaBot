package bot.telegrambot;

import bot.dao.responsedao;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bot.model.User;
import bot.service.UserService;



public class botCuestionario extends TelegramLongPollingBot {

    private responsedao responseDao = new responsedao();
    private Map<Long, String> estadoConversacion = new HashMap<>();
    User usuarioConectado = null;
    UserService userService = new UserService();


    @Override
    public String getBotUsername() {
        return " @nuevomiBot";
    }

    @Override
    public String getBotToken() {
        return "7300857281:AAEzlKv9Ip3quT3POqoP6-chBZoVHnj7dDU";
    }



    private void inicioCuestionario(long chatId, String section) {
        seccionActiva.put(chatId, section);
        indicePregunta.put(chatId, 0);
        enviarPregunta(chatId);
    }




    @Override
    public void onUpdateReceived(Update actualizacion) {



        if (actualizacion.hasMessage() && actualizacion.getMessage().hasText()) {
            String messageText = actualizacion.getMessage().getText();
            long chatId = actualizacion.getMessage().getChatId();



            if (messageText.equals("/menu")) {
                sendMenu(chatId);
            } else if (seccionActiva.containsKey(chatId)) {
                manejaCuestionario(chatId, messageText);
            }
        } else if (actualizacion.hasCallbackQuery()) { //es una respusta de un boton
            String callbackData = actualizacion.getCallbackQuery().getData();
            long chatId = actualizacion.getCallbackQuery().getMessage().getChatId();
            inicioCuestionario(chatId, callbackData);
        }


        //obtener el nombre y apellido del usuario en una variable
        String userFirstName = actualizacion.getMessage().getFrom().getFirstName();
        String userLastName = actualizacion.getMessage().getFrom().getLastName();
        String nickName = actualizacion.getMessage().getFrom().getUserName();
        long chat_id = actualizacion.getMessage().getChatId();
        String mensaje_Texto = actualizacion.getMessage().getText();


        try {
            String state = estadoConversacion.getOrDefault(chat_id, "");
            usuarioConectado = userService.getUserByTelegramId(chat_id);

            // Verificación inicial del usuario, si usuarioConectado es nullo, significa que no tiene registro de su id de telegram en la tabla
            if (usuarioConectado == null && state.isEmpty()) {
                sendText(chat_id, "Hola " + formatUserInfo(userFirstName, userLastName, nickName) + ", no tienes un usuario registrado en el sistema. Por favor ingresa tu correo electrónico:");
                estadoConversacion.put(chat_id, "ESPERANDO_CORREO");
                return;
            }

            // Manejo del estado ESPERANDO_CORREO
            if (state.equals("ESPERANDO_CORREO")) {
                processEmailInput(chat_id, mensaje_Texto);
                return;
            }

            sendText(chat_id, "Envia /menu para iniciar el cuestionario ");

        } catch (Exception e) {
            sendText(chat_id, "Ocurrió un error al procesar tu mensaje. Por favor intenta de nuevo.");
        }









    }






    //funcion para formatear la información del usuario
    private String formatUserInfo(String firstName, String lastName, String userName) {
        return firstName + " " + lastName + " (" + userName + ")";
    }



    private String formatUserInfo(long chat_id, String firstName, String lastName, String userName) {
        return chat_id + " " + formatUserInfo(firstName, lastName, userName);
    }





    //verifica si el usurio está registrado en la tabla con su correo electrónico
    private void processEmailInput(long chat_id, String email) {
        sendText(chat_id, "Recibo su Correo: " + email);
        estadoConversacion.remove(chat_id); // Reset del estado
        try {
            usuarioConectado = userService.getUserByEmail(email);
        } catch (Exception e) {
            System.err.println("Error al obtener el usuario por correo: " + e.getMessage());
            e.printStackTrace();
        }





        if (usuarioConectado == null) {
            sendText(chat_id, "El correo no se encuentra registrado en el sistema, por favor contacte al administrador.");
        } else {
            usuarioConectado.setTelegramid(chat_id);
            try {
                userService.updateUser(usuarioConectado);
            } catch (Exception e) {
                System.err.println("Error al actualizar el usuario: " + e.getMessage());
                e.printStackTrace();
            }

            sendText(chat_id, "Usuario actualizado con éxito!");
        }
    }






    //función para enviar mensajes
    public void sendText(Long chatId,  String text) {

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
           //Message content
        try {
            execute(message);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }




    }






    private final Map<Long, Integer> indicePregunta = new HashMap<>();
    private final Map<Long, String> seccionActiva = new HashMap<>();
    private final Map<String, String[]> preguntas = new HashMap<>();





    public botCuestionario() {


        // Inicializa los cuestionarios con las preguntas.
        preguntas.put("SECTION_1", new String[]{"🤦‍♂️1.1- Estas aburrido?", "😂😂 1.2- Te bañaste hoy?", "🤡🤡 Pregunta 1.3"});
        preguntas.put("SECTION_2", new String[]{"Pregunta 2.1", "Pregunta 2.2", "Pregunta 2.3"});
        preguntas.put("SECTION_3", new String[]{"Pregunta 3.1", "Pregunta 3.2", "Pregunta 3.3"});
        preguntas.put("SECTION_4", new String[]{"4.1 Ya tenes ganas de irte para tu casa?", " 4.2 Cuantos anios tienes?" });


    }








    private void sendMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Selecciona una sección:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Crea los botones del menú
        rows.add(crearFilaBoton("Sección 1", "SECTION_1"));
        rows.add(crearFilaBoton("Sección 2", "SECTION_2"));
        rows.add(crearFilaBoton("Sección 3", "SECTION_3"));
        rows.add(crearFilaBoton("Sección 4", "SECTION_4"));


        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }




    private List<InlineKeyboardButton> crearFilaBoton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }




    private void enviarPregunta(long chatId) {
        String seccion = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);
        String[] questions = preguntas.get(seccion);

        if (index < questions.length) {
            sendText(chatId, questions[index]);
        } else {
            sendText(chatId, "¡Has completado el cuestionario!");
            seccionActiva.remove(chatId);
            indicePregunta.remove(chatId);
        }
    }



    private void manejaCuestionario(long chatId, String response) {
        String section = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);

        if ("SECTION_4".equals(section) && index == 1) {  // Pregunta 2 de la Sección 4
            try {
                int edad = Integer.parseInt(response);

                // Validar que la edad esté dentro de un rango razonable
                if (edad >= 0 && edad <= 100) {
                    sendText(chatId, "Tu edad es: " + edad);

                    // Crear un objeto User y guardar la respuesta en la base de datos
                    User user = new User();
                    user.setId(1); // Ajusta según tu lógica para identificar al usuario
                    user.setSeccion(section);
                    user.setPregunta_id(index);
                    user.setrespuesta_texto(String.valueOf(edad));
                    user.setTelegramid(chatId);
                    user.setfecha_respuesta(new Timestamp(System.currentTimeMillis()));

                    try {
                        responseDao.inserResponse(user);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        sendText(chatId, "Error al guardar la respuesta.");
                    }

                    // Avanzar a la siguiente pregunta
                    indicePregunta.put(chatId, index + 1);
                    enviarPregunta(chatId);
                } else {
                    sendText(chatId, "La edad ingresada no es válida. Debe ser un número entre 0 y 100.");
                    enviarPregunta(chatId);
                }
            } catch (NumberFormatException e) {
                sendText(chatId, "La respuesta proporcionada no es un número válido.");
                enviarPregunta(chatId);
            }
        } else {
            // Para otras preguntas, simplemente registrar la respuesta y avanzar
            sendText(chatId, "Tu respuesta fue: " + response);

            // Crear un objeto User y guardar la respuesta en la base de datos
            User user = new User();
            user.setId(1); // Ajusta según tu lógica para identificar al usuario
            user.setSeccion(section);
            user.setPregunta_id(index);
            user.setrespuesta_texto(response);
            user.setTelegramid(chatId);
            user.setfecha_respuesta(new Timestamp(System.currentTimeMillis()));

            try {
                responseDao.inserResponse(user);
            } catch (SQLException e) {
                e.printStackTrace();
                sendText(chatId, "Error al guardar la respuesta.");
            }

            // Avanzar a la siguiente pregunta
            indicePregunta.put(chatId, index + 1);
            enviarPregunta(chatId);
        }
    }






}
