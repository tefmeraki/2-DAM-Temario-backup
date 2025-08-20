package edu.thepower.psp.u2programacionmultihilo;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E02: observar cómo los hilos toman el contador con un valor y, al incementarlo en 1, el nuevo valor del contador no es el que ha mostrado antes + 1.
 * Es necesario bloquear el acceso a la variable con synchronized para evitar que ocurra esto. 
 * 
 * Ejemplo de uso de join() para hacer que el thread principal espere a finalice la ejecución del thread hijo.
 * 
 */

public class U2E02VariableCompartidaCondicionCarrera2 {

	public static int contador = 0;
	
	static class MyThread implements Runnable {

		@Override
		public void run() {
			while (contador < 10) {
				System.out.println(Thread.currentThread().getName() + " - antes: " + contador);
				contador++;
				System.out.println(Thread.currentThread().getName() + " - después: " + contador);
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		Thread[] myThreads = new Thread[5];
		
		for (int i = 0; i < myThreads.length; i++) {
			myThreads[i] = new Thread(new MyThread(), "thread-" + i);
			myThreads[i].start();
		}

		for (int i = 0; i < myThreads.length; i++)
			myThreads[i].join();
	}

}
