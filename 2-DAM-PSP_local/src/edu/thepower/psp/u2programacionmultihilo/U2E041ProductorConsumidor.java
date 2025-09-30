package edu.thepower.psp.u2programacionmultihilo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class U2E041ProductorConsumidor {
	private static Queue<Integer> cola = new LinkedList<>();
	private static final int LIMITE = 5;

	public static void main(String[] args) {
		Thread productor = new Thread (() -> {
			int valor = 0;
			while (true) {
				if (cola.size() < LIMITE) {
					valor++;
					System.out.println("Valor producido: " + valor);
					cola.add(valor);
				}
				else {
					try {
						Thread.sleep(100); // Pausa arbitraria para reducir consumo de recursos del sistema
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		Thread consumidor = new Thread (() -> {
			while (true) {
				if (!cola.isEmpty())
					System.out.println("Valor consumido: " + cola.poll());
				else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		productor.start();
		consumidor.start();
		
	}

}
