package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class U3E02EchoServerCliente {

	public static void main(String[] args) {
		int puerto = 0;
		if (args.length == 0)
			System.out.println("Es necesario proporcionar el puerto en el que se recibirán las solicitudes.");
		else
			puerto = Integer.valueOf(args[0]);
		
		try (Scanner scanner = new Scanner(System.in);
			 Socket socket = new Socket("localhost", puerto)) {
			OutputStream out = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(out, true);
			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String linea;
			do {
				linea = scanner.nextLine();
				writer.println(linea);
				System.out.println("[Cliente] Recibido del servidor: " + br.readLine());
			} while (!linea.equals("0"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
