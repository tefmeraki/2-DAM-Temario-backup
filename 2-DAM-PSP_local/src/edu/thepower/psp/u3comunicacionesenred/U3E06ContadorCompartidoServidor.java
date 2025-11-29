package edu.thepower.psp.u3comunicacionesenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Aplicación cliente-servidor donde:
 * El servidor mantiene un contador entero compartido entre todos los clientes.
 * Varios clientes pueden conectarse al servidor a la vez (servidor multihilo).
 * Cada cliente puede enviar comandos de texto al servidor:
 * - inc → incrementa el contador en 1 y devuelve el nuevo valor.
 * - dec → decrementa el contador en 1 y devuelve el nuevo valor.
 * - get → devuelve el valor actual del contador sin modificarlo.
 * - bye → el servidor cierra la conexión con ese cliente.
 * Cualquier otro texto será devuelto por el servidor indicando que no es un comando válido.
 * El servidor debe:
 * - Atender a cada cliente en un thread independiente.
 * - Proteger el contador compartido para evitar condiciones de carrera.
 * El cliente:
 * - Lee comandos por teclado.
 * - Ignorar si el comando está en mayúsculas o minúsculas (no es "case sensitive").
 * - Los envía al servidor.
 * - Muestra la respuesta hasta que el usuario escriba BYE.
 */

public class U3E06ContadorCompartidoServidor {
	private static final String INC = "inc";
	private static final String DEC = "dec";
	private static final String GET = "get";
	private static final String BYE = "bye";
	private static final String CONTADOR_ACTUALIZADO = "Contador actualizado. ";
	private static final String CONTADOR_VALOR = "El valor del contador es: ";
	private static final String CIERRE = "¡Hasta pronto!";
	private static final String ERROR_COMANDO = "*** ERROR: el comando enviado no es válido.";
	
	private static AtomicInteger contador = new AtomicInteger();
	
	public static int incrementar() {
		return contador.incrementAndGet();
	}
	
	public static int decrementar() {
		return contador.decrementAndGet();
	}
	
	public static int devolver() {
		return contador.get();
	}
	
	static class ClienteThread implements Runnable {
		private Socket socket;
		
		public ClienteThread (Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
				String comando;
				String respuesta;
				boolean continuar = true;
				while ((comando = input.readLine())!= null && continuar) {
					respuesta = switch (comando.toLowerCase()) {
									case INC -> CONTADOR_ACTUALIZADO + CONTADOR_VALOR + incrementar();
									case DEC -> CONTADOR_ACTUALIZADO + CONTADOR_VALOR + decrementar();
									case GET -> CONTADOR_VALOR + devolver();
									case BYE -> {
										continuar = false;
										yield CIERRE;
									}
									default -> ERROR_COMANDO;
					};
					
					writer.println(respuesta);
				} 
			} catch (IOException e) {
				System.err.println("Error en la lectura/escritura: " + e.getMessage());
			}
			System.out.println("Cliente desconectado del servidor: " + socket.getInetAddress() + ":" + socket.getPort());
		}
	}

	public static void main(String[] args) {
		// Código para verificar que se evita la condición de carrera.
		/*
		List<Thread> threads = new ArrayList<>();
		for (int i = 0;i < 1_000;i++) {
			threads.add(new Thread(() -> {
				for (int j = 0;j < 10_000;j++) {
					if (j % 2 == 0)
						incrementar();
					else
						decrementar();
				}
			}));
			threads.get(i).start();
		}
		
		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println("Valor inicial del contador: " + devolver());
		System.out.println("***");
		*/
		
		try (ServerSocket servidor = new ServerSocket(5000)) {
			System.out.println("Esperando conexiones en el puerto 5.000");
			while (true) {
				Socket socket = servidor.accept();
				System.out.println("Conexión aceptada procedente de " + socket.getInetAddress() + ":" + socket.getPort());
				Thread thread = new Thread (new ClienteThread(socket));
				thread.start();
			}
		} catch (IOException e) {
			System.err.println("Error en el servidor: " + e.getMessage());
		}
	}

}
