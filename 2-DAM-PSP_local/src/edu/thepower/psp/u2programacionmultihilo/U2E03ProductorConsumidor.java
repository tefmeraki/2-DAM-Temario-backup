package edu.thepower.psp.u2programacionmultihilo;

import java.util.LinkedList;
import java.util.Queue;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E03: ejemplo clásico en programación concurrente que ilustra cómo dos o más hilos deben coordinarse para compartir un recurso común (como una cola o buffer),
 * evitando problemas como la sobreescritura o la lectura de datos inexistentes, sin generar condiciones de carrera ni desperdiciar recursos con esperas activas (busy waiting). 
 * 
 */

public class U2E03ProductorConsumidor {
	private static final Queue<Integer> cola = new LinkedList<>();
	private static final int LIMITE = 5;
	
	public static void main (String[] args) {
		Thread productor = new Thread (() -> {
			int valor = 0;
			while (true) {
				synchronized (cola) {
					while (cola.size() == LIMITE) // Uso de while en lugar de if para comprobar, cuando vuelva a activarse el hilo, que hay espacio en la cola
						try {					  // Podría haber otro productor que hubiera vuelto a ocupar el hueco que, a priori, había quedado
							cola.wait();
						} catch (InterruptedException e) {}
					cola.add(valor++);
					System.out.println("Producido: " + (valor-1));
					cola.notify();
				}
			}
		});
		
		Thread consumidor = new Thread (() -> {
			while (true) {
				synchronized (cola) {
					while (cola.isEmpty())
						try {
							cola.wait( 	);
						} catch (InterruptedException e) {}
					int v = cola.remove();
					System.out.println("Consumido " + v);
					cola.notify();
				}
			}
		});
		
		productor.start();
		consumidor.start();
	}
}
