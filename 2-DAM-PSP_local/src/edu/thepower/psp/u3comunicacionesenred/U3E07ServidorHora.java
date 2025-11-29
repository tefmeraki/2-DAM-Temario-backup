package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class U3E07ServidorHora {
	private static final int PUERTO = 5000;
	private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
	private static final String COMANDO_HORA = "hora";
	private static final String COMANDO_CONTADOR = "cont";
	private static final String COMANDO_SALIR = "bye";
	
	private static AtomicInteger contador = new AtomicInteger();

	public static void main(String[] args) {
		try (ServerSocket servidor = new ServerSocket(PUERTO)) {
			System.out.println("Servidor escuchando en puerto " + PUERTO);
			while (true) {
				Socket socket = servidor.accept();
				Thread t = new Thread (() -> atenderSolicitud(socket));
				t.start();
				contador.incrementAndGet();
			}
		} catch (IOException e) {
			System.err.println("ERROR en el servidor: " + e.getMessage());
		}
	}
	
	private static void atenderSolicitud (Socket socket) {
		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)){
			String comando;
			String respuesta;
			while ((comando = input.readLine()) != null) {
				respuesta = switch (comando.trim().toLowerCase()) {
								case "hora" -> "Fecha y hora actules: " + LocalDateTime.now().format(FORMATO);
								case "cont" -> "Número de clientes atendidos: " + contador.get();
								case "bye" -> "Hasta la vista.";
								default -> "ERROR. Comando no reconocido.";
				};
				
				writer.println (respuesta);
			}
			
		} catch (IOException e) {
			System.err.println("Error en la conexión: " + e.getMessage());
		}
	}
}
