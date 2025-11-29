package edu.thepower.psp.u3comunicacionesenred;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U3E09DatagramClient {

	public static void main(String[] args) {
		final int LONGITUD_BUFFER = 1024;
		final int PUERTO_SERVIDOR = 5000;
		
		String texto = "Hola, servidor, ¿cómo estás hoy?";
		byte[] buffer = texto.getBytes();
		
		try (DatagramSocket socket = new DatagramSocket();) {
			InetAddress SERVIDOR = InetAddress.getByName("localhost");
			DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, SERVIDOR, PUERTO_SERVIDOR);
			socket.send(mensaje);
			
			byte[] bufferRespuesta = new byte[1024];
			DatagramPacket respuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length);
			socket.receive(respuesta);
			String recibido = new String (respuesta.getData(), 0, respuesta.getLength());
			System.out.println("Respuesta del servidor: " + recibido);
		} catch (IOException e) {
			System.err.println("Error en la comunicación: " + e.getMessage());
		}
	}
}
