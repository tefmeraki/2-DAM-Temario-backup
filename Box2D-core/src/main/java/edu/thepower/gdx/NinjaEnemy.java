package edu.thepower.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class NinjaEnemy {
	private static float WIDTH = 15;
	private static float HEIGHT = 50;
	private Vector2 posicion;
	private float velocidad;
	private float direccion;
	
	private boolean isAlive;
	
	public NinjaEnemy (Vector2 posicion, float velocidad, float direccion) {
		this.posicion = posicion;
		this.velocidad = velocidad;
		this.direccion = direccion;
		
		isAlive = true;
	}
	
	public void update(float delta) {
		posicion.x += direccion * velocidad * delta;
	}
	
	public void draw (ShapeRenderer shape) {
		if (isAlive) {  // Solo se pinta si el ninja no lo ha matado
			shape.setColor(Color.RED);
			shape.rect(posicion.x, posicion.y, WIDTH, HEIGHT);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle (posicion.x, posicion.y, WIDTH, HEIGHT);
	}
	
	public void matar() {
		isAlive = false;
	}
	
	public boolean isAlive() {
		return isAlive;
	}
}
