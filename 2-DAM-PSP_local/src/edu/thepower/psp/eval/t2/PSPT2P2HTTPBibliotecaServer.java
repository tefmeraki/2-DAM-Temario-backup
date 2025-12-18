package edu.thepower.psp.eval.t2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class PSPT2P2HTTPBibliotecaServer {
	
	static class Libro {
		private String titulo;
		private String autor;
		
		public Libro(String titulo, String autor) {
			super();
			this.titulo = titulo;
			this.autor = autor;
		}

		public String getTitulo() {
			return titulo;
		}

		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}

		public String getAutor() {
			return autor;
		}

		public void setAutor(String autor) {
			this.autor = autor;
		}

		// ToDo: devolver formato lista HTML
		@Override
		public String toString() {
			return "<li>" + titulo + ", " + autor + "</li>";
		}
	}

	
	private static final int PUERTO = 8080;
	private static final String HTML_INICIO = """
			<!DOCTYPE html>
			<html lang="es">
			<head>
				<title>Catálogo de libros</title>
			</head>
			<body>
				<h1>Catálogo de libros</h1>
			""";
	private static final String HTML_FIN = """
			</body>
			</html>
			""";
	private static final String HTML_INDICE = """
			<p>Opciones disponibles:</p>
			<ul>
			     <li><a href="/libros">Ver lista completa de libros</a></li>
			     <li><a href="/libros_total">Número total de libros</a></li>
			</ul>
			""";
	
	private static final String HTTP_ESTADO_OK = "200 OK";
	private static final String HTTP_ESTADO_NOT_FOUND = "404 Not Found";
	private static final String HTTP_ESTADO_NOT_ALLOWED = "405 Not Allowed";
	private static final String HTTP_CONTENT = "Content-Type: text/html;charset=UTF-8";
	private static final String HTTP_LENGTH = "Content-Length: ";
	private static final String GET = "GET";
	
	private static final List<Libro> CATALOGO;
	
	static {
		Libro[] libros = {new Libro("El Quijote", "Miguel de Cervantes"),
				new Libro("Cien años de soledad", "Gabriel García Márquez"),
				new Libro("1984", "George Orwell"),
				new Libro("Pantaleón y las visitadoras", "Mario Vargas Llosa"),
				new Libro("Dune", "Frank Herbert")};
		
		CATALOGO = Arrays.asList(libros);
	}
	
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
					respuesta = switch (ruta.toLowerCase()) {
						case "/" ->  HTML_INDICE;
						case "/libros_total", "/libros_total/" -> "<p> Total libros: " + CATALOGO.size() + "</p>";
						case "/libros", "/libros/" -> listarCatalogo();
						/*
						case "/hora", "/hora/" -> "<p> Fecha y hora actuales: " + formatter.format(LocalDateTime.now()) +"</p>";
						case "/nombre", "/nombre/" -> "<p>El nombre del servidor es: " + NOMBRE_SERVER + "</p>";
						*/
						default -> {
							estado = HTTP_ESTADO_NOT_FOUND;
							yield String.format("<p>ERROR: no es una opción aceptada.</p>");
							}
						};
				} else {
					estado = HTTP_ESTADO_NOT_ALLOWED;
					respuesta = String.format("<p>ERROR: solo se admiten solicitudes de tipo GET</p>");
				}
				
				devolverRespuesta(writer, estado, String.format(respuesta));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String listarCatalogo () {
		StringBuffer libros = new StringBuffer();
		CATALOGO.forEach(l -> libros.append("<ul>").append(l).append("</ul>"));
		
		return libros.toString();
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
		String html = HTML_INICIO + respuesta + HTML_FIN;
		writer.println("HTTP/1.1 " + codigo);
		writer.println(HTTP_CONTENT);
		writer.println(HTTP_LENGTH + html.getBytes().length);
		writer.println();
		writer.println (html);
		writer.flush();
	}
}
