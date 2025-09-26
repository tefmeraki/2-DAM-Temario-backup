package edu.thepower.psp.u2programacionmultihilo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	public static void main(String[] args) {
		// Se muestra el número de CPUs virtuales disponibles, límite de threads que pueden ejecutarse simultáneamente.
		int procesadores = Runtime.getRuntime().availableProcessors();
		System.out.println("** Número de procesadores disponibles: " + procesadores);
		
		// Creación del servicio que controlará el pool de threads. 
		ExecutorService executorService = Executors.newFixedThreadPool(procesadores);
		 
		for (int i = 0; i < 20; i++)
			executorService.submit(() -> {System.out.println("Saludos desde el thread " + Thread.currentThread().getName());});

		executorService.submit(() -> {System.out.println("*** Nuevo saludo desde el thread " + Thread.currentThread().getName());});
		
		// Apagar el ExecutorService
		executorService.shutdown();
	}

}
