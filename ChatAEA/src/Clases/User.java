package Clases;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//Clase que representa un usuario
public class User {
	String username;
	InetAddress ipUsuario;
	int puerto;

	public User(String username, InetAddress ipUsuario, int puerto) {
		this.username = username;
		this.ipUsuario = ipUsuario;
		this.puerto = puerto;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public InetAddress getIpUsuario() {
		return ipUsuario;
	}

	public void setIpUsuario(InetAddress ipUsuario) {
		this.ipUsuario = ipUsuario;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	//Envía un mensaje al usuario
	public void enviarDatos(DatagramSocket serverSocket, Mensaje mensaje) throws IOException {
		System.out.println("Enviando mensaje: " + mensaje.toString());		
		DatagramPacket dp = new DatagramPacket(mensaje.toString().getBytes(), mensaje.toString().getBytes().length,
				ipUsuario, puerto);
		serverSocket.send(dp);
		System.out.println("Mensaje enviado");
	}

}
