package edu.thepower.psp.u3comunicacionesenred;

/*
 * Tema 3. Programación de comunicaciones en red.
 * https://oscarmaestre.github.io/servicios/textos/tema3.html
 * U3E00: ejmplo de uso de la clase URL para conectarse y descargar el contenido de una página web.
 * - Si se utiliza en clase, hacerlo a modo ilustrativo como ejemplo.
 * - El tema debe enfocarse en el uso de las clases Socket y ServerSocket
 */

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class U3E00UsoURLEjemplo {
	
	public static void main (String...args) {
		String url = "https://www.w3schools.com/java/";
		
		HttpClient cliente = HttpClient.newHttpClient();
		HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(url)).build();
		
		try {
            HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200)
            	System.out.println(respuesta.body());
            else
            	System.err.println("Error en la descarga. Código de error: " + respuesta.statusCode());
			
		} catch (IOException | InterruptedException e) {
			System.err.println("Error en la descarga: " + e.getMessage());
		}
	}

}
