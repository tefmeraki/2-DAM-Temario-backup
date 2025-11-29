package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class GestorCliente implements Runnable {
	Socket socket;
	String nombre;
	
	public GestorCliente (Socket socket) {
		this.socket = socket;
		// En nombre se almacena la IP (y puerto) origen de la comunicación.
		nombre = "[" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] ";
	}
	
	@Override
	public void run() {
		try {
			System.out.println(nombre + "Creada nueva conexión de cliente.");
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			OutputStream os = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(os, true);
			String linea;
			while ((linea = br.readLine()) != null) {
				System.out.println(nombre + "Recibido de cliente: " + linea);
				writer.println(linea.toLowerCase());
			}
		} catch (IOException e) {
			System.out.println("Error en la conexión: " + e.getMessage());
		}
		
		System.out.println(nombre + "*** Finalizada la conexión por el cliente.");
	}
}

public class U3E03ConcurrentEchoServerServidor {

	public static void main(String[] args) {
		final int PUERTO = 5000;
		
		System.out.println("Esperando conexiones en el puerto " + PUERTO);
		try (ServerSocket servidor = new ServerSocket(PUERTO)){
			while (true) {
				Thread t = new Thread(new GestorCliente(servidor.accept()));
				System.out.println("[Servidor] Recibida solicitud de conexión de nuevo cliente.");
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
