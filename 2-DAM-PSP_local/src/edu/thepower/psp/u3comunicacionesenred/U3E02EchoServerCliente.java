package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

/*
 * Uso: U3E02EchoServerCliente <número_puerto>
 * Donde <número_puerto> debe ser un valor entero entre 1.024 y 65.535
 */

public class U3E02EchoServerCliente {
	private static final String URL_SERVIDOR = "localhost";
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
		
		// En lugar de Scanner, uso alternativo de BufferedReader.
		// BufferedReader, como se verá en el ejercicio de la aplicación del chat, permite que no haya bloqueo cuando se espera que el usuario introduzca info
		
		//try (Scanner scanner = new Scanner(System.in);
		try (BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
			 Socket socket = new Socket(URL_SERVIDOR, puerto);) {
			//OutputStream out = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String linea = null;
			boolean continuar = true;
			do {
				System.out.println("Introduce un texto: ");
				linea = consola.readLine().trim(); // Leemos la entrada por teclado
				if (!linea.equalsIgnoreCase(SALIR)) {
					writer.println(linea); // Se envía al servidor lo que se ha introducido por teclado.
					System.out.println("*** Recibido del servidor: " + br.readLine());
				}
				else
					continuar = false; // Se ha dado la orden de salir. Hay que salir, por tanto, del bucle.
					// Se hace un cierre del socket auomático. Esto envía un fin de flujo al servidor (EOF) indicando que la conexión ha finalizado.
			} while (continuar); // Cuando se introduzca por teclado /salir, se sale del bucle y finaliza la conexión.
		} catch (IOException e) {
			System.err.println("Error en la comunicación con el servidor: " + e.getMessage());
		}
		
		System.out.println("** Fin conexión.");

	}

}
