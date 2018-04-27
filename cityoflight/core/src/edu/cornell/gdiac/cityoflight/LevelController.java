package edu.cornell.gdiac.cityoflight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.ScreenListener;
import com.badlogic.gdx.graphics.Texture;

import static com.badlogic.gdx.Gdx.*;

public class LevelController implements Screen, ControllerListener, ContactListener, InputProcessor {

    private class LevelSelect extends Stage {

    }
    private Texture background;
    private Texture backButton;
    private ObstacleCanvas canvas;
    private static final String LEVEL_BACKGROUND_FILE = "textures/level select assets/UI.png";
    private static final String BACK_FILE = "textures/level select assets/menu_button.png";

    private ScreenListener listener;
    private boolean active;

    private float scale;

    private int pressState;
    private int backX = 250;
    private int backY = 35;
    private int startX = 350;
    private int startY = 35;
    private int startState;

    public LevelController(ObstacleCanvas drawcanvas) {
        canvas = drawcanvas;
        background = new Texture(LEVEL_BACKGROUND_FILE);
        backButton = new Texture(BACK_FILE);
        active = false;
        pressState = 0;
        startState = 0;

    }

    public boolean isReady() {
        return pressState == 2;
    }

    public void setCanvas(ObstacleCanvas canvas) {
        this.canvas = canvas;
    }
    public void draw(){
        if (active) {
            canvas.begin();
        if (background != null) {
            canvas.draw(background, 0, 0);
        }
        if (backButton != null) {
            Color tint = (pressState == 1 ? Color.GRAY : Color.WHITE);
//            canvas.draw(backButton, tint, 0, 0, backX, backY, 0, .65f, .6f);
        }
//        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        stage.act(graphics.getDeltaTime());
//        stage.draw();
            canvas.end();
        }
    }
    public void update(float delta) {
//        create();
    }

    public void setActive(boolean val) {
        active = val;
        if (active) {
            Gdx.input.setInputProcessor(this);
        }
    }

    private Stage stage;
    private Table table;

    public void create () {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.setDebug(true); // This is optional, but enables debug lines for tables.

        // Add widgets to the table here.
    }

    public void resize (int width, int height) {
        float sx = ((float)width)/800;
        float sy = ((float)height)/700;
        scale = (sx < sy ? sx : sy);
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

//    public void render () {
//        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        stage.act(graphics.getDeltaTime());
//        stage.draw();
//    }
    public void render(float delta) {
        if (active) {
            update(delta);


            draw();

            stage.draw();
            stage.act();

            if (isReady() && listener != null) {
                listener.exitScreen(this, 1);
            }
        }
    }

    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (background != null) {
            background.dispose();
            background = null;
        }
        if (backButton != null){
            backButton.dispose();
            backButton = null;
        }
        pressState = 0;
        active = false;
    }
    public void show() {
//        System.out.println("show");
        // Useless if called in outside animation loop
        active = true;
        create();
    }
    public void pause() {
        // TODO Auto-generated method stub
    }

    public void resume() {
        // TODO Auto-generated method stub
    }

    public void reset(ObstacleCanvas canvas) {
//        dispose();
        background = new Texture(LEVEL_BACKGROUND_FILE);
        backButton = new Texture(BACK_FILE);
        setCanvas(canvas);
//        setActive(true);
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
        active = false;
        // Useless if called in outside animation loop
    }


    /**
     * Dispose of all (non-static) resources allocated to this mode.
     */
//    public void dispose() {}

//    public void resize(int width, int height) {}


    /**
     * Called when the mouse wheel was scrolled. (UNSUPPORTED)
     *
     * @param amount the amount of scroll from the wheel
     * @return whether to hand the event to other listeners.
     */
    public boolean scrolled(int amount) {
        return true;
    }

    /** Unused ContactListener method */
    public void endContact(Contact contact) {}
    /** Unused ContactListener method */
    public void postSolve(Contact contact, ContactImpulse impulse) {}
    /** Unused ContactListener method */
    public void preSolve(Contact contact, Manifold oldManifold) {}

    public void beginContact(Contact contact) {  }

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
        if (backButton == null || pressState == 2) {
//            System.out.println("pressState is 2");
            return true;
        }
//
//         Flip to match graphics coordinates
//        screenY = heightY-screenY;
//
//         TODO: Fix scaling
//         Play button is a circle.

        float radius = backButton.getWidth()*backButton.getHeight();
        float dist = (screenX-backX)*(screenX-backX)+(screenY-backX)*(screenY-backX);
        if (dist < radius*radius) {
//            System.out.println("pressState is 1");
            pressState = 1;
        }
//        float radius = BUTTON_SCALE*scale*playButton.getWidth()/2.0f;
//        float dist = (screenX-playX)*(screenX-playX)+(screenY-playY)*(screenY-playY);
//        if (dist < radius*radius) {
//            pressState = 1;
//        }
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
//        if (buttonCode == startButton && pressState == 0) {
//            pressState = 1;
//            return false;
//        }
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
//        if (pressState == 1 && buttonCode == startButton) {
//            pressState = 2;
//            return false;
//        }
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
