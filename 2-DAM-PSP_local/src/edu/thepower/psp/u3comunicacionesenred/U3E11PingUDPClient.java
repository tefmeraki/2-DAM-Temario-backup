package edu.thepower.psp.u3comunicacionesenred;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class U3E11PingUDPClient {

	public static void main(String[] args) {
		try (DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName("127.0.0.1");
			int port = 5000;
			String ping = "ping";
			byte[] buffer = new byte[1024];
			buffer = ping.getBytes();
			DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, host, port);
			
			byte[] bufferR = new byte[1024];
			DatagramPacket respuesta = new DatagramPacket(bufferR, bufferR.length);
			
			long tEnvio;
			long tRespuesta;
			for (int i = 1;i < 11; i++) {
				System.out.println("Enviando ping #" + i);
				try {
					tEnvio = System.nanoTime();
					socket.send(mensaje);
					socket.receive(respuesta);
					tRespuesta = System.nanoTime();
					String recibido = new String (respuesta.getData(), 0, respuesta.getLength());
					System.out.println("Recibido: " + recibido + " en " + String.format("%.2f", (tRespuesta - tEnvio)/1_000_000.0) + " ms.");
				} catch (SocketTimeoutException e) {
					System.err.println("SIN RESPUESTA. Posible pérdida de datagrama.");
				}
			}
			
		} catch (IOException e) {
			System.err.println("Error en la comunicación UDP: " + e.getMessage());
		}

	}

}
