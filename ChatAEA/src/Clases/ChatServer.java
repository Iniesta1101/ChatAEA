package Clases;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatServer {
	HashMap<String, Room> rooms = new HashMap<>();
	HashMap<String, User> usuarios = new HashMap<>();
	List<Mensaje> mensajesRecibidos = new ArrayList<>();
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Debe indicarse el puerto en el  que escuchar");
			System.exit(1);
		}

		int numPuerto = Integer.parseInt(args[0]);
		ChatServer cs = new ChatServer();
		cs.startServer(numPuerto);

	}

	// Inicia el servidor
	public void startServer(int numPuerto) {
		try {
			DatagramSocket serverSocket = new DatagramSocket(numPuerto);

			System.out.println("Servidor escuchando en el puerto " + numPuerto);
			
			//Hilo que recibe mensajes
			Recibir r = new Recibir(serverSocket, mensajesRecibidos, usuarios, false);
			Thread th = new Thread(r);
			th.start();

			System.out.println("Servidor listo para recibir mensajes.");

			//Hilo que procesa mensajes
			procesarMensaje(serverSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void procesarMensaje(DatagramSocket serverSocket) throws IOException {
		Room r;
		Mensaje mensajeAEnviar;
		int accion;

		//Se queda escuchando mensajes del cliente
		synchronized (mensajesRecibidos) {
			while (true) {
				//Espera a que haya mensajes en la lista de mensajes recibidos
				while (mensajesRecibidos.isEmpty()) {
					try {
						mensajesRecibidos.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				//Comprueba si hay usuarios en alguna sala, si no, la elimina
				for (Room room : rooms.values()) {
					if (room.getUsers().isEmpty()) {
						rooms.remove(room.getName());
					}
				}
				//Procesa el mensaje
				Mensaje mensajeRecibido = mensajesRecibidos.get(0);
				accion = Integer.parseInt(mensajeRecibido.accion);
				User u = usuarios.get(mensajeRecibido.nombreUsuario);

				
				switch (accion) {
					//Conexión
					case 0:
						mensajeAEnviar = new Mensaje("0", "", "", "200");
						try {
							u.enviarDatos(serverSocket, mensajeAEnviar);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case 1: // Listar las salas
						StringBuilder sBuilder = new StringBuilder();
						for (String sala : rooms.keySet()) {
							sBuilder.append(sala).append(".");
						}
						String listaSalas = sBuilder.toString();
						if (listaSalas == null || listaSalas.isEmpty()) {
							mensajeAEnviar = new Mensaje("1", "", "", "404");
						} else {
							mensajeAEnviar = new Mensaje("1", "", "", listaSalas);
						}
						try {
							u.enviarDatos(serverSocket, mensajeAEnviar);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case 2: // Crear sala
						System.out.println("Creando sala " + mensajeRecibido.nombreSala);
						r = new Room(mensajeRecibido.nombreSala, serverSocket);
						setRooms(mensajeRecibido.nombreSala, r);
						r.setUsers(u);
						Thread roomThread = new Thread(r);
						roomThread.start();
						mensajeAEnviar = new Mensaje("2", "", "", "201");
						;
						System.out.println("Sala creada con éxito.");
						u.enviarDatos(serverSocket, mensajeAEnviar);
						break;
					case 3: // entrar en la sala
						r = rooms.get(mensajeRecibido.nombreSala);
						if (r != null) {
							r.setUsers(u);
							mensajeAEnviar = new Mensaje("3", "", "", "200");
							;
						} else {
							mensajeAEnviar = new Mensaje("3", "", "", "404");
						}
						u.enviarDatos(serverSocket, mensajeAEnviar);
						break;
					case 4: //En la sala
						r = rooms.get(mensajeRecibido.nombreSala);
						r.setMensajes(mensajeRecibido);
						break;
					case 5: //Salir de la sala. Borro el usuario del HashMap de usuarios de la sala
						r = rooms.get(mensajeRecibido.nombreSala);
						usuarios.remove(mensajeRecibido.nombreUsuario);
						break;

				}
				//Elimina el mensaje de la lista de mensajes recibidos
				mensajesRecibidos.remove(0);				
			}
		}
	}

	//Añade una sala al HashMap de salas
	public void setRooms(String nombre, Room r) {
		this.rooms.put(nombre, r);
	}
}
