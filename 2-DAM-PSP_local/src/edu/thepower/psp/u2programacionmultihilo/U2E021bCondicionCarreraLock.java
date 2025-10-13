package edu.thepower.psp.u2programacionmultihilo;

import java.util.concurrent.locks.ReentrantLock;

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

class ContadorRR {
	private static int TIME_OUT = 10000;
	private int contador;
	private static int contadorStatic = 0;
	
	private ReentrantLock lockAct = new ReentrantLock();
	private ReentrantLock lockGet = new ReentrantLock();
	
	private static ReentrantLock lockSAct = new ReentrantLock();
	private static ReentrantLock lockSGet = new ReentrantLock();
	
	public ContadorRR (int contador) {
		this.contador = contador;
	}
	
	public void atualizaContador (int cantidad) {
		System.out.println(Thread.currentThread().getName() + ": *** Entrando a actualizar contador...");
		
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + ": *** Terminadas actividades previas a la actualización del contador...");
		lockAct.lock(); // Bloqueo lockAct
		try {
			System.out.println(Thread.currentThread().getName() + ": *** Actualizando contador...");
			contador += cantidad;
			System.out.println(Thread.currentThread().getName() + ": *** Fin actualización contador...");
		} finally { // Incluir unlock siempre en un bloque finally para que se produzca el desbloqueo aunque se produzca un error.
			lockAct.unlock(); // Liberación lockAct
		}
	}
	
	public static void atualizaContadorStatic () {
		System.out.println(Thread.currentThread().getName() + ": >>> Entrando a actualizar contador static...");
		
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + ": >>> Terminadas actividades previas a la actualización del contador static...");
		lockSAct.lock(); // Bloqueo lockSAct
		try {
			System.out.println(Thread.currentThread().getName() + ": >>> Actualizando contador static...");
			contadorStatic += 1;
			System.out.println(Thread.currentThread().getName() + ": >>> Fin actualización contador static...");
		} finally {
			lockSAct.unlock(); // Liberación lockAct
		}
	}
	
	public int getContador() {
		System.out.println(Thread.currentThread().getName() + ": *** Entrando a obtener valor contador...");
		
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + ": *** Terminadas actividades previas a la obtención del valor del contador...");
		lockGet.lock(); // Bloqueo lockGet
		try {
			System.out.println(Thread.currentThread().getName() + ": *** Obteniendo valor contador...");
			return contador;
		} finally {
			lockGet.unlock(); // Liberación lockAct
		}
	}
	
	public static int getContadorStatic() {
		System.out.println(Thread.currentThread().getName() + ": >>> Entrando a obtener valor contador static...");
		
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + ": >>> Terminadas actividades previas a la obtención del valor del contador static...");
		lockSGet.lock();
		try {
			System.out.println(Thread.currentThread().getName() + ": >>> Obteniendo valor contador static...");
			return contadorStatic;
		} finally {
			lockSGet.unlock();
		}
	}
}

public class U2E021bCondicionCarreraLock {

	public static void main(String[] args) {
		final int REPETICIONES = 2;
		ContadorRR c = new ContadorRR(10_000);
		
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
				ContadorRR.atualizaContadorStatic();
		}, "ThS+");
		
		Thread tSGet = new Thread (() -> {
			for (int i = 0; i < REPETICIONES; i++)
				System.out.println(Thread.currentThread().getName() + "*** Valor contador static: " +  ContadorRR.getContadorStatic());
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
		System.out.printf("Valor contador static: %,d", ContadorRR.getContadorStatic());
	}
}
