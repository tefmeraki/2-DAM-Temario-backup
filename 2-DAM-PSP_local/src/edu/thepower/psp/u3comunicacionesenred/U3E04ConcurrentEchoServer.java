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

public class U3E04ConcurrentEchoServer implements Runnable {
	private static SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	private static final long TIEMPO_LATIDO = 30000; // Intrevalo entre cada una de las trazas de latido.
	
	private Socket socket;
	private String nombre;
	
	public U3E04ConcurrentEchoServer (Socket socket) {
		this.socket = socket;
	}

	private static String getHora() {
		return formato.format(System.currentTimeMillis());
	}
	
	@Override
	public void run() {
		nombre = "[" + Thread.currentThread().getName()+"] ";
		boolean vivo = true;
		while (vivo) {
			try {
				InputStream in = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				OutputStream os = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(os, true);
				String linea;
				while ((linea = br.readLine()) != null) {
					System.out.println(getHora() + " - " + nombre + "Recibido del cliente: " + linea);
					if (!linea.equals("0"))
						writer.println(linea.toUpperCase());
					else
						vivo = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(nombre + "Finalizado.");
	}

	public static void main(String[] args) {
		// Thread demonio para mostrar una traza de latido cada cierto tiempo
		Thread latido = new Thread (() -> {
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
		
		try (ServerSocket servidor = new ServerSocket(5000)){
			System.out.println(getHora() + " *** Servidor: esperando conexiones en el puerto 5000...");
			while (true) {
				Socket s = servidor.accept();
				System.out.println(getHora() + " - Servidor - Nueva solicitud recibida de: " + s.getRemoteSocketAddress() + ":" + s.getPort());
				Thread t = new Thread(new U3E04ConcurrentEchoServer(s));
				t.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
