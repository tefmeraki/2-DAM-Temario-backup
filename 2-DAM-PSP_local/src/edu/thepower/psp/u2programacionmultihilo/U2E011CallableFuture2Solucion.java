package edu.thepower.psp.u2programacionmultihilo;

/*
 * Tema 2. Programación multihilo.
 * (Apartado 4) https://dev.to/danielrendox/thread-runnable-callable-executorservice-and-future-all-the-ways-to-create-threads-in-java-2o86
 * U2E01: uso de Callable y Future en lugar de Runnable para crear un thread. 
 * Un inconveniente a tener en cuenta al usar Runnable en programación concurrente es que su método run() no puede devolver un valor porque devuelve void.
 * Esto contrasta con la programación estándar (monohilo), donde se puede llamar a un método y obtener su resultado inmediatamente.
 * 
 * Por tanto, en programas de un solo hilo (estándar), si se asigna un valor a una variable, se sabe inmediatamente después de la asignación que la variable contiene el nuevo valor.
 * En código multihilo que usa Runnable, a menudo hay un hilo que asigna/produce un valor y otro que potencialmente lee o usa ese valor.
 * Como run() no devuelve un valor, no se puede obtener directamente el resultado del cálculo de un hilo.
 * Además, el hilo que produce el valor podría finalizar más tarde, por lo que el hilo "lector" no sabe cuándo está listo el valor.
 * Intentar leerlo inmediatamente puede generar datos obsoletos o sin configurar (condición de carrera).
 * Se podría crear una clase de datos compartida y hacer que un Runnable la modifique, pero implica manejar la sincronización y detectar cuándo el valor está disponible,
 * lo que a menudo complica el programa y genera riesgo de errores.
 * 
 * Uso de Callable y Future
 * ************************
 * Java proporciona la interfaz Callable, que es similar a Runnable pero su método call() devuelve un valor y puede generar excepciones controladas.
 * Se puede hacer el submit() del objeto Callable a un ExecutorService y obtener un obeto de tipo Future que represente el resultado,
 * lo que le permitirá esperar a que el valor esté listo de una manera segura y estructurada.
 */

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class U2E011CallableFuture2Solucion {

	public static void main(String[] args) {
		// Creamos un servicio con un solo thread
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		
		Future<Integer> future = executorService.submit(() -> {
			// Es opcional controlar InterruptedException. El método call() que se implementa, de la Interfaz Callable, ya lanza Exception
			// Incluimos bloque try/catch para dejarlo más completo
			
			// Hacemos que el thread duerma durante 1 s para provocar la excepción cuando se trata de recuperar el valor 
			// que se debería devolver
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
			return new Random().nextInt();
		});

		try {
			System.out.printf("Número aleatorio generado: %,d \n", future.get(1, TimeUnit.SECONDS));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.err.println("No se pudo copmletar la tarea antes de que expirara el plazo.");
		}
	}
}
