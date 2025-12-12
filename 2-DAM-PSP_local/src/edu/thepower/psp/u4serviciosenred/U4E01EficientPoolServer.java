package edu.thepower.psp.u4serviciosenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Ejemplo de servidor implementado de forma eficiente utilizando un pool de threads.
 * El servidor implementa un echo server.
 */

public class U4E01EficientPoolServer {
	private static final int PORT = 5000;
	private static final int POOL_SIZE = 5;
	private static final ExecutorService POOL_THREADS = Executors.newFixedThreadPool(POOL_SIZE);
	
	public static void main(String[] args) {
		
		try (ServerSocket servidor = new ServerSocket(PORT)) {
			System.out.println("Esperando conexiones en el puerto " + PORT);
			while (true) {
				Socket socket = servidor.accept();
				POOL_THREADS.submit(() -> procesarCliente(socket));
			}
		} catch (IOException e) {
			System.err.println("Error en el servidor: " + e.getMessage());
		}
	}
	
	private static void procesarCliente (Socket socket) {
		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
			String nombreThread = "[" + Thread.currentThread().getName() + "] ";
			System.out.println("Cliente atendido por thread " + nombreThread);
			String recibido;
			while ((recibido = input.readLine()) != null) {
				System.out.println(nombreThread + "Recibido de cliente: " + recibido);
				writer.println (recibido.toLowerCase());
			}
		} catch (IOException e) {
			System.err.println("Error en la conexión: " + e.getMessage());
		}
	}
}
