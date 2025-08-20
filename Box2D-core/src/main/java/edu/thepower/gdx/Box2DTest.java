package edu.thepower.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
//import com.badlogic.gdx.physics.box2d.Box2D;
//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
//import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
//import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Box2DTest extends ApplicationAdapter {
	private static final float WORLD_WIDTH = 16;
	private static final float WORLD_HEIGHT = 12;
	
	private World world;
	private Body circuloBody, leftWallBody, rightWallBody, groundBody;
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private Box2DDebugRenderer debugRenderer; 
	private ShapeRenderer shapeRenderer;
	
	private static Sound ballSound;
	private static Sound ballHit;
	
	boolean onGround = false;
	float y = 12;

	@Override
	public void create() {
		ballSound = Gdx.audio.newSound(Gdx.files.internal("ball.wav"));
		ballHit = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
		
		// 1. Creación del entorno Box2D con gravedad (hacia abajo)
		world = new World (new Vector2(0, -9.8f), true);
		
		// 2. Viewport y camara 2D
		camera = new OrthographicCamera();
		viewport = new FitViewport (WORLD_WIDTH, WORLD_HEIGHT, camera);
		
		// 3. Creación del círculo (dynamic body) en el entorno Box2D 
		BodyDef circuloDef = new BodyDef();
		circuloDef.type = BodyDef.BodyType.DynamicBody;
		circuloDef.position.set(WORLD_WIDTH/2, WORLD_HEIGHT - 1); // Centro de la pantalla, altura cielo - 1 metro
		circuloBody = world.createBody(circuloDef);
		
		CircleShape circuloShape = new CircleShape();
		circuloShape.setRadius(0.5f); // Radio de medio metro
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circuloShape;
		fixtureDef.density = 0.8f;
		fixtureDef.friction = 2f;
		fixtureDef.restitution = 0.8f; // "Bounciness"
		circuloBody.createFixture(fixtureDef);
		circuloShape.dispose();
		
		// 4. Dar velocidad aleatoria a la pelota
		float randomSpeed = (float)(Math.random()*5 + 15); // de 3 a 7 m/s
		float direction = Math.random() < 0.5 ? 1f : -1f;
		circuloBody.setLinearVelocity(randomSpeed * direction, 0); // Velocidad de desplazamiento x, y
		
		// 5. Creación del suelo
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(0, 0);
        groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(WORLD_WIDTH, 0); // Ancho del mundo, altura 0
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
        
        // 6. Creación de la pared izquierda
        BodyDef leftWallDef = new BodyDef();
        leftWallDef.type = BodyDef.BodyType.StaticBody;
        leftWallDef.position.set(0, 0);
        leftWallBody = world.createBody(leftWallDef);

        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(0, WORLD_HEIGHT);
        leftWallBody.createFixture(leftWallBox, 0.0f);
        leftWallBox.dispose();
        
        
        // 7. Creación de la pared derecha
        BodyDef rightWallDef = new BodyDef();
        rightWallDef.type = BodyDef.BodyType.StaticBody;
        rightWallDef.position.set(WORLD_WIDTH, 0);
        rightWallBody = world.createBody(rightWallDef);

        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(0, WORLD_HEIGHT);
        rightWallBody.createFixture(rightWallBox, 0.0f);
        rightWallBox.dispose();

        // 8. Renderizadores
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
		
		// Para el listener que escucha cuando la bola llega al suelo
        groundBody.setUserData("GROUND");
        circuloBody.setUserData("BALL");
        leftWallBody.setUserData("LEFT");
        rightWallBody.setUserData("RIGHT");
        
        world.setContactListener(new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				Body fa = contact.getFixtureA().getBody();
		        Body fb = contact.getFixtureB().getBody();

		        if ((fa.getUserData() == "BALL" && fb.getUserData() == "GROUND") ||
		            (fa.getUserData() == "GROUND" && fb.getUserData() == "BALL")) {
		            System.out.println("Ball hit the ground!");
		            ballSound.play();
		        } else  if ((fa.getUserData() == "BALL" && fb.getUserData() == "RIGHT") ||
			            	(fa.getUserData() == "RIGHT" && fb.getUserData() == "BALL")) {
			            	System.out.println("Ball hit the right wall!");
			            	ballHit.play();
			    		}  else  if ((fa.getUserData() == "BALL" && fb.getUserData() == "LEFT") ||
				            	(fa.getUserData() == "LEFT" && fb.getUserData() == "BALL")) {
				            	System.out.println("Ball hit the left wall!");
				            	ballHit.play();
				    		}
			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
	
		});
	}
	
	@Override
	public void render() {
		world.step (1/60f, 6, 2); // Determinar las condiciones físicas del entorno
		
		//Gdx.gl.glClearColor(0, 0, 0, 1);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Limpiar la pantalla
		
		// Limpiar la pantalla
		Gdx.gl.glClearColor(0.8f, 0.9f, 1f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
	    viewport.apply();
	    camera.update();
	    
	    // debugRenderer.render(world, camera.combined); // Render para depurar en desarrollo
	    shapeRenderer.setProjectionMatrix(camera.combined);
	    
	    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	    // Situar y dibujar la pelota
	    Vector2 circuloPos = circuloBody.getPosition();
	    shapeRenderer.setColor(1, 0, 0, 1); // rojo
	    Vector2 velocity = circuloBody.getLinearVelocity();
	    System.out.println("Y: " + y);
	    if (circuloPos.y < 0.51f && circuloPos.y == y) //suelo
	    	onGround = true;
	    if (onGround && Math.abs(velocity.y) < 1f && Math.abs(velocity.x) < 1f) {
	        circuloBody.setLinearVelocity(0, 0);
	        circuloBody.setAngularVelocity(0);
	    }
	    
	    shapeRenderer.circle (circuloPos.x, circuloPos.y, 0.5f, 32);
	    y = circuloPos.y;
	    
	    // Dibujar el suelo
	    shapeRenderer.setColor(0, 0.5f, 0, 1); // green
	    shapeRenderer.rect(0,  0, WORLD_WIDTH, 0);
	    
	    // Dibujar la pared izquierda
        shapeRenderer.setColor(0, 0, 1, 1); // azul
        shapeRenderer.rect(0, 0, 0, WORLD_HEIGHT);

        // Dibujar la pared derecha
        shapeRenderer.setColor(0, 0, 1, 1); // azul
        shapeRenderer.rect(WORLD_WIDTH, 0, 0, WORLD_HEIGHT);
	    
	    shapeRenderer.end();
	 }
	
	@Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
	
	@Override
	public void dispose() {
		//debugRenderer.dispose();
		shapeRenderer.dispose();
		world.dispose();
	}	
}