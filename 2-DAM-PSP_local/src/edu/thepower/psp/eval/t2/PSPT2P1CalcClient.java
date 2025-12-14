package edu.thepower.psp.eval.t2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PSPT2P1CalcClient {

	public static void main(String[] args) {
		try (Socket socket = new Socket("localhost", 5000)) {
			BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			String comando;
			do {
				System.out.println("Introduce operción y operandos (SUM(A) | RES | MUL(T) | DIV | FIN <opwrando 1> <operando 2>): ");
				comando = consola.readLine().trim();
				writer.println(comando);
				System.out.println(">>> " + input.readLine());
			} while (!comando.equalsIgnoreCase("fin"));
			
		} catch (IOException e) {
			System.err.println("Error en la lectura/escritura de datos: " + e.getMessage());
		}
		System.out.println("Finalizada conexión con el servidor.");
	}

}
