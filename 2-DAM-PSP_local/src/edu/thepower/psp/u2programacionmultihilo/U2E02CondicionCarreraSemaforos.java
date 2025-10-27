package edu.thepower.psp.u2programacionmultihilo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E08: semáforos
 * >>> Clases del paquete de utilidades para programación concurrente java.util.concurrent
 * >>> Semáforos
 *
 * Los semáforos en la programación concurrente son herramientas utilizadas para controlar el acceso de múltiples hilos a recursos compartidos limitados,
 * evitando condiciones de carrera y garantizando que no se sobrepase un número máximo de accesos simultáneos.
 * ¿Para qué se usan los semáforos?
 * - Control de concurrencia: Permiten limitar cuántos hilos pueden acceder a una sección crítica o recurso compartido simultáneamente.
 * - Evitar colisiones y garantizar seguridad: Al impedir que demasiados hilos modifiquen o usen al mismo tiempo un recurso, se evita la corrupción de datos.
 * - Gestionar recursos limitados: Por ejemplo, controlar que no más de N conexiones se usen en un pool, o que solo cierto número de procesos trabajen sobre un archivo o puerto.
 * - Coordinar la ejecución de hilos: Forzar que algunos hilos esperen hasta que otros liberen recursos.
 *
 */

public class U2E02CondicionCarreraSemaforos implements Runnable{
	private static final int MAX_SEMAFOROS = 5; // Número máximo de threads que pdrán acceder al semáforo
	private static Semaphore semaforo = new Semaphore (MAX_SEMAFOROS, true); // El semáforo solo permite el acceso concurrente de MAX_SEMAFOROS thredas. Si true, garantiza igualdad, first in - first out
	private static AtomicInteger contador = new AtomicInteger(); // Contador como mecanismos de seguridad para garantizar el funcionamiento del semáforo
	private static long endMillis = System.currentTimeMillis() + 100; // Tiempo que va a estar activa la prueba
	
	private static Map<String, AtomicInteger> mapaUsoThreadsSemaforos = new ConcurrentHashMap<String, AtomicInteger>(); // Mapa para contar uso de semáforos por cada Thread
	private static AtomicInteger usoSemaforos = new AtomicInteger(); // Variable para contar el uso de los semáforos por todos los threads

	public static void main(String[] args) {
		List<Thread> threads = new ArrayList<>();
		// Ejecutamos 10 threads de forma concurrente.
		System.out.println("** INICIO EJECUCIÓN ***");
		for (int i = 0; i < 10; i++) {
			threads.add(new Thread(new U2E02CondicionCarreraSemaforos(), "Thread_" + i));
			threads.get(i).start();
		}
		
		// Esperams a que se complete la ejecución de todos los thredas
		threads.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		System.out.println("** FIN EJECUCIÓN ***");
		
		// Imprimimos estadísticas
		System.out.println(">>> ESTADÍSTICAS DE USO");
		// 1. Uso del semáforo por cada thread
		mapaUsoThreadsSemaforos.forEach((k, v) -> System.out.printf(k + "ha utilizado el semáforo %,d veces.\n", v.get()));
		// 2. Uso total de los semáforos. Si se han controlado bien las condiciones de carrera, el valor de usoSemaforos
		// debería coincidir con la suma de los valores asociados a las claves (nombres de los threads) del mapa que contiene el
		// uso del semáforo por cada thread.
		System.out.printf("Número total de veces que se ha utilizado el semáforo: %,d \n", usoSemaforos.get());
		System.out.printf("Número total de veces que se ha utilizado el semáforo: %,d", 
							mapaUsoThreadsSemaforos.values().stream().mapToInt(v -> v.get()).sum());
	}

	@Override
	public void run() {
		String nombre = "[" + Thread.currentThread().getName() + "] ";
		
		while (System.currentTimeMillis() < endMillis) {
			try {
				semaforo.acquire();
				// Incrementamos el contador global de uso de semáforos por los threads
				// Al ser una variable Atomic, está garantizada la exclusión mutua.
				usoSemaforos.incrementAndGet();
				// Contabilizamos el uso del semáforo por el threda.
				// La operación deber ser atómica, un solo comando, para evitar condiciones de carrera.
				// Si se hace en dos pasos, put + getOrDeafult para determinar el valor, se pierde la atomicidad.
				// computeIfAbsent (ejecución del código proporcionado por la expresión lambda si el get de la clave devuelve null) garantiza la atomicidad
				mapaUsoThreadsSemaforos.computeIfAbsent(nombre, k -> new AtomicInteger()).incrementAndGet();
			} catch (InterruptedException e) {
				System.out.println(nombre+ "Interrumpido en operación acquire()");
			}
			System.out.println(nombre + "Acquire() OK: " + contador.incrementAndGet());
			
			if (contador.get() > MAX_SEMAFOROS) // Mecanismo de seguridad para garantizar que el semáforo hace su trabajo
				throw new IllegalStateException("Más de 3 threads han adquirido el bloqueo.");
			
			contador.decrementAndGet();
			semaforo.release();
		}
		
	}

}
