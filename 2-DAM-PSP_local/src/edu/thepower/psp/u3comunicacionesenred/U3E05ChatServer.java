package edu.thepower.psp.u3comunicacionesenred;

/*
 * Versión de echo server concurrente (multicliente) pero incluyendo día y hora en las trazas.
 * Incluye también un thread demonio que muestra una traza de latido para 30 segundos para mostrar que el servidor sigue ejecutándose.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class Usuario implements Runnable {
	private static AtomicInteger invitado = new AtomicInteger();
	private Socket socket;
	private String nombre;
	private PrintWriter writer;
	private Set<Usuario> usuarios;

	public Usuario(Socket socket, Set<Usuario> usuarios) {
		this.socket = socket;
		this.usuarios = usuarios;
	}

	private void difundir(String mensaje) {
		synchronized (usuarios) { // Al ser un Set sincronizado, posiblemente esto es redundante.
			Iterator<Usuario> it = usuarios.iterator();
			while (it.hasNext()) {
				Usuario u = it.next();
				try {
					u.imprimir(mensaje);
				} catch (Exception e) {
					// El cliente se ha desconectado y hay que eliminarlo de la lista
					System.out.println("El usuario " + u + " se ha desconectado: " + e.getMessage());
					it.remove();
				}

			}
		}
	}

	private void imprimir(String mensaje) {
		writer.println(mensaje);
	}

	@Override
	public void run() {
		boolean vivo = true;

		try {
			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			OutputStream os = socket.getOutputStream();
			writer = new PrintWriter(os, true);
			String linea;
			// Solicitar nombre
			writer.println("Bienvendo al chat, ¿cómo te llamas?");
			nombre = br.readLine().trim();
			if (nombre == null || nombre.isBlank())
				nombre = "Invitado-" + invitado.incrementAndGet();
			// Añadimos a la lista de usuarios para que le lleguen las difusiones después de unirse y no antes.
			usuarios.add(this);
			difundir("[Servidor] " + nombre + " se ha unido al chat.");

			// Empezar a recibir mensajes
			while (vivo && (linea = br.readLine()) != null) {
				if (linea.trim().equalsIgnoreCase("/salir"))
					vivo = false;
				else
					difundir(nombre + ": " + linea);
			}
		} catch (IOException e) {
			System.out.println(nombre + " Salida abrupta.");
			// No mostramos la excepción.
			// e.printStackTrace();
		} finally {
			usuarios.remove(this);
			System.out.println(nombre+" se ha desconectado.");
			difundir("[Servidor] " + nombre + " ha abandonado el chat.");
		}
	}

	@Override
	public String toString() {
		return nombre;
	}

}

public class U3E05ChatServer {
	private static final long TIEMPO_LATIDO = 30000; // Intrevalo entre cada una de las trazas de latido.
	private static SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	private static final Set<Usuario> USUARIOS = Collections.synchronizedSet(new HashSet<>()); //

	public static void main(String[] args) {
		// Thread demonio para mostrar una traza de latido cada cierto tiempo
		Thread latido = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(TIEMPO_LATIDO);
				} catch (InterruptedException e) {
					System.out.println(getHora() + " - Servidor: latido interrumpido por excepción.");
					e.printStackTrace();
				}

				System.out.println(getHora() + " *** El servidor continúa activo.");
			}
		});
		latido.setDaemon(true);
		latido.start();

		try (ServerSocket servidor = new ServerSocket(5000)) {
			System.out.println(getHora() + " *** Servidor: esperando conexiones en el puerto 5000...");
			while (true) {
				Socket s = servidor.accept();
				System.out.println(getHora() + " - Servidor - Nueva solicitud recibida de: "
						+ s.getRemoteSocketAddress() + ":" + s.getPort());
				Usuario usuario = new Usuario(s, USUARIOS);
				// Si se une aquí, puede recibir difusiones antes de acceder al chat
				// En su lugar, se incluye en el Set en el código del thread, justo después de registrarse
				// USUARIOS.add(usuario);
				Thread t = new Thread(usuario);
				t.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getHora() {
		return formato.format(System.currentTimeMillis());
	}

}
