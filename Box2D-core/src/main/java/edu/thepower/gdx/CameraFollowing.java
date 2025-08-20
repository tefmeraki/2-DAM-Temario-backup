package edu.thepower.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class CameraFollowing extends ApplicationAdapter {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private Vector2 characterPosition;
    private float characterSpeed = 200f;
    private float characterSize = 30f;

    @Override
    public void create() {
        float viewportWidth = 800;
        float viewportHeight = 480;

        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera.setToOrtho(false);

        shapeRenderer = new ShapeRenderer();

        characterPosition = new Vector2(100, 100);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        handleInput(deltaTime);

        // Camera follows the character
        camera.position.set(characterPosition.x, characterPosition.y, 0);
        camera.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw a moving grid background
        drawGrid();

        // Draw the character
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(characterPosition.x, characterPosition.y, characterSize, characterSize);

        shapeRenderer.end();
    }

    private void handleInput(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterPosition.x -= characterSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            characterPosition.x += characterSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            characterPosition.y += characterSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            characterPosition.y -= characterSpeed * deltaTime;
        }
    }

    private void drawGrid() {
        shapeRenderer.setColor(Color.DARK_GRAY);
        for (int x = -2000; x <= 2000; x += 100) {
            shapeRenderer.rectLine(x, -2000, x, 2000, 1);
        }
        for (int y = -2000; y <= 2000; y += 100) {
            shapeRenderer.rectLine(-2000, y, 2000, y, 1);
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
