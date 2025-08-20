package edu.thepower.gdx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Collisions extends ApplicationAdapter {
	int MIN_RADIO = 5;
	int MAX_RADIO = 30;
	int MAX_VELOCIDAD_X = 10;
	int MAX_VELOCIDAD_Y = 10;
	
	ShapeRenderer shape;
	List<Ball> balls;
	
	@Override
	public void create() {
		shape = new ShapeRenderer();
		balls = new ArrayList<>();
		
		Random rand = new Random();
		
		for (int i = 0; i < 10; i++) {
			int radio = rand.nextInt(MIN_RADIO, MAX_RADIO);
			balls.add(new Ball (rand.nextInt(radio, Gdx.graphics.getWidth()-radio),
								rand.nextInt(radio, Gdx.graphics.getHeight()-radio),
								radio, rand.nextInt(1, MAX_VELOCIDAD_X), // suma 1 para evitar que velocidad horizontal = 0
								rand.nextInt(1, MAX_VELOCIDAD_Y), // suma 1 para evitar que velocidad vertical = 0
								new Color (rand.nextFloat(0, 1), rand.nextFloat(0, 1), rand.nextFloat(0, 1), rand.nextFloat(0, 1))));
		}
	}
	
	@Override
	public void render() {
		shape.begin(ShapeRenderer.ShapeType.Filled);
		ScreenUtils.clear(Color.BLACK);
		for (Ball ball : balls) {
			ball.draw(shape);
			ball.update(balls);
		}
		shape.end();
	}
}