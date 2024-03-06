package Clases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//clase que se encarga de la comunicación con el servidor
public class ChatClient {
	private final static String COD_TEXTO = "UTF-8";
	String username, nombreRoom, recibido, mensaje;
	DatagramSocket clientSocket;
	String host = "";
	int puertoServ = 0;
	InetAddress ipServidor = null;
	List<Mensaje> mensajesRecibidos = new ArrayList<>();
	boolean estoyEnSala = false;
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		//Inicia el cliente
		ChatClient chatClient = new ChatClient();
		chatClient.displayMenu();
	}

	public ChatClient() throws SocketException {
		//Se crea el socket del cliente
		this.clientSocket = new DatagramSocket();
		//Inicamos el hilo que se encargará de recibir los mensajes del servidor
		Recibir r = new Recibir(clientSocket, mensajesRecibidos, null, true);
		Thread th = new Thread(r);
		th.start();
	}

	public void displayMenu() throws UnsupportedEncodingException, IOException {
		Mensaje mensaje;
		Scanner sc = new Scanner(System.in);
		//Pide los datos necesarios para conectarse al servidor
		System.out.println("Bienvenido al Sistema de Chat en Tiempo Real");
		System.out.println("--------------------------------------------");

		System.out.println("Por favor, ingresa tu nombre de usuario:");
		username = sc.next();

		System.out.println("Ingresa la IP del servidor al que deseas conectarte:");
		host = sc.next();
		try {
			ipServidor = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println("Ingresa el puerto del servidor:");
		this.puertoServ = sc.nextInt();

		System.out.println("Intentando conectar...");
		//Envía un mensaje al servidor para que le confirme que se ha conectado
		mensaje = new Mensaje("0", username, "", "");
		try {
			enviarDatos(puertoServ, ipServidor, mensaje.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Espera a que el servidor le confirme que se ha conectado
		synchronized (mensajesRecibidos) {
			while (mensajesRecibidos.isEmpty()) {
				try {
					mensajesRecibidos.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Mensaje mensajeRecibido = mensajesRecibidos.remove(0);
			procesarMensajeRecibido(mensajeRecibido);
		}

		while (true) {
			synchronized (mensajesRecibidos) {
				//Si no está en una sala, muestra el menú principal
				if (!estoyEnSala) {
					//Muestra el menú principal
					System.out.println("1. Listar salas disponibles");
					System.out.println("2. Crear una nueva sala");
					System.out.println("3. Unirse a una sala existente");
					System.out.println("4. Salir del chat");
					System.out.println("Por favor, elige una opción");
					int opcion = sc.nextInt();
					//Dependiendo de la opción elegida, envía un mensaje al servidor
					switch (opcion) {
						case 1:
							mensaje = new Mensaje("1", username, "", "");
							try {
								enviarDatos(puertoServ, ipServidor, mensaje.toString());
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						case 2:
							System.out.println("Ingresa el nombre de la nueva sala:");
							nombreRoom = sc.next();
							mensaje = new Mensaje("2", username, nombreRoom, "");
							try {
								enviarDatos(puertoServ, ipServidor, mensaje.toString());
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						case 3:
							System.out.println("Ingresa el nombre de la sala a la que desea unirse");
							nombreRoom = sc.next();
							mensaje = new Mensaje("3", username, nombreRoom, "");
							try {
								enviarDatos(puertoServ, ipServidor, mensaje.toString());
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						case 4:
							System.out.println("Cerrando conexión...");
							System.out.println("Gracias por usar el chat. ¡Hasta la próxima!");
							mensaje = new Mensaje("5", username, nombreRoom, "");
							try {
								enviarDatos(puertoServ, ipServidor, mensaje.toString());
							} catch (IOException e) {
								e.printStackTrace();
							}
							sc.close();
							System.exit(0);
							break;
						default:
							System.out.println("Opción no válida");
							continue;
					}
				}
				//Espera a que el servidor le responda
				while (mensajesRecibidos.isEmpty()) {
					try {
						mensajesRecibidos.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//Procesa el mensaje recibido
				Mensaje mensajeRecibido = mensajesRecibidos.remove(0);
				procesarMensajeRecibido(mensajeRecibido);
			}
		}
	}

	public void procesarMensajeRecibido(Mensaje recibido) throws IOException {
		//Procesa el mensaje recibido del servidor
		switch (recibido.accion) {
			case "0":
				System.out.println("Conexión establecida con el servidor");
				break;
			case "1":
			//Muestra las salas disponibles
				if (recibido.getContenido().equals("404")) {
					System.out.println("No hay salas disponibles");
					break;
				}
				String[] partes = recibido.getContenido().split("\\.");
				for (String sala : partes) {
					System.out.println("-" + sala);
				}
				break;
			case "2":
			//Crea una sala y accede a ella
				System.out.println("Sala creada con éxito");
				System.out.println("Has accedido a la sala " + nombreRoom);
				System.out.println("Para salir escriba '/exit'");
				estoyEnSala = true;
				salaChat();
				break;
			case "3":
			//Accede a una sala si existe
				if (recibido.getContenido().equals("200")) {
					System.out.println("Has accedido a la sala " + nombreRoom);
					System.out.println("Para salir escriba '/exit' ");
					estoyEnSala = true;
					salaChat();
				} else {
					System.out.println("Esa sala no existe");
				}
				break;
			case "4":
			//Muestra los mensajes de la sala
				System.out.println(recibido.getContenido());
				break;
			case "5":
			//Sale de la sala
				estoyEnSala = false;
				break;
			default:
				System.out.println("Opción no válida");
				break;
		}
	}

	//Envía un mensaje al servidor
	public void enviarDatos(int numPuerto, InetAddress ipServidor,
			String mensaje) throws IOException {
		DatagramPacket dp = new DatagramPacket(mensaje.getBytes(), mensaje.getBytes().length, ipServidor, numPuerto);
		clientSocket.send(dp);
	}

	//Método que se encarga de la comunicación en la sala
	public void salaChat() throws IOException {
		
		InputStreamReader is = new InputStreamReader(System.in, COD_TEXTO);
		BufferedReader bfr = new BufferedReader(is);
		//Inicia un hilo que se encargará de enviar los mensajes al servidor
		new Thread(() -> {
			while (true) {
				String lineaLeida;
				try {
					//Lee los mensajes del usuario y los envía al servidor
					lineaLeida = bfr.readLine();
					Mensaje mensaje = new Mensaje("4", username, nombreRoom, lineaLeida);

					enviarDatos(puertoServ, ipServidor, mensaje.toString());
					if (lineaLeida.equals("/exit")) {
						//Si el usuario escribe /exit, sale de la sala
						estoyEnSala = false;
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

}
