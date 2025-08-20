package edu.thepower.gdx;

/*
 * Descargar colección de sprites.
 * Empaquetar las imágenes deseadas con TexturePacker (dodeandweb.com)
 * - Drag and drop your images.
 * - Set the output format to "libGDX."
 * - Export to generate both a .png (the actual sheet) and a .atlas (description file for libGDX)
 * 
 * La clase implementa InputProcessor para procesar los eventos de teclado en lugar de utilizar Gdx.input.isKeyPressed (Input.Keys.LEFT) o la tecla que sea
 * 
 ***** ToDo: crear clase ninja
 */

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportSpriteSheetSample extends ApplicationAdapter implements InputProcessor {
	private static final float WORLD_WIDTH = 640f;
    private static final float WORLD_HEIGHT = 480f;
    
    private static final int NINJA_IDLE = 0;
    private static final int NINJA_RUN_R = 1;
    private static final int NINJA_RUN_L = 2;
    private static final int NINJA_JUMP = 3;
    private static final float NINJA_SPEED = 500;
    private static final float NINJA_SPEEDX2 = NINJA_SPEED * 2;
	
	private SpriteBatch spriteBatch;
	private Sprite sprite;
	private Animation<Sprite> runAnimation;
	private Animation<Sprite> idleAnimation;
	private Animation<Sprite> jumpAnimation;
	private float stateTime;
	
	private OrthographicCamera camara;
	private Viewport viewport;
		
	private float delta;
	
	private float ninjaX;
	private boolean facingRight = true;
	private int ninjaStatus;
	
	@Override
	public void create() {
		spriteBatch = new SpriteBatch();
		
		// Cámara y viewport
		camara = new OrthographicCamera();
		camara.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camara);

		runAnimation = cargaSprites("ninja_run.atlas", "Run_"); // Carga de los sprites CORRER
		idleAnimation = cargaSprites("ninja_idle.atlas", "Idle_"); // Carga de los sprites IDLE
		jumpAnimation = cargaSprites("ninja_jump.atlas", "Jump_"); // Carga de los sprites IDLE
        
        stateTime = 0;
		
		// Registrar esta pantalla como procesador de eventos de entrada
        Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void render() {
		// Obtener delta
		delta = Gdx.graphics.getDeltaTime();
		// Borrar pantalla - opción 2
		ScreenUtils.clear(Color.WHITE);
		
		stateTime += delta;

		sprite = getAnimation().getKeyFrame(stateTime, true);

		actualizaNinja();
        sprite.setPosition(ninjaX, 0);
		
        // Apply viewport and camera updates
        viewport.apply();
        spriteBatch.setProjectionMatrix(camara.combined);

        spriteBatch.begin();
        sprite.draw(spriteBatch);
        spriteBatch.end();
	}
	
	private Animation<Sprite> cargaSprites(String atlasFile, String nombreSprite) {
		TextureAtlas atlas;
		//Animation<Sprite> animation;
		Array<Sprite> sprites;
    	float targetHeight = 100;
		
		atlas = new TextureAtlas(atlasFile);
		sprites = atlas.createSprites(nombreSprite);
        for (int i = 0; i < sprites.size; i++) {
        	Sprite s = sprites.get(i);
        	float aspectRatio = s.getWidth() / s.getHeight();
        	float scaledWidth = targetHeight * aspectRatio;

        	// Set the size, keeping aspect ratio
        	s.setSize(scaledWidth, targetHeight);
        	// Adjust origin to center for consistent transformations
            s.setOriginCenter();
        }
        
        return new Animation<>(0.08f, sprites, Animation.PlayMode.LOOP);        
	}
	
	private Animation<Sprite> getAnimation() {
		return switch (ninjaStatus) {
			case NINJA_IDLE -> idleAnimation;
			case NINJA_RUN_L, NINJA_RUN_R -> runAnimation;
			case NINJA_JUMP -> jumpAnimation;
			default -> throw new IllegalArgumentException("Unexpected value: " + ninjaStatus);
		};
	}
	
	private void flipAllSprites() {
	    for (Sprite s : runAnimation.getKeyFrames())
	    	s.flip(true, false);
	}
	
	@Override
	public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
	}
	
	private void actualizaNinja() {
		switch (ninjaStatus) {
			case NINJA_RUN_R:
				ninjaX += NINJA_SPEED * delta;
				break;
			case NINJA_RUN_L:
				ninjaX -= NINJA_SPEED * delta;
				break;
			case NINJA_JUMP:
				float salto;
				if (facingRight)
					salto = NINJA_SPEEDX2;
				else
					salto = -NINJA_SPEEDX2;
				ninjaX += salto * delta;
				break;
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Input.Keys.RIGHT:
				checkFacingRight(false);
				ninjaStatus = NINJA_RUN_R;
	        	break;
			case Input.Keys.LEFT:
				checkFacingRight(true);
				ninjaStatus = NINJA_RUN_L;
    			break;
			case Input.Keys.SPACE:
				ninjaStatus = NINJA_JUMP;
				break;
		}
		
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.LEFT) {
            // Example: Only fall back to IDLE if neither key is still down
            if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                ninjaStatus = NINJA_IDLE;
                stateTime = 0; // Resetear stateTime para comenzar con el primer frame de los sprites IDLE
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            	ninjaStatus = NINJA_RUN_L;
            	checkFacingRight(true);
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            	ninjaStatus = NINJA_RUN_R;
            	checkFacingRight(false);
            }
		}
		
		return false;
	}
	
	private void checkFacingRight(boolean right) {
		if (facingRight == right) {
    		flipAllSprites(); // los sprites deben estar todos en la posición original
    		facingRight = !right;
		}
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
