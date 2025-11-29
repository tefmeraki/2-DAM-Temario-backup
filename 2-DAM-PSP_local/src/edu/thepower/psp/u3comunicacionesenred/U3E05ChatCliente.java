package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class U3E05ChatCliente {
	private static final String DESCONEXION = "*** Desconectado del chat. Intenta unirte de nuevo si quieres cotinuar con la conversación.";

	public static void main(String[] args) {
		// Esta variable es compartida por ambos hilos y determina si hay que seguir leyendo de teclado.
		// Si el usuario sale voluntariamente o hay cualquier problema con el demonio que escucha, indicará al programa que finalice.
		// Debe ser final (o efective final) para que no varíe la referencia y le thread siempre sepa qué valor tiene (no ha sido modificada la referencia por el thread principal, por ejemplo).
		// Tiene que ser AtomicBoolean para evitar la condición de carrera.
		final AtomicBoolean vivo = new AtomicBoolean(true);
		
		try (Socket s = new Socket("127.0.0.1", 5000)) {
			InputStream is = s.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			OutputStream os = s.getOutputStream();
			PrintWriter writer = new PrintWriter(os, true);
			
			// En lugar de Scanner, se utiliza BufferedReader para evitar que nextLine() bloquee la ejecución.
			// Si la ejecución se bloquea, el thread principal no sabe si tiene que finalizar porque ha ocurrido, por ejemplo, una excepción en el demonio que escucah
			//Scanner sc = new Scanner(System.in);
			BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
			
			// El thread escuchar recogerá todos los mensajes enviados por el servidor o por otro usuario del chat.
			Thread escuchar = new Thread (() -> {
				String recibido;
				try {
					while ((recibido = br.readLine()) != null) {
						System.out.println(recibido);
					}
				} catch (IOException e) {
					// e.printStackTrace();
					System.out.println(DESCONEXION); // Desconectado del chat
					vivo.set(false);
				}
			});
			escuchar.setDaemon(true);
			escuchar.start();
			
			// El primer mensaje que se lee es el que envía el servidor solicitando el nomnbre del usuario
			// Enviar nombre
			String nombre = sc.readLine();
			writer.println(nombre);
			
			// Enviar resto de mensajes
			String enviar;
			while (vivo.get()) {
				if (sc.ready()) {
					enviar = sc.readLine().trim();
					if (enviar == null)  // Hay  un EOF en consola
						vivo.set(false);
					else {
						if (enviar.equalsIgnoreCase("/salir"))
							vivo.set(false);
						else {
							if (!enviar.isBlank()) // Solo se envían mensajes si hay contenido.
								writer.println(enviar);
						}
					}
				}
			}
		} catch (IOException e) {
			// Desconectado del chat. El servidor, por ejemplo, ha dejado de estar activo.
			System.out.println("*** Desconectado del chat. Intenta unirte de nuevo si quieres cotinuar con la conversación.");
		}
	}
}
