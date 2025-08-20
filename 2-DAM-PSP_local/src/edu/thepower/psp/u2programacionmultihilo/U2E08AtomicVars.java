package edu.thepower.psp.u2programacionmultihilo;

import java.util.concurrent.atomic.AtomicInteger;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E08: variables atómicas
 * >>> Clases del paquete de utilidades para programación concurrente java.util.concurrent
 * >>> Uso de Atomic Vars (AtomicInteger, AtomicLong, AtomicBoolean, por ejemplo)
 * ¿Qué son las variables atómicas?
 * Las variables atómicas en Java son tipos de datos especiales (por ejemplo, AtomicInteger, AtomicLong, AtomicBoolean, etc.) que permiten realizar operaciones sobre valores compartidos entre hilos de forma atómica (esto es, indivisible y sin interferencias entre hilos), sin necesidad de utilizar bloqueos (synchronized, Lock) explícitos.
 * Se encuentran en el paquete java.util.concurrent.atomic.
 * Fundamento: ¿En qué se basan?
 * Las operaciones atómicas se basan en instrucciones de bajo nivel proporcionadas por el hardware del procesador (por ejemplo, el comando CAS — Compare-And-Swap) que garantizan que un cambio en el valor solo ocurre si no ha sido modificado por otro hilo simultáneamente.
 * No usan bloqueos exclusivos (locks), sino mecanismos de concurrencia sin bloqueo (lock-free), lo que hace que sean muy rápidas y evitan problemas como deadlocks.
 * Internamente, cuando algo como atomic.incrementAndGet() se ejecuta, el JVM usa instrucciones del procesador para garantizar que la operación sea segura aunque varios hilos intenten modificar el valor al mismo tiempo.

 * ¿Para qué sirven?
 * Las variables atómicas sirven principalmente para:
 * Compartir datos simples (contadores, banderas, referencias a objetos) entre varios hilos sin tener que usar sincronización tradicional.
 * Mejorar el rendimiento en escenarios donde se necesita alta concurrencia y operaciones simples como suma/resta/comparación.
 * Construir estructuras más complejas o algoritmos concurrentes sin bloqueos.

 * Ejemplos de usos típicos:
 * Contadores de accesos, de tareas completadas.
 * Señales booleanas (flags) para indicar estado entre threads.
 * Referencias seguras a objetos compartidos.
*/

public class U2E08AtomicVars {
	private static AtomicInteger contador = new AtomicInteger(0);
	private static AtomicInteger pares = new AtomicInteger(0);
	private static AtomicInteger impares = new AtomicInteger(0);
	
	public static void main(String[] args) throws InterruptedException {
		Thread[] threads = new Thread[10];
		
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread (() -> {
				if (Thread.currentThread().threadId() % 2 == 0)
					pares.getAndIncrement();
				else
					impares.getAndIncrement();
				
				for (int j = 0; j < 1000; j++) {
					if (Thread.currentThread().threadId() % 2 == 0)
						contador.getAndIncrement();
					else
						contador.getAndDecrement(); 
				}
			});
			
			threads[i].start();
		}
		
		for (int i = 0; i < threads.length; i++)
			threads[i].join();
		
		System.out.println("Threads pares: " + pares);
		System.out.println("Threads impares: " + impares);
		System.out.println("Número esperado: " + (pares.get() * 1000 - impares.get() * 1000));
		System.out.println("Número obtenido: " + contador.get()); 

	}

}
