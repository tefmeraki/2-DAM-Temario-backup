package edu.thepower.psp.u2programacionmultihilo;

import java.util.concurrent.atomic.AtomicInteger;

/*
* Tema 2. Programación multihilo.
 *** Problema condición de carrera: solución (2) utilizando AtomicInteger que aporta métodos "atómicos" que garantian el acceso thread-safe a variables de tipo int
 * Una operación atómica es una operación que se realiza de una sola vez:
 * - Restar un número de una variable no es una operación atómica.
 * - De igual manera, i++ parece ser atómica, pero no lo es, ya que el valor se lee y luego se escribe (es decir, dos operaciones, no una).
 * - Existen clases como AtomicInteger, AtomicLong, AtomicBoolean, etc. que internamente hacen que estas operaciones sean atómicas y de esta manera resuelven el problema.
 * 
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E02: crear dos hilos, uno que incrementa una variable compartida estática y otro que la decrementa. Ambos se ejecutan las mismas veces. 
 * El resultado final debería ser 0, pero no es lo que suele ocurrir.
 * Para evitarlo, se sincroniza el acceso a un método que es el encargado de modificar el valor de la variable.
 * Se sincroniza el acceso a la sección crítica para garantizar que solo un hilo modifica el contador al mismo tiempo, evitando la condición de carrera. 
 * 
 */

public class U2E02VariableCompartidaCondicionCarrera12AtomicInt {
	private static AtomicInteger contador = new AtomicInteger(0);
	
	public static void modificarValor (int valor) {
			contador.addAndGet(valor);
	}
	
	public static int getContador () {
		return contador.get();
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
		
		System.out.println("Valor final: " + getContador());
	}

}
