package edu.thepower.psp.u3comunicacionesenred;

/*
 * Uso: U3E02EchoServerServidor <número_puerto>
 * Donde <número_puerto> debe ser un valor entero entre 1.024 y 65.535
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class U3E02EchoServerServidor {
	private static final String SALIR = "/salir";
	
	public static void main(String[] args) {
		int puerto = 0;
		// Hay que proporcionar al programa, como argumento de entrada, el puerto en el que debe escuchar.
		try {
			puerto = U3E02EchoServerVerificadorPuerto.verificarPuerto(args);
		} catch (IllegalArgumentException e) {
			System.err.println("Error inicio servidor: " + e.getMessage());
			System.exit(1);
		}
		
		try (ServerSocket servidor = new ServerSocket(puerto)){
			while (true) { // El servidor seguirá escuchando aunque el cliente se desconecte
				System.out.println("Esperando solicitudes en el puerto " + String.format("%,d", puerto));
				Socket socket = servidor.accept();
				System.out.println("Cliente conectado. Origen: " + socket.getInetAddress() + ":" + socket.getPort());
				InputStream is = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				OutputStream out = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(out, true);
				String mensaje;
				// En esta opción, al cerrarse el socket cliente, llega al final del flujo EOF, y el readLine recibe null.
				// while ((mensaje = br.readLine().trim()) != null) {
				while ((mensaje = br.readLine().trim()) != null && !mensaje.equalsIgnoreCase(SALIR)) { 
					System.out.println("Recibido del cliente: " + mensaje);
					// Se devuelve lo que se ha recibido pasado a minúsculas
					writer.println(mensaje.toLowerCase());
				}
				
				System.out.println("*** Cliente desconectado.");
			}
		} catch (IOException e) {
			System.out.println("Error en lectura/escritura soscket: " + e.getMessage());
		}
	}
}
