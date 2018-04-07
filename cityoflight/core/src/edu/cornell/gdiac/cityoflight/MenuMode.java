package edu.cornell.gdiac.cityoflight;

import java.util.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.ScreenListener;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class MenuMode implements Screen, ControllerListener, ContactListener, InputProcessor {
    //
//    public class MyActor extends Actor {
//        public int getExit() { return exit; }
//        Texture texture;
//        float actorX;
//        float actorY;
//        boolean started;
//        String name;
//        int exit;
//        public MyActor(Texture text, float x, float y, String n) {
//            texture = text;
//            actorX = x;
//            actorY = y;
//            name = n;
//
//            setBounds(actorX,actorY,texture.getWidth(),texture.getHeight());
//            addListener(new InputListener());
//        }
//        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//            ((MyActor)event.getTarget()).started = true;
//            return true;
//        }
//        public void draw(ObstacleCanvas batch, float alpha){
//            batch.draw(texture,0,0);
//        }
//
//        public void act(float delta){
//            if(started){
//                System.out.println("started true");
//                if (name.equals("play")) {
//                    exit=2;
//                }
//                else if (name.equals("level")) {
//                    exit=3;
//                }
//            }
//        }
//    }
//
    private static final String PLAY_BTN_FILE = "textures/playgame.png";
    private static final String LEVEL_BTN_FILE = "textures/levelselect.png";
    /** Background texture for start-up */
    /** Play button to display when done */
    private Texture playButton;
    private Texture levelButton;
    /** Amount to scale the play button */
    private static float BUTTON_SCALE  = 0.75f;
    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;
    /** The current state of the play button */
    private int pressState;
    private int levelState;
    /** Support for the X-Box start button in place of play button */
    private int   startButton;
    /** Whether or not this player mode is still active */
    private boolean active;


    private Texture background;
    /** Standard window size (for scaling) */
    private static int STANDARD_WIDTH  = 800;
    /** Standard window height (for scaling) */
    private static int STANDARD_HEIGHT = 700;

    ObstacleCanvas drawcanvas;

    private static final String BACKGROUND_FILE = "textures/loading.png";
    /** The height of the canvas window (necessary since sprite origin != screen origin) */
    private int heightY;
    /** Scaling factor for when the student changes the resolution. */
    private float scale;
    private float width;
    private float height;
    private float playX;
    private float playY;
    private float levelX;
    private float levelY;

    LoadingMode loading;

    Stage stage;

    public MenuMode(ObstacleCanvas canvas) {
        drawcanvas = canvas;
        background = new Texture(BACKGROUND_FILE);

        pressState = 0;
        levelState = 0;
        playButton = null;
        levelButton = null;
        active = false;

        loading = new LoadingMode(drawcanvas);
        Gdx.input.setInputProcessor(this);

//        for(Controller controller : Controllers.getControllers()) {
//            controller.addListener(this);
//        }

    }

//    public void create() {
//        stage = new Stage();
//        Gdx.input.setInputProcessor(stage);
//        if (playButton != null) {
//            MyActor playbutton = new MyActor(playButton, playX, playY, "play");
//            playbutton.setTouchable(Touchable.enabled);
//            stage.addActor(playbutton);
//        }
//        if (levelButton != null) {
//            MyActor levelbutton = new MyActor(levelButton, playX, playY - 100, "level");
//            levelbutton.setTouchable(Touchable.enabled);
//            stage.addActor(levelbutton);
//        }
//    }

    /**
     * Returns true if all assets are loaded and the player is ready to go.
     *
     * @return true if the player is ready to go
     */
    public boolean isReady() {
//        return progress==1;
//    }
//        Array actors = stage.getActors();
//        System.out.println(stage.getActors().size);
//        if (stage.getActors().size > 0) {
//            return ((MyActor) stage.getActors().get(0)).getExit() == 2;
//        } else {
//            return false;
        return pressState == 2;
//        }
    }

    public boolean toLevelSelect() {
//        Array actors = stage.getActors();
//        if (actors.size > 0) {
//            return ((MyActor) actors.get(0)).getExit() == 3;
//        }
//        else {
//            return false;
//        }
        return levelState == 2;
    }

    public void reset() {
    }
    /**
     * Dispose of all (non-static) resources allocated to this mode.
     */
    public void dispose() {
        background.dispose();
        background = null;
        if (playButton != null) {
            playButton.dispose();
            playButton = null;
        }
        if (levelButton != null) {
            levelButton.dispose();
            levelButton = null;
        }
        stage.dispose();
    }

    public void resize(int width, int height) {
        // Compute the drawing scale
        float sx = ((float)width)/STANDARD_WIDTH;
        float sy = ((float)height)/STANDARD_HEIGHT;
        scale = (sx < sy ? sx : sy);

        this.width = (int)(.66f*width);
        playY = (int)(.25f*height);
        playX = width/2;
        levelY = (int)(.25f*height);
        levelX = width/2;
        heightY = height;
    }


    public void show() {
        // Useless if called in outside animation loop
        active = true;
    }
    public void pause() {
        // TODO Auto-generated method stub
    }

    public void resume() {
        // TODO Auto-generated method stub
    }
    /**
     * Sets the canvas associated with this controller
     *
     * The canvas is shared across all controllers.  Setting this value will compute
     * the drawing scale from the canvas size.
     *
     * @param canvas the canvas associated with this controller
     */
    public void setCanvas(ObstacleCanvas canvas) {
        this.drawcanvas = canvas;
    }


    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }
    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
        // Useless if called in outside animation loop
    }

    /**
     * Called when the Screen should render itself.
     *
     * We defer to the other methods update() and draw().  However, it is VERY important
     * that we only quit AFTER a draw.
     *
     *   Number of seconds since last animation frame
     */

    public void render(float delta) {
        update();
        draw();
//            stage.draw();
//            stage.act();

        if (isReady() && listener != null) {

            listener.exitScreen(this, 2);
        }
        if (toLevelSelect() && listener != null) {

            listener.exitScreen(this, 3);
        }
    }



    /** Unused ContactListener method */
    public void endContact(Contact contact) {}
    /** Unused ContactListener method */
    public void postSolve(Contact contact, ContactImpulse impulse) {}
    /** Unused ContactListener method */
    public void preSolve(Contact contact, Manifold oldManifold) {}

    public void beginContact(Contact contact) {  }

    public void update() {
        if (playButton == null) {
            playButton = new Texture(PLAY_BTN_FILE);
            playButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        }
        if (levelButton == null) {
            levelButton = new Texture(LEVEL_BTN_FILE);
            levelButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//            create();
        }
    }

    public void draw() {
        drawcanvas.begin();
        drawcanvas.draw(background, 0, 0);
        Color tint = (pressState == 1 ? Color.GRAY: Color.WHITE);
        drawcanvas.draw(playButton, tint, playButton.getWidth()/2, playButton.getHeight()/2,
                playX, playY, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        Color tint2 = (levelState == 1 ? Color.GRAY: Color.WHITE);
        drawcanvas.draw(levelButton, tint2, levelButton.getWidth()/2, levelButton.getHeight()/2,
                playX, playY-100, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        drawcanvas.end();
    }



    // PROCESSING PLAYER INPUT
    /**
     * Called when the screen was touched or a mouse button was pressed.
     *
     * This method checks to see if the play button is available and if the click
     * is in the bounds of the play button.  If so, it signals the that the button
     * has been pressed and is currently down. Any mouse button is accepted.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        for (Actor ma : stage.getActors()) {
//            ((MyActor)ma).touchDown(event, (float)screenX, (float)screenY, pointer, button);
//        }
        if (playButton == null || pressState == 2) {
            return true;
        }

        if (levelButton == null || levelState == 2) {
            return true;
        }
        // Flip to match graphics coordinates
        screenY = heightY-screenY;

        // TODO: Fix scaling
        // Play button is a circle.
        float radius = BUTTON_SCALE*scale*playButton.getWidth()/2.0f;
        float dist = (screenX-playX)*(screenX-playX)+(screenY-playY)*(screenY-playY);
        if (dist < radius*radius) {
            pressState = 1;
        }

        float dist2 = (screenX-playX)*(screenX-playX)+(screenY-(playY-100))*(screenY-(playY-100));
        if (dist2 < radius) {
            levelState = 1;
        }
        return false;
    }

    /**
     * Called when a finger was lifted or a mouse button was released.
     *
     * This method checks to see if the play button is currently pressed down. If so,
     * it signals the that the player is ready to go.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (pressState == 1) {
            pressState = 2;
            return false;
        }
        if (levelState == 1) {
            levelState = 2;
            return false;
        }
        return true;
    }

    /**
     * Called when a button on the Controller was pressed.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * pressing (but not releasing) the play button.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonDown (Controller controller, int buttonCode) {
        if (buttonCode == startButton && pressState == 0) {
            pressState = 1;
            return false;
        }
        return true;
    }

    /**
     * Called when a button on the Controller was released.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * releasing the the play button after pressing it.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonUp (Controller controller, int buttonCode) {
        if (pressState == 1 && buttonCode == startButton) {
            pressState = 2;
            return false;
        }
        return true;
    }

    // UNSUPPORTED METHODS FROM InputProcessor

    /**
     * Called when a key is pressed (UNSUPPORTED)
     *
     * @param keycode the key pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyDown(int keycode) {
        return true;
    }

    /**
     * Called when a key is typed (UNSUPPORTED)
     *
     * @param character the key typed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyTyped(char character) {
        return true;
    }

    /**
     * Called when a key is released.
     *
     * We allow key commands to start the game this time.
     *
     * @param keycode the key released
     * @return whether to hand the event to other listeners.
     */
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.N || keycode == Input.Keys.P) {
//			pressState = 2;
            return false;
        }
        return true;
    }

    /**
     * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @return whether to hand the event to other listeners.
     */
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    /**
     * Called when the mouse wheel was scrolled. (UNSUPPORTED)
     *
     * @param amount the amount of scroll from the wheel
     * @return whether to hand the event to other listeners.
     */
    public boolean scrolled(int amount) {
        return true;
    }

    /**
     * Called when the mouse or finger was dragged. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    // UNSUPPORTED METHODS FROM ControllerListener

    /**
     * Called when a controller is connected. (UNSUPPORTED)
     *
     * @param controller The game controller
     */
    public void connected (Controller controller) {}

    /**
     * Called when a controller is disconnected. (UNSUPPORTED)
     *
     * @param controller The game controller
     */
    public void disconnected (Controller controller) {}

    /**
     * Called when an axis on the Controller moved. (UNSUPPORTED)
     *
     * The axisCode is controller specific. The axis value is in the range [-1, 1].
     *
     * @param controller The game controller
     * @param axisCode 	The axis moved
     * @param value 	The axis value, -1 to 1
     * @return whether to hand the event to other listeners.
     */
    public boolean axisMoved (Controller controller, int axisCode, float value) {
        return true;
    }

    /**
     * Called when a POV on the Controller moved. (UNSUPPORTED)
     *
     * The povCode is controller specific. The value is a cardinal direction.
     *
     * @param controller The game controller
     * @param povCode 	The POV controller moved
     * @param value 	The direction of the POV
     * @return whether to hand the event to other listeners.
     */
    public boolean povMoved (Controller controller, int povCode, PovDirection value) {
        return true;
    }

    /**
     * Called when an x-slider on the Controller moved. (UNSUPPORTED)
     *
     * The x-slider is controller specific.
     *
     * @param controller The game controller
     * @param sliderCode The slider controller moved
     * @param value 	 The direction of the slider
     * @return whether to hand the event to other listeners.
     */
    public boolean xSliderMoved (Controller controller, int sliderCode, boolean value) {
        return true;
    }

    /**
     * Called when a y-slider on the Controller moved. (UNSUPPORTED)
     *
     * The y-slider is controller specific.
     *
     * @param controller The game controller
     * @param sliderCode The slider controller moved
     * @param value 	 The direction of the slider
     * @return whether to hand the event to other listeners.
     */
    public boolean ySliderMoved (Controller controller, int sliderCode, boolean value) {
        return true;
    }

    /**
     * Called when an accelerometer value on the Controller changed. (UNSUPPORTED)
     *
     * The accelerometerCode is controller specific. The value is a Vector3 representing
     * the acceleration on a 3-axis accelerometer in m/s^2.
     *
     * @param controller The game controller
     * @param accelerometerCode The accelerometer adjusted
     * @param value A vector with the 3-axis acceleration
     * @return whether to hand the event to other listeners.
     */
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return true;
    }


}


/**
 what I need:

 different states in World Controller - PlayState (handled by GameController), MenuState (handled by MenuMode),
 LevelSelectState (handled by LevelController)

 different states in GameController - PlayState (normal stuff), PauseState (can handle either in PauseMode or just in
 GameController)

 MenuMode (essentially LoadingMode but with no loading bar and more options besides just playing the game)

 LevelController (may change name) (again essentially just LoadingMode but with user able to select from multiple levels
 laid out in a grid, option to go back to menu mode)

 (Potentially) PauseMode (allows player to re-enter game, go back to level select, go back to main menu, or go to
 options)

 Pause Mode separate class? PauseState in World or CameController?


 basically all LoadingMode needs to do is load stuff and update progress bar, after progress is done it switches to menu


 */