package edu.thepower.psp.u2programacionmultihilo;

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

public class U2E09SemaforoEjemplo implements Runnable{
	private static final Semaphore semaforo = new Semaphore (3, true); // El semáforo solo permite el acceso concurrente de 3 thredas. Si true, garantiza igualdad, first in - first out
	private static final AtomicInteger contador = new AtomicInteger(0); // Contador como mecanismos de seguridad para garantizar el funcionamiento del semáforo
	private static final long endMillis = System.currentTimeMillis() + 100;

	public static void main(String[] args) {
		ExecutorService ejecutor = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 5; i++)
			ejecutor.execute(new U2E09SemaforoEjemplo());
		ejecutor.shutdown();
	}

	@Override
	public void run() {
		while (System.currentTimeMillis() < endMillis) {
			try {
				semaforo.acquire();
			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getName() + " interrumpido en operación acquire()");
			}
			System.out.println(Thread.currentThread().getName() + " acquire() OK: " + contador.incrementAndGet());
			
			if (contador.get() > 3) // Mecanismo de seguridad para garantizar que el semáforo hace su trabajo
				throw new IllegalStateException("Más de 3 threads han adquirido el bloqueo.");
			
			contador.decrementAndGet();
			semaforo.release();
		}
		
	}

}
