package edu.thepower.gdx.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import edu.thepower.gdx.Box2DTest;
import edu.thepower.gdx.Gravity;
import edu.thepower.gdx.Collisions;
import edu.thepower.gdx.ViewportSample;
import edu.thepower.gdx.ViewportCameraSample;
import edu.thepower.gdx.ViewportSpriteSample;
import edu.thepower.gdx.ViewportSpriteSheetSample;
import edu.thepower.gdx.Test;
import edu.thepower.gdx.NinjaGame;
import edu.thepower.gdx.CameraFollowing;
import edu.thepower.gdx.Breakout;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        // return new Lwjgl3Application(new Box2DTest(), getDefaultConfiguration());
        // return new Lwjgl3Application(new Breakout(), getDefaultConfiguration());
        return new Lwjgl3Application(new Gravity(), getDefaultConfiguration());
    	// return new Lwjgl3Application(new Collisions(), getDefaultConfiguration());
    	// return new Lwjgl3Application(new ViewportSample(), getDefaultConfiguration());
    	// return new Lwjgl3Application(new ViewportSpriteSample(), getDefaultConfiguration());
    	// return new Lwjgl3Application(new ViewportSpriteSheetSample(), getDefaultConfiguration());
    	// return new Lwjgl3Application(new NinjaGame(), getDefaultConfiguration());
    	// return new Lwjgl3Application(new CameraFollowing(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        
        //***configuration.setTitle("Box2D");
        //***configuration.setTitle("Breakout");
        //***configuration.setTitle("Gravity");
        //***configuration.setTitle("Collisions");
        configuration.setTitle("Ejemplos libGDX");
        
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(640, 480);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}