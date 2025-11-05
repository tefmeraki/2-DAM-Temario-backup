package edu.thepower.psp.eval;

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

interface Equipo {
	public void realizarTrabajo (String documento);
}

public class PSPT1P2Corregido {
	static class Impresora implements Equipo{
	    @Override
		public String toString() {
			return "Impresora";
		}

		@Override
		public void realizarTrabajo(String documento) {
			System.out.println(Thread.currentThread().getName() + " imprime: " + documento);
	        try {
	        	Thread.sleep(50);
	        } catch (InterruptedException ignored) {
	        	
	        }
		}
	}

	static class Scanner implements Equipo{
		@Override
		public String toString() {
			return "Scanner";
		}

		@Override
		public void realizarTrabajo(String documento) {
			System.out.println(Thread.currentThread().getName() + " escanea: " + documento);
	        try {
	        	Thread.sleep(50);
	        } catch (InterruptedException ignored){
	        	
	        }
		}
	}
	
	public static void procesarTrabajo (Equipo equipo1, Equipo equipo2, String trabajo) {
		Equipo recurso1 = equipo1.hashCode() < equipo2.hashCode() ? equipo1 : equipo2;
		Equipo recurso2 = equipo1.hashCode() < equipo2.hashCode() ? equipo2 : equipo1;
		
		synchronized (recurso1) {
            System.out.println(Thread.currentThread().getName() + " acceso a " + recurso1.toString());
            recurso1.realizarTrabajo(trabajo);
            synchronized (recurso2) {
            	System.out.println(Thread.currentThread().getName() + " acceso a " + recurso2.toString());
                recurso2.realizarTrabajo(trabajo);
            }
        }
	}

	public static void main(String[] args) {
        Impresora impresora = new Impresora();
        Scanner scanner = new Scanner();

        Thread tA = new Thread(() -> {
        	procesarTrabajo(impresora, scanner, "Documento A");
        }, "Tarea_A");
        
        Thread tB = new Thread(() -> {
        	procesarTrabajo(scanner, impresora, "Documento B");
        }, "Tarea_B");
        
        tA.start();
        tB.start();
    }
}
