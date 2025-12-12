package edu.thepower.psp.u3comunicacionesenred;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U3E10BroadcastUDPClient {

	public static void main(String[] args) {
		try (DatagramSocket socket = new DatagramSocket()) {
			socket.setBroadcast(true);
			InetAddress broadcastIP = InetAddress.getByName("192.168.1.255");
			int port = 5000;
			String mensaje = "Mensaje de broadcast.";
			byte[] buffer = mensaje.getBytes();
			DatagramPacket salida = new DatagramPacket(buffer, buffer.length, broadcastIP, port);
			socket.send(salida);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
