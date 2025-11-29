package edu.thepower.psp.u3comunicacionesenred;

/*
 * Cliente multithread para conectarse al servidor y recibir, de vuelta, el mensaje enviado, convertido a mayúsculas.
 * Los clientes acabam enviado un 0, que es la señal para que el servidor cierre la conexión.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class U3E04ClienteMultiThread {
	private static final String CIERRE = "0";

	public static void main(String[] args) {
		for (int i = 0;i < 10;i++) {
			Thread t = new Thread(() -> {
				String nombre = "[" + Thread.currentThread().getName() + "] ";
				try (Socket s = new Socket("localhost", 5000)){
					System.out.println(nombre + "Conectado al servidor.");
					OutputStream os = s.getOutputStream();
					PrintWriter writer = new PrintWriter(os, true);
					InputStream is = s.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					for (int j = 0;j < 10;j++) {
						String texto = nombre + ": " + j;
						System.out.println(nombre + "Enviando al servidor: " + texto);
						writer.println(texto);
						System.out.println(nombre + "Recibido del servidor: " + br.readLine());
						System.out.println(nombre + "Esperando 2 segundos para continuar...");
						Thread.sleep(2000);
					}
					// Cerrando la conexión enviando un 0
					writer.println(CIERRE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			
			t.start();
		}
	}
}
