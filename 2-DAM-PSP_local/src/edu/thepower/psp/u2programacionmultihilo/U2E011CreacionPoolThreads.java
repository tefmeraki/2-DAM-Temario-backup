package edu.thepower.psp.u2programacionmultihilo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
* Tema 2. Programación multihilo.
 * https://dev.to/danielrendox/multithreading-in-java-part-1-process-vs-thread-2glf
 * https://dev.to/danielrendox/thread-runnable-callable-executorservice-and-future-all-the-ways-to-create-threads-in-java-2o86
 * U2E01: creación de pool de threads.
 * En función de la capacidad de la máquina (número de CPUs virtuales) determinamos el número óptimo de hilos que deberian ejecutarse. 
 * Se determina el tamaño del pool del que formarán parte y no será posible tener un número mayor de threads al que existe en el pool.
 * 
 * El método submit() indica al thread qué debe hacerse y lo inicia.
 * Es conveniente apagar el ejecutor para que el programa deje de ejecutarse indefinidamente.
 */

public class U2E011CreacionPoolThreads {
	// Mapa para contar el número de veces que es utilizado cada uno de los threads del pool
	// Alternativa, utilizar Collections.synchronizedMap(new HashMap<>()) para tener un mapa thread-safe map que sincroniza el acceso consurrente.
	private static Map<String, Integer> contadores = new HashMap<String, Integer>();

	public static void main(String[] args) {
		// Se muestra el número de CPUs virtuales disponibles, límite de threads que pueden ejecutarse simultáneamente.
		int procesadores = Runtime.getRuntime().availableProcessors();
		System.out.println("** Número de procesadores disponibles: " + procesadores);
		
		// Creación del servicio que controlará el pool de threads. 
		ExecutorService executorService = Executors.newFixedThreadPool(procesadores);
		 
		for (int i = 0; i < 20; i++)
			executorService.submit(() -> {System.out.println("Saludos desde el thread " + Thread.currentThread().getName());
										  synchronized (contadores) {
											  contadores.put(Thread.currentThread().getName(), contadores.getOrDefault(Thread.currentThread().getName(), 0) + 1);
										  }
										 });

		executorService.submit(() -> {System.out.println("*** Nuevo saludo desde el thread " + Thread.currentThread().getName());});
		
		// Apagar el ExecutorService.
		// La parada se hace de forma ordenada, y no se completa hasta que no han finalizado los threads en ejecución.
		// Lo que no permite el comando shoudown() es que se ejecuten nuevos submit()
		executorService.shutdown();
		
		// Esperar a que los threads finalicen el trabajo
		try {
			while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS))
				System.out.println("Esperando a la finalización de los threads...");
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		    Thread.currentThread().interrupt();
		}
		
		// Mostrar cuántas veces se ha ejecutado cada thread
		contadores.forEach((clave, valor) -> {System.out.println("Thread " + clave + " utilizado " + valor + " veces.");});
	}
}
