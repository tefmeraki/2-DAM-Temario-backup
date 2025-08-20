package edu.thepower.psp.u1procesosservicios;

import java.io.BufferedReader;

/*
* Tema 1. Programación multiproceso.
 * https://oscarmaestre.github.io/servicios/textos/tema1.html
 * U1E03: clase que ejecuta como proceso (varios procesos en paralelo) la clase que cuenta vocales.
 * Crear un programa que sea capaz de contar cuantas vocales hay en un fichero.
 * El programa padre debe lanzar cinco procesos hijo, donde cada uno de ellos se ocupará de contar una vocal concreta (que puede ser minúscula o mayúscula).
 * Cada subproceso que cuenta vocales deberá dejar el resultado en un fichero.
 * El programa padre se ocupará de recuperar los resultados de los ficheros para mostrar el resultado por vocal y el total de vocales.
 */

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class U1E03EjecutarProcesoContarVocales {
	private static final String FICH_IN_VOCALES = "recursos\\vocales.txt";
	private static final String DIR_OUTPUT = "output\\";
	private static final String CLASE = "edu.thepower.psp.u1procesosservicios.U1E03ContarVocales";
	private static final String CLASSPATH = "C:\\Users\\zc00963\\eclipse-workspace\\2-DAM-PSP_local\\bin";
	private static final String FICH_OUT_CONTAR = "contar_";
	private static final String FICH_OUT_ERROR = "error_";
	private static final String EXT_TXT = ".txt";
	
	private Process proceso;
	
	
	private void lanzarProceso(String inputFile, char vocal) {
		// Crear directorio, si no existe, para guardar los archivos de salida.
		File f = new File("output");
		if (f.mkdir())
			System.out.println("Directorio output creado.");
		
		
		try {
			ProcessBuilder pb = new ProcessBuilder("java", "-cp", CLASSPATH, CLASE, inputFile, String.valueOf(vocal));
			pb.redirectOutput(new File(DIR_OUTPUT + FICH_OUT_CONTAR + vocal + EXT_TXT));
			pb.redirectError(new File(DIR_OUTPUT + FICH_OUT_ERROR + vocal + EXT_TXT));
			proceso = pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		char[] vocales = new char[] {'a', 'e', 'i', 'o', 'u'};
		U1E03EjecutarProcesoContarVocales[] procesos = new U1E03EjecutarProcesoContarVocales[vocales.length];
		
		for (int i = 0; i < 5; i++) {
			procesos[i] = new U1E03EjecutarProcesoContarVocales();
			procesos[i].lanzarProceso(FICH_IN_VOCALES, vocales[i]);
		}
		
		//Mostrar el número de veces que aparece cada vocal y el total de vocales
		int total_vocales = 0;
		for (int i = 0; i < vocales.length; i++) {
			try {
				procesos[i].proceso.waitFor(); // Esperamos a que el proceso haya terminado
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try (BufferedReader br = new BufferedReader(new FileReader(DIR_OUTPUT + FICH_OUT_CONTAR + vocales[i] + EXT_TXT))) {
				int num = Integer.parseInt(br.readLine());
				System.out.println(vocales[i] + ": " + num);
				total_vocales += num;			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("*** Total vocales: " + total_vocales);
		
	}

}
