package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.ScreenListener;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import edu.cornell.gdiac.util.SoundController;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class MenuMode implements Screen, ControllerListener, ContactListener, InputProcessor {

    public class MyActor extends Actor {
        public float x;
        public float y;
        public float width;
        public float height;
        public MyListener listener;
        public MyActor(float x, float y, MyListener listen) {
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
        public boolean getHover() { return hover; }
        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            System.out.println("enter");
            hover = true;
//            if (fromActor == play) {
////                System.out.println("play");
//                hoverplay = true;
//            }
//            else if (fromActor == level) {
////                System.out.println("level");
//                hoverlevel = true;
//            }
//            else if (fromActor == setting) {
//                hoversetting = true;
//            }
//            else if (fromActor == quit){
//                hoverquit = true;
//            }
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            hover = false;
//            if (toActor == play) {
//                hoverplay = false;
//            }
//            else if (toActor == level) {
//                hoverlevel = false;
//            }
//            else if (toActor == setting) {
//                hoversetting = false;
//            }
//            else if (toActor == quit){
//                hoverquit = false;
//            }
        }


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
    }
//
    private static final String BACKGROUND_FILE ="textures/menu assets/background_small.png";
    private static final String PLAY_BTN_FILE = "textures/menu assets/start_game.png";
    private static final String LEVEL_BTN_FILE = "textures/menu assets/level_select.png";
    private static final String TITLE_FILE ="textures/menu assets/title.png";
    private static final String HELP_FILE ="textures/menu assets/help.png";
    private static final String QUIT_FILE ="textures/menu assets/quit.png";
    private static final String LEVEL_HOVER_FILE ="textures/menu assets/level_select_hover.png";
    private static final String PLAY_HOVER ="textures/menu assets/start_game_hover.png";
    private static final String HELP_HOVER ="textures/menu assets/help_hover.png";
    private static final String QUIT_HOVER ="textures/menu assets/quit_hover.png";

//    private static final String START_SOUND = "sounds/select_effect.wav";
//    private static final String LEVEL_SOUND = "sounds/seen_effect.wav";
    Sound startSound = Gdx.audio.newSound(Gdx.files.internal("sounds/select_effect.wav"));
    Sound menuSound = Gdx.audio.newSound(Gdx.files.internal("sounds/seen_effect.wav"));
    Music menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu_music.mp3"));

    private Texture title;
    private Texture helpButton;
    private Texture quitButton;

    private Texture playHover;
    private Texture levelHover;
    private Texture helpHover;
    private Texture quitHover;

//    private Sound startSound;
//    private Sound levelSound;

    private MyActor play;
    private boolean hoverplay;
    private MyActor level;
    private boolean hoverlevel;
    private MyActor setting;
    private boolean hoverhelp;
    private MyActor quit;
    private boolean hoverquit;
    /** Play button to display when done */
    private Texture playButton;
    private Texture levelButton;
    /** Amount to scale the play button */
    private static float BUTTON_SCALE  = 0.75f;
    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;
    private InputListener inputlisten;
    /** The current state of the play button */
    private int pressState;
    private int levelState;
    private int helpState;
    private int quitState;
    /** Support for the X-Box start button in place of play button */
    private int   startButton;
    /** Whether or not this player mode is still active */
    private boolean active;

    private DrawHelper drawHelper;
    private static final String TRANSITION_FILE = "pip/transitions/general_transition_medium.png";
    public FilmStrip transition_strip;

    private Game parent;

    private GameController gameController;
    public void setGameController(GameController val) {
        gameController = val;
    }

    /** Background texture for start-up */
    private Texture background;
    /** Standard window size (for scaling) */
    private static int STANDARD_WIDTH  = 800;
    /** Standard window height (for scaling) */
    private static int STANDARD_HEIGHT = 700;

    private boolean menuPlay = true;

    ObstacleCanvas drawcanvas;

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
    private float helpX;
    private float helpY;
    private float quitX;
    private float quitY;

    private int buttonWidth;
    private int buttonHeight;
    private float hoverOffX;
    private float hoverOffY;

    LoadingMode loading;

    private Stage stage;
    private Table table;
    private SpriteBatch batch;
    private BitmapFont font; //** same as that used in Tut 7 **//
    private TextButton start; //** the level1 - the only actor in program **//
    private TextButton levels;
    private TextButton help;
    private TextButton quitTest;

    SoundController sound = SoundController.getInstance();

    public boolean getHover(MyActor actor) {
        if (actor != null) {
            return actor.getHover();
        }
        return false;
    }

    public Music getMenuMusic() {
        return menuMusic;
    }

    public MenuMode(ObstacleCanvas canvas, Game parent) {
        drawcanvas = canvas;
        background = new Texture(BACKGROUND_FILE);
        title = new Texture(TITLE_FILE);

        pressState = 0;
        levelState = 0;
        helpState = 0;
        quitState = 0;
        playButton = null;
        levelButton = null;
        helpButton = null;
        quitButton = null;
        active = false;
        this.parent = parent;

//        startSound = null;
//        levelSound = null;

        playX = 600;
        playY = 300;
        levelX = 600;
        levelY = 250;
        helpX = 600;
        helpY = 200;
        quitX = 600;
        quitY = 150;

        buttonWidth = 180;
        buttonHeight = 45;
        hoverOffX = 80;
        hoverOffY = 20;

//        System.out.println(menuMusic);

        drawHelper = new DrawHelper();
        transition_strip = new FilmStrip((new Texture(TRANSITION_FILE)), 1, 36);
//        System.out.println("transition_strip = " + transition_strip);

//        level = new MyActor(levelX, levelY, new MyListener());
//        setting = new MyActor(helpX,helpY,new MyListener());
//        quit = new MyActor(quitX,quitY,new MyListener());
//
//        level.setPosition(levelX, levelY);
//        setting.setPosition(helpX, helpY);
//        quit.setPosition(quitX, quitY);

//        level.addListener(new MyListener());
//        setting.addListener(new MyListener());
//        setting.addListener(new MyListener());
//        loading = new LoadingMode(drawcanvas);
        Gdx.input.setInputProcessor(this);

//        for(Controller controller : Controllers.getControllers()) {
//            controller.addListener(this);
//        }

    }

    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        font = new BitmapFont(false);
        font.setColor(Color.CLEAR);
        batch = new SpriteBatch();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        start = new TextButton("", style);
        start.setPosition(playX - hoverOffX, playY - hoverOffY);
        start.setHeight(buttonHeight);
        start.setWidth(buttonWidth);
        start.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                System.out.println("touchdown");
                pressState = 2;
                menuMusic.stop();
                menuPlay = false;
                startSound.play();
                return false;
            }
        });
        start.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hoverplay = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hoverplay = false;
            }
        });

        levels = new TextButton("", style);
        levels.setPosition(levelX - hoverOffX, levelY - hoverOffY);
        levels.setHeight(buttonHeight);
        levels.setWidth(buttonWidth);
        levels.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                System.out.println("touchdown");
                levelState = 2;
//                menuMusic.stop();
//                menuPlay = false;
                menuSound.play();
                return false;
            }
        });
        levels.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hoverlevel = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hoverlevel = false;
            }
        });

        help = new TextButton("", style);
        help.setPosition(helpX - hoverOffX, helpY - hoverOffY);
        help.setHeight(buttonHeight);
        help.setWidth(buttonWidth);
        help.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                System.out.println("touchdown");
                helpState = 2;
//                menuMusic.stop();
//                menuPlay = false;
                menuSound.play();
                return false;
            }
        });
        help.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hoverhelp = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hoverhelp = false;
            }
        });

        quitTest = new TextButton("", style);
        quitTest.setPosition(quitX - hoverOffX, quitY - hoverOffY);
        quitTest.setHeight(buttonHeight);
        quitTest.setWidth(buttonWidth);
        quitTest.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                System.out.println("touchdown");
                quitState = 2;
//                menuMusic.stop();
//                menuPlay = false;
//                menuSound.play();
                return false;
            }
        });
        quitTest.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hoverquit = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hoverquit = false;
            }
        });

        stage.addActor(start);
        stage.addActor(levels);
        stage.addActor(help);
        stage.addActor(quitTest);
//        stage = new Stage();
//        if (play == null) {
//            play = new MyActor(playX,playY, new MyListener());
//            play.setPosition(playX, playY);
//            play.setWidth(playButton.getWidth());
//            play.setHeight(playButton.getHeight());
//            play.setTouchable(Touchable.enabled);
//            play.addListener(play.listener);
//            stage.addActor(play);


        }


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

    public boolean toHelp() {
        return helpState == 2;
    }

    public boolean didQuit() {
        return quitState == 2;
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
        if (helpButton != null) {
            helpButton.dispose();
            helpButton = null;
        }
        if (quitButton != null) {
            quitButton.dispose();
            quitButton = null;
        }
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.dispose();
            menuMusic = null;
        }
        //        stage.dispose();
    }

    public void resize(int width, int height) {
        // Compute the drawing scale
        float sx = ((float)width)/STANDARD_WIDTH;
        float sy = ((float)height)/STANDARD_HEIGHT;
        scale = (sx < sy ? sx : sy);

        this.width = (int)(.66f*width);
//        playY = (int)(.25f*height);
//        playX = width/2;
//        levelY = (int)(.25f*height);
//        levelX = width/2;
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

    public void setInputListener(InputListener listener) {
        this.inputlisten = listener;
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
        if (stage != null) { stage.act(); }

        if (isReady() && listener != null) {
            menuMusic.stop();
            System.out.println("is ready?");
            listener.exitScreen(this, 2);
        }
        if (toLevelSelect() && listener != null) {
            listener.exitScreen(this, 3);
        }

        if (toHelp() && listener != null) {
            listener.exitScreen(this, 5);
        }

        if (didQuit()) {
            menuMusic.stop();
            listener.exitScreen(this, 0);
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
            create();
        }
        if (playHover == null) {
            playHover = new Texture(PLAY_HOVER);
            playHover.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        }
        if (levelButton == null) {
            levelButton = new Texture(LEVEL_BTN_FILE);
            levelButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//            level.setWidth(levelButton.getWidth());
//            level.setHeight(levelButton.getHeight());

        }
        if (levelHover == null) {
            levelHover = new Texture(LEVEL_HOVER_FILE);
            levelHover.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//            create();
        }
        if (helpButton == null) {
            helpButton = new Texture(HELP_FILE);
            levelButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//            setting.setWidth(helpButton.getWidth());
//            setting.setHeight(helpButton.getHeight());
        }
        if (helpHover == null) {
            helpHover = new Texture(HELP_HOVER);
            helpHover.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//            create();
        }
        if (quitButton == null) {
            quitButton = new Texture(QUIT_FILE);
            quitButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//            quit.setWidth(quitButton.getWidth());
//            quit.setHeight(quitButton.getHeight());
        }
        if (quitHover == null) {
            quitHover = new Texture(QUIT_HOVER);
            quitHover.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//            create();
        }

        if (!menuMusic.isPlaying() && menuPlay) {
            menuMusic.play();
        }
        else if (menuMusic.isPlaying() && !menuPlay) {
            menuMusic.stop();
            System.out.println(menuMusic.isPlaying());
        }

    }


    public void draw() {
        if (drawcanvas != null) {
            if (drawHelper.get_general_transition_second_part()) {
                drawcanvas.begin();
                drawcanvas.draw(background, 0, 0);
                drawcanvas.draw(title, 480, 350);
//        Color tint = (pressState == 1 ? Color.GRAY: Color.WHITE);
//            if (getHover(play)) {
                if (hoverplay == true) {
//                    drawcanvas.draw(playHover, Color.WHITE, playButton.getWidth() / 2, playButton.getHeight() / 2,
//                            playX - playButton.getWidth() / 5, playY - playButton.getHeight() / 3, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                    drawcanvas.draw(playHover, Color.WHITE, playHover.getWidth() / 2, playHover.getHeight() / 2,
                            playX, playY + 5, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                } else {
                    drawcanvas.draw(playButton, Color.WHITE, playButton.getWidth() / 2, playButton.getHeight() / 2,
                            playX, playY, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                }
//            if (hoverlevel) {
                if (hoverlevel == true) {
//                    drawcanvas.draw(levelHover, Color.WHITE, levelButton.getWidth() / 2, levelButton.getHeight() / 2,
//                            levelX - levelButton.getWidth() / 6, levelY - levelButton.getHeight() / 2, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                    drawcanvas.draw(levelHover, Color.WHITE, levelHover.getWidth() / 2, levelHover.getHeight() / 2,
                            levelX, levelY + 5, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                } else {
                    drawcanvas.draw(levelButton, Color.WHITE, levelButton.getWidth() / 2, levelButton.getHeight() / 2,
                            levelX, levelY, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                } //        Color tint2 = (levelState == 1 ? Color.GRAY: Color.WHITE);
//            if (hoversetting) {
                if (hoverhelp == true) {
                    drawcanvas.draw(helpHover, Color.WHITE, helpHover.getWidth() / 2, helpHover.getHeight() / 2,
                            helpX, helpY + 15, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
//                    drawcanvas.draw(helpHover, Color.WHITE, helpButton.getWidth() / 2, helpButton.getHeight() / 2,
//                            helpX - helpButton.getWidth() + 25, helpY - helpButton.getHeight() / 3, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                } else {
                    drawcanvas.draw(helpButton, Color.WHITE, helpButton.getWidth() / 2, helpButton.getHeight() / 2,
                            helpX, helpY, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                }
//            if (hoverquit) {
                if (hoverquit == true) {
//                    drawcanvas.draw(quitHover, Color.WHITE, quitButton.getWidth() / 2, quitButton.getHeight() / 2,
//                            quitX - quitButton.getWidth(), quitY - quitButton.getHeight() / 4, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                    drawcanvas.draw(quitHover, Color.WHITE, quitHover.getWidth() / 2, quitHover.getHeight() / 2,
                            quitX, quitY + 10, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);

                } else {
                    drawcanvas.draw(quitButton, Color.WHITE, quitButton.getWidth() / 2, quitButton.getHeight() / 2,
                            quitX, quitY, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
                }
                drawcanvas.end();
            }
            if (!drawHelper.get_general_transition_hasAnimated()) {
                drawHelper.drawGeneralTransition(drawcanvas, transition_strip);
            }
        }
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
        if (helpButton == null || helpState == 2) {
            return true;
        }
        if (quitButton == null || quitState == 2) {
            return true;
        }

        // Flip to match graphics coordinates
        screenY = heightY-screenY;

        // TODO: Fix scaling
        // Play button is a circle.
//        float radius = BUTTON_SCALE*scale*playButton.getWidth()/2.0f;
        float dist = (screenX-playX)*(screenX-playX)+(screenY-playY)*(screenY-playY);
        if (dist < playButton.getWidth()*playButton.getHeight()*.75f && screenY > levelY +levelButton.getHeight()) {
            pressState = 1;
        }

        float dist2 = (screenX-levelX)*(screenX-levelX)+(screenY-levelY)*(screenY-levelY);
        if (dist2 < levelButton.getWidth()*levelButton.getHeight()*.75f && screenY < playY - playButton.getHeight() &&
                screenY >= levelY-20) {
            levelState = 1;
        }

        float dist3 = (screenX- helpX)*(screenX- helpX)+(screenY- helpY)*(screenY- helpY);
        if (dist3 < helpButton.getWidth()* helpButton.getHeight() && screenY < levelY - levelButton.getHeight()
                && screenY >= helpY) {
            helpState = 1;
        }
        float dist4 = (screenX-quitX)*(screenX-quitX)+(screenY-quitY)*(screenY-quitY);
        if (dist4 < quitButton.getHeight()*quitButton.getWidth() && screenY < helpY - helpButton.getHeight()) {
            quitState = 1;
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
//            sound.stop("select_effect");
//            System.out.println("stop");
            menuMusic.stop();
//            System.out.println("stop???");
            menuPlay = false;
            startSound.play();
//            System.out.println(sound.play("select_effect", "sounds/select_effect.wav", false, 1.0f, true));;
            return false;
        }
        if (levelState == 1) {
            levelState = 2;
//            sound.stop("seen_effect");
//            System.out.println(sound.play("seen_effect", "sounds/seen_effect.wav", false, 1.0f, true));
            menuSound.play();
            return false;
        }
        if (helpState == 1) {
            helpState = 2;
//            sound.stop("seen_effect");
//            System.out.println(sound.play("seen_effect", "sounds/seen_effect.wav", false, 1.0f, true));
            menuSound.play();
            return false;
        }
        if (quitState == 1) {
            quitState = 2;
//            sound.stop("seen_effect");
//            System.out.println(sound.play("seen_effect", "sounds/seen_effect.wav", false, 1.0f, true));
            menuMusic.stop();
            menuPlay = false;
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
