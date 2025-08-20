package edu.thepower.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportSpriteSample extends ApplicationAdapter {
	private Viewport viewport;
		
	private float delta;
	
	private SpriteBatch spriteBatch;
	private Texture dropTexture;
	private Sprite dropSprite;
	private Rectangle dropRect;
	private Texture cuboTexture;
	private Sprite cuboSprite;
	private Rectangle cuboRect;
	
	@Override
	public void create() {
		// Cámara y viewport
		// camara = new OrthographicCamera();
		viewport = new FitViewport(8, 6);
		
		dropTexture = new Texture("drop.png");
		cuboTexture = new Texture("bucket.png");
		
		dropSprite = new Sprite(dropTexture);
    	dropSprite.setSize(0.5f, 0.5f);
    	// Centrar la gota en la pantalla
    	dropSprite.setPosition(viewport.getWorldWidth()/2 - dropSprite.getWidth()/2, viewport.getWorldHeight() - dropSprite.getHeight()); // Se podía haber usado setCenter para no restar dimensiones del sprite
    	
    	cuboSprite = new Sprite(cuboTexture);
    	cuboSprite.setSize(1f, 1f);
    	// Centrar la gota en la pantalla
    	cuboSprite.setX(viewport.getWorldWidth()/2 - cuboSprite.getWidth()/2);
		
		spriteBatch = new SpriteBatch();
		
		
	}
	
	@Override
	public void render() {
		// Obtener delta
		delta = Gdx.graphics.getDeltaTime();
		
		// Borrar pantalla - opción 2
		ScreenUtils.clear(Color.BLACK);
		spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
		
		// Obtener posición si se pulsa con el ratón en la pantalla
		if (Gdx.input.justTouched()) {
			float posX = Gdx.input.getX();
			float posY = Gdx.input.getY();
			Vector2 pos = new Vector2(posX, posY); // Si ya está creado el vector, usar pos.set(posX, posY)
			// Con unproject se convierte la coordenada de la pantalla (640 x 480) a coordenada del viewport (8 x 6)
			// Además, el viewport se ocupa de hacer la conversión de la coordenada y. 
			// En la pantalla y empieza en la esquina superior izquierda. En la lógica del juego/renderización, empieza en la inferior izquierda.
			// Unprojet convierte, por ejemplo, (0, 0), click en esquina superior izwuierda, en (0, 6) o (640, 480), click en esquina inferior derecha, en (8, 0)
			pos = viewport.unproject(pos);
			dropSprite.setCenter(pos.x, pos.y);
		}
		
		// Mover el cubo
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			cuboSprite.translateX(3 * delta);
		else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
				cuboSprite.translateX(-3 * delta);
		
		// Función alternativa a los ifs, toma el mínimo 0 o el máximo, si getX supera esos límites mínimo y máximo
		// cuboSprite.setX(MathUtils.clamp(cuboSprite.getX(), 0, viewport.getWorldWidth() - cuboSprite.getWidth()));
		if (cuboSprite.getX() >= viewport.getWorldWidth() - cuboSprite.getWidth())
			cuboSprite.setX(viewport.getWorldWidth() - cuboSprite.getWidth());
		else if (cuboSprite.getX() <= 0)
				cuboSprite.setX(0);
		
		// La gota cae
		dropSprite.translateY(-2 * delta);
		
		// Comprobar colisión o si la gota toca el suelo
		detectarColision();
				
		// Aplicar gravedad y recalcular posición
		spriteBatch.begin();
		dropSprite.draw(spriteBatch);
		cuboSprite.draw(spriteBatch);
		spriteBatch.end();
	}
	
	private void detectarColision() {
		// Crear los rectángulos que permiten detectar la colisión
		cuboRect = new Rectangle(cuboSprite.getX(), cuboSprite.getY(), cuboSprite.getWidth(), cuboSprite.getHeight());
		dropRect = new Rectangle(dropSprite.getX(), dropSprite.getY(), dropSprite.getWidth(), dropSprite.getHeight());
		
		if (cuboRect.overlaps(dropRect) || dropSprite.getY() < -dropSprite.getHeight())
			dropSprite.setY(-dropSprite.getHeight());
	}
	
	@Override
	public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
	}
}
