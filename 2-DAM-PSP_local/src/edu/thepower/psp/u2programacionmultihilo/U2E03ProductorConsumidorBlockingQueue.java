package edu.thepower.psp.u2programacionmultihilo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E03: ejemplo clásico en programación concurrente que ilustra cómo dos o más hilos deben coordinarse para compartir un recurso común (como una cola o buffer),
 * evitando problemas como la sobreescritura o la lectura de datos inexistentes, sin generar condiciones de carrera ni desperdiciar recursos con esperas activas (busy waiting).
 * 
 *  Uso de BlockingQueue que gestiona la espera y la notificación automáticamente, evitando la sincronización manual y posibles errores
 * 
 */

public class U2E03ProductorConsumidorBlockingQueue {
	private static final int LIMITE = 5;
	private static final BlockingQueue<Integer> cola = new ArrayBlockingQueue<Integer>(LIMITE);
	
	public static void main (String[] args) {
		Thread productor = new Thread (() -> {
			int valor = 0;
			while (true) {
				try {
					cola.put(valor++);
					System.out.println("Producido: " + (valor-1));
					Thread.sleep(100); // Simulación trabajo
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread consumidor = new Thread (() -> {
			while (true) {
				try {
					int valor = cola.take();
					System.out.println("Consumido " + valor);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		});
		
		productor.start();
		consumidor.start();
	}
}
