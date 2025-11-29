package edu.thepower.psp.u3comunicacionesenred;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U3E09DatagramServer {

	public static void main(String[] args) {
		final int PUERTO = 5000;
		final int LONGITUD_PAQUETE = 1024;
		
		byte[] buffer = new byte[LONGITUD_PAQUETE];
		
		try (DatagramSocket servidor = new DatagramSocket(PUERTO)) {
			System.out.println("Servidor UDP escuchando en el puerto " + PUERTO);
			while (true) {
				DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
				servidor.receive(paquete);
				System.out.println(">>> Recibido paquete.");
				System.out.println("Tamaño: " + paquete.getLength());
				// Recogemos e contenido de lo recibido en un string, utilizando getData()
				// Hay que tener la precacuión de solo incluir lo que se recibe, que no tiene por qué ocupar 1024 bytes.
				// Así, se comienza en (offset) 0 y se toma la longitud real como límite.
				String mensaje = new String(paquete.getData(), 0, paquete.getLength());
				System.out.println("Contenido: " + mensaje);
				
				// ToDo: parar el proceso con Thread.sleep() para provocar saturación en el servidor y provocar la pérdida de paquetes 
				
				// Enviar respuesta
				String respuesta = "ACK-" + mensaje;
				byte[] datosRespuesta = respuesta.getBytes();
				
				InetAddress origenCliente = paquete.getAddress();
				int puertoCliente = paquete.getPort();
				
				DatagramPacket ack = new DatagramPacket(datosRespuesta, datosRespuesta.length, origenCliente, puertoCliente);
				servidor.send(ack);
			}
			
		} catch (IOException e) {
			System.err.println("Error en la coexión: " + e.getMessage());
		}
	}
}
