package Clases;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

//Clase que representa una sala de chat
public class Room implements Runnable {
	String name;
	List<Mensaje> mensajesRoom = new ArrayList<>();
	HashMap<String, User> users = new HashMap<>();
	DatagramSocket serverSocket = null;

	public Room(String name, DatagramSocket serverSocket) {
		this.name = name;
		this.serverSocket = serverSocket;
	}

	//Hilo que procesa mensajes
	@Override
	public void run() {
		synchronized (mensajesRoom) {
			//Espera a que haya mensajes en la lista de mensajes recibidos para esta sala
			while (true) {
				synchronized (mensajesRoom) {
					while (mensajesRoom.isEmpty()) {
						try {
							mensajesRoom.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				//Procesa los mensajes
				for (Mensaje m : mensajesRoom) {
					if (m.contenido.equals("/exit")) {
						//Le mando un mensaje al usuario para confirmar que ha salido de la sala
						User u = users.get(m.nombreUsuario);
						Mensaje mensaje = new Mensaje("5", "", "", "");
						try {
							u.enviarDatos(serverSocket, mensaje);
						} catch (IOException e) {
							e.printStackTrace();
						}
						users.remove(m.nombreUsuario);
						m.contenido = m.nombreUsuario + " ha salido de la sala";
					}
					
					//Envía el mensaje a todos los usuarios de la sala
					if (m.nombreSala.equals(name)) {
						for (User u : users.values()) {
							Mensaje mensaje = new Mensaje(m.accion, m.nombreUsuario, m.nombreSala, "");
							//Si el usuario que envía el mensaje es el mismo que el que lo recibe, se le añade "Me >" al mensaje
							if (u.getUsername().equals(m.nombreUsuario)) {
								mensaje.contenido = "Me > " + m.contenido;
							} else {
								mensaje.accion = "4";
								mensaje.contenido = m.nombreUsuario + " > " + m.contenido;
							}
							try {
								u.enviarDatos(serverSocket, mensaje);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				//Se vacía la lista de mensajes
				mensajesRoom.clear();

				//Si no hay usuarios en la sala, se sale del bucle y se acaba el thread
				if (users.isEmpty()) { 
					break;
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public List<User> getUsers() {
		return new ArrayList<>(users.values());
	}

	public void setUsers(User user) {
		this.users.put(user.username, user);
	}

	public void setMensajes(Mensaje m) {
		synchronized (mensajesRoom) {
			this.mensajesRoom.add(m);
			mensajesRoom.notifyAll();
		}

	}

}
