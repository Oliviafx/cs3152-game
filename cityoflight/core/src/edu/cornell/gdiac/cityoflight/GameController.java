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
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.lights.LightSource;
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

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

	/** Offset for box when summoning */
	private static final float  BOX_HOFFSET = 1.0f;
	private static final float  BOX_VOFFSET = 1.0f;


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
	public static final int EXIT_COUNT = 120;

	/** Reference to the game canvas */
	protected ObstacleCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** Reference to the game level */
	protected LevelModel level;

	/** Whether or not this is an active controller */
	private boolean active;
	/** Whether we have completed this level */
	private boolean complete;
	/** Whether we have failed at this world (and need a reset) */
	private boolean failed;
	/** Countdown active for winning or losing */
	private int countdown;

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
			countdown = EXIT_COUNT;
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
		if (value) {
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

		setComplete(false);
		setFailure(false);
		countdown = -1;

		// Reload the json each time
		levelFormat = jsonReader.parse(Gdx.files.internal("jsons/level.json"));
		level.populate(levelFormat);
		level.getWorld().setContactListener(this);
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

		// Now it is time to maybe switch screens.
		if (input.didExit()) {
			listener.exitScreen(this, EXIT_QUIT);
			return false;
		} else if (countdown > 0) {
			countdown--;
		} else if (countdown == 0) {
			reset();
		}

		return true;
	}

	private Vector2 aAngleCache = new Vector2();
	private Vector2 cAngleCache = new Vector2();
	private Vector2 dAngleCache = new Vector2();


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
		// Process actions in object model
		AnnetteModel annette = level.getAnnette();

		BoxModel box = level.getBox();
		DistractionModel distraction = level.getDistraction();
//		if (level.isDistraction()) {
//			distraction.setPosition(annette.getPosition());
//		}
		InputController input = InputController.getInstance();

		float xoff = 0;
		float yoff = 0;

		// Rotate the avatar to face the direction of movement
		aAngleCache.set(input.getaHoriz(),input.getaVert());
//		if (aAngleCache.len2() > 0.0f) {
//			float angle = aAngleCache.angle();
//			// Convert to radians with up as 0
//			angle = (float)Math.PI*(angle-90.0f)/180.0f;
//			annette.setAngle(angle);
//		}
		aAngleCache.scl(annette.getForce());
		annette.setMovement(aAngleCache.x,aAngleCache.y);
		annette.setDirection(input.getDirection());
		annette.setSummoning(InputController.getInstance().didSpace());
//		System.out.println("Input direction null");
//		System.out.println(input.getDirection()==null);
		annette.setBird(input.didX());
		annette.applyForce();

		//Check if distraction was called
		if (annette.getBird()&&!level.isDistraction() ) {
//			System.out.println("here");
			level.createDistraction(levelFormat);
			level.getDistraction().setAlive(true);
			dAngleCache.set(input.getaHoriz(),input.getaVert());
			//			dAngleCache.set(1,1);
			if (dAngleCache.len2() > 0.0f) {
				float angle = aAngleCache.angle();
				// Convert to radians with up as 0
				angle = (float)Math.PI*(angle-90.0f)/180.0f;
//				annette.setAngle(angle);
			}
			if (distraction != null) {
				dAngleCache.scl(distraction.getForce());
				distraction.setMovement(dAngleCache.x,dAngleCache.y);
			}
		}
//		level.getDistraction().setAlive(input.didX()&&!level.getDistraction().getAlive());

		// creature AI temporary.

        int index = 0;
        CreatureModel currentcreature = level.getCreature(index);

        while (currentcreature != null){
			currentcreature.setTurnCool(currentcreature.getTurnCool() - 1);

			if (currentcreature.getType() == 1) {
				if (currentcreature.getStuck() && currentcreature.getTurnCool() <= 0) {
					System.out.println("snail behavior: change direction");
					currentcreature.setXInput(-currentcreature.getXInput());
					currentcreature.setYInput(-currentcreature.getYInput());
					currentcreature.setStuck(false);
					currentcreature.setTurnCool(currentcreature.getTurnLimit());
				}
			}

			if (currentcreature.getType() == 2){
				if (currentcreature.getStuck() && currentcreature.getTurnCool() <= 0) {
					System.out.println("dragon behavior: turns right");
					if (currentcreature.getXInput() > 0){
						currentcreature.setYInput(-currentcreature.getXInput());
						currentcreature.setXInput(0);
					} else if (currentcreature.getXInput() < 0){
						currentcreature.setYInput(-currentcreature.getXInput());
						currentcreature.setXInput(0);
					} else if (currentcreature.getYInput() > 0){
						currentcreature.setXInput(currentcreature.getYInput());
						currentcreature.setYInput(0);
					} else if (currentcreature.getYInput() < 0){
						currentcreature.setXInput(currentcreature.getYInput());
						currentcreature.setYInput(0);
					}

					currentcreature.setStuck(false);
					currentcreature.setTurnCool(currentcreature.getTurnLimit());
				}
			}

			if (currentcreature.getType() == 3){
				// dame blanche.
			}

			cAngleCache.set(currentcreature.getXInput(),currentcreature.getYInput());
			//System.out.println("movement = " + currentcreature.getMovement());

			if (cAngleCache.len2() > 0.0f) {
                float angle = cAngleCache.angle();
                // Convert to radians with up as 0
                angle = (float)Math.PI*(angle-90.0f)/180.0f;
                currentcreature.setAngle(angle);
            }
            cAngleCache.scl(currentcreature.getForce());
            currentcreature.setMovement(cAngleCache.x,cAngleCache.y);
            currentcreature.applyForce();

            // try to get next creature from level.
            index ++;
            currentcreature = level.getCreature(index);
        }

		JsonValue boxdata = levelFormat.get("box");
		box.setDrawScale(level.scale);
		if (annette.isSummoning() && !box.getDoesExist()) {

			// get direction annette is facing
			switch (annette.getDirection()) {
				case RIGHT: xoff = BOX_HOFFSET;
					break;
				case LEFT:	xoff = -BOX_HOFFSET;
					break;
				case UP:	yoff = BOX_VOFFSET;
					break;
				case DOWN:	yoff = -BOX_VOFFSET;
					break;
				default: {
					xoff = 0;
					yoff = 0;
					break;
				}
			}
			box.initialize(boxdata, annette.getPosition(), xoff, yoff);
			level.activate(box);
			box.setActive(true);
			box.setDoesExist(true);
			box.setDeactivated(false);
			box.setDeactivating(false);
		}
		box.applyForce();

		float dist = (float)Math.hypot(Math.abs(box.getPosition().x - annette.getPosition().x), Math.abs(box.getPosition().y - annette.getPosition().y));

		// box is deactivatING
		if (box.getDoesExist() && !box.getDeactivated() && dist > BoxModel.INNER_RADIUS){
			box.setDeactivating(true);
		}

		// box is deactivatED
		if (box.getDoesExist() && !box.getDeactivated() && dist > BoxModel.OUTER_RADIUS){
			box.setDeactivated(true);
			box.deactivate();
		}

		// set debug colors
		if (box.getDeactivated()) box.setDebugColor(Color.RED);
		else if (box.getDeactivating()) box.setDebugColor(Color.YELLOW);
		else box.setDebugColor(Color.GREEN);

		// Turn the physics engine crank.
		checkSeen();
		level.update(dt);
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
//		level.getDistraction().draw(canvas);
		// Final message
		if (complete && !failed) {
			displayFont.setColor(Color.YELLOW);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("well of power", displayFont, 0.0f);
			canvas.end();
		} else if (failed) {
			displayFont.setColor(Color.RED);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("You have\nbeen seen.\nGoodbye", displayFont, 0.0f);
			canvas.end();
		}
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
			}
			draw(delta);
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

	public void checkSeen(){
		AnnetteModel annette = level.getAnnette();

		// Check condition : Annette gets seen
        for (LightSource currentlight : level.getVision()){
            if (currentlight.contains(annette.getX(), annette.getY())){
                setFailure(true);
            }
        }

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

			AnnetteModel annette = level.getAnnette();
			BoxModel box = level.getBox();
			ExitModel door   = level.getExit();
			DistractionModel distraction = level.getDistraction();

            // Check for win condition : Annette reaches exit
			if ( (bd1 == annette && bd2 == door) || (bd1 == door && bd2 == annette) ){
				setComplete(true);
			}


            // Check for losing condition : Annette collides with creature.
            for (CreatureModel currentcreature : level.getCreature()){
                if ( (bd1 == annette && bd2 == currentcreature) || (bd1 == currentcreature && bd2 == annette) ){
                    setFailure(true);
                }
            }

			// Check if bird hits box
			if ((bd1 == distraction && bd2 == box) || (bd1==box && bd2==distraction)) {
				annette.setBird(false);
				distraction.setAlive(false);
//				distraction.deactivatePhysics(level.getWorld());
//				distraction.setActive(false);
				level.objects.remove(distraction);
//				distraction.dispose();
//				distraction.deactivatePhysics(level.getWorld());
			}

			for (Obstacle b : level.getMazes()) {
				if ((bd1 == b && bd2 == distraction) || (bd1 == distraction && bd2 == b )) {
					annette.setBird(false);
					distraction.setAlive(false);
					level.objects.remove(distraction);
//					distraction.dispose();
//					distraction.deactivatePhysics(level.getWorld());
				}
			}

			for (Obstacle w : level.getBarriers()) {
				if ((bd1 == w && bd2 == distraction) || (bd1 == distraction && bd2== w )) {
					annette.setBird(false);
					distraction.setAlive(false);
//					level.objects.remove(distraction);
//					distraction.dispose();
//					distraction.setDeactivated(true);
				}
			}

			for (CreatureModel c : level.getCreature()) {
				if ((bd1 == c && bd2 == distraction) || (bd1 == distraction && bd2 == c )) {
					// some code that sets creature alertness idk
					System.out.println("distract creature");
				}
			}

			for (CreatureModel c : level.getCreature()) {
				for (Obstacle o : level.getBarriers()) {
					if ((bd1 == c && bd2 == o) || (bd1 == o && bd2 == c)) {
						c.setStuck(true);
					}
				}
			}


			for (CreatureModel c : level.getCreature()) {
					if ((bd1 == c && bd2 == box) || (bd1 == box && bd2 == c)) {
						c.setStuck(true);
					}
			}

			// check reactivation

			if ((bd1 == annette && bd2 == box ) ||
					(bd1 == box   && bd2 == annette)) {
				box.setDeactivated(false);
				box.setDeactivating(false);
				box.reactivate();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/** Unused ContactListener method */
	public void endContact(Contact contact) {}
	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	/** Unused ContactListener method */
	public void preSolve(Contact contact, Manifold oldManifold) {}
}
