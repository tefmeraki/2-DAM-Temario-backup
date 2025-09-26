package edu.thepower.psp.u2programacionmultihilo;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E01: aplicación de sleep() a un thread para detener momentáneamente su ejecución.
 * Uso de interrupt() para interrumpir un thread que "duerme"
 * 
 */

public class U2E012SleepingInterrupting {
	
	static class MiThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				System.out.println("Thread " + Thread.currentThread().getName() + " interrumpido por excepción.");
			}
			
			while (!Thread.interrupted()) {
				// No hacer nada mientras no sea interrumpido
			}
			System.out.println("Thread " + Thread.currentThread().getName() + " interrumpido por excepción, segunda vez.");
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		// El thread que se crea duerme durante 1 segundo antes de volver a ejecutarse (indefinidamente).
		Thread hilo1 = new Thread(() -> {
			while (true) {
				for (int i = 0; i < 100; i++)
					System.out.println(i + "- ¡Saludos!");
				try {
					Thread.sleep(1000); // El tiempo que duerme no está en una unidad concreta
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		// hilo1.start();  // *** Comentar para poder ejecutar la segunda parte sleep - interrupt
		
		Thread hilo2 = new Thread (new MiThread(), "Runnable");
		hilo2.start();
		
		System.out.println(Thread.currentThread().getName() + " duerme durante 5 s");
		Thread.sleep(5000);
		
		System.out.println(Thread.currentThread().getName() + " interrumpiendo a hilo2");
		hilo2.interrupt();
		
		System.out.println(Thread.currentThread().getName() + " duerme durante 5 s");
		Thread.sleep(5000);
		
		System.out.println(Thread.currentThread().getName() + " interrumpiendo a hilo2");
		hilo2.interrupt();
	}

}
