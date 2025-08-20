package edu.thepower.psp.u2programacionmultihilo;

/*
 * U2E07: threads diferentes utlizando el mismo objeto Runnable.
 * 
 * +-------------------+          +-------------------+
 * |   Thread t1       |          |   Thread t2       |
 * |-------------------|          |-------------------|
 * |  Runnable ref --->|----------|                   |
 * | (sharedTask)      |          |                   |
 * +-------------------+          +-------------------+
 *          |                           |
 *          | Executes                  | Executes
 *          V                           V
 *   +-----------------------------------------+
 *   |           Runnable Object                |
 *   |-----------------------------------------|
 *   | private final int value;                 |
 *   | run() { ... }                           |
 *   +-----------------------------------------+
 *
 * Un objeto Runnable no es un Hilo en sí, sino una tarea que debe ejecutar un Hilo.
 * Cada Hilo ejecuta el objeto Runnable que se le pasa.
 * Diferentes hilos pueden compartir el mismo objeto Runnable, compartiendo así sus campos de instancia.
 * Si desea aislar los datos de cada hilo incluso compartiendo el mismo objeto, se debe utilizar objetos del tipo ThreadLocal.
 */

public class U2E07ThreadsSharingRunnableObject {
	
	static class ThreadLocalEjemplo implements Runnable {
		private static final ThreadLocal<Integer> varExclusivaThread = new ThreadLocal<Integer>(); // Variable exclusiva para cada thread, no es compartida
		private int varComun; // Si se usa el mismo objeto Runnable para crear el thread, esta variables será compartida por todos los threads 

		public ThreadLocalEjemplo (int varComun) {
			this.varComun = varComun;
		}
		
		public void setVarComun (int valor) {
			varComun = valor;
		}
		
		@Override
		public void run() {
			varExclusivaThread.set(varComun++);
			for (int i = 0; i < 3; i++) { // Repetimos el bloque de código varias veces para observar cómo la variable exclusiva no cambia y la común se queda con el último valor
				System.out.println(Thread.currentThread().getName() + " - valor var exclusiva: " + varExclusivaThread.get());
				System.out.println(Thread.currentThread().getName() + " - valor var común: " + varComun);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		Thread threads[] = new Thread[5];
		Runnable r = new ThreadLocalEjemplo(0);

		for (int i = 0; i < 5; i++) {
			threads[i] = new Thread(r, "Thread-" + i);
			threads[i].start(); // El valor de varComun se asigna a la variable ThreadLocal, exclusiva del propio thread
		}
	}

}
