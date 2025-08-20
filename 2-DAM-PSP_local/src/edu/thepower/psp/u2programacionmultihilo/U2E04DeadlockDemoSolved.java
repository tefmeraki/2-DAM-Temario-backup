package edu.thepower.psp.u2programacionmultihilo;

import java.util.LinkedList;
import java.util.Queue;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E04: deadlock demo solucionado.
 * Para evitar el bloqueo (deadlock) en el ejercicio anterior, es necesario que ambos hilos bloqueen los recursos en el mismo orden.
 * Al bloquear los recursos en el mismo orden, ninguno de los 2 hilos va a esperar a algún recurso bloqueado por el otro.
 * 
 */

public class U2E04DeadlockDemoSolved {
	
	private static Object lock1 = new Object();
	private static Object lock2 = new Object();
	
	public static void main (String[] args) {
		
		Runnable noDeadLockThread = new Thread(() -> {
			synchronized (lock1) {
				System.out.println(Thread.currentThread().getName() + " bloquea lock1");
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			synchronized (lock2) {
				System.out.println(Thread.currentThread().getName() + " bloquea lock2");
			}
		});
		
		Thread hilo1 = new Thread(noDeadLockThread, "Hilo 1");
		Thread hilo2 = new Thread(noDeadLockThread, "Hilo 2");
		
		hilo1.start();
		hilo2.start();
	}
}
