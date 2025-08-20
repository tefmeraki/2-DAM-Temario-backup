package edu.thepower.psp.u2programacionmultihilo;

public class U2E00MainThread {

	public static void main(String[] args) {
		System.out.println ("ID main thread: " + Thread.currentThread().threadId()); // ID
		System.out.println ("Nombre main thread: " + Thread.currentThread().getName()); // Nombre
		System.out.println("Prioridad main thread: " + Thread.currentThread().getPriority()); // Prioridad
		System.out.println("Estado main thread: " + Thread.currentThread().getState()); // Estado
		System.out.println("Grupo main thread: " + Thread.currentThread().getThreadGroup().getName()); // Nombre del grupo al que pertenece
		
	}

}
