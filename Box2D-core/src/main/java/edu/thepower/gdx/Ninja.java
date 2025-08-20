package edu.thepower.gdx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Ninja {
	private static final float GRAVEDAD = -200;
	private static final float RUN_SPEED = 500;
	private static final float JUMP_SPEED = RUN_SPEED * 2;
	private static final float KNIFE_SPEED = RUN_SPEED;
	private static final float KNIFE_RELEASE_TIME = 0.1f; // El cuchillo se libera a los 0,3 segundos de haberse iniciado la acción de lanzamiento

	private static enum Estado {
		IDLE, RUN, JUMP, ATTACK, DEAD, FALL, THROW
	};

	private static TextureAtlas atlas;

	private static List<Animation<Sprite>> animations;
	private static Animation<Sprite> runAnimation;
	private static Animation<Sprite> idleAnimation;
	private static Animation<Sprite> jumpAnimation;
	private static Animation<Sprite> attackAnimation;
	private static Animation<Sprite> deadAnimation;
	private static Animation<Sprite> glideAnimation;
	private static Animation<Sprite> jumpthrowAnimation;

	private Vector2 posicion;
	private Estado estado;
	private float stateTime;
	private Sprite sprite;

	private boolean isRunning;
	private boolean isJumping;
	private boolean isAttacking;
	private boolean isFalling;
	private boolean facingRight;
	private boolean isOnPlatform;
	private boolean isThrowing;
	private boolean isThrown;

	// Input flags
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean upPressed;
	private boolean downPressed;
	private boolean attackPressed;
	
	// Colección para alamcenar los cuchillos lanzados y activos
	List<Knife> cuchillos;
	
	static {
		animations = new ArrayList<>();
		// Carga de los sprite sheets
		runAnimation = cargaSprites("ninja_run.atlas", "Run_"); // Carga de los sprites de correr
		animations.add(runAnimation);
		idleAnimation = cargaSprites("ninja_idle.atlas", "Idle_"); // Carga de los sprites de parado
		animations.add(idleAnimation);
		jumpAnimation = cargaSprites("ninja_jump.atlas", "Jump_"); // Carga de los sprites de saltar
		animations.add(jumpAnimation);
		attackAnimation = cargaSprites("ninja_attack.atlas", "Attack_"); // Carga de los sprites de atacar
		animations.add(attackAnimation);
		deadAnimation = cargaSprites("ninja_dead.atlas", "Dead_"); // Carga de los sprites cuando es eliminado
		animations.add(deadAnimation);
		glideAnimation = cargaSprites("ninja_glide.atlas", "Glide"); // Carga de los sprites cuando es eliminado
		animations.add(glideAnimation);
		jumpthrowAnimation = cargaSprites("ninja_jumpthrow.atlas", "Jump_Throw_"); // Carga de los sprites para lanzar cuchillo
		animations.add(jumpthrowAnimation);
	}

	public Ninja(Vector2 posicion) {
		this.posicion = posicion;

		// Inciar variables
		estado = Estado.IDLE;
		facingRight = true;
		stateTime = 0;
		
		/*
		 * por defecto las variables boolean deben estar iniciadas a false
		leftPressed = false;
		rightPressed = false;
		attackPressed = false;

		isRunning = false;
		isJumping = false;
		isAttacking = false;
		isFalling = false;
		isOnPlatform = false;
		isThrowing = false;
		isThrown = false;
		*/
		
		// Iniciamos la colección que va a contener los cuchillos lanzados y activos
		cuchillos = new ArrayList<>();
	}

	public void update(Viewport viewport,float delta, List<NinjaEnemy> enemies, List<Rectangle> platforms) {
		isRunning = false;
		
		if (estado != Estado.DEAD) {
			if (attackPressed && !isRunning && !isJumping && !isFalling && !isAttacking)
				isAttacking = true;
			
			if (downPressed && !isRunning && !isJumping && !isFalling && !isThrowing) {
				isThrowing = true;
				isAttacking = false;  // Se pone a false por si se viene de ese estado, para evitar que se vuelva a asignar el estado ATTACK en el if-else
									  // múltiple para asignar estados
			}
			
			if (isThrowing && !isThrown && stateTime >= KNIFE_RELEASE_TIME) {
				// Crear sprite para el cuchillo y añadirlo a la colección de cuchillos activos
				cuchillos.add(new Knife(new Vector2(sprite.getX() + sprite.getWidth() * 0.5f, sprite.getY() + sprite.getHeight() * 0.3f), KNIFE_SPEED, facingRight?1:-1));
				isThrown = true;
			}

			if (!isAttacking && !isThrowing) {
				if (rightPressed || leftPressed) {
					if (rightPressed) {
						checkFacingRight(false);
						posicion.x += RUN_SPEED * delta;
						posicion.x = Math.min(posicion.x, viewport.getWorldWidth() - sprite.getWidth());
					} else if (leftPressed) {
						checkFacingRight(true);
						posicion.x -= RUN_SPEED * delta;
						posicion.x = Math.max(0, posicion.x);
					}
					
					boolean stillOnPlatform = false;
					if (isOnPlatform)
						for (int i = 0; i < platforms.size() && !stillOnPlatform; i++)
							if (isNinjaOnPlatform(platforms.get(i)))
								stillOnPlatform = true;
						
					if (isOnPlatform && !stillOnPlatform) // Si no está en una plaaforma o si estaba y ya no continúa.
						isFalling = true;
					else
						isRunning = true;
				}

				if (upPressed) {
					isJumping = true; 
					posicion.y += JUMP_SPEED * delta;
					
					// Impedir que se rebase el límite superior de la pantalla.
					posicion.y = Math.min(posicion.y, viewport.getWorldHeight() - sprite.getHeight());
					
					posicion.x += checkDireccion() * RUN_SPEED * delta;
					posicion.x = Math.clamp(posicion.x, 0, viewport.getWorldWidth() - sprite.getWidth());
					
					// Impedir que se rebase las plataformas cuando con el impulso hacia arriba
					for (Rectangle r : platforms)
						if (posicion.y + sprite.getHeight() >= r.y && posicion.y <= r.y && posicion.x + sprite.getWidth() >= r.x && posicion.x <= r.x + r.getWidth())
						{
							posicion.x -=  checkDireccion() * RUN_SPEED * delta;
							posicion.y = r.y - sprite.getHeight();
						}
				}

				if (isJumping && !upPressed) {
						stateTime = 0;
						isFalling = true;
						isJumping = false;
				}
				
				if (isFalling) {
					if (posicion.y > 0) {
						posicion.y += GRAVEDAD * delta;
						//posicion.x += checkDireccion() * RUN_SPEED / 10 * delta;  // sin alterar x para que la caída sea vertical
					} else {
						posicion.y = 0;
						isFalling = false;
					}
					
					/*
					for (Rectangle r : platforms)
						if (isNinjaOnPlatform(r)) {
							isOnPlatform = true;
							isFalling = false;
							posicion.y = r.y + r.height;
						}
					*/
					
					if (!isOnPlatform)
						for (int i = 0; i < platforms.size() && !isOnPlatform; i++) {
							Rectangle r = platforms.get(i);
							if (isNinjaOnPlatform(r)) {
								isOnPlatform = true;
								isFalling = false;
								posicion.y = r.y + r.height;
							}		
					} else 
						isOnPlatform = false;
				}
				
			} else { 
				// comprobar si al atacar con la espada hay impacto con algún enemigo
				if (isAttacking)
					for (NinjaEnemy enemy : enemies)
						if (getSwordBounds().overlaps(enemy.getBounds()))
							enemy.matar();
			}

			if (isAttacking)
				estado = Estado.ATTACK;
			else if (isThrowing)
				estado = Estado.THROW;
			else if (isJumping)
				estado = Estado.JUMP;
			else if (isFalling)
				estado = Estado.FALL;
			else if (isRunning)
				estado = Estado.RUN;
			else
				estado = Estado.IDLE;

			if (getAnimation().isAnimationFinished(stateTime))
				if (estado == Estado.ATTACK)
					isAttacking = false;
				else if (estado == Estado.THROW) {
						isThrowing = false;
						isThrown = false;
						stateTime = 0;
					}
			
			// Actualizar cuchillos y comprobar si ha impactado con algún enemigo
			updateKnifes(delta, viewport, enemies);
			
			// Comprobar si es alcanzado por algún enemigo, controlando solo el primer contacto
			// Así se evita que la secuencia de sprites no evolucione mientras el tiempo de contacto se prolongue.
			for (NinjaEnemy enemy : enemies)
				if (enemy.isAlive() && enemy.getBounds().overlaps(getNinjaBounds())) {
					stateTime = 0;
					estado = Estado.DEAD;
					posicion.y = 0; // En caso de que se produzca el solape cuando el ninja salta o está cayendo
				}
		}

		// Incrementamos el stateTime para refrescar el sprite a mostrar
		stateTime += delta;
		
		// Comprobar si es alcanzado por algún enemigo, controlando solo el primer contacto
		// Así se evita que la secuencia de sprites no evolucione mientras el tiempo de contacto se prolongue. 
		/*if (estado != Estado.DEAD && enemy.isAlive() && enemy.getBounds().overlaps(getNinjaBounds())) {
			stateTime = 0;
			estado = Estado.DEAD;
			posicion.y = 0; // En caso de que se produzca el solape cuando el ninja salta o está cayendo
		}*/
	}
	
	private void updateKnifes(float delta, Viewport viewport, List<NinjaEnemy> enemies) {
		// Recorrer la colección de cuchillos para actualizar posición x
		Knife k;
		Iterator<Knife> it = cuchillos.iterator(); // Es necesario utilizar un iterator para eliminar los cuchillos de la lista de forma segura
		while (it.hasNext())
		{
			k = it.next();
			k.update(delta);
			// Verificar si hay impacto con algún enemigo: enemigo muerto, se elimina cuchillo de la lista
			boolean eliminado = false; // Variable para determinar si el cuchillo sigue activo
			Rectangle r = k.getBounds();
			for (int i = 0;i < enemies.size() && !eliminado; i++) {
				NinjaEnemy enemy = enemies.get(i);
				if (enemy.getBounds().overlaps(r)) {
					enemy.matar();
					it.remove();
				} 
			}
			
			if (!eliminado && (r.x + r.getWidth() <= 0 || r.x >= viewport.getWorldWidth())) // Si no ha impactado antes con ningún enemigo y llega a los límites de la pantalla, eliminar de la lista
				it.remove();
		}
	}
	
	private boolean isNinjaOnPlatform (Rectangle platform) {
		/*
		boolean overlaps = false;
		
		if (isOnPlatform) {
			overlaps = (posicion.x >= platform.x && posicion.x <= platform.x + platform.width && posicion.y <= platform.y + platform.height && posicion.y + sprite.getHeight() >= platform.y);
			System.out.println(">>> CHECK estándar");
		}
		else {
			overlaps = (posicion.x + sprite.getWidth() >= platform.x && posicion.x <= platform.x + platform.width && posicion.y <= platform.y + platform.height && posicion.y + sprite.getHeight() >= platform.y);
			System.out.println("<<< CHECK EN VUELO");
			System.out.println("Posición x" + posicion.x);
			System.out.println("Poscion x + width: " + (posicion.x + sprite.getWidth()));
			System.out.println("Platform x: " + platform.x);
			System.out.println("Posicion y: " + posicion.y);
			System.out.println("Platform y: " + platform.y);
			System.out.println("Platform y + height: " + (platform.y + platform.height));
			System.out.println("posicion.y + sprite.getHeight: " + (posicion.y + sprite.getHeight()));
		}
		
		System.out.println("overlaps: " + overlaps);
		return overlaps;
		*/
		
		/*
		float minRequiredOverlapX = sprite.getWidth() * 0.2f;
		float overlapX = Math.min(posicion.x + sprite.getWidth(), platform.x + platform.width) - Math.max(posicion.x, platform.x);
		
		float minRequiredOverlapY = sprite.getHeight() * 0.01f;
		float overlapY = Math.min(posicion.y + sprite.getHeight(), platform.y + platform.height) - Math.max(posicion.y, platform.y);
		
		return overlapX >= minRequiredOverlapX && overlapY >= minRequiredOverlapY;
		*/
		
		// Después de revisarlo varias veces, se opta por la opción de que el sprite debe tener un 60 % de su ancho dentro de la plataforma para estar sobre ella.
		// Cuando está sobre la plataforma, esto hace que sobresalga el srpite más de lo que sería deseable por el lado izquierdo.
		return posicion.x + sprite.getWidth() * 0.6f >= platform.x && posicion.x <= platform.x + platform.width && posicion.y <= platform.y + platform.height && posicion.y + sprite.getHeight() >= platform.y;
	}
	
	private Rectangle getNinjaBounds() {
		Rectangle r;
		
		if (estado == Estado.ATTACK)
			r = getSwordBounds();
		else
			//r = sprite == null ? new Rectangle(0, 0 , 0, 0) : sprite.getBoundingRectangle();
			r = sprite == null ? new Rectangle(0, 0 , 0, 0) : new Rectangle(posicion.x, posicion.y, sprite.getWidth() * 0.9f, sprite.getHeight());
		
		return r;
	}

	private Rectangle getSwordBounds() {
		if (estado != Estado.ATTACK)
			return new Rectangle(0, 0, 0, 0);
		/*
		 * Determinar el área de contacto de la espada (rectángulo):
		 * - Mirando hacia la derecha: a la posición x del sprite se le suma el ancho del sprite y se le resta el
		 * offset y el ancho de la espada.
		 * - Mirando hacia la izquierda: a la posición x se le suma el offset de la espada (incremento de x). El rectángulo de contaco se representa
		 * a partir de esa posición x con el ancho de la espada.
		 * 
		 */
		float swordOffsetX = sprite.getWidth() / 3;
		float swordWidth = sprite.getWidth() / 10;
		float sx = facingRight ? sprite.getX() + sprite.getWidth() - swordOffsetX - swordWidth : sprite.getX() + swordOffsetX;
		return new Rectangle(sx, sprite.getY(), swordWidth, sprite.getHeight());
	}

	public void draw(SpriteBatch batch) { //, ShapeRenderer shape) {

		// Congelar el último frame de la sprite sheet del ninja muerto si cae eliminado
		// Si se toma justo getAnimationDuration, se muestra el primer sprite. Hay que restarle una cantidad mínima para que apunte al último sprite.
		if (estado == Estado.DEAD && getAnimation().isAnimationFinished(stateTime))
	        stateTime = getAnimation().getAnimationDuration() - 0.01f;
		
	    sprite = getAnimation().getKeyFrame(stateTime, true);

		sprite.setPosition(posicion.x - getXOffset(), posicion.y - getYOffset());
		sprite.draw(batch);
		
		 // Dibujar rectángulo de la espada para depurar
		/*if (estado == Estado.ATTACK)
		  { Rectangle sword = getSwordBounds();
		  shape.begin(ShapeRenderer.ShapeType.Filled); shape.setColor(Color.BLUE);
		  shape.rect(sword.x, sword.y, sword.getWidth(), sword.getHeight());
		  //shape.rect(sprite.getBoundingRectangle().x, sprite.getBoundingRectangle().y, sprite.getBoundingRectangle().getWidth(), sprite.getBoundingRectangle().getHeight());
		  shape.end();
		  }*/
		
		// Dibujar los cuchillos
		for (Knife k : cuchillos)
			k.draw(batch);
	}
	
	private float getXOffset() {
		float xOffset = 0;
		
		if (!facingRight && (estado == Estado.ATTACK || estado == Estado.DEAD))
			xOffset = sprite.getWidth() / 2; // corregir la posición x del sprite cuando está orientado a la izquierda
		
		return xOffset;
	}
	
	private float getYOffset() {
		float yOffset = 0;
		if (estado == Estado.RUN || estado == Estado.DEAD || estado == Estado.THROW)
			yOffset = 5; // corregir la posición y de los sprites que aparecen elevados sobre el suelo
		
		if (estado == Estado.ATTACK)
			yOffset = 8;
		
		return yOffset;
	}

	private Animation<Sprite> getAnimation() {
		return switch (estado) {
		case Estado.IDLE -> idleAnimation;
		case Estado.RUN -> runAnimation;
		case Estado.JUMP -> jumpAnimation;
		case Estado.ATTACK -> attackAnimation;
		case Estado.THROW -> jumpthrowAnimation;
		case Estado.DEAD -> deadAnimation;
		case Estado.FALL -> glideAnimation;
		default -> throw new IllegalArgumentException("Unexpected value: " + estado);
		};
	}

	public void keyDown(int tecla) {
		// Solo se procesa la tecla pulsada si el ninja está vivo
		if (estado != Estado.DEAD)
			switch (tecla) {
			case Input.Keys.RIGHT:
				rightPressed = true;
				break;
			case Input.Keys.LEFT:
				leftPressed = true;
				break;
			case Input.Keys.UP:
				/* 
				 * Explicación if
				 * 
				 * Si se reinicia stateTime cuando está atacando o lanzando cuchillos, se reinicia el ciclo de frames de atacar o lanzar.
				 * Esto provoca que la condición donde se evalúa si se ha legado al final de la animación de atacar o lanzar, en el update, nunca se cumpla
				 * impidiendo que se pase a la acción de saltar al pulsar UP. En su lugar, se sigue atacando o lanzando cuchillos. 
				 * 
				 */
				if (!isAttacking && !isThrowing) 
					stateTime = 0;
				upPressed = true;
				break;
			case Input.Keys.DOWN:
				if (!isThrowing) // Si ya está lanzando, impedimos que vuelva a lanzar hasta que no finalice todo el flujo de frames de lanzamiento
					stateTime = 0;
				downPressed = true;
				break;
			case Input.Keys.SPACE:
				stateTime = 0;
				attackPressed = true;
				break;
			}
	}

	public void keyUp(int tecla) {
		// Solo se procesa la tecla pulsada si el ninja está vivo
		if (estado != Estado.DEAD)
			switch (tecla) {
			case Input.Keys.RIGHT:
				rightPressed = false;
				break;
			case Input.Keys.LEFT:
				leftPressed = false;
				break;
			case Input.Keys.UP:
				upPressed = false;
				break;
			case Input.Keys.DOWN:
				downPressed = false;
				break;
			case Input.Keys.SPACE:
				attackPressed = false;
				break;
			}
	}

	private static Animation<Sprite> cargaSprites(String atlasFile, String nombreSprite) {
		Array<Sprite> sprites;
		float targetHeight = 75; //Ajustar el valor en base al tamaño que se desee del ninja

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

		return new Animation<>(0.05f, sprites, Animation.PlayMode.LOOP); // valor inicial 0.08f
	}

	private void checkFacingRight(boolean right) {
		if (facingRight == right) {
			flipSprites();
			facingRight = !right;
		}
	}

	private void flipSprites() {
		for (Animation<Sprite> animation : animations)
			for (Sprite s : animation.getKeyFrames())
				s.flip(true, false);
	}

	private float checkDireccion() {
		return facingRight ? 1f : -1f;
	}

	public Vector2 getPosition() {
		return posicion;
	}
}
