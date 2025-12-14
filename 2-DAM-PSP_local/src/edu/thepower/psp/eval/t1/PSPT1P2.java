package edu.thepower.psp.eval.t1;

/*
 * PSP - Trimestre 1, Ejercicio 2
 * 
 * Una empresa desea simular un flujo de trabajo donde dos tareas (TareaA y TareaB) comparten dos recursos: una impresora y un escáner.
 * TaskA primero imprime y luego escanea.
 * TaskB primero escanea y luego imprime.
 * 
 * 1. Ejecuta el código tantas veces como sea necesario:
 * - Observar el comportamiento y resultado obtenido.
 * - Incluye los comentarios que sean necesarios para describir el objetivo de las diferntes líneas de código.
 * - Describe, en los comentarios, qué ocurre al ejecutar el programa, así como la causa.
 * 
 * 2. Desarrolla una nueva clase, denominada "PSPT1P1Corregido", que incluya las modificaciones necesarias para obtener el resultado esperado.
 * - Aporta los comentarios que sean necesarios para explicar los cambios realizados.
 * 
 */

public class PSPT1P2 {
	static class Impresora {
	    public synchronized void imprimir(String doc) {
	        System.out.println(Thread.currentThread().getName() + " imprime: " + doc);
	        try {
	        	Thread.sleep(50);
	        } catch (InterruptedException ignored) {
	        	
	        }
	    }
	}

	static class Scanner {
	    public synchronized void scan(String doc) {
	        System.out.println(Thread.currentThread().getName() + " escanea: " + doc);
	        try {
	        	Thread.sleep(50);
	        } catch (InterruptedException ignored){
	        	
	        }
	    }
	}

	public static void main(String[] args) {
        Impresora impresora = new Impresora();
        Scanner scanner = new Scanner();

        Thread tA = new Thread(() -> {
        	synchronized (impresora) {
	            System.out.println(Thread.currentThread().getName() + " acceso a impresora...");
	            impresora.imprimir("Documento A");
	            synchronized (scanner) {
	                scanner.scan("Documento A");
	            }
	        }
        }, "Tarea-A");
        
        
        Thread tB = new Thread(() -> {
        	synchronized (scanner) {
	            System.out.println(Thread.currentThread().getName() + " acceso a escáner...");
	            scanner.scan("Documento B");
	            synchronized (impresora) {
	                impresora.imprimir("Documento B");
	            }
	        }
        }, "Tarea-B");

        tA.start();
        tB.start();
    }
}
