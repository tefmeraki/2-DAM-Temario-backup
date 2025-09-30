package edu.thepower.psp.u2programacionmultihilo;

import java.util.concurrent.locks.ReentrantLock;

/*
 * Tema 2. Programación multihilo.
 *** Problema condición de carrera: solución (3) utilizando ReentrantLock, que permite un control avanzado y explicito para realizar bloqueos.
 * ReentrantLock ofrece la posibilidad de un mayor y mejor control de bloqueos para escenarios concurrentes más complejos.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E02: crear dos hilos, uno que incrementa una variable compartida estática y otro que la decrementa. Ambos se ejecutan las mismas veces. 
 * El resultado final debería ser 0, pero no es lo que suele ocurrir.
 * 
 * Ejemplo de uso de join() para hacer que el thread principal espere a finalice la ejecución del thread hijo.
 * 
 */

public class U2E02VariableCompartidaCondicionCarrera13ReentrantLock {
	private static ReentrantLock lock = new ReentrantLock();
	private static int contador = 0;
	
	public static void modificarValor (int valor) {
		lock.lock();
		try {
			contador += valor;
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		final int N = 10_000_000;

		Thread t1 = new Thread(() -> {
			for (int i = 0; i < N; i++)
				modificarValor(1);
		});
		
		Thread t2 = new Thread(() -> {
			for (int i = 0; i < N; i++)
				modificarValor(-1);
		});
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
		
		System.out.println("Valor final: " + contador);
	}

}
