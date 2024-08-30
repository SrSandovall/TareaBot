package bot.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String carne;
    private String nombre;
    private String correo;
    private String seccion;
    private long telegramid;
    private String activo;
    private Timestamp fecha_respuesta;
    private int pregunta_id;
    private String respuesta_texto;



    public int getPregunta_id() {return pregunta_id;}
    public void setPregunta_id(int pregunta_id) {this.pregunta_id = pregunta_id;}


    public Timestamp getfecha_respuesta(){return fecha_respuesta;}
    public void setfecha_respuesta(Timestamp fecha_respuesta){this.fecha_respuesta = fecha_respuesta;}

    public String getrespuesta_texto() {return respuesta_texto;}
    public void setrespuesta_texto(String texto) {this.respuesta_texto = texto;}



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarne() {
        return carne;
    }

    public void setCarne(String carne) {
        this.carne = carne;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public long getTelegramid() {
        return telegramid;
    }

    public void setTelegramid(long telegramid) {
        this.telegramid = telegramid;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }
}
