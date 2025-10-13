package edu.thepower.psp.u2programacionmultihilo;

/*
 * Tema 2. Programación multihilo.
 *** Problema programación concurrente: condición de carrera.
 * En este ejemplo se muestra cómo se puede bloquear el acceso a recursos comunes, evitando condiciones de carrera, utilizando monitores.
 * Los monitores son un mecanismo de sincronización de alto nivel, cada objeto Java puede actuar como uno.
 * La palabra clave synchronized proporciona exclusión mutua (solo un hilo entra a la vez) y la capacidad de los hilos de esperar/notificar dentro de un bloque.
 * 
 * Cada objeto Java tiene su propio monitor intrínseco.
 * Cuando un hilo entra en un bloque o método sincronizado de un objeto, adquiere el monitor de dicho objeto,
 * lo que impide que otros hilos ejecuten simultáneamente código sincronizado en la misma instancia del objeto.
 * Varios threads que acceden a diferentes instancias de una clase pueden adquirir el monitor para su respectivo objeto y ejecutar métodos sincronizados simultáneamente.
 * 
 * Por otra parte, un monitor de clase está asociado a la propia clase, y se obtiene al sincronizar métodos o bloques estáticos.
 * La sincronización en la clase garantiza que solo un thread, de forma simultánea, pueda ejecutar cualquier bloque o método sincronizado estático para esa clase.
 * 
 * El ejemplo siguiente muestra cómo ThreadMas y ThreadMenos no pueden acceder simutáneamente a diferentes métodos sincronizados del mismo objeto.
 * Sin embargo, el ThreadS sí puede acceder de forma simultánea, aunque, en este caso, lo hace a recursos sincronizados de clase (static).
 * No obstante, si otro thread intentará acceder a cualquier otro recurso de la clase, debería esperar a que el ThreadS terminará su tarea.  
 */

class ContadorR {
	private static int TIME_OUT = 10000;
	private int contador;
	private static int contadorStatic = 0;
	
	public ContadorR (int contador) {
		this.contador = contador;
	}
	
	public synchronized void atualizaContador (int cantidad) {
		System.out.println(Thread.currentThread().getName() + ": *** Entrando a actualizar contador...");
		
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + ": *** Terminadas actividades previas a la actualización del contador...");
		contador += cantidad;
	}
	
	public synchronized static void atualizaContadorStatic () {
		System.out.println(Thread.currentThread().getName() + ": >>> Entrando a actualizar contador static...");
		
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + ": >>> Terminadas actividades previas a la actualización del contador static...");
		contadorStatic += 1;
	}
	
	public synchronized int getContador() {
		System.out.println(Thread.currentThread().getName() + ": *** Entrando a obtener valor contador...");
		
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + ": *** Terminadas actividades previas a la obtención del valor del contador...");
		return contador;
	}
	
	public synchronized static int getContadorStatic() {
		System.out.println(Thread.currentThread().getName() + ": >>> Entrando a obtener valor contador static...");
		
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + ": >>> Terminadas actividades previas a la obtención del valor del contador static...");
		return contadorStatic;
	}
}

public class U2E021aCondicionCarreraMonitor {

	public static void main(String[] args) {
		final int REPETICIONES = 2;
		ContadorR c = new ContadorR(10_000);
		
		Thread tMas = new Thread (() -> {
			for (int i = 0; i < REPETICIONES; i++)
				c.atualizaContador(10);
		}, "Th+");
		
		Thread tMenos = new Thread (() -> {
			for (int i = 0; i < REPETICIONES; i++)
				System.out.println(Thread.currentThread().getName() + "*** Valor contador: " +  c.getContador());
		}, "Th-");
		
		Thread tSMas = new Thread (() -> {
			for (int i = 0; i < REPETICIONES; i++)
				ContadorR.atualizaContadorStatic();
		}, "ThS+");
		
		Thread tSGet = new Thread (() -> {
			for (int i = 0; i < REPETICIONES; i++)
				System.out.println(Thread.currentThread().getName() + "*** Valor contador static: " +  + ContadorR.getContadorStatic());
		}, "ThSGet");

		tMas.start();
		tMenos.start();
		tSMas.start();
		tSGet.start();
		
		try {
			tMas.join();
			tMenos.join();
			tSMas.join();
			tSGet.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Valor contador: %,d", c.getContador());
		System.out.printf("Valor contador static: %,d", ContadorR.getContadorStatic());
	}
}
