package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

import javax.xml.soap.Text;

public class HelpMode implements Screen, ControllerListener, ContactListener, InputProcessor, ApplicationListener {
    private static final String BACKGROUND_FILE = "help/help_ui_top_menu.png";
    private static final String CONTROLS_FILE = "help/help_ui_controls.png";
    private static final String CREATURE_SELECTION_FILE = "help/help_ui_top menu (hover).png";
    private static final String BACK_FILE = "textures/menu assets/help.png";
    private static final String LOU_CARCOLH_FILE = "help/help_ui_lou carcolh.png";
    private static final String TARASQUE_FILE = "help/help_ui_tarasque.png";
    private static final String DAME_BLANCHE_FILE = "help/help_ui_dame blanche.png";
    private static final String MENU_FILE = "textures/level select assets/menu_button.png";

//    private static final String PLAY_BTN_FILE = "textures/resume.png";
//    private static final String QUIT_BTN_FILE = "textures/quit.png";
    /** Standard window size (for scaling) */
    private static int STANDARD_WIDTH  = 1792;
    /** Standard window height (for scaling) */
    private static int STANDARD_HEIGHT = 1024;

    /** Amount to scale the play button */
    private static float BUTTON_SCALE  = 0.75f;

    private Texture background;
//    private Texture playButton;
    private Texture quitButton;
    private Texture exitButton;
    private int controlState;
    private int creatureState;
    private int quitState;
    private int snailState;
    private int tarasqueState;
    private int ladyState;
    private int exitState;

    /** Scaling factor for when the student changes the resolution. */
    private float scale;

    private float width;

    private int heightY;
    private float controlX;
    private float controlY;
    private float creatureX;
    private float creatureY;


    private boolean showControls;
    private boolean showCreatureSelect;
    private boolean showHelp;
    private boolean showSnail;
    private boolean showTarasque;
    private boolean showLady;
    private boolean showExit;
    private boolean active;

    private ObstacleCanvas canvas;

    private ScreenListener listener;

    private SoundController sound = SoundController.getInstance();

    public HelpMode(ObstacleCanvas drawcanvas) {
        canvas = drawcanvas;
        background = new Texture(BACKGROUND_FILE);
        controlState = 0;
        creatureState = 0;
        quitState = 0;
        snailState = 0;
        tarasqueState = 0;
        ladyState = 0;
        exitState = 0;

//        exitButton = null;
//        playButton = null;
        quitButton = new Texture(BACK_FILE);
        quitButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        exitButton= new Texture(MENU_FILE);
        exitButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        active = false;
        showControls = false;
        showCreatureSelect = false;
        showHelp = true;
        showLady = false;
        showSnail = false;
        showTarasque = false;
        showExit = true;


    }

    public void reset() {
        controlState = 0;
        creatureState = 0;
//        dispose();
//        if (stage != null) {
//            playbutton = null;
//            quitbutton = null;
//            stage.dispose();
//        }
    }

    public void resize(int width, int height) {
        float sx = ((float)width)/STANDARD_WIDTH;
        float sy = ((float)height)/STANDARD_HEIGHT;
        scale = (sx < sy ? sx : sy);

        this.width = (int)(.66f*width);

        heightY = height;
    }

    public void dispose() {
        if (background != null) {
            background.dispose();
            background = null;
        }
    }

    public void show() {
        // Useless if called in outside animation loop
        active = true;
        Gdx.input.setInputProcessor(this);
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
     * @param drawcanvas the canvas associated with this controller
     */
    public void setCanvas(ObstacleCanvas drawcanvas) {
        canvas = drawcanvas;
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

    private boolean toSnail() {
        return snailState == 2;
    }
    private boolean toTarasque() {
        return tarasqueState == 2;
    }
    private boolean toLady() {
        return ladyState == 2;
    }

    private boolean backToHelp() {
        return quitState == 2;
    }

    private boolean backToMenu() {
        return exitState == 2;
    }
    private boolean toControls() {
//        boolean val = (playbutton != null) ? playbutton.isPressed() : false;
        return controlState == 2;// || val;
    }

    private boolean toCreatures() {
//        boolean val = (quitbutton != null) ? quitbutton.isPressed() : false;
        return creatureState == 2;// || val;
    }


    private Stage stage;
    private Table table;
    private SpriteBatch batch;
    private BitmapFont font; //** same as that used in Tut 7 **//
    private TextureAtlas buttonsAtlas; //** image of buttons **//
    private Skin buttonSkin; //** images are used as skins of the level1 **//
    private TextButton playbutton;
    private TextButton quitbutton;
    public void create () {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
//        buttonsAtlas = new TextureAtlas("buttons.pack"); //** level1 atlas image **//
        buttonSkin = new Skin();
//        buttonSkin.addRegions(buttonsAtlas); //** skins for on and off **//
        font = new BitmapFont(false);
        font.setColor(Color.CLEAR);
        batch = new SpriteBatch();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
//        style.up = buttonSkin.getDrawable("ButtonOff");
//        style.down = buttonSkin.getDrawable("ButtonOn");
        style.font = font;

        playbutton = new TextButton("", style);
        playbutton.setPosition(147, 62);
        playbutton.setHeight(60);
        playbutton.setWidth(240);
        playbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Released");
            }
        });
        quitbutton = new TextButton("", style);
        quitbutton.setPosition(502, 62);
        quitbutton.setHeight(60);
        quitbutton.setWidth(340);
        quitbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Released");
            }
        });

        stage.addActor(playbutton);
        stage.addActor(quitbutton);
    }

    public void update() {
//        if (stage == null) {
//            create();
//        }
        if (quitButton != null && toControls()) {
//            System.out.println("quit button");
            quitButton = new Texture(BACK_FILE);
            quitButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
//        if (playButton == null) {
//            playButton = new Texture(PLAY_BTN_FILE);
//            playButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            controlY =  218;//+playButton.getHeight();//(int)(.25f*height) - 50;
            controlX = 65;//+playButton.getWidth();//width/2 - 200;
//            System.out.println("play not null");
//        }
//        if (quitButton == null) {
//            quitButton = new Texture(QUIT_BTN_FILE);
//            quitButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            creatureY =  293;//+quitButton.getHeight();//(int)(.25f*height) - 50;
            creatureX = 67;//+quitButton.getWidth();//width/2+200;
//            System.out.println("quit not null");
//            create();
//        }
    }

    public void draw() {
        canvas.begin();
        if (!showControls && !showCreatureSelect && showHelp && showExit) {
            canvas.draw(background, Color.WHITE, 0, 0, 0, 0, 0, 1f, 1f);
            if (exitButton != null) {
//                System.out.println("here");
                Color tint2 = (exitState == 1 ? Color.WHITE : Color.BLACK);
                canvas.draw(exitButton, tint2, quitButton.getWidth(), quitButton.getHeight(),
                        creatureX + exitButton.getWidth(), creatureY + exitButton.getHeight()-270, 0, 1f, 1f);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
            }
        }
        else if (showControls) {
//            System.out.println(quitButton == null);
            canvas.draw(new Texture(CONTROLS_FILE), Color.WHITE, 0, 0, 0, 0, 0, 1f, 1f);
            if (quitButton != null) {
//                System.out.println("here");
                Color tint2 = (creatureState == 1 ? Color.GRAY : Color.WHITE);
                canvas.draw(quitButton, tint2, quitButton.getWidth(), quitButton.getHeight(),
                        creatureX + quitButton.getWidth(), creatureY + quitButton.getHeight()-270, 0, 1, 1);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
            }
        }
        else if (showCreatureSelect) {
            canvas.draw(new Texture(CREATURE_SELECTION_FILE), Color.WHITE, 0, 0, 0, 0, 0, 1f, 1f);
            if (exitButton != null) {
//                System.out.println("here");
                Color tint2 = (exitState == 1 ? Color.WHITE : Color.BLACK);
                canvas.draw(exitButton, tint2, quitButton.getWidth(), quitButton.getHeight(),
                        creatureX + exitButton.getWidth(), creatureY + exitButton.getHeight()-270, 0, 1f, 1f);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
            }
        }
        else if (showSnail) {
            canvas.draw(new Texture(LOU_CARCOLH_FILE), Color.WHITE, 0,0,0,0,0,1,1);
            if (quitButton != null) {
//                System.out.println("here");
                Color tint2 = (creatureState == 1 ? Color.GRAY : Color.WHITE);
                canvas.draw(quitButton, tint2, quitButton.getWidth(), quitButton.getHeight(),
                        creatureX + quitButton.getWidth(), creatureY + quitButton.getHeight()-270, 0, 1, 1);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
            }
        }
        else if (showTarasque) {
            canvas.draw(new Texture(TARASQUE_FILE), Color.WHITE, 0,0,0,0,0,1,1);
            if (quitButton != null) {
//                System.out.println("here");
                Color tint2 = (creatureState == 1 ? Color.GRAY : Color.WHITE);
                canvas.draw(quitButton, tint2, quitButton.getWidth(), quitButton.getHeight(),
                        creatureX + quitButton.getWidth(), creatureY + quitButton.getHeight()-270, 0, 1, 1);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
            }
        }
        else if (showLady) {
            canvas.draw(new Texture(DAME_BLANCHE_FILE), Color.WHITE, 0,0,0,0,0,1,1);
            if (quitButton != null) {
//                System.out.println("here");
                Color tint2 = (creatureState == 1 ? Color.GRAY : Color.WHITE);
                canvas.draw(quitButton, tint2, quitButton.getWidth(), quitButton.getHeight(),
                        creatureX + quitButton.getWidth(), creatureY + quitButton.getHeight()-270, 0, 1, 1);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
            }
        }
        Color tint = (controlState == 1 ? Color.GRAY: Color.WHITE);
//        canvas.draw(playButton, tint, playButton.getWidth(), playButton.getHeight(),
//                controlX +playButton.getWidth(), controlY +playButton.getHeight(), 0, 1, 1);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
//        Color tint2 = (creatureState == 1 ? Color.GRAY: Color.WHITE);
//        canvas.draw(quitButton, tint2, quitButton.getWidth(), quitButton.getHeight(),
//                creatureX +quitButton.getWidth(), creatureY +quitButton.getHeight(), 0, 1, 1);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        canvas.end();
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
        if (toControls() && listener != null) {
            showHelp = false;
            showExit = false;
            showControls = true;
            controlState = 0;
//            listener.exitScreen(this, 6);
        }
        if (toCreatures() && listener != null) {
            showHelp = false;
            showExit = false;
            showCreatureSelect = true;
            creatureState = 0;
//            listener.exitScreen(this, 7);
        }
        if (backToHelp() && listener != null) {
//            System.out.println("here");
            creatureState = 0;
            controlState = 0;
            quitState = 0;
            exitState = 0;
            showControls=false;
            showCreatureSelect=false;
            showHelp = true;
            showExit = true;
            showSnail = false;
            showTarasque = false;
            showLady = false;
        }
        if (backToMenu() && listener != null) {
            listener.exitScreen(this, GameController.EXIT_MENU);
        }
        if (toSnail() && listener != null) {
            snailState = 0;
            creatureState = 0;
            controlState = 0;
            quitState = 0;
            showSnail = true;
            showCreatureSelect = false;
            showHelp = false;
            showExit = false;
        }
        if (toTarasque() && listener != null) {
            snailState = 0;
            tarasqueState = 0;
            creatureState = 0;
            controlState = 0;
            quitState = 0;
            showTarasque = true;
            showCreatureSelect = false;
            showHelp = false;
            showExit = false;
        }
        if (toLady() && listener != null) {
            ladyState = 0;
            creatureState = 0;
            controlState = 0;
            quitState = 0;
            showLady = true;
            showCreatureSelect = false;
            showHelp = false;
            showExit = false;
        }
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
//        for (Actor ma : stage.getActors()) {
//            ((MyActor)ma).touchDown(event, (float)screenX, (float)screenY, pointer, button);
//        }
        if (/**playButton == null || */controlState == 2) {
            return true;
        }

        if (/**quitButton == null || */creatureState == 2) {
            return true;
        }

        if (quitButton == null || quitState == 2) {
            return true;
        }
        if (exitButton == null || exitState == 2) {
            return true;
        }
        if (snailState == 2) {
            return true;
        }
        if (tarasqueState == 2) {
            return true;
        }
        if (ladyState == 2) {
            return true;
        }
        // Flip to match graphics coordinates
        screenY = heightY-screenY;

        // TODO: Fix scaling
        // Play button is a circle.
//        float radius = BUTTON_SCALE*scale*playButton.getWidth();
        float dist = (screenX- controlX)*(screenX- controlX)+(screenY- controlY)*(screenY- controlY);
//        if (dist < radius*radius) {
        if ((screenX > controlX && screenX < controlX +300) && (screenY > controlY && screenY < controlY +30)) {
            creatureState = 1;

        }

        float dist2 = (screenX- creatureX)*(screenX- creatureX)+(screenY-(creatureY))*(screenY- creatureY);
//        if (dist2 < radius*radius) {
        if ((screenX > creatureX && screenX < creatureX +330) && (screenY > creatureY && screenY < creatureY +30)) {

            controlState = 1;
        }

//        System.out.println("screenX "+screenX);
//        System.out.println("screenY "+screenY);
//        System.out.println("creatureX "+creatureX);
//        System.out.println("creature Y "+creatureY);

        if ((screenX > creatureX && screenX < creatureX +quitButton.getWidth()) && (screenY > creatureY-270 && screenY < creatureY +quitButton.getHeight()-270)) {
            if (!showExit) {
                quitState = 1;
            }
            else {
                exitState = 1;
            }
        }


        if ((screenX > 588 && screenX < 682)  && (screenY > 512-223 && screenY < 512-200)) {
            snailState = 1;
        }

        if ((screenX > 591 && screenX <700) && (screenY > 512-279 && screenY < 512-259)) {
            tarasqueState = 1;
        }
        if ((screenX > 590 && screenX < 705) && (screenY > 512-339 && screenY < 512-319)) {
            ladyState = 1;
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

        if (controlState == 1) {
            controlState = 2;
            return false;
        }
        if (creatureState == 1) {
            creatureState = 2;
            return false;
        }
        if (quitState == 1) {
            quitState = 2;
        }

        if (exitState == 1) {
            exitState = 2;
        }

        if (snailState == 1) {
            snailState = 2;
        }
        if (tarasqueState == 1) {
            tarasqueState = 2;
        }
        if (ladyState == 1) {
            ladyState = 2;
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
//        if (buttonCode == startButton && controlState == 0) {
//            controlState = 1;
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
//        if (controlState == 1 && buttonCode == startButton) {
//            controlState = 2;
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
//			controlState = 2;
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

