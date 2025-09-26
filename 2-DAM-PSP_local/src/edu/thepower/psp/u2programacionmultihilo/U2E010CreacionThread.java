package edu.thepower.psp.u2programacionmultihilo;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E01: creación de threads
 * - Implementación de interfaz Runnable
 * - Exensión clase padre Thread
 * - Implementación de interfaz funcional Runnable a través de una expresión lambda
 * 
 */

public class U2E010CreacionThread {
	
	// Como una clase que implementa la interfaz Runnable
	static class MiThread1 implements Runnable {
		@Override
		public void run() {
			System.out.println("Saludos desde el thread 1: " + Thread.currentThread().threadId() + " - " + Thread.currentThread().getName());			
		}
	}
	
	// Como una clase que extiende de la clase padre Thread
	static class MiThread2 extends Thread {
		public MiThread2 (String name) {
			super(name);
		}
		
		@Override
		public void run() {
			System.out.println("Saludos desde el thread 2: " + Thread.currentThread().threadId() + " - " + Thread.currentThread().getName());
		}
	}
	
	public static void main(String[] args) {
		Thread hilo1 = new Thread(new MiThread1() ,"Runnable");
		hilo1.start();
		
		MiThread2 hilo2 = new MiThread2("Extends");
		hilo2.start();
		
		// Como una expresión lambda que implementa la interfaz funcional Runnable
		Thread hilo3 = new Thread(() -> {
			System.out.println("Saludos desde el thread 3: " +  Thread.currentThread().threadId() + " - " + Thread.currentThread().getName());
		}, "Lambda Expr.");
		hilo3.start();
		
		System.out.println("Saludos desde el main.");
	}

}
