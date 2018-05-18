package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.*;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

public class PauseMode implements Screen, ControllerListener, ContactListener, InputProcessor, ApplicationListener {
    public class MyActor extends Actor {
        public float x;
        public float y;
        public float width;
        public float height;
        public PauseMode.MyListener listener;
        public MyActor(float x, float y, PauseMode.MyListener listen) {
            this.x=x;
            this.y=y;
            listener = listen;
        }
        public boolean getHover() {
            return listener.getHover();
        }

    }

    public class MyListener extends ClickListener {

        public boolean hover;

        public MyListener() {
            hover = false;
        }

        public boolean getHover() {
            return hover;
        }

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//            System.out.println("enter");
            hover = true;
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            hover = false;
        }
    }


    private static final String BACKGROUND_FILE = "textures/pause assets/pause_bg.png";
    private static final String PLAY_BTN_FILE = "textures/pause assets/resume_button.png";
    private static final String QUIT_BTN_FILE = "textures/pause assets/quit_button.png";

    private static final String PLAY_HOVER = "textures/pause assets/resume_hover.png";
    private static final String QUIT_HOVER = "textures/pause assets/quit_hover.png";

    private static final String MUSIC_BTN_FILE = "textures/pause assets/music_button.png";
    private static final String SOUND_BTN_FILE = "textures/pause assets/sound_button.png";
    private static final String MUSIC_MUTE_BTN_FILE = "textures/pause assets/music_mute.png";
    private static final String SOUND_MUTE_BTN_FILE = "textures/pause assets/sound_mute.png";

    /** Standard window size (for scaling) */
    private static int STANDARD_WIDTH  = 1792;
    /** Standard window height (for scaling) */
    private static int STANDARD_HEIGHT = 1024;

    /** Amount to scale the play button */
    private static float BUTTON_SCALE  = 0.75f;

    private Texture background;
    private Texture playButton;
    private Texture quitButton;
    private Texture exitButton;

    private Texture playHover;
    private Texture quitHover;

    private boolean hoverplay;
    private boolean hoverquit;

    private Texture musicButton;
    private Texture soundButton;
    private Texture muteMusicButton;
    private Texture muteSoundButton;

    private int pressState;
    private int quitState;

    private int musicState;
    private int soundState;

    // sorry these are Amanda's variables because i don't understand the buttons
    private boolean isMusic = true;
    private boolean isSound = true;

    /** Scaling factor for when the student changes the resolution. */
    private float scale;

    private float width;

    private int heightY;
    private float playX;
    private float playY;
    private float quitX;
    private float quitY;

    private float musicX;
    private float musicY;
    private float soundX;
    private float soundY;

    private boolean active;

    private ObstacleCanvas canvas;

    private ScreenListener listener;

    private Stage stage;
    private Table table;
    private SpriteBatch batch;
    private BitmapFont font; //** same as that used in Tut 7 **//
    private TextButton start; //** the level1 - the only actor in program **//
    private TextButton levels;
    private TextButton help;
    private TextButton quitTest;

    private SoundController sound = SoundController.getInstance();

    public PauseMode(ObstacleCanvas drawcanvas) {
        canvas = drawcanvas;
        background = new Texture(BACKGROUND_FILE);
        pressState = 0;
        quitState = 0;
        playButton = null;
        quitButton = null;

        musicButton = null;
        soundButton = null;
        active = false;

        Gdx.input.setInputProcessor(this);
    }

    public void reset() {
        pressState = 0;
        quitState = 0;
        hoverplay = false;
        hoverquit = false;
//        dispose();
        if (stage != null) {
            playbutton = null;
            quitbutton = null;
//            stage.dispose();
        }
    }

    public void resize(int width, int height) {
        float sx = ((float)width)/STANDARD_WIDTH;
        float sy = ((float)height)/STANDARD_HEIGHT;
        scale = (sx < sy ? sx : sy);

        this.width = (int)(.66f*width);

        heightY = height;
    }

    public void dispose() {
        background.dispose();
        background = null;
        if (playButton != null) {
            playButton.dispose();
            playButton = null;
        }
        if (quitButton != null) {
            quitButton.dispose();
            quitButton = null;
        }
        if (musicButton != null) {
            musicButton.dispose();
            quitButton = null;
        }
        if (soundButton != null) {
            soundButton.dispose();
            soundButton = null;
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

    private boolean backToGame() {
//        boolean val = (playbutton != null) ? playbutton.isPressed() : false;
        return pressState == 2;// || val;
    }

    private boolean toMenu() {
//        boolean val = (quitbutton != null) ? quitbutton.isPressed() : false;
        return quitState == 2;// || val;
    }

    public boolean isMusic() {
//        System.out.println("mute music");
        return isMusic;
    }

    public boolean isSound() {
//        System.out.println("mute sound");
        return isSound;
    }

//    private static PauseMode pauseController = null;
//
//    public static PauseMode getInstance() {
//        if (pauseController == null) {
//            pauseController = PauseMode.this;
//            System.out.println("null lol");
//        }
//        return pauseController;
//    }

    private TextButton playbutton;
    private TextButton quitbutton;
    private TextButton musicbutton;
    private TextButton soundbutton;


    public void create () {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        font = new BitmapFont(false);
        font.setColor(Color.CLEAR);
        batch = new SpriteBatch();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

//        System.out.println("new play button");

        playbutton = new TextButton("", style);
        playbutton.setPosition(147, 62);
        playbutton.setHeight(60);
        playbutton.setWidth(240);
        playbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
//                System.out.println("quit state 2");
                pressState = 2;
//                return false;
//                float screenX = x * scale;
//                float screenY = y * scale;
//                if (playButton == null || pressState == 2) {
//                    return true;
//                }
//                // Flip to match graphics coordinates
//                screenY = heightY- screenY;
//
//                // TODO: Fix scaling
//                // Play button is a circle.
//                float radius = BUTTON_SCALE*scale*playButton.getWidth();
//                float dist = (screenX-playX)*(screenX-playX)+(screenY-playY)*(screenY-playY);
////        if (dist < radius*radius) {
//                if ((screenX > playX && screenX < playX+playButton.getWidth()) && (screenY > playY && screenY < playY+playButton.getHeight())) {
//                    pressState = 1;
//                    sound.stop("select_effect");
//                    sound.play("select_effect", "sounds/select_effect.wav", false, 0.7f, isSound);
//
//                }
                return false;
            }
        });
        playbutton.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                System.out.println("enter??");
                hoverplay = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                System.out.println("exit??");
                hoverplay = false;
            }
        });

        quitbutton = new TextButton("", style);
        quitbutton.setPosition(502, 62);
        quitbutton.setHeight(60);
        quitbutton.setWidth(340);
        quitbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
//                System.out.println("quit state 2");
                quitState = 2;
                return false;
            }

        });
        quitbutton.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                System.out.println("enter quit");
                hoverquit = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                System.out.println("exit quit");
                hoverquit = false;
            }
        });

        musicbutton = new TextButton("", style);
        musicbutton.setPosition(725, 300);
        musicbutton.setHeight(148 / 2);
        musicbutton.setWidth(123 / 2);
        musicbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Released");

            }
        });
//        isMusic = true;
        soundbutton = new TextButton("", style);
        soundbutton.setPosition(730, 250);
        soundbutton.setHeight(108 / 2);
        soundbutton.setWidth(127 / 2);
        soundbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Released");
            }
        });
//        isSound = true;

        stage.addActor(playbutton);
        stage.addActor(quitbutton);

        stage.addActor(musicbutton);
        stage.addActor(soundbutton);
    }

    public void update() {
        InputController input = InputController.getInstance();
        input.readInput();
        if (input.didPause() || input.didExit()) {
            listener.exitScreen(this, 2);
        }
//        if (stage == null) {
        if (playbutton == null && quitbutton == null)
            create();
            System.out.println("stage null");
//        }
        if (playButton == null) {
            playButton = new Texture(PLAY_BTN_FILE);
            playButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            playY =  62;//+playButton.getHeight();//(int)(.25f*height) - 50;
            playX = 147;//+playButton.getWidth();//width/2 - 200;
//            create();
//            System.out.println("play not null");
        }
        if (playHover == null) {
            playHover = new Texture(PLAY_HOVER);
            playHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (quitButton == null) {
            quitButton = new Texture(QUIT_BTN_FILE);
            quitButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            quitY =  60;//+quitButton.getHeight();//(int)(.25f*height) - 50;
            quitX = 500;//+quitButton.getWidth();//width/2+200;
//            System.out.println("quit not null");
//            create();
        }
        if (quitHover == null) {
            quitHover = new Texture(QUIT_HOVER);
            quitHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//            create();
        }

        if (musicButton == null) {
            musicButton = new Texture(MUSIC_BTN_FILE);
            musicButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            musicY =  300;//+playButton.getHeight();//(int)(.25f*height) - 50;
            musicX = 725;//+playButton.getWidth();//width/2 - 200;

        }
        if (soundButton == null) {
            soundButton = new Texture(SOUND_BTN_FILE);
            soundButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            soundY =  250;//+quitButton.getHeight();//(int)(.25f*height) - 50;
            soundX = 730;//+quitButton.getWidth();//width/2+200;
        }

        if (muteMusicButton == null) {
            muteMusicButton = new Texture(MUSIC_MUTE_BTN_FILE);
            muteMusicButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            musicY =  300;//+playButton.getHeight();//(int)(.25f*height) - 50;
            musicX = 725;//+playButton.getWidth();//width/2 - 200;

        }
        if (muteSoundButton == null) {
            muteSoundButton = new Texture(SOUND_MUTE_BTN_FILE);
            muteSoundButton.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            soundY =  250;//+quitButton.getHeight();//(int)(.25f*height) - 50;
            soundX = 730;//+quitButton.getWidth();//width/2+200;
        }
    }

    public void draw() {
        float scaling = 0.5f;

        canvas.begin();
        canvas.draw(background, Color.WHITE, 0, 0, 0 ,0, 0, 1f, 1f);
//        Color tint = (pressState == 1 ? Color.GRAY: Color.WHITE);
//        canvas.draw(playButton, tint, playButton.getWidth(), playButton.getHeight(),
//                playX+playButton.getWidth(), playY+playButton.getHeight(), 0, 1, 1);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        if (hoverplay) {
            System.out.println("drawing play hover");
            canvas.draw(playHover, Color.WHITE, playHover.getWidth(), playHover.getHeight(),
                    playX + playHover.getWidth()/2, playY + playHover.getHeight()/2, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        }
        else {
            canvas.draw(playButton, Color.WHITE, playButton.getWidth(), playButton.getHeight(),
                    playX + playButton.getWidth()/2, playY + playButton.getHeight()/2, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        }
//        Color tint2 = (quitState == 1 ? Color.GRAY: Color.WHITE);
//        canvas.draw(quitButton, tint2, quitButton.getWidth(), quitButton.getHeight(),
//                quitX+quitButton.getWidth(), quitY+quitButton.getHeight(), 0, 1, 1);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        if (hoverquit) {
            System.out.println("drawing quit hover");
            canvas.draw(quitHover, Color.WHITE, quitHover.getWidth(), quitHover.getHeight(),
                    quitX+quitButton.getWidth()/2, quitY+quitButton.getHeight()/2, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        }
        else {
            canvas.draw(quitButton, Color.WHITE, quitButton.getWidth(), quitButton.getHeight(),
                    quitX+quitButton.getWidth()/2, quitY+quitButton.getHeight()/2, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        }

//        Color tint3 = (isMusic ? Color.GRAY: Color.WHITE);
        if (isMusic) {
            canvas.draw(musicButton, Color.WHITE, musicButton.getWidth(), musicButton.getHeight(),
                    musicX+musicButton.getWidth(), musicY+musicButton.getHeight(), 0, 0.5f, 0.5f);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        }
        else {
            canvas.draw(muteMusicButton, Color.WHITE, muteMusicButton.getWidth(), muteMusicButton.getHeight(),
                    musicX+muteMusicButton.getWidth(), musicY+muteMusicButton.getHeight(), 0, 0.5f, 0.5f);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);            canvas
        }

//        Color tint4 = (isSound ? Color.GRAY: Color.WHITE);
        if (isSound) {
            canvas.draw(soundButton, Color.WHITE, soundButton.getWidth(), soundButton.getHeight(),
                    soundX+soundButton.getWidth(), soundY+soundButton.getHeight(), 0, 0.5f, 0.5f);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
        }
        else {
            canvas.draw(muteSoundButton, Color.WHITE, muteSoundButton.getWidth(), muteSoundButton.getHeight(),
                    soundX+muteSoundButton.getWidth(), soundY+muteSoundButton.getHeight(), 0, 0.5f, 0.5f);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);            canvas
        }

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
        if (stage != null) { stage.act(); }

        if (backToGame() && listener != null) {

            listener.exitScreen(this, 2);
        }
        if (toMenu() && listener != null) {

            listener.exitScreen(this, 1);
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
        if (playButton == null || pressState == 2) {
            return true;
        }

        if (quitButton == null || quitState == 2) {
            return true;
        }

        if (musicButton == null) {
            return true;
        }

        if (soundButton == null) {
            return true;
        }

        // Flip to match graphics coordinates
        screenY = heightY-screenY;

        // TODO: Fix scaling
        // Play button is a circle.
        float radius = BUTTON_SCALE*scale*playButton.getWidth();
        float dist = (screenX-playX)*(screenX-playX)+(screenY-playY)*(screenY-playY);
//        if (dist < radius*radius) {
        if ((screenX > playX && screenX < playX+playButton.getWidth()) && (screenY > playY && screenY < playY+playButton.getHeight())) {
            pressState = 1;
            sound.stop("select_effect");
            sound.play("select_effect", "sounds/select_effect.wav", false, 0.7f, isSound);

        }

        float dist2 = (screenX-quitX)*(screenX-quitX)+(screenY-(quitY))*(screenY-quitY);
//        if (dist2 < radius*radius) {
        if ((screenX > quitX && screenX < quitX+quitButton.getWidth()) && (screenY > quitY && screenY < quitY+quitButton.getHeight())) {

            quitState = 1;
            sound.stop("seen_effect");
            sound.play("seen_effect", "sounds/seen_effect.wav", false, 0.7f, isSound);
        }

        if ((screenX > musicX + musicButton.getWidth()/2 && screenX < musicX+musicButton.getWidth()) && (screenY > musicY + musicButton.getHeight()/2 && screenY < musicY+musicButton.getHeight())) {
            musicState = 1;
//            System.out.println("touchdown music");
        }

        if ((screenX > soundX + soundButton.getWidth()/2 && screenX < soundX+soundButton.getWidth()) && (screenY > soundY+ soundButton.getHeight()/2  && screenY < soundY+soundButton.getHeight())) {
            soundState = 1;
//            System.out.println("touchdown sound");
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
        System.out.println("touchup");

        if (pressState == 1) {
            pressState = 2;
            return false;
        }
        if (quitState == 1) {
            quitState = 2;
            return false;
        }

        if (musicState == 1) {
            musicState = 2;
//            System.out.println("toggle music");
            if (isMusic) {
                isMusic = false;
//                System.out.println("ISMUSIC FALSE");
            }
            else {
                isMusic = true;
//                System.out.println("ISMUSIC TRUE");
                sound.stop("click_effect");
                sound.play("click_effect", "sounds/click_effect.wav", false, 0.7f, true);
            }
            return false;
        }
        if (soundState == 1) {
            soundState = 2;
//            System.out.println("toggle sound");
            if (isSound) {
                isSound = false;
            }
            else {
                isSound = true;
                sound.stop("click_effect");
                sound.play("click_effect", "sounds/click_effect.wav", false, 0.7f, true);
            }
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
