package Clases;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.List;

public class Recibir implements Runnable {
    private final static int MAX_BYTES = 1400;
    DatagramSocket socket;
    private final static String COD_TEXTO = "UTF-8";
    private List<Mensaje> mensajesRecibidos = null;
    HashMap<String, User> usuarios = null;
    boolean esCliente = false;

    public Recibir(DatagramSocket socket, List<Mensaje> mensajesRecibidos, HashMap<String, User> usuarios, boolean esCliente) { 
        this.socket = socket;
        this.mensajesRecibidos = mensajesRecibidos;
        this.usuarios = usuarios;
        this.esCliente = esCliente;
    }

    @Override
    public void run() {
        byte[] recibido = new byte[MAX_BYTES];
        DatagramPacket pRecibido = new DatagramPacket(recibido, recibido.length);
        //Se queda escuchando mensajes del servidor y del cliente
        while (true) {
            try {
                //Recibe el mensaje
                socket.receive(pRecibido);
                String mensaje = new String(pRecibido.getData(), 0, pRecibido.getLength(), COD_TEXTO);
                if (mensajesRecibidos != null) {
                    Mensaje mensajeRecibido = new Mensaje(mensaje);
                    //Si es un cliente, se agrega el usuario a la lista de usuarios
                    if (usuarios != null) {
                        //Si el usuario no está en la lista de usuarios, se agrega
                        if (!usuarios.containsKey(mensajeRecibido.nombreUsuario)) {
                            User user = new User(mensajeRecibido.nombreUsuario, pRecibido.getAddress(),
                                    pRecibido.getPort());
                            usuarios.put(user.getUsername(), user);
                        }
                    }
                    //Se agrega el mensaje a la lista de mensajes recibidos
                    synchronized (mensajesRecibidos) {
                        mensajesRecibidos.add(mensajeRecibido);
                        //Se notifica a los hilos que están esperando un mensaje
                        mensajesRecibidos.notifyAll();
                    }
                } 
              
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
