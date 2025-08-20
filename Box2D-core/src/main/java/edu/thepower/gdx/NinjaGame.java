package edu.thepower.gdx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class NinjaGame extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private ShapeRenderer shape; // ***BORRAR** Utilizado para renderizar formas rectangulares que simulan los enemigos
	
	// Viewport y cámara
	private Viewport viewport;
	private OrthographicCamera camara;

	// Dimensiones
	private static final float WORLD_WIDTH = 640;
	private static final float WORLD_HEIGHT = 480;
	
	// Delta time
	private float delta;
	
	// Ninja
	private Ninja ninja;
	
	// Ninja enemies
	private List<NinjaEnemy> enemies;
	private float enemySpawnTimer = 0f; // Temporizador que acumulará el tiempo transcurrido
	private float spawnInterval; // Valor asignado aleatoriamente que determina cuándo se crea un nuevo enemigo
	
	// Plataforma ejemplo
	private List<Rectangle> platforms;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		shape = new ShapeRenderer(); // ***BORRAR** 
		
		// Cámara y viewport
		camara = new OrthographicCamera();
		camara.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camara);
		
		// Centrar la cámara
		camara.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		//viewport.apply();  // en principio, se invoca por defecto en el create()
		
		ninja = new Ninja(new Vector2(Gdx.graphics.getWidth()/2, 0)); //Posición inicial
		
		// new NinjaEnemy (new Vector2(0, 0), 50, 1);
		enemies = new ArrayList<>();
		spawnInterval = MathUtils.random(1, 5); // Genera un valor aleatorio entre 1 y 5 que serán segundos
		
		// Plataformas
		platforms = new ArrayList<>();
		platforms.add(new Rectangle(400, 300, 150, 5));
		platforms.add(new Rectangle(200, 200, 100, 5));
		
		Gdx.input.setInputProcessor(this);
	}
	
	public void resize(int width, int height) {
        viewport.update(width, height); // update() hace el viewport.apply() por defecto.
        camara.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
    } 
	
	@Override
	public void render() {
		// Determinar valor de delta
		delta = Gdx.graphics.getDeltaTime();
		// Borrar la pantalla
		ScreenUtils.clear(Color.WHITE);
		
		creaEnemigo();
		
		// Actualzamos ninja
		ninja.update(viewport, delta, enemies, platforms);
		
		// Actualizando la posición del enemigo
		for (NinjaEnemy enemy : enemies)
			enemy.update(delta);
		
		// Apply viewport and camera updates
		// camara.position.set(ninja.getPosition().x, ninja.getPosition().y, 0);
		// camara.update();
		
        batch.setProjectionMatrix(camara.combined); // utilizar la matriz de la cámara para renderizar
        
		batch.begin();
		//ninja.draw(batch, shape);
		ninja.draw(batch);
		batch.end();
		
		// ***BORRAR** 
		shape.setProjectionMatrix(camara.combined);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		for (NinjaEnemy enemy : enemies)
			enemy.draw(shape);
		
		// Plataforma
		shape.setColor(0.3f, 0.7f, 0.3f, 1);
		for (Rectangle r : platforms)
			shape.rect(r.x, r.y, r.width, r.height); 
		shape.end();
		// ***BORRAR***
	}
	
	private void creaEnemigo() {
		// Crear nuevo enemigo si ha transcurrido el tiempo necesario
		if (enemySpawnTimer >= spawnInterval) {
			// Vector2 posicion = new Vector2(MathUtils.random(0, viewport.getWorldWidth()), MathUtils.random(0, viewport.getWorldHeight()));
			Vector2 posicion = new Vector2(MathUtils.random(0, viewport.getWorldWidth()), 0);
			float velocidad = MathUtils.random(50, 100);
			float direccion = MathUtils.random(-1, 1) < 0?-1:1;
			enemies.add(new NinjaEnemy(posicion, velocidad, direccion));
			
			// Reiniciar temporizador
			enemySpawnTimer = 0f;
			// Obtener nuevo plazo de tiempo para crear un nuevo enemigo
			spawnInterval = MathUtils.random(1, 5);
		}
		
		// Incrementar el contador de tiempo para la creación de enemigos
		enemySpawnTimer += delta;
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		ninja.keyDown(keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		ninja.keyUp(keycode);
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}
