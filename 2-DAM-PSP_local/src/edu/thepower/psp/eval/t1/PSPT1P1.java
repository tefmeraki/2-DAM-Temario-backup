package edu.thepower.psp.eval.t1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/*
 *** Generador de pedidos concurrente
 * Objetivo
 * --------
 * Una empresa desea desarrollar un módulo que permita registrar pedidos de forma concurrente.
 * Este módulo será utilizado por varios hilos que simulan distintos procesos o usuarios generando pedidos de manera simultánea sobre un mismo servicio compartido.
 * Cada pedido deberá tener:
 * - Identificador único (incremental)
 * - Nombre del cliente
 * - Marca temporal asociada al momento de creación del pedido.
 * Además, el sistema debe mantener un registro histórico de todos los pedidos y un contador agregado por cliente, que indique cuántos pedidos ha realizado cada uno.
 * 
 * Tareas a realizar
 * - Generar identificadores únicos de pedido.
 * Cada pedido debe disponer de un identificador numérico secuencial, sin repeticiones.
 * Varios hilos deben poder solicitar nuevos identificadores al mismo tiempo.
 * - Registrar pedidos concurrentes.
 * Cada vez que un hilo genera un pedido, este debe almacenarse en una estructura que actúe como registro general (histórico de pedidos).
 * Este registro debe conservar todos los pedidos creados, incluyendo el cliente, el identificador y la marca temporal.
 * - Actualizar el número de pedidos por cliente.
 * Se debe mantener una estructura adicional que contabilice, para cada cliente, cuántos pedidos ha generado.
 * Al finalizar la ejecución, la suma total de pedidos registrados debe coincidir con el número de pedidos esperados.
 * - Ejecutar múltiples hilos simultáneamente.
 * El programa debe crear varios hilos (por ejemplo, 10), y cada uno debe registrar 10 de pedidos.
 * Todos los hilos trabajarán sobre el mismo servicio compartido.
 * - Comprobar la consistencia de los resultados.
 * Al finalizar, el número total de pedidos registrados, el número de identificadores únicos y la suma de los pedidos por cliente deben ser idénticos.
 * Debe mostrarse un resumen con estos valores y el tiempo de ejecución.
 * 
 * Implementar el código de manera que el programa funcione correctamente bajo condiciones de concurrencia, garantizando la consistencia de los datos.
 * Se deben utilizar estructuras y técnicas de programación concurrente en Java que eviten:
 * - Condiciones de carrera.
 * - Lecturas o escrituras inconsistentes.
 * - Problemas de visibilidad o sincronización entre hilos.
 */

class Pedido {
	private static AtomicInteger idContador = new AtomicInteger();
	private static SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	
	private String id;
	private String cliente;
	private long fecha;
	
	public Pedido (String cliente) {
		this.id = String.valueOf(idContador.incrementAndGet());
		this.cliente = cliente;
		this.fecha = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return "*** Pedido: " + id + "\nCliente: " + cliente + "\nFecha: " + formato.format(fecha); 
	}
}

public class PSPT1P1 implements Runnable{
	private final static int NUM_THREADS = 10; 
	private final static int NUM_PEDIDOS = 10;
	private final static String CLIENTE = "Cliente-";
	private final static Random generaIDCliente = new Random ();
	
		private static ReentrantLock lock = new ReentrantLock();
	private static List<Pedido> pedidos = new ArrayList<>();
	private static Map<String, AtomicInteger> pedidosCliente = new ConcurrentHashMap<>();

	@Override
	public void run() {
		for (int i=0;i < NUM_PEDIDOS;i++) {
			String cliente = CLIENTE + generaIDCliente.nextInt(0, 10);
			
			lock.lock();
			try {
				pedidos.add(new Pedido(cliente));
			} finally {
				lock.unlock();
			}
			pedidosCliente.computeIfAbsent(cliente,  (k -> new AtomicInteger())).incrementAndGet();
		}
	}
	
	public static void main (String[] args) {
		List<Thread> threads = new ArrayList<>();
		
		System.out.println("*** Inicio generación de pedidos...");
		
		for (int i = 0;i < NUM_THREADS;i++) {
			threads.add(new Thread(new PSPT1P1()));
			threads.get(i).start();
		}
		
		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		System.out.println("*** Fin generación de pedidos...");
		
		System.out.println(">>> Listado pedidos");
		for (Pedido p : pedidos)
			System.out.println(p);
		System.out.println("Número total de pedidos: " + pedidos.size());
		
		System.out.println(">>> Estadísticas por cliente.");
		int acumulador = 0;
		for (int i = 0;i < 10;i++) {
			String cliente = CLIENTE + i;
			Integer pedidos = pedidosCliente.get(cliente).get();
			acumulador += pedidos;
			System.out.println("El cliente " + cliente + " ha realizado " + (pedidos==null ? 0 : pedidos));
		}
		System.out.println("Número total de pedidos: " + acumulador);
		// Alternativa al acumulador utilizando streams
		//System.out.println("Número total de pedidos: " + pedidosCliente.values().stream().mapToInt(i -> i.get()).sum());
	}
}
