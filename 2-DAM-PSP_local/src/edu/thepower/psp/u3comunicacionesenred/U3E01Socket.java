package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/*
 * Tema 3. Programación de comunicaciones en red.
 * https://oscarmaestre.github.io/servicios/textos/tema3.html
 * U3E01: código que muestra cómo se usa la clase Socket para contactar con un programa o servicio remoto.
 * 
 */

public class U3E01Socket {
	private static final String HOST = "whois.internic.net";
	private static final int PUERTO = 43;
	private static final String DOMINIO = "google.com";
	
	public static void main (String...args) {
		try (Socket socket = new Socket (HOST, PUERTO)) {
			OutputStream out = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(out, true);
			writer.println(DOMINIO);
			
			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String linea;
			while ((linea = br.readLine()) != null)
				System.out.println(linea);
		} catch (IOException e) {
			System.err.println("Error en la conexión: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
