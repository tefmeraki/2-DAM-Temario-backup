package edu.thepower.gdx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Breakout extends ApplicationAdapter {
	private static final float WORLD_WIDTH = 8;
	private static final float WORLD_HEIGHT = 6;
	
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private Rectangle paddle;
	private float paddleSpeed = 5f;
	private float ballRadius = 0.1f;
	private Vector2 ballPos;
	private Vector2 ballVelocity;
	
	private List<Rectangle> bricks;
	private int brickRows = 5;
	private int brickCols = 10;
	private float brickWidth = 0.6f;
	private float brickHeight = 0.25f;
	private float brickPadding = 0.05f;
	
	@Override
	public void create() {
		// 1. Creación cámara y viewport
		camera = new OrthographicCamera();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		viewport.apply();
		
		// 2. Posicionar la cámara
		camera.position.set(WORLD_WIDTH/2f, WORLD_HEIGHT/2f, 0);
		camera.update();
		
		// 3. Creación del renderer
		shapeRenderer = new ShapeRenderer();
		
		// 4. Creación de los elementos del juego
		paddle = new Rectangle(WORLD_WIDTH/2f - 1, 0.2f, 2f, 0.2f); // x, y, ancho, alto
		ballPos = new Vector2(WORLD_WIDTH/2f, 1);
		ballVelocity = new Vector2(2.5f, 2.5f);
		bricks = new ArrayList<>();
		
		crearBricks();		
	}
	
	@Override
	public void render() {
		// 1. Obtener delta time
		float dt = Gdx.graphics.getDeltaTime(); // Delta time, permite mantener el movimiento independiente del frame rate
		update(dt);
		
		// 2. Limpiar la pantalla
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // 3. Actualizar cámara
        camera.update(); // Actualizar la cámara
        shapeRenderer.setProjectionMatrix(camera.combined); // Renderizar de acuerdo a la situación de la cámara
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Las formas que se pinten estarán rellenas
        // 4. Dibujar la pala
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(paddle.x, paddle.y, paddle.getWidth(), paddle.getHeight());
        
        // 5. Dibujar la pelota
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(ballPos.x, ballPos.y, ballRadius, 32);
        
        // 6. Dibujar los ladrillos
        shapeRenderer.setColor(Color.WHITE);
        for (Rectangle brick : bricks)
        		shapeRenderer.rect(brick.x, brick.y, brick.getWidth(), brick.getHeight());
        
        shapeRenderer.end();
	}
	
	private void update (float dt) {
		// Mover la pala
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			paddle.x -= paddleSpeed * dt;
		else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
				paddle.x += paddleSpeed * dt;
		
		if (paddle.x <= 0)
			paddle.x = 0;
		else if (paddle.x >= WORLD_WIDTH - paddle.width)
				paddle.x = WORLD_WIDTH - paddle.width;
		
		// De otra forma
		// paddle.x = Math.max (0, Math.min(WORLD_WIDTH - paddle.width, paddle.x));
		
		// Movimiento de la pelota
		ballPos.x += ballVelocity.x * dt;
		ballPos.y += ballVelocity.y * dt;
		
		if (ballPos.x <= ballRadius || ballPos.x >= WORLD_WIDTH - ballRadius)
			ballVelocity.x = -ballVelocity.x;
		
		if (ballPos.y >= WORLD_HEIGHT - ballRadius)
			ballVelocity.y = -ballVelocity.y;
		
		if (ballPos.y <= ballRadius)
			resetBallAndPaddle();
		
		// Colisión de la pelota y el paddle
		// Se crea un rectángulo que representa a la pelota
		Rectangle ballRect = new Rectangle (ballPos.x - ballRadius, ballPos.y - ballRadius, ballRadius * 2, ballRadius * 2);
		if (ballRect.overlaps(paddle) && ballVelocity.y < 0) {
            ballPos.y = paddle.y + paddle.height + ballRadius;
            ballVelocity.y = Math.abs(ballVelocity.y);
            float impactPoint = (ballPos.x - paddle.x) / paddle.width - 0.5f;
            ballVelocity.x = impactPoint * 5f;
            System.out.println("*** Nueva velocidad x: " + ballVelocity.x);
        }
		
		// Colisión con los ladrillos
		Iterator<Rectangle> iter = bricks.iterator();
		boolean hit = false;
		while (iter.hasNext() && !hit) {
			Rectangle brick = iter.next();
			if (ballRect.overlaps(brick)) {
				iter.remove();
				ballVelocity.y = -ballVelocity.y;
				hit = true;
			}
		}
	}
	
	private void resetBallAndPaddle() {
        paddle.x = WORLD_WIDTH / 2 - paddle.width / 2;
        ballPos.set(WORLD_WIDTH / 2, 1f);
        ballVelocity.set(2.5f, 2.5f);
    }
	
	private void crearBricks() {
		bricks.clear();
		float offsetx = (WORLD_WIDTH - (brickCols * (brickWidth + brickPadding) - brickPadding)) / 2f;  
				
		for (int fila = 1; fila <= brickRows; fila++) // El índice comienza en 1 para que haya separación con el borde superior de la pantalla en la primera fila
			for (int col = 0; col < brickCols; col++) {
				float x = offsetx + (brickWidth + brickPadding) * col;
				float y = WORLD_HEIGHT - (brickHeight + brickPadding) * fila - 0.3f;
				bricks.add(new Rectangle(x, y, brickWidth, brickHeight));
			}
	}
	
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true para centrar la cámara
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

}
