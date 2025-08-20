package edu.thepower.psp.u2programacionmultihilo;

/*
* Tema 2. Programación multihilo.
 * https://oscarmaestre.github.io/servicios/textos/tema2.html
 * U2E02: verificar que las variables de instancia no son compartidas y, por tanto, no requieren acceso sincronizado.
 * 
 */

class Hilo implements Runnable {
	private int num;

	public Hilo (int num) {
		this.num = num;
	}
	
	@Override
	public void run() {
		System.out.println("Hilo número " + num);
		
	}
	
}

public class U2E02VariableNoCompartida {
	
	public static void main(String[] args) {
		
		for (int i = 0; i < 10; i++)
			new Thread(new Hilo(i)).run();
	}

}
