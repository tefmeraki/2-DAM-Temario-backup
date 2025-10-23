package edu.thepower.psp.u2programacionmultihilo;

import java.util.LinkedList;
import java.util.Queue;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E05: transferencias bancarias multithread
 * El programa gestiona las transferencias entre dos cuentas bancarias.
 * Utiliza un par de threads/hilos para realizar el traspaso de las cantdades.
 * Garantiza, a us vez, la seguridad de los threads/hilos, evitando bloqueos durante las transferencias.
 * 
 * La comparación de los hashcodes de las cuentas bancarias se utiliza para determinar un orden consistente de adquisición de bloqueos (locks)
 * cuando se sincroniza sobre dos objetos de tipo cuenta en el método de transferencia.
 * El objetivo es evitar un deadlock en escenarios concurrentes donde dos hilos podrían intentar transferir dinero simultáneamente entre el mismo
 * par de cuentas, pero en direcciones opuestas.
 * ¿Por qué es necesario?
 * Un deadlock puede producirse si un hilo bloquea primero la cuenta a y luego la b, y otro hilo bloquea primero la cuenta b y luego la a.
 * Cada uno queda esperando que el otro libere el segundo bloqueo y ambos se quedan bloqueados indefinidamente.
 * Al bloquear siempre primero el objeto con el hashcode más pequeño, todos los hilos que intenten transferir entre el mismo par de cuentas adquirirán
 * los bloqueos en el mismo orden. Esto garantiza que no puedan bloquearse mutuamente para siempre debido a un orden de bloqueo invertido.
 * Regla general:
 * El “orden global” para adquirir bloqueos se establece comparando los hashcodes de los objetos (aquí mediante hashCode()), asegurando que los hilos
 * siempre tomen los bloqueos en la misma secuencia.
 * Este patrón rompe el ciclo necesario para que ocurra un deadlock, haciendo que las transferencias multihilo sean seguras
 * 
 */

public class U2E05TransferenciasMultiThread {

	static class CuentaCorriente {
		private float saldo;
		
		public CuentaCorriente (float saldo) {
			this.saldo = saldo;
		}
		
		public float getSaldo() {
			return saldo;
		}
		
		public void ingresar (float importe) {
			saldo += importe;
		}
		
		public void retirar (float importe) {
			saldo -= importe;
		}
	}

	public static void transferir (CuentaCorriente cc1, CuentaCorriente cc2, float importe) {
		/*
		 * La comparación de los hashcodes de las cuentas bancarias se utiliza para determinar un orden consistente de adquisición de bloqueos (locks)
		 * cuando se sincroniza sobre dos objetos de tipo cuenta en el método de transferencia.
		 * El objetivo es evitar un deadlock en escenarios concurrentes donde dos hilos podrían intentar transferir dinero simultáneamente entre el mismo
		 * par de cuentas, pero en direcciones opuestas.
		 */
		
		CuentaCorriente primera = cc1.hashCode() < cc2.hashCode() ? cc1 : cc2;
		CuentaCorriente segunda = cc1.hashCode() < cc2.hashCode() ? cc2 : cc1;
		synchronized (primera) {
			synchronized (segunda) {
				cc1.retirar(importe);
				cc2.ingresar(importe);
			}
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
			for (int i = 0;i < 100; i++)
				transferir (cc2, cc1, 2);
		});
		
		hilo1.start();
		hilo2.start();
		
		// Es necesario que el thread principal espere a que concluya la ejecución de ambos threads antes de mostrar el saldo resultante
		try {
			hilo1.join();
			hilo2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Saldo cuenta cc1: %,.2f \n", cc1.getSaldo());
		System.out.printf("Saldo cuenta cc2: %,.2f", cc2.getSaldo());
		
	}
}
