package edu.thepower.gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Knife {
	private static Texture knifeTexture;
	
	private Sprite knifeSprite;
	
	private Vector2 posicion;
	private float velocidad;
	private float direccion;
	
	static {
		// carga de la imagen del cuchillo en la RAM de vídeo
		knifeTexture = new Texture ("knife.png");
	}
	
	public Knife (Vector2 posicion, float velocidad, float direccion) {
		this.posicion = posicion;
		this.velocidad = velocidad;
		this.direccion = direccion;
		
		knifeSprite = new Sprite(knifeTexture);
		knifeSprite.setSize(32, 6.4f); // Ajustar con dimensiones relativas, no absolutas
	}
	
	public void update(float delta) {
		posicion.x += direccion * velocidad * delta;
	}
	
	public void draw (SpriteBatch batch) {
		knifeSprite.setPosition(posicion.x, posicion.y);
		knifeSprite.draw(batch);
	}

	public Rectangle getBounds() {
		return new Rectangle (posicion.x, posicion.y, knifeSprite.getWidth(), knifeSprite.getHeight());
	}
}
