package edu.thepower.psp.u1procesosservicios;

/*
 * Tema 1. Programación multiproceso.
 * https://oscarmaestre.github.io/servicios/textos/tema1.html
 * U1E01 - Ejecutar un proceso externo desde un programa Java
 */

import java.io.IOException;

public class U1E01EjecutarProcesoExterno {
	
	public void ejecutar(String aplicacion) {
		ProcessBuilder pb = new ProcessBuilder(aplicacion);
		try {
			pb.start();
		} catch (IOException e) {
			System.out.println("Error en la ejecución: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		U1E01EjecutarProcesoExterno u1e01 = new U1E01EjecutarProcesoExterno();
		u1e01.ejecutar("notepad"); // Ejecutar notepad. No es necesario indicar el path.
		u1e01.ejecutar("C:\\Program Files\\Adobe\\Acrobat DC\\Acrobat\\Acrobat.exe"); // Con ruta completa
	}

}
