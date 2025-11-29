package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class U3E08ClienteDiccionario {
	private static final String BYE = "bye";
	private static final String SAL = "sal";

	public static void main(String[] args) {
		try (Socket socket = new Socket("localhost", 5000)) {
			BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			String comando;
			do {
				System.out.println("Introduce un comando (TRD <palabra> | INC <palabra> <significado> | LIS | BYE o SAL): ");
				comando = consola.readLine().trim();
				writer.println(comando);
				System.out.println(">>> " + input.readLine());
			} while (!comando.equalsIgnoreCase(BYE) && !comando.equalsIgnoreCase(SAL));
			
		} catch (IOException e) {
			System.err.println("Error en la lectura/escritura de datos: " + e.getMessage());
		}
		System.out.println("Finalizada conexión con el servidor.");
	}

}
