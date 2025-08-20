package edu.thepower.gdx;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;

public class Test extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private Animation<TextureRegion> runAnimation;
    private float stateTime = 0f;
    private Vector2 position = new Vector2(100, 100);
    private float speed = 200f;
    private boolean movingLeft = false;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        atlas = new TextureAtlas(Gdx.files.internal("ninja_run.atlas"));

        // Extract animation frames sorted by index
        Array<TextureAtlas.AtlasRegion> runFrames = atlas.findRegions("Run_");
        runFrames.sort((a, b) -> Integer.compare(a.index, b.index));
        runAnimation = new Animation<>(0.1f, runFrames, Animation.PlayMode.LOOP);
    }

    @Override
    public void render() {
    	float delta = Gdx.graphics.getDeltaTime();
        handleInput(delta);
        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        TextureRegion currentFrame = runAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        if (movingLeft) {
            // Flip left by drawing with negative width
            batch.draw(currentFrame, position.x + currentFrame.getRegionWidth(), position.y,
                       -currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        } else {
            batch.draw(currentFrame, position.x, position.y);
        }
        batch.end();
    }

    private void handleInput(float delta) {
        movingLeft = false;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            position.x -= speed * delta;
            movingLeft = true;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            position.x += speed * delta;
            movingLeft = false;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
    }
}

