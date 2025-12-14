package edu.thepower.psp.eval.t2;

public class PSPT2P2Libro {
	private String titulo;
	private String autor;
	
	public PSPT2P2Libro(String titulo, String autor) {
		super();
		this.titulo = titulo;
		this.autor = autor;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	// ToDo: devolver formato lista HTML
	@Override
	public String toString() {
		return "<li>" + titulo + ", " + autor + "</li>";
	}
}
