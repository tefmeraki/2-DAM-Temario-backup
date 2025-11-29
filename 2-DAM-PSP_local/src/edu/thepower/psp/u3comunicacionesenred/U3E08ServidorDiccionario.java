package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Iterator;

/*
 * U3E08 - Diccionario inglés-español
 * Aplicación cliente-servidor donde:
 * - El servidor mantiene un diccionario inglés-español en memoria.
 * - Varios clientes se pueden conectar simultáneamente para:
 * 		- Consultar traducciones: TRD <palabra>
 * 		- Añadir nuevas traducciones: INC <palabra> <traducción>
 * 		- Listar todas las palabras del diccionario y su traducción: LIS
 * 		- Salir: SAL o BYE
 * - El servidor es multithread: un hilo por cliente.
 */

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class U3E08ServidorDiccionario {
	private static final int PUERTO = 5000;
	//private static final Map<String, String> DICCIONARIO = new ConcurrentHashMap<>();
	private static final Map<String, String> DICCIONARIO = Collections.synchronizedMap(new TreeMap<>()); // TreeMap ordena por clave
	
	// Creación de las entradas iniciales del diccionario
	static {
		String[] claves = {"house", "water", "family", "good", "time"};
		String[] valores = {"casa", "agua", "familia", "bueno", "tiempo"};
		for (int i = 0;i < claves.length;i++)
			DICCIONARIO.put(claves[i], valores[i]);
	}

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
			String[] particulas;
			String respuesta;
			while ((comando = reader.readLine()) != null) {
				particulas = comando.split("\\s+", 3);
				respuesta = switch (particulas[0].trim().toUpperCase()) {
								case "TRD" -> {
									if (particulas.length > 1 && !particulas[1].isBlank())
										yield DICCIONARIO.getOrDefault(particulas[1].trim().toLowerCase(), "No se ha encontrado la palabra en el diccionario");
									else
										yield "*** ERROR: uso TRD <palabra>";
								}
								case "INC" -> {
									if (particulas.length > 2 && !particulas[1].isBlank() && !particulas[2].isBlank()) {
										DICCIONARIO.put(particulas[1].trim().toLowerCase(), particulas[2].trim().toLowerCase());
										yield "La traducción de " + particulas[1] + " como " + particulas[2] + " ha sido incluida en el diccionario.";
									} else
										yield "*** ERROR: uso INC <palabra> <traducción>";
								}
								case "LIS" ->
								// 1. Utilizando un stream
								//DICCIONARIO.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(", "));
								
								// 2. Con bucle for-each y StringBuffer
								/*{
									StringBuffer sb = new StringBuffer();
									for (Entry<String, String> e : DICCIONARIO.entrySet())
										sb.append(e.getKey()).append(": ").append(e.getValue()).append(", ");
									// Eliminar última coma
									if (sb.length() > 0)
										sb.setLength(sb.length() - 2);
									yield sb.toString();
								}*/
								
								// 3. Con ListIterator y StringBuffer
								{
									 StringBuffer sb = new StringBuffer();
									 Iterator<Entry<String, String>> it = DICCIONARIO.entrySet().iterator();
									 while (it.hasNext()) {
										 Entry<String, String> e = it.next();
										 sb.append(e.getKey()).append(": ").append(e.getValue());
										 if (it.hasNext())
											 sb.append(", ");
									 }
									 yield sb.toString();
								}
								case "BYE", "SAL" -> "Hasta la vista";
								default -> "*** ERROR: el comando enviado no existe.";
				};
			
				writer.println(respuesta);
			}
		} catch (IOException e) {
			System.err.println("Error en la conexión con " + cliente + ": " + e.getMessage());
		}
		
		System.out.println("Cliente " + cliente + " desconectado.");
	}
}
