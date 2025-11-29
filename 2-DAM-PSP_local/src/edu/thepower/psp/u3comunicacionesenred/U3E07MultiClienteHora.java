package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class U3E07MultiClienteHora {
	private static String[] comandos = {"hora", "cont", "nocom", "bye"};
	
	public static void main(String[] args) {
		for (int i = 0; i < 50;i++) {
			Thread t = new Thread (() -> simularCliente());
			t.start();
		}
	}
	
	private static void simularCliente() {
		try (Socket socket = new Socket ("localhost", 5000);
				 BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
				
				for (int i = 0; i < 50;i++) {
					System.out.println("*** Solicitud " + (i+1));
					for (int j = 0;j < comandos.length;j++) {
						writer.println(comandos[j]);
						System.out.println(input.readLine());
					}
				}
			} catch (IOException e) {
				System.err.println("Error en la conexión con el servidor: " + e.getMessage());
			}
	}
}
