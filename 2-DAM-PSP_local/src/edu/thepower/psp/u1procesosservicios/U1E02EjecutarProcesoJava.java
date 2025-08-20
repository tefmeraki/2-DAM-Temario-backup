package edu.thepower.psp.u1procesosservicios;

/*
 * Tema 1. Programación multiproceso.
 * https://oscarmaestre.github.io/servicios/textos/tema1.html
 * U1E02EjecutarProcesoJava - Invocar una clase Java, como un proceso, desde otra.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class U1E02EjecutarProcesoJava {

	public void lanzarSumador (int n1, int n2) {
		String clase = "edu.thepower.psp.u1procesosservicios.U1E02Sumador";
		String classpath = "C:\\Users\\zc00963\\eclipse-workspace\\2-DAM-PSP_local\\bin";
		//String jre = "C:\\Users\\zc00963\\.p2\\pool\\plugins\\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.5.v20241023-1957\\jre\\bin\\java.exe";
		
		try {
			//ProcessBuilder pb = new ProcessBuilder("java", "-version");
			ProcessBuilder pb = new ProcessBuilder("java", "-cp" , classpath, clase, String.valueOf(n1), String.valueOf(n2));
			
			/*
			// 1. Recuperar resultado y errores de la ejecución del proceso
			// Es necesario leer la salida del proceso. En caso contrario no se obtendrá nada en la ejecución. 
			// Leer la salida del proceso, volcar a la consola estándar.
			Process p = pb.start();
			BufferedReader br = new BufferedReader (new InputStreamReader(p.getInputStream()));
			String linea;
			while ((linea = br.readLine()) != null)
				System.out.println(linea);
			
			// Leer errores del proceso, si aparecen. Volcar a la consola de error.
			br = new BufferedReader (new InputStreamReader(p.getErrorStream()));
			while ((linea = br.readLine()) != null)
				System.err.println(linea);
				
			p.waitFor(); // Se espera a que el proceso finalice.
			*/
			
			/*
			// 2. Recuperar rsultado y errores de la ejecución con inheritIO (o redirect)
			pb.inheritIO();
			// *** Alternativamente
			// pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			// pb.redirectError(ProcessBuilder.Redirect.INHERIT);
			pb.start();
			*/
			
			// 3. Recuperar rsultado y errores de la ejecución y redirigir a fichero
			pb.redirectOutput(new File("salida_" + n1 + "-" + n2 + ".txt"));
			pb.redirectError(new File("error_" + n1 + "-" + n2 + ".txt"));
			pb.start();
		} catch (Exception e) {
			System.out.println("Error al ejecutar el sumador: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		U1E02EjecutarProcesoJava u1e02 = new U1E02EjecutarProcesoJava();
		u1e02.lanzarSumador(1, 50);
		u1e02.lanzarSumador(100, 150);
		System.out.println("*** Fin del proceso ***");
	}

}
