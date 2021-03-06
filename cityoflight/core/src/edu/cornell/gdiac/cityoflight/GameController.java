/*
 * GameController.java
 *
 * This combines the WorldController with the mini-game specific PlatformController
 * in the last lab.  With that said, some of the work is now offloaded to the new
 * LevelModel class, which allows us to serialize and deserialize a level.
 *
 * This is a refactored version of WorldController from Lab 4.  It separate the
 * level out into a new class called LevelModel.  This model is, in turn, read
 * from a JSON file.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * B2Lights version, 3/12/2016
 */
package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.util.*;

import edu.cornell.gdiac.physics.obstacle.*;

/**
 * Gameplay controller for the game.
 *
 * This class does not have the Box2d world.  That is stored inside of the
 * LevelModel object, as the world settings are determined by the JSON
 * file.  However, the class does have all of the controller functionality,
 * including collision listeners for the active level.
 *
 * You will notice that asset loading is very different.  It relies on the
 * singleton asset manager to manage the various assets.
 */
public class GameController implements Screen, ContactListener {
	/**
	 * Tracks the asset state.  Otherwise subclasses will try to load assets
	 */
	protected enum AssetState {
		/** No assets loaded */
		EMPTY,
		/** Still loading assets */
		LOADING,
		/** Assets are complete */
		COMPLETE
	}

	/** The reader to process JSON files */
	private JsonReader jsonReader;
	/** The JSON asset directory */
	private JsonValue  assetDirectory;
	/** The JSON defining the level model */
	private JsonValue  levelFormat;

	/** The DrawHelper to help with drawing transitions, screens, and indicators. */
	private DrawHelper drawHelper;

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;
	protected BitmapFont textFont;

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

	/** Offset for box when summoning */
	private static final float  BOX_HOFFSET = 0.65f;
	private static final float  BOX_VOFFSET = 0.5f;
	public static final float	TEMP_SCALE	= 0.5f;

	/** Walk in place effective range */
	public float WALK_IN_PLACE_EFFECTIVE_RANGE = 20.0f;

	private int LEVEL_TIME_LIMIT = 7000;
	private boolean daredevil;
	private boolean daredevilSet = false;
	private boolean hasUsedBox = false;
	private boolean hasUsedDistraction = false;
	private boolean hasUsedDistractionEffectively = false;
	private boolean hasUsedWalking = false;
	private boolean hasUsedWalkingEffectively = false;

	private boolean endSoundhasPlayed = false;

	private PauseMode pause;
	private MenuMode menu;

	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 *
	 */
	public void preLoadContent() {
		if (assetState != AssetState.EMPTY) {
			return;
		}

		assetState = AssetState.LOADING;

		jsonReader = new JsonReader();
		assetDirectory = jsonReader.parse(Gdx.files.internal("jsons/assets.json"));

		JsonAssetManager.getInstance().loadDirectory(assetDirectory);
	}

	/**
	 * Load the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 *
	 */
	public void loadContent() {
		if (assetState != AssetState.LOADING) {
			return;
		}

		JsonAssetManager.getInstance().allocateDirectory();
		displayFont = JsonAssetManager.getInstance().getEntry("display", BitmapFont.class);
		textFont = JsonAssetManager.getInstance().getEntry("text", BitmapFont.class);
		assetState = AssetState.COMPLETE;
	}


	/**
	 * Unloads the assets for this game.
	 *
	 * This method erases the static variables.  It also deletes the associated textures
	 * from the asset manager. If no assets are loaded, this method does nothing.
	 *
	 */
	public void unloadContent() {
		JsonAssetManager.getInstance().unloadDirectory();
		JsonAssetManager.clearInstance();
	}


	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	/** How many frames after winning/losing do we continue? */
	public static final int EXIT_COUNT = 200;
	/** Exit code for going to the main menu screen */
	public static final int EXIT_MENU = 1;
	/** Exit code for going to the game screen */
	public static final int EXIT_PLAY = 2;
	/** Exit code for going to the level select screen */
	public static final int EXIT_LEVEL = 3;
	/** Exit code for going to the pause menu */
	public static final int EXIT_PAUSE = 4;
	/** Exit code for going to the help menu */
	public static final int EXIT_HELP = 5;
	/** Exit code for going to the control menu screen*/
	public static final int EXIT_CONTROLS = 6;
	/** Exit code for going to the control menu screen*/
	public static final int EXIT_CREATURES = 7;
	/** Which level to load */
	public int whichlevel;
	public void setWhichLevel(int val) {
		whichlevel = val;
	}
	private int numLevels;

	/** Reference to the game canvas */
	protected ObstacleCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** Reference to the game level */
	protected LevelModel level;

	/** Reference to the AIControllers */
	private Array<AIController> AIcontrollers = new Array<AIController>();

	private SoundController sound;

	private Music bgm;
	private Music det_bgm;

	/** Whether or not this is an active controller */
	private boolean active;
	/** Whether we have completed this level */
	private boolean complete;
	/** Whether we have failed at this world (and need a reset) */
	private boolean failed;
	/** Countdown active for winning or losing */
	private int countdown;

	private float dist;

	private boolean downBox = false;
	private boolean upBox = false;
	private boolean rightBox = false;
	private boolean leftBox = false;

	private boolean musicPlay = true;
	private boolean detectedPlay = false;
	private boolean soundPlay = true;

	public void setMenu(MenuMode val) {
		menu = val;
	}

	public Music getBGM() {
		return bgm;
	}

	public Music getDet_bgm() {
		return det_bgm;
	}

	public void setMusicPlay(boolean val) { musicPlay = val; }
	public void setSoundPlay(boolean val) { soundPlay = val; }

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;

	/**
	 * Returns true if the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @return true if the level is completed.
	 */
	public boolean isComplete( ) {
		return complete;
	}

	/**
	 * Sets whether the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @param value whether the level is completed.
	 */
	public void setComplete(boolean value) {
		if (value) {
			countdown = EXIT_COUNT + 200;
		}
		complete = value;
	}

	/**
	 * Returns true if the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @return true if the level is failed.
	 */
	public boolean isFailure( ) {
		return failed;
	}

	/**
	 * Sets whether the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @param value whether the level is failed.
	 */
	public void setFailure(boolean value) {
		if (value && !failed) {
			countdown = EXIT_COUNT;
		}
		failed = value;
	}

	/**
	 * Returns true if this is the active screen
	 *
	 * @return true if this is the active screen
	 */
	public boolean isActive( ) {
		return active;
	}

	/**
	 * Returns the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers
	 *
	 * @return canvas associated with this controller
	 */
	public ObstacleCanvas getCanvas() {
		return canvas;
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
		this.canvas = canvas;
	}

	/**
	 * Returns the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers
	 *
	 * @return canvas associated with this controller
	 */


	/**
	 * Creates a new game world
	 *
	 * The physics bounds and drawing scale are now stored in the LevelModel and
	 * defined by the appropriate JSON file.
	 */
	public GameController() {
		jsonReader = new JsonReader();
		level = new LevelModel();
		complete = false;
		failed = false;
		active = false;
		countdown = -1;
		sound = SoundController.getInstance();
		bgm = Gdx.audio.newMusic(Gdx.files.internal("music/120bpm_music.mp3"));
		det_bgm = Gdx.audio.newMusic(Gdx.files.internal("music/120bpm_detected_music.mp3"));
		bgm.setLooping(true);
		bgm.setVolume(0.6f);
		det_bgm.setLooping(true);
		det_bgm.setVolume(0.0f);
		drawHelper = new DrawHelper();

		setComplete(false);
		setFailure(false);
	}

	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		level.dispose();
		level  = null;
		canvas = null;
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the level and creates a new one. It will
	 * reread from the JSON file, allowing us to make changes on the fly.
	 */
	public void reset() {

		level.dispose();

		AIcontrollers.clear();
		setComplete(false);
		setFailure(false);
		countdown = -1;
		// Reload the json each time
		if (whichlevel == 2) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Level1_5.json"));
		}
		if (whichlevel == 3) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Level2_5.json"));
		}
		if (whichlevel == 4) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Level3_5.json"));
		}
		if (whichlevel == 5) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Level4_5.json"));
		}
		if (whichlevel == 6) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Level5_5.json"));
		}
		if (whichlevel == 7) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/lvl_num.json"));
		}
		if (whichlevel == 8) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Emmalevel.json"));
		}
		if (whichlevel == 9) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Level10.json"));
		}
		if (whichlevel == 10) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Level11.json"));
		}
		if (whichlevel == 1) {
			levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Level0.json"));
		}
		// CHANGE THIS IF YOU ADD LEVELS
		numLevels = 10;

		level.populate(levelFormat);
		level.getWorld().setContactListener(this);

		if (level.getDistraction() != null) {
			level.getDistraction().setAlive(false);
//			level.getDistraction().deactivatePhysics(level.getWorld());
//			level.getDistraction().dispose();
			level.objects.remove(level.getDistraction());
		}
		drawHelper.reset();

		level.resetAchievements();
		LEVEL_TIME_LIMIT = 1200;
		endSoundhasPlayed = false;
		daredevilSet = false;
	}

	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * @param dt Number of seconds since last animation frame
	 *
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		InputController input = InputController.getInstance();
		input.readInput();
//		if (menu.menuMusic!=null) {
//			menu.menuMusic.stop();
//		}
		if (listener == null) {
			return true;
		}

		// Toggle debug
		if (input.didDebug()) {
			level.setDebug(!level.getDebug());
		}

		// Handle resets
		if (input.didReset()) {
			reset();
		}

		if (complete){
			if (input.didSpace()){
				countdown = 20;
			}
		}

		if (input.didMute()) {
			bgm.stop();
			det_bgm.stop();
			for (String s : sound.getCollection()) {
				sound.stop(s);
			}
			if (musicPlay){	musicPlay = false; }
			else { musicPlay = true; }
			if (soundPlay){	soundPlay = false; }
			else { soundPlay = true; }
//			System.out.println("muted");
		}

		// Now it is time to maybe switch screens.
		if (input.didExit()) {
//			bgm.stop();
//			det_bgm.stop();
			listener.exitScreen(this, EXIT_PAUSE);
			return false;
		}
		else if (input.didPause()) {
			listener.exitScreen(this, EXIT_PAUSE);
			return false;
		}
		else if (countdown > 0) {
			countdown--;
		} else if (countdown == 0) {
			if (complete) {
				if (whichlevel < numLevels) {
					whichlevel++;
				}
				else {
//					whichlevel = 1;
					bgm.stop();
					det_bgm.stop();
					listener.exitScreen(this, EXIT_MENU);
					return false;
				}
			}
			reset();

		}

		return true;
	}

	private Vector2 aAngleCache = new Vector2();
	private Vector2 dAngleCache = new Vector2();


	/** Update loop for pause menu */
	public void updatePause(float delta) {


	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void update(float dt) {
		if (!complete) {
			LEVEL_TIME_LIMIT--;
		}
		// Process actions in object model
		AnnetteModel annette = level.getAnnette();

		BoxModel box = level.getBox();
		DistractionModel distraction = level.getDistraction();
		InputController input = InputController.getInstance();

		float xoff = 0;
		float yoff = 0;

		if (!musicPlay) {
			bgm.pause();
			det_bgm.pause();
		}
		else {
			if (!bgm.isPlaying()) {
				bgm.play();
				det_bgm.play();
			}
			if (detectedPlay && !complete) {
				det_bgm.setVolume(0.3f);
				bgm.setVolume(0.0f);
				det_bgm.setVolume(0.6f);
			}
			else {
//				System.out.println("normal music");
				bgm.setVolume(0.3f);
				det_bgm.setVolume(0.0f);
				bgm.setVolume(0.6f);
			}
		}

		// creature AI.
        for (CreatureModel c : level.getCreature()) {
		    if (distraction != null) {
		        if (distraction.getX() > c.getX() && distraction.getX() < c.getX()+c.getWidth() &&
                        distraction.getY() > c.getY() && distraction.getY() < c.getY()+c.getHeight()) {
		            c.setDistracted(true);
		            distraction.setSeen(true);
                }
            }
        }

		createAIControllers();

		for (AIController controller : AIcontrollers){
			controller.chooseAction();
			controller.doAction();

			if (controller.isDistracted()) {
				distraction.setSeen(true);
			}
		}


		// Rotate the avatar to face the direction of movement
		aAngleCache.set(input.getaHoriz(),input.getaVert());

		//set walking in place
		if (whichlevel != 1 && whichlevel != 2)
		annette.setWalkingInPlace(InputController.getInstance().didHoldShift());
		if (annette.isWalkingInPlace()) {
			hasUsedWalking = true;
			sound.play("ambient_effect", "sounds/ambient_effect.wav", true, 0.1f, soundPlay);
		}
		else { sound.stop("ambient_effect"); }
		aAngleCache.scl(annette.getForce());
		annette.setDirection(input.getDirection());
		if (!complete && !failed && whichlevel!= 1) {
			annette.setSummoning(InputController.getInstance().didSpace());
		}
		if (!complete && !failed && whichlevel != 1 && whichlevel != 2 && whichlevel != 3) {
			annette.setBird(input.didX());
		}
		annette.applyForce();

		//Check if distraction was called
		if (annette.getBird()&&!level.isDistraction() && !isFailure()) {
			level.createDistraction(levelFormat);
			hasUsedDistraction = true;
			sound.play("distraction_effect", "sounds/distraction_effect.wav", false, 0.2f, soundPlay);
			level.getDistraction().setAlive(true);
			dAngleCache.set(input.getaHoriz(),input.getaVert());

			if (dAngleCache.len2() > 0.0f) {
				float angle = aAngleCache.angle();
				// Convert to radians with up as 0
				angle = (float)Math.PI*(angle-90.0f)/180.0f;

			}
			if (distraction != null) {// && !distraction.isSeen()) {
				dAngleCache.scl(distraction.getForce());
				distraction.setMovement(dAngleCache.x,dAngleCache.y);
			}
		}

		if (distraction != null) {
//			System.out.println(level.objects.contains(distraction));
			if (!distraction.getAlive() && distraction.isActive()) {
				sound.play("distraction_gone_effect", "sounds/distraction_gone_effect.wav", false, 1.0f, soundPlay);
//				distraction.deactivatePhysics(level.getWorld());
				distraction.dispose();
				level.objects.remove(distraction);
			}
		}

		// creature AI.
//		createAIControllers();
//
//		for (AIController controller : AIcontrollers){
//			controller.chooseAction();
//			controller.doAction();
//		}

		box.setDrawScale(level.scale);
		if (annette.isSummoning() && !box.getDoesExist()) {
			boolean canBox;
			// get direction annette is facing
			switch (annette.getDirection()) {
				case RIGHT:
					xoff = BOX_HOFFSET;
					canBox = rightBox;
					break;
				case LEFT:
					xoff = -BOX_HOFFSET;
					canBox = leftBox;
					break;
				case UP:
					yoff = BOX_VOFFSET;
					canBox = upBox;
					break;
				case DOWN:
					yoff = -BOX_VOFFSET;
					canBox = downBox;
					break;
				default: {
					xoff = 0;
					yoff = 0;
					canBox = false;
					break;
				}
			}
			// TODO: REMOVE THIS LINE!
			canBox = true;
			if (canBox) {
				try {
					box.initialize(levelFormat, annette.getPosition(), xoff, yoff);
				}
				catch (Exception e) {
					box = new BoxModel(1, 1);
					level.setBox(box);
					box.setDrawScale(level.scale);
					box.initialize(levelFormat, annette.getPosition(), xoff, yoff);
				}
				level.activate(box);
				box.setActive(true);
				box.setDoesExist(true);
				box.setDeactivated(false);
				box.setDeactivating(false);
				hasUsedBox = true;
				sound.play("box_effect", "sounds/box_effect.wav", false, 0.8f, soundPlay);
			}
			else {
				sound.stop("no_box_effect");
				sound.play("no_box_effect", "sounds/no_box_effect.wav", false, 0.75f, soundPlay);
			}
		}
		else if (annette.isSummoning() && box.getDoesExist()) {
			if (dist  <= BoxModel.INNER_RADIUS){
				if (annette.isSummoning() && box.getDoesExist()) {
					box.setDoesExist(false);
					box.deactivatePhysics(level.getWorld());
					box.dispose();
					level.objects.remove(box);
				}
			}
			else {
				sound.stop("no_box_effect");
				sound.play("no_box_effect", "sounds/no_box_effect.wav", false, 0.75f, soundPlay);
			}
		}
		box.applyForce();

		dist = (float)Math.hypot(Math.abs(box.getPosition().x - annette.getPosition().x), Math.abs(box.getPosition().y - annette.getPosition().y));

		// box is deactivatING
		if (box.getDoesExist() && !box.getDeactivated() && dist > BoxModel.INNER_RADIUS){
			box.setDeactivating(true);

		}

		// box is deactivatED
		if (box.getDoesExist() && !box.getDeactivated() && dist > BoxModel.OUTER_RADIUS){
			box.setDeactivated(true);
			box.deactivate();
			sound.stop("box_deactivate_effect");
			sound.play("box_deactivate_effect", "sounds/box_deactivate_effect.wav", false, 0.5f, soundPlay);
		}

		// box is GONE
		if (box.getDoesExist() && dist > BoxModel.GONE_RADIUS){
			box.setDoesExist(false);
			box.deactivatePhysics(level.getWorld());
			box.dispose();
			level.objects.remove(box);
			if (level.getAchievementType1() == 4){level.setGetAchievement1(true);}
			if (level.getAchievementType2() == 4){level.setGetAchievement2(true);}
			sound.stop("box_gone_effect");
			sound.play("box_gone_effect", "sounds/box_gone_effect.wav", false, 0.5f, soundPlay);
		}



		// set debug colors
		if (box.getDeactivated()) box.setDebugColor(Color.RED);
		else if (box.getDeactivating()) box.setDebugColor(Color.YELLOW);
		else box.setDebugColor(Color.GREEN);


		if(annette.isWalkingInPlace() && !annette.getBird()){
			level.getRadiusOfPower().setActive(true);
			level.darkenLights(level.getRayHandler());
			if (box.getDoesExist() && box.getPosition().sub(annette.getPosition()).len2() <= WALK_IN_PLACE_EFFECTIVE_RANGE ) {
				box.setVelocity(input.getbHoriz(), input.getbVert());
			}
			annette.setMovement(0,0);

		} else{
			level.getRadiusOfPower().setActive(false);
			level.brightenLights(level.getRayHandler());
			drawHelper.setWalkHasAnimatedFalse();
			if (drawHelper.getIndicator_out() != null){
				drawHelper.getIndicator_out().setFrame(0);
			}
			annette.setMovement(aAngleCache.x,aAngleCache.y);
		}

		// Turn the physics engine crank.
		level.update(dt);
		sound.update();
	}

	/**
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself.  It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 *
	 * @param delta
	 */
	public void draw(float delta) {

		canvas.clear();
		level.draw(canvas);

		if (level.getAnnette().isWalkingInPlace()){
			 drawHelper.drawWalkInPlace(canvas,level);
		}

		for (AIController controller : AIcontrollers){
			if(controller.isChasing()) {
					detectedPlay = true;
				drawHelper.drawisSeen(canvas,level);
			}
		}

		if (noOneSeesMe()){
			//System.out.println("in seen reset");
			detectedPlay = false;
			drawHelper.setSeenHasAnimatedFalse();
			if (drawHelper.getIndicator_seen() != null){
				drawHelper.getIndicator_seen().setFrame(0);
			}
		}

		drawHelper.drawTutorial(canvas,level,whichlevel,detectedPlay);

		// Final message
		if (complete) {

			//calcaulating achievements
			if (LEVEL_TIME_LIMIT <= 0){
				if (level.getAchievementType1() == 3){ level.setGetAchievement1(false);}
				if (level.getAchievementType2() == 3){ level.setGetAchievement2(false);}
			}

			if (!daredevilSet) {
				daredevil = detectedPlay;
				daredevilSet = true;
			}
			if(level.getAchievementType1() == 2 ){level.setGetAchievement1(daredevil);}
			if(level.getAchievementType2() == 2 ){level.setGetAchievement2(daredevil);}

			effectiveUsageofBird();
			effectiveUsageofWalk();

			if (hasUsedBox && hasUsedWalkingEffectively && hasUsedDistractionEffectively){
				if (level.getAchievementType1() == 5){level.setGetAchievement1(true);}
				if (level.getAchievementType2() == 5){level.setGetAchievement2(true);}
			}

			if (!((!hasUsedBox && !hasUsedDistraction) || (!hasUsedBox && !hasUsedWalking) || (!hasUsedDistraction && !hasUsedWalking))){
				if (level.getAchievementType1() == 6){level.setGetAchievement1(false);}
				if (level.getAchievementType2() == 6){level.setGetAchievement2(false);}
			}
			if (!endSoundhasPlayed) {
				sound.play("win_effect", "sounds/win_effect.wav", false, 0.5f, soundPlay);
				endSoundhasPlayed = true;
			}
			if (drawHelper.get_win_transition_second_part()) {
				drawHelper.drawEndScreen(canvas, textFont,1);
				drawHelper.drawTopAchievement(canvas,level.didGetAchievement1(),level.getAchievementType1());
				drawHelper.drawBottomAchievement(canvas,level.didGetAchievement2(),level.getAchievementType2());
			}
			//drawHelper.drawGeneralTransition(canvas);

			drawHelper.drawLevelTransition(canvas,level,1);
		} else if (failed) {
			if (!endSoundhasPlayed) {
				sound.play("lose_effect", "sounds/lose_effect.wav", false, 0.5f, soundPlay);
				endSoundhasPlayed = true;
			}
			if (drawHelper.get_win_transition_second_part()) {
				drawHelper.drawEndScreen(canvas,textFont,0);
			}
			drawHelper.drawLevelTransition(canvas,level,0);
			//drawHelper.drawGeneralTransition(canvas);
		}
	}

	public boolean noOneSeesMe(){
		boolean isbeingseen = false;
		for (AIController controller : AIcontrollers){
			if(controller.isChasing()) {
				isbeingseen = true;
				if (! complete) {
					if (level.getAchievementType1() == 1) {
						level.setGetAchievement1(false);
					}
					if (level.getAchievementType2() == 1) {
						level.setGetAchievement2(false);
					}
				}
			}
		}
		return !isbeingseen;
	}

	/**
	 * Called when the Screen is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// IGNORE FOR NOW
	}

	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			if (preUpdate(delta)) {
				update(delta);
				draw(delta);
			}
			else {
				updatePause(delta);
			}

		}
	}

	/**
	 * Called when the Screen is paused.
	 *
	 * This is usually when it's not active or visible on screen. An Application is
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub
	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub
	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
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
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects.  We use
	 * this method to test if it is the "right" kind of collision.  In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		try {
			Obstacle bd1 = (Obstacle)body1.getUserData();
			Obstacle bd2 = (Obstacle)body2.getUserData();

			String sf1 = "";
			String sf2 = "";

			if (fd1 != null) {
				sf1 = (String)fd1;
			}
			if (fd2 != null) {
				sf2 = (String)fd2;
			}

			AnnetteModel annette = level.getAnnette();
			BoxModel box = level.getBox();
			ExitModel door   = level.getExit();
			DistractionModel distraction = level.getDistraction();

			// win state
			if ((sf1.contains("center") && bd2 == door) || (sf2.contains("center") && bd1 == door)) {
				if (!failed) {
					setComplete(true);
				}
//				if (soundPlay) {
//					sound.play("win_effect", "sounds/win_effect.wav", false, 0.5f);
//				}
			}

			//collision with creature lose state
			for (CreatureModel c : level.getCreature()){
				if ((sf1.contains("center") && bd2 == c) || (sf2.contains("center") && bd1 == c)){
					if (!isFailure()) {
//						if (soundPlay) {
//							sound.play("lose_effect", "sounds/lose_effect.wav", false, 0.5f);
//						}
					}
					if (!complete) {
						setFailure(true);
					}
				}
			}

			// checking sensors to see if box can be made
			if (sf1.contains("annetteDown") || sf2.contains("annetteDown")) {
				downBox = false; }
//			else { downBox = true; }
			if (sf1.contains("annetteUp") || sf2.contains("annetteUp")) { upBox = false; }
//			else { upBox = true; }
			if (sf1.contains("annetteRight") || sf2.contains("annetteRight")) { rightBox = false; }
//			else { rightBox = true; }
			if (sf1.contains("annetteLeft") || sf2.contains("annetteLeft")) { leftBox = false; }
//			else { leftBox = true; }


			// Check if bird hits box
			if ((bd1 == distraction && bd2 == box) || (bd1==box && bd2==distraction)) {
				annette.setBird(false);
				distraction.setAlive(false);
//				distraction.deactivatePhysics(level.getWorld());
//				distraction.dispose();
				level.objects.remove(distraction);
			}

			// check for distraction collisions with mazes
			for (Obstacle b : level.getMazes()) {
				if ((bd1 == b && bd2 == distraction) || (bd1 == distraction && bd2 == b )) {
					annette.setBird(false);
					distraction.setAlive(false);
//					distraction.deactivatePhysics(level.getWorld());
//					distraction.dispose();
					level.objects.remove(distraction);
				}
			}

			// check for distraction collisions with barriers
			for (Obstacle w : level.getBarriers()) {
				if ((bd1 == w && bd2 == distraction) || (bd1 == distraction && bd2== w )) {
					annette.setBird(false);
					distraction.setAlive(false);
//					distraction.deactivatePhysics(level.getWorld());
//					distraction.dispose();
					level.objects.remove(distraction);
				}
			}

			// check annette against mazes
			for (Obstacle b : level.getMazes()) {
				if ((bd1 == b && bd2 == annette) || (bd1 == annette && bd2 == b )
						|| (sf1.contains("center") && bd2 == b) || (sf2.contains("center") && bd1 == b)) {
					downBox = false;
					upBox = false;
					leftBox = false;
					rightBox = false;
				}
			}
			// check annette against barriers
			for (Obstacle w : level.getBarriers()) {
				if ((bd1 == w && bd2 == annette) || (bd1 == annette && bd2== w )) {
					downBox = false;
					upBox = false;
					leftBox = false;
					rightBox = false;
				}
			}


			// check if creature is distracted
			for (CreatureModel c : level.getCreature()) {
				if ((bd1 == c && bd2 == distraction) || (bd1 == distraction && bd2 == c )) {
					c.setDistracted(true);
				}
			}

			// check if creature hits barrier
			for (CreatureModel c : level.getCreature()) {
				for (Obstacle o : level.getBarriers()) {
					if ((bd1 == c && bd2 == o) || (bd1 == o && bd2 == c)) {
						c.setStuck(true);
					}
				}
				for (Obstacle o : level.getMazes()) {
					if ((bd1 == c && bd2 == o) || (bd1 == o && bd2 == c)) {
						c.setStuck(true);
					}
				}
				if ((bd1 == c && bd2 == box) || (bd1 == box && bd2 == c)) {
					c.setStuck(true);
					c.setStuckBox(true);
				}
			}

			for (CreatureModel c : level.getCreature()) {
				int index = 0;
				CreatureModel c2 = level.getCreature(index);

				do {
					if (c != c2) {
						if ((bd1 == c && bd2 == c2) || (bd1 == c2 && bd2 == c)) {
							c.setStuck(true);
						}
					}
					index++;
					c2 = level.getCreature(index);
				} while (c2 != null);
			}

			// check reactivation
			if (((sf1.contains("center") && bd2 == box  ) ||
					(bd1 == box   && sf2.contains("center"))) && box.getDeactivated()) {
				box.setDeactivated(false);
				box.setDeactivating(false);
				box.reactivate();
				level.setAlpha(255);
				sound.stop("box_effect");
				sound.play("box_effect", "sounds/box_effect.wav", false, 0.8f, soundPlay);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void createAIControllers(){
		if (AIcontrollers.size == 0) {
			for (CreatureModel c : level.getCreature()) {
				AIController controller = new AIController(c, level);
				AIcontrollers.add(controller);
			}
		}
	}

	public void effectiveUsageofBird(){
		for (AIController ai : AIcontrollers){
			if (ai.creatureAffectedByDistraction == true){
				hasUsedDistractionEffectively = true;
			}
		}
	}

	public void effectiveUsageofWalk(){
		for (AIController ai : AIcontrollers){
			if (ai.creatureAffectedByWalk == true){
				hasUsedWalkingEffectively = true;
			}
		}
	}

	/** Unused ContactListener method */
	public void endContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		try {
			Obstacle bd1 = (Obstacle) body1.getUserData();
			Obstacle bd2 = (Obstacle) body2.getUserData();

			String sf1 = "";
			String sf2 = "";

			if (fd1 != null) {
				sf1 = (String) fd1;
			}
			if (fd2 != null) {
				sf2 = (String) fd2;
			}

			AnnetteModel annette = level.getAnnette();
//			BoxModel box = level.getBox();

			// checking sensors to see if box can be made
			if (!sf1.contains("annetteDown") && !sf2.contains("annetteDown")) {
				downBox = true; }
//			else { downBox = true; }
			if (!sf1.contains("annetteUp") && !sf2.contains("annetteUp")) { upBox = true; }
//			else { upBox = true; }
			if (!sf1.contains("annetteRight") && !sf2.contains("annetteRight")) { rightBox = true; }
//			else { rightBox = true; }
			if (!sf1.contains("annetteLeft") && !sf2.contains("annetteLeft")) { leftBox = true; }
//			else { leftBox = true; }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	/** Unused ContactListener method */
	public void preSolve(Contact contact, Manifold oldManifold) {}

}
