package edu.thepower.psp.u3comunicacionesenred;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * El servidor UDP recibirá PING y devolveá PONG, simulando el protocolo ICMP.
 * El cliente pordrá determinar la latencia, así como la posible pérdida de paquetes.
 */

public class U3E11PingUDPServer {

	public static void main(String[] args) {
		try (DatagramSocket socket = new DatagramSocket(5000)) {
			byte[] buffer = new byte[1024];
			DatagramPacket entrada = new DatagramPacket(buffer, buffer.length);
			System.out.println("Esperando la recepción de mensajes...");
			
			String respuesta;
			byte[] data = new byte[1024];
			while (true) {
				socket.receive(entrada);
					
				// Devolver PONG
				String msg = new String(entrada.getData(), 0, entrada.getLength());
				InetAddress host = entrada.getAddress();
				int port = entrada.getPort();
				System.out.println("Recibido " + msg + " de " + host + ":" + port);
				respuesta = msg.replace("ping", "pong");
				data = respuesta.getBytes();
				DatagramPacket salida = new DatagramPacket(data, data.length, host, port);
				socket.send(salida);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
