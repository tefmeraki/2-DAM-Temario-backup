package edu.thepower.psp.u3comunicacionesenred;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class U3E10BroadcastUDPServer {

	public static void main(String[] args) {
		try (DatagramSocket socket = new DatagramSocket(5000)) {
			byte[] buffer = new byte[1024];
			DatagramPacket entrada = new DatagramPacket(buffer, buffer.length);
			socket.receive(entrada);
			System.out.println("Recibido: " + new String(entrada.getData(), 0, entrada.getLength()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
