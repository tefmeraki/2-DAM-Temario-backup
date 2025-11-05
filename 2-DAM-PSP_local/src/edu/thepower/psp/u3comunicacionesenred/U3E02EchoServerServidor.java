package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class U3E02EchoServerServidor {

	public static void main(String[] args) {
		int puerto = 0;
		if (args.length == 0)
			System.out.println("Es necesario proporcionar el puerto en el que se recibirán las solicitudes.");
		else
			puerto = Integer.valueOf(args[0]);
		
		try (ServerSocket servidor = new ServerSocket(puerto)){
			while (true) { // El servidor seguirá escuchando aunque el cliente se desconecte
				System.out.println("[Servidor] Esperando solicitudes en el puerto " + puerto);
				Socket socket = servidor.accept();
				InputStream is = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				OutputStream out = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(out, true);
				String linea;
				while ((linea = br.readLine()) != null) {
					System.out.println("[Servidor] Recibido del cliente: " + linea);
					writer.println("Esto es lo recibido, pasado a minúsculas: " + linea.toLowerCase());
				}
				
				System.out.println("[Servidor] Cliente desconectado.");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
