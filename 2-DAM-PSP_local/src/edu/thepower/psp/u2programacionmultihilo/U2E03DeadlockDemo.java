package edu.thepower.psp.u2programacionmultihilo;

import java.util.LinkedList;
import java.util.Queue;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E04: deadlock demo
 * En este ejemplo de código, cada hilo intenta realizar dos bloqueos, pero en orden inverso.
 • Si ambos hilos obtienen su primer bloqueo antes de que el otro libere el segundo, se produce una situación sin posibilidad de desbloqueo (lockdown o "abrazo mortal". 
 * Nos encontramos, por tanto, ante una situación clásica de interbloqueo. 
 * 
 */

public class U2E03DeadlockDemo {
	
	private static Object lock1 = new Object();
	private static Object lock2 = new Object();
	
	public static void main (String[] args) {
		Thread hilo1 = new Thread (() -> {
			synchronized (lock1) {
				System.out.println("Thread 1: objeto lock1 bloqueado.");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				synchronized (lock2) {
					System.out.println("Thread 1: objeto lock2 bloqueado.");
				}
			}
		});
		
		Thread hilo2 = new Thread(() -> {
			synchronized (lock2) {
				System.out.println("Thread 2: objeto lock2 bloqueado.");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (lock1) {
					System.out.println("Thread 2: objeto lock1 bloqueado.");
				}
			}
		});
		
		hilo1.start();
		hilo2.start();
	}
}
