package edu.thepower.psp.u4serviciosenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Servidor HTTP básico que escucha en el puerto 8080 y devuelve una página HTML con un mensaje
 * Formato HTTP response: https://www.tutorialspoint.com/http/http_responses.htm
 */

public class U4E03BasicHTTPServer {
	private static final int PORT = 8080;
	private static final String HTML_RESP = """
			<HTML>
			<HEAD>
				<TITLE>Servidor HTTP</TITLE>
			</HEAD>
			<BODY>
				<H1>¡Hola, mundo!</H1>
				<P>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus ullamcorper rutrum metus, quis faucibus mauris pellentesque vel.
				Nam posuere lacus egestas sodales vestibulum. Praesent eget sagittis risus. Mauris ornare purus et lobortis porttitor.
				Etiam pulvinar erat odio, in consectetur dui aliquam eget.
				Pellentesque sagittis, velit et mattis tincidunt, tortor orci semper tellus, sodales accumsan nibh leo sit amet eros.
				Nam sed tellus sit amet ipsum condimentum facilisis. <p>
			</BODY>
		</HTML>
		"""; 
	
	public static void main(String[] args) {
		try (ServerSocket servidorHTTP = new ServerSocket(PORT)) {
			System.out.println("Servidor escuchando en puerto " + PORT);
			while (true) {
				Socket socket = servidorHTTP.accept();
				System.out.println("Solicitud HTTP recibida de " + socket.getInetAddress() + ":" + socket.getPort());
				Thread t = new Thread(() -> atenderSolicitud(socket));
				t.start();
			}
		} catch (IOException e) {
			System.err.println("Error en el servidor HTTP: " + e.getMessage());
		}
	}

	private static void atenderSolicitud(Socket socket) {
		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
			String linea = input.readLine();
			System.out.println("Petición recibida: " + linea);

			// Opcional: leer y descartar el resto de encabezados:
	        //while (input.ready()) input.readLine();
		
			//Responder siguiendo el protocolo HTTP
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println("Content-Length: " + HTML_RESP.getBytes().length);
            writer.println(); // Línea en blanco obligatoria entre headers y cuerpo
            writer.println(HTML_RESP);
            //writer.flush();  // Si no ponemos el autoflush
		} catch (IOException e) {
			System.err.println("Error en la conexión " + e.getMessage());
		}
	}
}
