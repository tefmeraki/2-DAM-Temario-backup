package edu.thepower.psp.u2programacionmultihilo;

import java.util.concurrent.locks.ReentrantLock;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E05: transferencias bancarias multithread utilizando ReentrantLock en lugar de bloquear los recursos con synchronize
 * >>> Clases del paquete de utilidades para programación concurrente java.util.concurrent
 * >>> Uso de ReentrantLock
 * El programa gestiona las transferencias entre dos cuentas bancarias.
 * Utiliza un par de threads/hilos para realizar el traspaso de las cantdades.
 * Garantiza, a us vez, la seguridad de los threads/hilos, evitando bloqueos durante las transferencias.
 * 
 * En la programación multihilo en Java, ReentrantLock es una clase del paquete java.util.concurrent.locks que proporciona un mecanismo explícito
 * para bloquear y desbloquear secciones críticas, ofreciendo más flexibilidad y ventajas en comparación con la palabra clave tradicional synchronized.
 * Con ReentrantLock, se realiza un bloqueo explçitico usando lock() y se libera con unlock(). Esto da la posibilidad de:
 * - Intentar adquirir el bloqueo sin quedarte esperando indefinidamente (tryLock()).
 * - Adquirir el bloqueo con un tiempo de espera (tryLock(long time, TimeUnit unit)).
 * - Responder a interrupciones mientras se espera (lockInterruptibly()).
 * - Configurar políticas de equidad (fairness policies) para que el bloqueo favorezca al hilo que más tiempo haya estado esperando.
 * - Usar variables de condición (Condition), similares a wait()/notify() con synchronized, pero más potentes y personalizables.
 * 
 */

public class U2E06TransferenciasMultiThreadReentrantLock {

	static class CuentaCorriente {
		private float saldo;
		private ReentrantLock lock;
		
		public CuentaCorriente (float saldo) {
			this.saldo = saldo;
			lock = new ReentrantLock();
		}
		
		public float getSaldo () {
			return saldo;
		}
		
		public ReentrantLock getLock() {
			return lock;
		}
		
		public void ingresar (float importe) {
			saldo += importe;
		}
		
		public void retirar (float importe) {
			saldo -= importe;
		}
	}

	public static void transferir (CuentaCorriente from, CuentaCorriente to, float importe) {
		ReentrantLock lock1 = from.getLock();
		ReentrantLock lock2 = to.getLock();
		
		ReentrantLock primero = from.hashCode() < to.hashCode() ? lock1 : lock2;
		ReentrantLock segundo = from.hashCode() < to.hashCode() ? lock2 : lock1;
		
		primero.lock();
		try {
			segundo.lock();
			try {
				from.retirar(importe);
				to.ingresar(importe);
			} finally {
				segundo.unlock();
			} 
		} finally {
			primero.unlock();
		}
	}
	
	public static void main (String[] args) {
		CuentaCorriente cc1 = new CuentaCorriente(1000);
		CuentaCorriente cc2 = new CuentaCorriente(1000);
		
		Thread hilo1 = new Thread (() -> {
			for (int i = 0; i < 100; i++)
				transferir (cc1, cc2, 1);
		});
		
		Thread hilo2 = new Thread (() -> {
			for (int i = 0; i < 100; i++)
				transferir (cc2, cc1, 2);
		});
		
		hilo1.start();
		hilo2.start();
		
		try {
			hilo1.join();
			hilo2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Saldo final CC1: %,.2f \n", cc1.getSaldo());
		System.out.printf("Saldo final CC2: %,.2f \n", cc2.getSaldo());
	}
}
