package edu.thepower.psp.u4serviciosenred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class U4E04BasicHTTPServer2 {
	private static AtomicInteger contador = new AtomicInteger();
	private static final String NOMBRE_SERVER = "Web Server 1";
	private static final int PUERTO = 8080;
	private static final String HTML = """
			<HTML>
				<HEAD>
					<TITLE>Servidor web básico</TITLE>
				</HEAD>
				<BODY>
					<H1>Devuelto por el servidor</H1>
					%s
				</BODY>
			</HTML>
			""";
	private static final String HTTP_ESTADO_OK = "200 OK";
	private static final String HTTP_ESTADO_NOT_FOUND = "404 Not Found";
	private static final String HTTP_ESTADO_NOT_ALLOWED = "405 Not Allowed";
	private static final String HTTP_CONTENT = "Content-Type: text/html;charset=UTF-8";
	private static final String HTTP_LENGTH = "Content-Length: ";
	private static final String GET = "GET";
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public static void main(String[] args) {
		try (ServerSocket servidor = new ServerSocket(PUERTO)) {
			System.out.println("Servidor web esperando conexiones en el puerto " + PUERTO);
			while (true) {
				Socket s = servidor.accept();
				Thread t = new Thread (() -> responderHTML(s));
				t.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void responderHTML (Socket socket) {
		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
			System.out.println("Nuevo cliente: " + socket.getInetAddress() + ":" + socket.getPort());
			String linea;
			String[] partes;
			// Leer la primera línea recibida
			linea = input.readLine();
			if (linea != null && !linea.isBlank()) {
				System.out.println("Línea recibida: " + linea);
				// Se divide la solicitud en las partes recibidas, utilizando el espacio en blanco como separador
				partes = linea.split("\\s+");
				String metodo = partes[0];
				String ruta = partes.length > 1?partes[1]:"/";
				
				// Leer el resto de los datos recibidos, enviados por el navegador
				while ((linea = input.readLine()) != null && !linea.isBlank())
					System.out.println("*** " + linea);
				System.out.println("Finalizada lectura de datos recibidos.");
		
				// Se supone que va a ir todo bien y, por tanto, se devuelve un código de estado 200
				String estado = HTTP_ESTADO_OK;
				String respuesta = "";
				if (metodo.equalsIgnoreCase(GET)) {
					
					// No tener en cuenta la solicitud del favicon. Evitar incrementar el valor del contador
					if (!ruta.trim().toLowerCase().contains("favicon")) {
						// Incrementar el valor del contador de visitas.
						contador.incrementAndGet();
					
						respuesta = switch (ruta.toLowerCase()) {
							case "/" ->  "<p>Eres el vistante número " + contador.get() + "<p>";
							case "/hora", "/hora/" -> "<p> Fecha y hora actuales: " + formatter.format(LocalDateTime.now()) +"</p>";
							case "/nombre", "/nombre/" -> "<p>El nombre del servidor es: " + NOMBRE_SERVER + "</p>";
							default -> {
								estado = HTTP_ESTADO_NOT_FOUND;
								yield "<p>ERROR: no es una opción aceptada.</p>";
								}
							};
					}
				} else {
					estado = HTTP_ESTADO_NOT_ALLOWED;
					respuesta = "Solo se admiten solicitudes de tipo GET";
				}
				
				devolverRespuesta(writer, estado, String.format(HTML, respuesta));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void devolverRespuesta (PrintWriter writer, String codigo, String respuesta) {
		// Usando StringBuffer
		/*StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.1 ").append(codigo).append("\n");
		sb.append(HTTP_CONTENT).append("\n");
		sb.append(HTTP_LENGTH + respuesta.getBytes().length).append("\n");
		sb.append("\n");
		sb.append(respuesta);
		System.out.println(">>> Respuesta: " + sb.toString());
		writer.println(sb.toString());
		writer.flush();
		*/
		
		// Usando writer
		writer.println("HTTP/1.1 " + codigo);
		writer.println(HTTP_CONTENT);
		writer.println(HTTP_LENGTH + respuesta.toString().getBytes().length);
		writer.println();
		writer.println(respuesta);
		writer.flush();
	}
}
