package Clases;

//Clase que representa un mensaje
public class Mensaje {
    String accion;
    String nombreUsuario;
    String nombreSala;
    String contenido;

    public Mensaje(String accion, String nombreUsuario, String nombreSala, String contenido) {
        this.accion = accion;
        this.nombreUsuario = nombreUsuario;
        this.nombreSala = nombreSala;
        this.contenido = contenido;
    }

    public Mensaje(String mensaje) {
        String[] datos = mensaje.split("\\|", -1);
        this.accion = datos[0];
        this.nombreUsuario = datos[1];
        this.nombreSala = datos[2];            
        this.contenido = datos[3];
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public String getContenido() {
        return contenido;
    }

    public String toString() {
        return accion + "|" + nombreUsuario + "|" + nombreSala + "|" + contenido;
    }
}
