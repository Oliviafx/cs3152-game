package edu.cornell.gdiac.cityoflight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.ScreenListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class LevelController implements Screen, ControllerListener, ContactListener, InputProcessor, ApplicationListener {
    public class MyActor extends Actor {
        public float x;
        public float y;
        public float width;
        public float height;
        public MenuMode.MyListener listener;
        public MyActor(float x, float y, MenuMode.MyListener listen) {
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
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            hover = false;
        }

    }

    private class LevelSelect extends Stage {

    }
    private Texture background;
    private Texture title;

    private Texture locktex;

    private Texture menutex;
    private Texture menuhovtex;

    private Texture starttex;
    private Texture starthovtex;

    private Texture level1tex;
    private Texture level2tex;
    private Texture level3tex;
    private Texture level4tex;
    private Texture level5tex;
    private Texture level6tex;
    private Texture level7tex;
    private Texture level8tex;
    private Texture level9tex;
    private Texture level10tex;



    private Texture level1hover;
    private Texture level2hover;
    private Texture level3hover;
    private Texture level4hover;
    private Texture level5hover;
    private Texture level6hover;
    private Texture level7hover;
    private Texture level8hover;
    private Texture level9hover;
    private Texture level10hover;

    private ObstacleCanvas canvas;
    private static final String LEVEL_TITLE = "textures/level select assets/levels_title.png";
    private static final String LEVEL_BACKGROUND_FILE = "textures/level select assets/background.png";

    private static final String LOCK_FILE = "textures/level select assets/level_15.png";

    private static final String BACK_FILE = "textures/level select assets/menu_button.png";
    private static final String BACK_HOVER = "textures/level select assets/menu_hover.png";
    private static final String START_FILE = "textures/level select assets/start_button.png";
    private static final String START_HOVER = "textures/level select assets/start_hover.png";

    private static final String LEVEL_1 = "textures/level select assets/level_1.png";
    private static final String LEVEL_1_HOVER = "textures/level select assets/level_1_hover.png";
    private static final String LEVEL_2 ="textures/level select assets/level_2.png";
    private static final String LEVEL_2_HOVER ="textures/level select assets/level_2_hover.png";
    private static final String LEVEL_3_HOVER ="textures/level select assets/level_3_hover.png";
    private static final String LEVEL_3 = "textures/level select assets/level_3.png";
    private static final String LEVEL_4 ="textures/level select assets/level_4.png";
    private static final String LEVEL_4_HOVER ="textures/level select assets/level_4_hover.png";
    private static final String LEVEL_5 ="textures/level select assets/level_5.png";
    private static final String LEVEL_5_HOVER ="textures/level select assets/level_5_hover.png";
    private static final String LEVEL_6 ="textures/level select assets/level_6.png";
    private static final String LEVEL_6_HOVER ="textures/level select assets/level_6_hover.png";
    private static final String LEVEL_7 ="textures/level select assets/level_7.png";
    private static final String LEVEL_7_HOVER ="textures/level select assets/level_7_hover.png";
    private static final String LEVEL_8 ="textures/level select assets/level_8.png";
    private static final String LEVEL_8_HOVER ="textures/level select assets/level_8_hover.png";
    private static final String LEVEL_9 ="textures/level select assets/level_9.png";
    private static final String LEVEL_9_HOVER ="textures/level select assets/level_9_hover.png";
    private static final String LEVEL_10 ="textures/level select assets/level_10.png";
    private static final String LEVEL_10_HOVER ="textures/level select assets/level_10_hover.png";

    Sound startSound = Gdx.audio.newSound(Gdx.files.internal("sounds/select_effect.wav"));
    Sound menuSound = Gdx.audio.newSound(Gdx.files.internal("sounds/seen_effect.wav"));

    private ScreenListener listener;
    private boolean active;

    private float scale;

    private int pressState;
    private int backX = 250;
    private int backY = 35;
    private int startX = 350;
    private int startY = 35;
    private int startState;

    private float buttonOffX = 3;
    private float buttonOffY = 3;

    private float hoverOffX = 18;
    private float hoverOffY = 23;

    private boolean mhover;
    private boolean shover;

    private boolean hover1;
    private boolean hover2;
    private boolean hover3;
    private boolean hover4;
    private boolean hover5;
    private boolean hover6;
    private boolean hover7;
    private boolean hover8;
    private boolean hover9;
    private boolean hover10;

//    private DrawHelper drawHelper;
//    private static final String TRANSITION_FILE = "pip/transitions/general_transition_medium.png";
//    public FilmStrip transition_strip;

    public LevelController(ObstacleCanvas drawcanvas) {
        canvas = drawcanvas;
        background = new Texture(LEVEL_BACKGROUND_FILE);
//        menuButton = new Texture(BACK_FILE);
        active = false;
        pressState = 0;
        startState = 0;

//        drawHelper = new DrawHelper();
//        transition_strip = new FilmStrip((new Texture(TRANSITION_FILE)), 1, 36);
//        System.out.println("transition_strip = " + transition_strip);
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
    public boolean goLevelSeven() {
        return level7.isPressed();
    }
    public boolean goLevelEight() {
        return level8.isPressed();
    }
    public boolean goLevelNine() { return level9.isPressed(); }
    public boolean isReady() {
        return menubutton.isPressed();
    }

    public void setCanvas(ObstacleCanvas canvas) {
        this.canvas = canvas;
    }

    public void draw(){
        float scaling = 0.5f;
        if (active) {
//            if (drawHelper.get_general_transition_second_part()) {
                canvas.begin();
                if (background != null) {
                    canvas.draw(background, 0, 0);
                }
                if (title != null) {
                    canvas.draw(title, Color.WHITE, title.getWidth(), title.getHeight(),
                            canvas.getWidth()/2 + title.getWidth()/4, 480, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (mhover) {
                    canvas.draw(menuhovtex, Color.WHITE, menuhovtex.getWidth(), menuhovtex.getHeight(),
                            312 + menuhovtex.getWidth()/2 - 36, 65 + 20, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(menutex, Color.WHITE, menutex.getWidth(), menutex.getHeight(),
                            312 + menutex.getWidth()/2, 65, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (shover) {
                    canvas.draw(starthovtex, Color.WHITE, starthovtex.getWidth(), starthovtex.getHeight(),
                            503 + starthovtex.getWidth()/2 - 25, 65 + 20, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(starttex, Color.WHITE, starttex.getWidth(), starttex.getHeight(),
                            503 + starttex.getWidth()/2, 65, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }


                if (hover1) {
                    canvas.draw(level1hover, Color.WHITE, level1hover.getWidth(), level1hover.getHeight(),
                            98 + level1hover.getWidth()/2 - hoverOffX, 512-196 + level1hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(level1tex, Color.WHITE, level1.getWidth(), level1.getHeight(),
                            98 + level1.getWidth()/2 - buttonOffX, 512-196 + level1.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (hover2) {
                    canvas.draw(level2hover, Color.WHITE, level2hover.getWidth(), level2hover.getHeight(),
                            244 + level2hover.getWidth()/2 - hoverOffX, 512-196 + level2hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(level2tex, Color.WHITE, level2.getWidth(), level2.getHeight(),
                            244 + level2.getWidth()/2 - buttonOffX, 512-196 + level2.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (hover3) {
                    canvas.draw(level3hover, Color.WHITE, level3hover.getWidth(), level3hover.getHeight(),
                            395 + level3hover.getWidth()/2 - hoverOffX, 512-196 + level3hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(level3tex, Color.WHITE, level3.getWidth(), level3.getHeight(),
                            395 + level3.getWidth()/2 - buttonOffX, 512-196 + level2.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (hover4) {
                    canvas.draw(level4hover, Color.WHITE, level4hover.getWidth(), level4hover.getHeight(),
                            542 + level4hover.getWidth()/2 - hoverOffX + 2, 512-196 + level4hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(level4tex, Color.WHITE, level4.getWidth(), level4.getHeight(),
                            542 + level4.getWidth()/2 - buttonOffX + 2, 512-196 + level4.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (hover5) {
                    canvas.draw(level5hover, Color.WHITE, level5hover.getWidth(), level5hover.getHeight(),
                            695 + level5hover.getWidth()/2 - hoverOffX, 512-196 + level5hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(level5tex, Color.WHITE, level5.getWidth(), level5.getHeight(),
                            695 + level5.getWidth()/2 - buttonOffX, 512-196 + level5.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (hover6) {
                    canvas.draw(level6hover, Color.WHITE, level6hover.getWidth(), level6hover.getHeight(),
                            98 + level6hover.getWidth()/2 - hoverOffX, 512-306 + level6hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(level6tex, Color.WHITE, level6.getWidth(), level6.getHeight(),
                            98 + level6.getWidth()/2 - buttonOffX, 512-306 + level6.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (hover7) {
                    canvas.draw(level7hover, Color.WHITE, level7hover.getWidth(), level7hover.getHeight(),
                            244 + level7hover.getWidth()/2 - hoverOffX, 512-306 + level7hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(level7tex, Color.WHITE, level7.getWidth(), level7.getHeight(),
                            244 + level7.getWidth()/2 - buttonOffX, 512-306 + level7.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (hover8) {
                    canvas.draw(level8hover, Color.WHITE, level8hover.getWidth(), level8hover.getHeight(),
                            395 + level8hover.getWidth()/2 - hoverOffX, 512-306 + level8hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                else {
                    canvas.draw(level8tex, Color.WHITE, level8.getWidth(), level8.getHeight(),
                            395 + level8.getWidth()/2 - buttonOffX, 512-306 + level8.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                }
                if (locktex != null) {
                    canvas.draw(locktex, Color.WHITE, locktex.getWidth(), locktex.getHeight(),
                            542 + locktex.getWidth()/2 - buttonOffX, 512-306 + locktex.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                    canvas.draw(locktex, Color.WHITE, locktex.getWidth(), locktex.getHeight(),
                            695 + locktex.getWidth()/2 - buttonOffX, 512-306 + locktex.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                    canvas.draw(locktex, Color.WHITE, locktex.getWidth(), locktex.getHeight(),
                            98 + locktex.getWidth()/2 - buttonOffX, 512-416 + locktex.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                    canvas.draw(locktex, Color.WHITE, locktex.getWidth(), locktex.getHeight(),
                            244 + locktex.getWidth()/2 - buttonOffX, 512-416 + locktex.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                    canvas.draw(locktex, Color.WHITE, locktex.getWidth(), locktex.getHeight(),
                            395 + locktex.getWidth()/2 - buttonOffX, 512-416 + locktex.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                    canvas.draw(locktex, Color.WHITE, locktex.getWidth(), locktex.getHeight(),
                            542 + locktex.getWidth()/2 - buttonOffX, 512-416 + locktex.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
                    canvas.draw(locktex, Color.WHITE, locktex.getWidth(), locktex.getHeight(),
                            695 + locktex.getWidth()/2 - buttonOffX, 512-416 + locktex.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);

                }
//                if (hover9) {
//                    canvas.draw(level9hover, Color.WHITE, level9hover.getWidth(), level9hover.getHeight(),
//                            542 + level9hover.getWidth()/2 - hoverOffX + 2, 512-306 + level9hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
//                }
//                else {
//                    canvas.draw(level9tex, Color.WHITE, level9.getWidth(), level9.getHeight(),
//                            542 + level9.getWidth()/2 - buttonOffX + 2, 512-306 + level9.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
//                }
//            if (hover10) {
//                canvas.draw(level4hover, Color.WHITE, level4hover.getWidth(), level4hover.getHeight(),
//                        542 + level4hover.getWidth()/2 - hoverOffX, 512-196 + level4hover.getHeight()/2 - hoverOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
//            }
//            else {
//                canvas.draw(level4tex, Color.WHITE, level4.getWidth(), level4.getHeight(),
//                        542 + level4.getWidth()/2 - buttonOffX, 512-196 + level4.getHeight()/2 - buttonOffY, 0, scaling, scaling);//BUTTON_SCALE*scale, BUTTON_SCALE*scale);
//            }

//                if (backButton != null) {
//                    Color tint = (pressState == 1 ? Color.GRAY : Color.WHITE);
////            canvas.draw(backButton, tint, 0, 0, backX, backY, 0, .65f, .6f);
//                }
//        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        if (stage != null) {
//            stage.act(graphics.getDeltaTime());
//            stage.draw();
//        }
                canvas.end();
//            }
//            if (!drawHelper.get_general_transition_hasAnimated()) {
//                drawHelper.drawGeneralTransition2(canvas, transition_strip);
//            }
        }
    }
    public void update(float delta) {
        if (stage == null) {
            create();
        }
        if (title == null) {
            title = new Texture(LEVEL_TITLE);
            title.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (menutex == null) {
            menutex = new Texture(BACK_FILE);
            menutex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (menuhovtex == null) {
            menuhovtex = new Texture(BACK_HOVER);
            menuhovtex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (starttex == null) {
            starttex = new Texture(START_FILE);
            starttex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (starthovtex == null) {
            starthovtex = new Texture(START_HOVER);
            starthovtex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level1tex == null) {
            level1tex = new Texture(LEVEL_1);
            level1tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//            create();
        }
        if (level1hover == null) {
            level1hover = new Texture(LEVEL_1_HOVER);
            level1hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level2tex == null) {
            level2tex = new Texture(LEVEL_2);
            level2tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level2hover == null) {
            level2hover = new Texture(LEVEL_2_HOVER);
            level2hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level3tex == null) {
            level3tex = new Texture(LEVEL_3);
            level3tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level3hover == null) {
            level3hover = new Texture(LEVEL_3_HOVER);
            level3hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level4tex == null) {
            level4tex = new Texture(LEVEL_4);
            level4tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level4hover == null) {
            level4hover = new Texture(LEVEL_4_HOVER);
            level4hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level5tex == null) {
            level5tex = new Texture(LEVEL_5);
            level5tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level5hover == null) {
            level5hover = new Texture(LEVEL_5_HOVER);
            level5hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level6tex == null) {
            level6tex = new Texture(LEVEL_6);
            level6tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level6hover == null) {
            level6hover = new Texture(LEVEL_6_HOVER);
            level6hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level7tex == null) {
            level7tex = new Texture(LEVEL_7);
            level7tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level7hover == null) {
            level7hover = new Texture(LEVEL_7_HOVER);
            level7hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level8tex == null) {
            level8tex = new Texture(LEVEL_8);
            level8tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level8hover == null) {
            level8hover = new Texture(LEVEL_8_HOVER);
            level8hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level9tex == null) {
            level9tex = new Texture(LEVEL_9);
            level9tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (level9hover == null) {
            level9hover = new Texture(LEVEL_9_HOVER);
            level9hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        if (locktex == null) {
            locktex = new Texture(LOCK_FILE);
            locktex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
//        if (level6tex == null) {
//            level6tex = new Texture(LEVEL_6);
//            level6tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//        }
//        if (level6hover == null) {
//            level6hover = new Texture(LEVEL_6_HOVER);
//            level6hover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//        }

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
    private TextButton level7;
    private TextButton level8;
    private TextButton level9;
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
//                System.out.println("startSound play");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");

                startSound.play();
            }
        });
        level1.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover1 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover1 = false;
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
                startSound.play();
            }
        });
        level2.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover2 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover2 = false;
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
                startSound.play();
            }
        });

        level3.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover3 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover3 = false;
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
                startSound.play();
            }
        });
        level4.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover4 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover4 = false;
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
                startSound.play();
            }
        });
        level5.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover5 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover5 = false;
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
                startSound.play();
            }
        });
        level6.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover6 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover6 = false;
            }
        });

        level7 = new TextButton("", style);
        level7.setPosition(244, 512-306);
        level7.setHeight(100);
        level7.setWidth(100);
        level7.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("my app", "Released");
                startSound.play();
            }
        });
        level7.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover7 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover7 = false;
            }
        });

        level8 = new TextButton("", style);
        level8.setPosition(395, 512-306);
        level8.setHeight(100);
        level8.setWidth(100);
        level8.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                startSound.play();
            }
        });
        level8.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover8 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover8 = false;
            }
        });
        level9 = new TextButton("", style);
        level9.setPosition(542, 512-306);
        level9.setHeight(100);
        level9.setWidth(100);
        level9.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                startSound.play();
            }
        });
        level9.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                hover9 = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                hover9 = false;
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
                menuSound.play();
            }
        });
        menubutton.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                mhover = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                mhover = false;
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
                startSound.play();
            }
        });
        startbutton.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("enter??");
                shover = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("exit??");
                shover = false;
            }
        });

        stage.addActor(level1);
        stage.addActor(level2);
        stage.addActor(level3);
        stage.addActor(level4);
        stage.addActor(level5);
        stage.addActor(level6);
        stage.addActor(level7);
        stage.addActor(level8);
        stage.addActor(level9);
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
                listener.exitScreen(this, 1);
            }
            if ((goLevelOne() || goLevelTwo() || goLevelThree() || goLevelFour() || goLevelFive() || goLevelSix() ||
                    goLevelSeven() || goLevelEight() || goLevelNine())
                    && listener != null) {
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
        if (menutex != null){
            menutex.dispose();
            menutex = null;
        }
        if (menuhovtex != null){
            menuhovtex.dispose();
            menuhovtex = null;
        }
        if (starttex != null){
            starttex.dispose();
            starttex = null;
        }
        if (starthovtex != null){
            starthovtex.dispose();
            starthovtex = null;
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
//        menuButton = new Texture(BACK_FILE);
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
        if (menubutton == null || pressState == 2) {
//            System.out.println("pressState is 2");
            return true;
        }
//
//         Flip to match graphics coordinates
//        screenY = heightY-screenY;
//
//         TODO: Fix scaling
//         Play level1 is a circle.

        float radius = menubutton.getWidth()*menubutton.getHeight();
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
