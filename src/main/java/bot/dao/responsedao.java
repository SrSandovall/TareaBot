package bot.dao;
import bot.db.DatabaseConnection;
import bot.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;

public class responsedao {

    public void inserResponse(User user) throws SQLException {
        String query = "INSERT INTO tb_respuestas ( seccion , pregunta_id, respuesta_texto, telegram_id, fecha_respuesta) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getSeccion());
            statement.setInt(2, user.getPregunta_id());
            statement.setString(3, user.getrespuesta_texto());
            statement.setLong(4, user.getTelegramid());
            statement.setTimestamp(5, user.getfecha_respuesta());

            statement.executeUpdate();
        }
    }

}
