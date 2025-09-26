package edu.thepower.psp.u2programacionmultihilo;

import java.util.Random;

/*
 * Tema 2. Programación multihilo.
 * (Apartado 4) https://dev.to/danielrendox/thread-runnable-callable-executorservice-and-future-all-the-ways-to-create-threads-in-java-2o86
 * U2E01: uso de Callable y Future en lugar de Runnable para crear un thread. 
 * Un inconveniente a tener en cuenta al usar Runnable en programación concurrente es que su método run() no puede devolver un valor porque devuelve void.
 * Esto contrasta con la programación estándar (monohilo), donde se puede llamar a un método y obtener su resultado inmediatamente.
 * lo que le permitirá esperar a que el valor esté listo de una manera segura y estructurada.
 * 
 */

public class U2E011CallableFuture1Problema {

	static class Generador implements Runnable{
		// Esta variable contendrá un valor que se asigna en la ejecución del código del thread
		private int numero;
		
		// El método run no devuelve ningún valor. 
		@Override
		public void run() {
			// Con sleep (1000) se simula el tiempo de ejecución del código anterior a la asignación de valor a la variable numero
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("Thread interrumpido mientras dormía.");
				e.printStackTrace();
			}
			
			// Se asigna valor a la variable numero
			numero = new Random().nextInt();
		}
		
		public int getNumero() {
			return numero;
		}
	}
	
	public static void main(String[] args) {
		Generador g = new Generador();
		Thread t = new Thread (g);
		t.start();
		// El varlo de numero es 0 porque todavía no se ha podido asignar uno.
		// Es necesario hacer un join para esperar a que acabe la ejecución del hilo, antes de poder acceder a su valor
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.printf("Valor de número: %,d \n", g.getNumero()); 
	}
}
