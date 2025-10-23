package edu.thepower.psp.u2programacionmultihilo;

import java.util.concurrent.atomic.AtomicInteger;

public class U2E035Inanicion {
	private static AtomicInteger max = new AtomicInteger();
	private static AtomicInteger min = new AtomicInteger();
	private static long tiempoTest = System.currentTimeMillis() + 100;

	public static void main(String[] args) {
		Thread tMax = new Thread (() -> {
			while (System.currentTimeMillis() < tiempoTest) {
                System.out.println("[" + Thread.currentThread().getName() + "] " + "Thread prioridad máxima ejecutándose");
                max.incrementAndGet();
            }
		}, "Thread-MAX");
		tMax.setPriority(Thread.MAX_PRIORITY);
		
		Thread tMax2 = new Thread (() -> {
			while (System.currentTimeMillis() < tiempoTest) {
                System.out.println("[" + Thread.currentThread().getName() + "] " + "Thread prioridad máxima ejecutándose");
                max.incrementAndGet();
            }
		}, "Thread-MAX2");
		tMax2.setPriority(Thread.MAX_PRIORITY);

		Thread tMin = new Thread (() -> {
			while (System.currentTimeMillis() < tiempoTest) {
                System.out.println("[" + Thread.currentThread().getName() + "] " + "Thread prioridad mínima ejecutándose");
                min.incrementAndGet();
            }
		}, "Thread-MIN");
		tMin.setPriority(Thread.MIN_PRIORITY);
		
		tMax.start();
		tMin.start();
		
		try {
			tMax.join();
			tMin.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Veces max: " + max.get());
		System.out.println("Veces min: " + min.get());
	}
}
