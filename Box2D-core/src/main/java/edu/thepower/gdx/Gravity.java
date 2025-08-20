package edu.thepower.gdx;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Gravity extends ApplicationAdapter {

	private final float MAX_WIDTH = 640;
	private final float MAX_HEIGHT = 480;
	
	// private OrthographicCamera camera;
	// private Viewport viewport;
	private ShapeRenderer shape;
	
	private float posX;
	private float posY;
	private float radio;
	
	private float velocidadX;
	private float friccion = 0.98f; // se aplica a velocidadX como consecuencia del rozamiento con paredes o suelo
	private float velocidadY = 0;
	private float gravedad = -500f;
	private float delta;
	
	// Generador de números aleatorios
	Random rand;
	
	boolean dragged = false;
	
	@Override
	public void create() {
		// Creación del render
		shape = new ShapeRenderer();
		
		radio = 15;
		posX = Gdx.graphics.getWidth()/2;
		posY = Gdx.graphics.getHeight() - radio;
				
		rand = new Random();
		// Velocidad X aleatoria
		velocidadH();
	}
	
	@Override
	public void render() {
		// Colocar en cualquier posición de la pantalla
		dragAndDrop();
		
		// Obtener delta
		delta = Gdx.graphics.getDeltaTime();
		
		// Actualizar posición X
		posX += velocidadX * delta;
		
		if (posX >= Gdx.graphics.getWidth() - radio || posX <= radio) {
			velocidadX = -velocidadX;
			velocidadX *= friccion;
		}
		
		// Actualizar posición aplicando gravedad
		velocidadY += gravedad * delta;
		posY += velocidadY * delta;
		
		if (posY <= radio) {
			posY = radio;
			velocidadY *= -0.7f; // Pierde un 30% de la velocidad al chocar con el suelo y rebotar
			velocidadX *= friccion; // Pierde velocidad horizontal al golpear el suelo
		}
		
		
		// Limpiar pantalla
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Ciclo de renderización
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(Color.WHITE);
		shape.circle(posX, posY, radio);
		shape.end();
	}
	
	private void velocidadH() {
		velocidadX = 500f * rand.nextFloat() * (rand.nextBoolean()? 1:-1); // velocidad horizontal entre 0 y 100
	}
	
	private void dragAndDrop() {		
		if (Gdx.input.justTouched()) {
			posX = Gdx.input.getX();
			posY = Gdx.graphics.getHeight() - Gdx.input.getY();
			velocidadH();
		}
	}
}