package edu.thepower.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportCameraSample extends ApplicationAdapter {
	private ShapeRenderer shape;
	
	private Viewport viewport;
	private OrthographicCamera camara;
	
	private float delta;
	
	private float radio = 0.1f;
	private Circle c;
	
	// Gravedad
	float gravedad = -2f;
	float velocidadY = 0;
	float rebote = -0.8f;
	
	@Override
	public void create() {
		// Cámara y viewport
		camara = new OrthographicCamera();
		viewport = new FillViewport(8, 6, camara);
		
		// Shape renderer
		shape = new ShapeRenderer();
		
		// Instanciar círculo
		c = new Circle(4, 3, radio);
	}
	
	@Override
	public void render() {
		// Manejar la cámara
		manejarCamara();
		
		// Obtener delta
		delta = Gdx.graphics.getDeltaTime();
		
		// Obtener posición si se pulsa con el ratón en la pantalla
		if (Gdx.input.justTouched()) {
			float posX = Gdx.input.getX();
			float posY = Gdx.input.getY();
			System.out.println("Coordenadas click pantalla: (" + posX + ", " + posY + ")");
			Vector2 pos = new Vector2(posX, posY); // Si ya está creado el vector, usar pos.set(posX, posY)
			// Con unproject se convierte la coordenada de la pantalla (640 x 480) a coordenada del viewport (8 x 6)
			// Además, el viewport se ocupa de hacer la conversión de la coordenada y. 
			// En la pantalla y empieza en la esquina superior izquierda. En la lógica del juego/renderización, empieza en la inferior izquierda.
			// Unprojet convierte, por ejemplo, (0, 0), click en esquina superior izwuierda, en (0, 6) o (640, 480), click en esquina inferior derecha, en (8, 0)
			pos = viewport.unproject(pos);
			System.out.println("Coordenadas convertidas a dimensiones viewport: (" + pos.x + ", " + pos.y + ")");
			c.x = pos.x;
			c.y = pos.y;
		}
		
		// Borrar pantalla - opción 1
		// Gdx.gl.glClearColor(0, 0, 0, 0);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Borrar pantalla - opción 2
		ScreenUtils.clear(Color.WHITE);
		
		camara.update();
        viewport.apply();
		shape.setProjectionMatrix(viewport.getCamera().combined);
		
		// Aplicar gravedad y recalcular posición
		velocidadY += gravedad * delta;
		c.y += velocidadY * delta;
		if (c.y <= c.radius) {
			c.y = c.radius;
			velocidadY *= rebote;
		}
		
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(Color.BLACK);
		shape.circle(c.x, c.y, c.radius, 32);
		shape.end();
	}
	
	private void manejarCamara() {
        float moveSpeed = 5f * delta;
        float zoomSpeed = 0.95f;
        
        // Flechas para panear la cámara
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) camara.position.x -= moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) camara.position.x += moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camara.position.y += moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camara.position.y -= moveSpeed;
        
        // Q/A para hacer zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) camara.zoom /= zoomSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) camara.zoom *= zoomSpeed;
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
