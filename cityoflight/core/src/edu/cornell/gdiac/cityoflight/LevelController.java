package edu.cornell.gdiac.cityoflight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class LevelController implements Screen, ControllerListener, ContactListener, InputProcessor, ApplicationListener {

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

    public boolean goLevelOne() {
        return level1.isPressed() || startbutton.isPressed();
    }
    public boolean goLevelTwo() {
        return level2.isPressed();
    }
    public boolean goLevelThree() {
        return level3.isPressed();
    }
    public boolean goLevelFour() {
        return level4.isPressed();
    }
    public boolean goLevelFive() {
        return level5.isPressed();
    }
    public boolean goLevelSix() {
        return level6.isPressed();
    }
    public boolean isReady() {
        return menubutton.isPressed();
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
//        if (stage != null) {
//            stage.act(graphics.getDeltaTime());
//            stage.draw();
//        }
            canvas.end();
        }
    }
    public void update(float delta) {
        if (stage == null) {
            create();
        }
    }

    public void setActive(boolean val) {
        active = val;
        if (active) {
            Gdx.input.setInputProcessor(this);
        }
    }

    private Stage stage;
    private Table table;
    private SpriteBatch batch;
    private BitmapFont font; //** same as that used in Tut 7 **//
    private TextureAtlas buttonsAtlas; //** image of buttons **//
    private Skin buttonSkin; //** images are used as skins of the level1 **//
    private TextButton level1; //** the level1 - the only actor in program **//
    private String fontName = "fonts/Belladonna.ttf";
    private TextButton level2;
    private TextButton level3;
    private TextButton level4;
    private TextButton level5;
    private TextButton level6;
    private TextButton menubutton;
    private TextButton startbutton;
    public void create () {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
//        stage.addActor(table);
//        buttonsAtlas = new TextureAtlas("buttons.pack"); //** level1 atlas image **//
        buttonSkin = new Skin();
//        buttonSkin.addRegions(buttonsAtlas); //** skins for on and off **//
        font = new BitmapFont(false);
        font.setColor(Color.CLEAR);
        batch = new SpriteBatch();
        TextButtonStyle style = new TextButtonStyle();
//        style.up = buttonSkin.getDrawable("ButtonOff");
//        style.down = buttonSkin.getDrawable("ButtonOn");
        style.font = font;

        level1 = new TextButton("", style);
        level1.setPosition(98, 512-196);
        level1.setHeight(100);
        level1.setWidth(100);
        level1.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
            }
        });
        level2 = new TextButton("", style);
        level2.setPosition(244, 512-196);
        level2.setHeight(100);
        level2.setWidth(100);
        level2.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
            }
        });
        level3 = new TextButton("", style);
        level3.setPosition(395, 512-196);
        level3.setHeight(100);
        level3.setWidth(100);
        level3.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
            }
        });
        level4 = new TextButton("", style);
        level4.setPosition(542, 512-196);
        level4.setHeight(100);
        level4.setWidth(100);
        level4.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
            }
        });
        level5 = new TextButton("", style);
        level5.setPosition(695, 512-196);
        level5.setHeight(100);
        level5.setWidth(100);
        level5.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
            }
        });

        level6 = new TextButton("", style);
        level6.setPosition(98, 512-306);
        level6.setHeight(100);
        level6.setWidth(100);
        level6.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
            }
        });
        menubutton = new TextButton("", style);
        menubutton.setPosition(312, 28);
        menubutton.setHeight(40);
        menubutton.setWidth(70);
        menubutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
            }
        });
        startbutton = new TextButton("", style);
        startbutton.setPosition(503, 28);
        startbutton.setHeight(40);
        startbutton.setWidth(70);
        startbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
            }
        });

        stage.addActor(level1);
        stage.addActor(level2);
        stage.addActor(level3);
        stage.addActor(level4);
        stage.addActor(level5);
        stage.addActor(level6);
        stage.addActor(menubutton);
        stage.addActor(startbutton);
    }

//        table.setDebug(true); // This is optional, but enables debug lines for tables.

        // Add widgets to the table here.


    public void resize (int width, int height) {
        float sx = ((float)width)/800;
        float sy = ((float)height)/700;
        scale = (sx < sy ? sx : sy);
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    public void render () {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();

//        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        stage.draw();
        batch.end();
    }
//        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        stage.act(graphics.getDeltaTime());
//        stage.draw();
//    }
    public void render(float delta) {
        if (active) {
            update(delta);


            draw();

            if (stage != null) {
                stage.draw();
                stage.act();
            }
            if (isReady() && listener != null) {
                System.out.println("heres");
                listener.exitScreen(this, 1);
            }
            if ((goLevelOne() || goLevelTwo() || goLevelThree()) && listener != null) {
                listener.exitScreen(this, 2);
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
        batch.dispose();
//        buttonSkin.dispose();
//        buttonsAtlas.dispose();
        font.dispose();
//        stage.dispose();
    }
    public void show() {
//        System.out.println("show");
        // Useless if called in outside animation loop
        active = true;
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
     * Called when the screen was touched or a mouse level1 was pressed.
     *
     * This method checks to see if the play level1 is available and if the click
     * is in the bounds of the play level1.  If so, it signals the that the level1
     * has been pressed and is currently down. Any mouse level1 is accepted.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the level1 or touch finger number
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
//         Play level1 is a circle.

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
     * Called when a finger was lifted or a mouse level1 was released.
     *
     * This method checks to see if the play level1 is currently pressed down. If so,
     * it signals the that the player is ready to go.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the level1 or touch finger number
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
     * Called when a level1 on the Controller was pressed.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * level1 on an X-Box controller.  This outcome of this method is identical to
     * pressing (but not releasing) the play level1.
     *
     * @param controller The game controller
     * @param buttonCode The level1 pressed
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
     * Called when a level1 on the Controller was released.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * level1 on an X-Box controller.  This outcome of this method is identical to
     * releasing the the play level1 after pressing it.
     *
     * @param controller The game controller
     * @param buttonCode The level1 pressed
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
     * @param pointer the level1 or touch finger number
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
