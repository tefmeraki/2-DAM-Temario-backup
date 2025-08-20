package edu.thepower.gdx;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

public class Ball {
	private static final Sound BALL_SOUND;
	private static final Sound BALL_HIT;
	
	private Circle circulo;
	private int velocidadX;
	private int velocidadY;
	private Color color;
	
	static {
		BALL_SOUND = Gdx.audio.newSound(Gdx.files.internal("ball.wav"));
		BALL_HIT = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
	}
	
	public Ball(int x, int y, int radio, int velocidadX, int velocidadY, Color color) {
		circulo = new Circle (x, y, radio);
		this.velocidadX = velocidadX;
		this.velocidadY = velocidadY;
		this.color = color;
	}
	
	public void update(List<Ball> balls) {
		circulo.setX(circulo.x + velocidadX);
		circulo.setY(circulo.y + velocidadY);
		
		boolean colision = collides(balls);
		if (circulo.x > Gdx.graphics.getWidth() - circulo.radius || circulo.x < circulo.radius || colision) {
			BALL_SOUND.play();
			velocidadX = -velocidadX;
		}
		
		if (circulo.y > Gdx.graphics.getHeight() - circulo.radius || circulo.y < circulo.radius || colision) {
			BALL_SOUND.play();
			velocidadY = -velocidadY;
		}
	}
	
	public void draw (ShapeRenderer shape) {
		shape.setColor(color);
		shape.circle (circulo.x, circulo.y, circulo.radius);
	}
	
	public boolean collides (List<Ball> balls) {
		boolean collides = false;
		
		for (int i = 0;!collides && i < balls.size();i++)
			if (circulo != balls.get(i).circulo && circulo.overlaps(balls.get(i).circulo)) {
				BALL_HIT.play();
				collides = true;
			}
		
		return collides;
	}
}