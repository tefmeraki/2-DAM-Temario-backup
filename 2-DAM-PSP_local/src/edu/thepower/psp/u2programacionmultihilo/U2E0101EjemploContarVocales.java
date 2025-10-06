package edu.thepower.psp.u2programacionmultihilo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Contador implements Runnable {

	private static final Map<Character, Character> VOCALES;
	
	private char vocal;
	private String archivo;
	private String outDir;
	
	static {
		VOCALES = Map.of ('a', 'á',
						  'e', 'é',
						  'i', 'í',
						  'o', 'ó',
						  'u', 'ú');
	}
	
	public Contador (char vocal, String archivo, String outDir) {
		this.vocal = vocal;
		this.archivo = archivo;
		this.outDir = outDir;
	}
	
	@Override
	public void run() {
		int contador = 0;
		String linea;
		
		try (BufferedReader in = new BufferedReader(new FileReader(archivo));
			 BufferedWriter out = new BufferedWriter(new FileWriter("./" + outDir + "/" + vocal + ".txt"))){
			while ((linea = in.readLine()) != null) {
				linea = linea.toLowerCase();
				for (int i = 0;i < linea.length();i++)
					if (linea.charAt(i) == vocal || linea.charAt(i) == VOCALES.get(vocal))
						contador++;
			}
			
			out.write(Integer.toString(contador));
			
			// Provocamos una parada del thread, simulando actividad adicional dle thread, para que sea necesario esperar al join
			// Thread.sleep(5000);
		} catch (IOException e) {
			e.printStackTrace();
		} //catch (InterruptedException e) {
		  //	e.printStackTrace();
		  //    }
		
	}
	
}

public class U2E0101EjemploContarVocales extends Thread {
	
	public static void main(String[] args) {
		final String ARCHIVO = "./recursos/texto.txt";
		final String OUT_DIR = "salida";
		final char[] VOCALES = new char[] {'a', 'e', 'i', 'o', 'u'};
		final List<Thread> hilos = new ArrayList<>();
		
		File dir = new File(OUT_DIR);
		// Eliminar directorio salida y contenido, si existe.
		// En caso contrario, crear directorio de salida
		if (dir.exists()) {
			System.out.println("Directorio "+ OUT_DIR + " ya existe.");
			System.out.println("Borrando directorio y contenido.");
			// Borrar archivos
			for (File f : dir.listFiles())
				f.delete();
			// Borrar directorio
			dir.delete();
		}
		
		// Crear directorio de salida.
		if (dir.mkdir())
			System.out.println("Directorio de salida creado");
		
		for (int i = 0;i < VOCALES.length;i++) {
			System.out.println("Nuevo thread contando " + VOCALES[i] + "...");
			hilos.add(new Thread (new Contador(VOCALES[i], ARCHIVO, OUT_DIR)));
			hilos.get(i).start();
		}
		
		System.out.println("*** Fin creación hilos contadores.");

		// Esperando a que los hilos acaben de procesar el archivo de entrada
		for (Thread t : hilos)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println("*** Fin procesos contar.");
		
		// Indicar número de vocales por vocal y total vocales
		int totalVocales = 0;
		for (char vocal : VOCALES) {
			try (BufferedReader in = new BufferedReader(new FileReader("./" + OUT_DIR + "/" + vocal + ".txt"))){
				int contador = Integer.parseInt(in.readLine());
				System.out.println("Número de "+ vocal + ": " + contador);
				totalVocales += contador;
			} catch (IOException | NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(">>> Total vocales: " + totalVocales);
	}
}
