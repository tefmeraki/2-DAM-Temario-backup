package edu.thepower.psp.u2programacionmultihilo;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E02: crear dos hilos, uno que incrementa una variable compartida estática y otro que la decrementa. Ambos se ejecutan las mismas veces. 
 * El resultado final debería ser 0, pero no es lo que suele ocurrir.
 * Para evitarlo, se sincroniza el acceso a un método que es el encargado de modificar el valor de la variable.
 * Se sincroniza el acceso a la sección crítica para garantizar que solo un hilo modifica el contador al mismo tiempo, evitando la condición de carrera. 
 * 
 */

public class U2E02VariableCompartidaCondicionCarrera1Sync {
	private static int contador = 0;
	
	// Aplicación de synchronized al método que se utiliza para modificar el valor de la variable contador
	public static synchronized void modificarValor(int num) {
		contador += num;
	}
	
	public static int getContador() {
		return contador;
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
