package edu.thepower.psp.u1procesosservicios;

/*
 * Tema 1. Programación multiproceso.
 * https://oscarmaestre.github.io/servicios/textos/tema1.html
 * U1E02 - Clase Sumador: devuelve la suma comprendida entre los 2 números enteros que recibe.
 */

public class U1E02Sumador {

	public int sumar (int n1, int n2) {
		int acumulador = 0;
		if (n1 > n2) {
			int auxiliar = n1;
			n1 = n2;
			n2 = auxiliar;
		}
		
		for (int i = n1; i <= n2; i++)
			acumulador += i;
		
		return acumulador;
	}
	
	public static void main(String[] args) {
		U1E02Sumador s = new U1E02Sumador();
		System.out.println("Resultado sumar desde " + args[0] + " hasta " + args[1] + " es " + s.sumar(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
	}

}
