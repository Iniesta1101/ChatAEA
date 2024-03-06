# Chat en Tiempo Real con Sockets en Java
---
Este proyecto trata de un aplicación de chat en timepo real utilizando la programación de sockets en java.
Está aplicación permitirá a los usuarios conectarse a un servidor, unirse a diferentes salas de chat , enviar y recibir mensajes, y explorar las salas de chat disponibles.

## Descripción

Cuando un usuario entre a la aplicación se le pedirá su nombre, y la ip y el puerto del servidor. Luego le mostrará un menú con cuatro opciones:

    1. Mostrar las salas disponibles
    2. Crear una nueva sala y acceder a ella
    3. Acceder a una sala que ya ha creado otro usuario
    4. Salir de la aplicación. (El programa finaliza)

En caso de que hayas entrado en alguna de las salas pordrás comunicarte con los usuarios que accedan a ella. Para salir de una sala deberás escribir '/exit', y volverás al menú principal

## Clases

Este proyecto está compuesto de las siguientes clases:

    - ChatClient: Esta clase se encarga de la comunicación con el servidor. Tiene propiedades para el nombre de usuario, el nombre de la sala, el mensaje
    recibido, el mensaje a enviar, el socket del cliente, el host, el puerto del servidor, la dirección IP del servidor, una lista de mensajes recibidos y un
    indicador de si el cliente está en una sala. Los métodos incluyen un constructor que crea el socket del cliente e inicia un hilo para recibir mensajes del
    servidor, y un método main que inicia el cliente y muestra el menú

    - ChatServer: Esta clase representa el servidor de chat. Mantiene un registro de todas las salas de chat (rooms) y usuarios (usuarios) en HashMaps. También
    mantiene una lista de todos los mensajes recibidos (mensajesRecibidos). Los métodos incluyen startServer para iniciar el servidor, procesarMensaje para
    procesar los mensajes recibidos y setRooms para añadir una sala al HashMap de salas. ChatServer necesita el puerto pasado por argumentos y escuha por defecto
    la dirección local 'localhost'

    - Recibir: Esta clase implementa la interfaz Runnable y se encarga de recibir mensajes de un socket. Tiene propiedades para el socket, una lista de mensajes
    recibidos, un mapa de usuarios, y un indicador de si es un cliente. En su método run, se queda escuchando mensajes del servidor y del cliente, y procesa cada
    mensaje recibido. Si el mensaje es de un nuevo usuario, se agrega el usuario a la lista de usuarios. Luego, se agrega el mensaje a la lista de mensajes
    recibidos y se notifica a todos los hilos que están esperando un mensaje

    - Mensaje: Esta clase representa un mensaje en el sistema de chat. Tiene propiedades para la acción (el tipo de mensaje), el nombre del usuario que envía el
    mensaje, el nombre de la sala de chat donde se envía el mensaje, y el contenido del mensaje. Los métodos incluyen dos constructores (uno que toma los valores
    de las propiedades como parámetros, y otro que toma una cadena y la divide en las propiedades), métodos getter para las propiedades, y un método toString que
    devuelve una representación de cadena del mensaje.

    - Room:  Esta clase representa una sala de chat. Tiene propiedades para el nombre de la sala, una lista de mensajes de la sala, un mapa de usuarios en la
    sala, y un socket del servidor. Los métodos incluyen un constructor que establece el nombre de la sala y el socket del servidor, un método run que procesa los
    mensajes en la sala, métodos para obtener y establecer los usuarios en la sala, y un método para añadir mensajes a la lista de mensajes de la sala.

    - User: Esta clase representa un usuario en el sistema de chat. Tiene propiedades para el nombre de usuario, la dirección IP del usuario y el puerto. Los
    métodos incluyen un constructor que establece el nombre de usuario, la dirección IP y el puerto, métodos getter y setter para las propiedades, y un método
    para enviar un mensaje al usuario. 

---
URL: https://github.com/Iniesta1101/ChatAEA/tree/main
