package edu.thepower.psp.u4serviciosenred;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class U4E02AutohealingServer {
	private static final int PORT = 5000;
	
	public static void main(String[] args) {
		while (true) {
			try {
				arrancaServidor();
			} catch (Exception e) {
				System.out.println("Servidor caído: " + e.getMessage());
				System.out.println("Reiniciando en 2 segundos...");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	private static void arrancaServidor() {
		try (ServerSocket servidor = new ServerSocket(PORT)) {
			// Thread que provocará una excepción cada 10 segundos
			Thread t = new Thread (() -> {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					servidor.close();
				} catch (IOException e) {
					System.err.println("Error al cerrar el socket servidor.");
				}
			});
			t.start();
			
			System.out.println("Servidor escuchando en puerto " + PORT);
			while (true) {
				Socket socket = servidor.accept();
				System.out.println("Nuevo cliente - " + socket.getInetAddress() + ":" + socket.getPort());
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
