package edu.thepower.psp.eval.t2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PSPT2P1CalcServer {
	private static final int PUERTO = 5000;
	private static final String RESULTADO = "RESULTADO = ";
	
	public static void main(String[] args) {
		try (ServerSocket servidor = new ServerSocket(PUERTO)) {
			System.out.println("Esperando conexiones en el puerto " + PUERTO);
			while (true) {
				Socket socket = servidor.accept();
				Thread t = new Thread(() -> atenderCliente(socket));
				t.start();
			}
		} catch (IOException e) {
			System.err.println("Error en el servidor: " + e.getMessage());
		}
	}
	
	private static void atenderCliente (Socket socket) {
		String cliente = socket.getInetAddress() + ":" + socket.getPort();
		System.out.println("Nuevo cliente - " + cliente);
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
			String comando;
			String[] partes;
			String respuesta;
			while ((comando = reader.readLine()) != null) {
				respuesta = "";
				partes = comando.split("\\s+", 4);
				// Validar que hay, al menos 3 partes, operador y los dos operandos
				if (partes.length > 2) {
					String operacion = partes[0].trim();
					float operando1 = 0;
					float operando2 = 0;
					
					// 	Validar que los operandos son números
					try {
						operando1 = Float.valueOf(partes[1]);
						operando2 = Float.valueOf(partes[2]);
					} catch (NumberFormatException e) {
						respuesta = "ERROR: alguno de los operandos no es un número.";
					}
					
					if (!respuesta.contains("ERROR")) {
						respuesta = switch (operacion.toUpperCase()) {
								case "SUM", "SUMA" -> RESULTADO + (operando1 + operando2); 
								case "RES" -> RESULTADO + (operando1 - operando2);
								case "MUL", "MULT" -> RESULTADO + (operando1 * operando2);
								case "DIV" -> {
									if (operando2 == 0)
										yield "ERROR: el segundo operando no puede ser 0 (división por 0)";
									else
										yield RESULTADO + (operando1 / operando2);
								} 
								default -> "*** ERROR: el comando enviado no existe.";
							};
					}
				} else {
					if (partes[0].trim().equalsIgnoreCase("fin"))
						respuesta = "Hasta la vista";
					else
						respuesta = "ERROR: Uso -> operador <operando 1> <operando 2>";
				}
				
				writer.println(respuesta);
			}
		} catch (IOException e) {
			System.err.println("Error en la conexión con " + cliente + ": " + e.getMessage());
		}
		
		System.out.println("Cliente " + cliente + " desconectado.");
	}
}
