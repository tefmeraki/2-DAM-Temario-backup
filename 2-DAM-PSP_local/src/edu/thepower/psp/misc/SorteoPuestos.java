package edu.thepower.psp.misc;

/*
 * Programa para asignar al azar los puestos de clase. 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class SorteoPuestos {

	public static void main(String[] args) {
		List<String> alumnos = Arrays.asList(
				"Sebas",
				"Mario",
				"Marcos",
				"Sergio G.",
				"Aarón",
				"Guti",
				"Pablo",
				"Johan",
				"Alejandro",
				"David",
				"Sergio M.",
				"Luisa",
				"Esteban",
				"Génesis",
				"Astrid",
				"Claudia"
				);
		List<Integer> puestos = new ArrayList<>();
		Map<Integer, String> resultado = new TreeMap<>();
		
		if (alumnos.size() != 16) {
			System.err.println("La lista no contiene 16 alumnos: " + alumnos.size());
			System.exit(1);
		}
		
		// Lista de puestos
		for (int i = 1;i < 17;i++)
			puestos.add(i);
		
		System.out.println("*** INICIO DEL SORTEO ***");
		Scanner sc = new Scanner(System.in);
		System.out.println("Pulsa una tecla para comenzar.");
		sc.nextLine();
		// Distribución de alumnos y puestos al azar.
		Collections.shuffle(alumnos);
		Collections.shuffle(puestos);
		
		System.out.println("*** ASIGNACIÓN DE PUESTOS ***");
		String alumno;
		int puesto;
		for (int i= 0; i < alumnos.size();i++) {
			alumno = alumnos.get(i);
			puesto = puestos.get(i);
			System.out.println("Alumno: " + alumno);
			System.out.println("*** Pulsa una tecla para continuar.");
			sc.nextLine();
			System.out.println("Puesto: " + puesto);
			System.out.println("A " + alumno + " le ha correspondido el puesto " + puesto);
			resultado.put(puesto, alumno);
		}
		
		System.out.println("*** RESULTAO DEL SORTEO ***");
		resultado.entrySet().forEach(e -> System.out.println("Puesto: " + e.getKey() + " | Alumno: " + e.getValue()));
		
		for (int i = 0;i < 4; i++) {
			for (int j = 0;j < 2;j++) {
				for (int k = 1;k <= 2;k++) {
					System.out.print(resultado.get(4 * i + 2 * j + k));
					System.out.print((k % 2 == 0)?"":" | ");
				}
				System.out.print((j % 2 == 0)?" --- " : "");
			}
			System.out.println();
		}
	}

}
