package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Aplicación cliente-servidor donde:
 * El servidor mantiene un contador entero compartido entre todos los clientes.
 * Varios clientes pueden conectarse al servidor a la vez (servidor multihilo).
 * Cada cliente puede enviar comandos de texto al servidor:
 * - inc → incrementa el contador en 1 y devuelve el nuevo valor.
 * - dec → decrementa el contador en 1 y devuelve el nuevo valor.
 * - get → devuelve el valor actual del contador sin modificarlo.
 * - bye → el servidor cierra la conexión con ese cliente.
 * Cualquier otro texto será devuelto por el servidor indicando que no es un comando válido.
 * El servidor debe:
 * - Atender a cada cliente en un thread independiente.
 * - Proteger el contador compartido para evitar condiciones de carrera.
 * El cliente:
 * - Lee comandos por teclado.
 * - Ignorar si el comando está en mayúsculas o minúsculas (no es "case sensitive").
 * - Los envía al servidor.
 * - Muestra la respuesta hasta que el usuario escriba BYE.
 */

public class U3E06ContadorCompartidoCliente {
	private static final String BYE = "bye";

	public static void main(String[] args) {
		try (Socket socket = new Socket("localhost", 5000)) {
			BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			String comando;
			do {
				System.out.println("Introduce un comando (inc/dec/get/bye): ");
				comando = consola.readLine().trim();
				writer.println(comando);
				System.out.println(">>> " + input.readLine());
			} while (!comando.equalsIgnoreCase(BYE));
			
		} catch (IOException e) {
			System.err.println("Error en la lectura/escritura de datos: " + e.getMessage());
		}
		System.out.println("Finalizada conexión con el servidor.");
	}

}
