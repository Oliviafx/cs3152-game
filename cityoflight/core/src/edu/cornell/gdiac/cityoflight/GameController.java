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
		CreatureModel bob = level.getCreature(0);
		CreatureModel fred = level.getCreature(1);
		CreatureModel john = level.getCreature(2);
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
		if (aAngleCache.len2() > 0.0f) {
			float angle = aAngleCache.angle();
			// Convert to radians with up as 0
			angle = (float)Math.PI*(angle-90.0f)/180.0f;
			annette.setAngle(angle);
		}
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

		//creature
		cAngleCache.set(0.0f,3.0f);
		if (cAngleCache.len2() > 0.0f) {
			float angle = cAngleCache.angle();
			// Convert to radians with up as 0
			angle = (float)Math.PI*(angle-90.0f)/180.0f;
			bob.setAngle(angle);
		}
		cAngleCache.scl(bob.getForce());
		bob.setMovement(cAngleCache.x,cAngleCache.y);
		bob.applyForce();

		cAngleCache.set(1.0f,0.0f);
		if (cAngleCache.len2() > 0.0f) {
			float angle = cAngleCache.angle();
			// Convert to radians with up as 0
			angle = (float)Math.PI*(angle-90.0f)/180.0f;
			fred.setAngle(angle);
		}
		cAngleCache.scl(fred.getForce());
		fred.setMovement(cAngleCache.x,cAngleCache.y);
		fred.applyForce();

		cAngleCache.set(0.2f,-0.8f);
		if (cAngleCache.len2() > 0.0f) {
			float angle = cAngleCache.angle();
			// Convert to radians with up as 0
			angle = (float)Math.PI*(angle-90.0f)/180.0f;
			john.setAngle(angle);
		}
		cAngleCache.scl(john.getForce());
		john.setMovement(cAngleCache.x,cAngleCache.y);
		john.applyForce();

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
		checkFail();
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

	public void checkFail(){
		AnnetteModel annette   = level.getAnnette();

		//Check for losing condition
		if (level.getVision(0).contains(annette.getX(), annette.getY())||
				level.getVision(1).contains(annette.getX(), annette.getY())||
				level.getVision(2).contains(annette.getX(), annette.getY())) {
			setFailure(true);
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
			CreatureModel bob = level.getCreature(0);
			CreatureModel fred = level.getCreature(1);
			CreatureModel john = level.getCreature(2);
			ExitModel door   = level.getExit();
			DistractionModel distraction = level.getDistraction();
			InteriorModel maze1 = (InteriorModel)level.getMazes().get(0);
			InteriorModel maze2 = (InteriorModel)level.getMazes().get(1);
			InteriorModel maze3 = (InteriorModel)level.getMazes().get(2);
			InteriorModel maze4 = (InteriorModel)level.getMazes().get(3);
//			InteriorModel maze5 = (InteriorModel)level.getMazes().get(4);
			ExteriorModel wall1 = (ExteriorModel)level.getBarriers().get(0);
			ExteriorModel wall2 = (ExteriorModel)level.getBarriers().get(1);
			// Check for win condition
			if ((bd1 == annette && bd2 == door  ) ||
				(bd1 == door   && bd2 == annette)) {
				setComplete(true);
			}
			// Check for losing condition
			if ((bd1 == annette && bd2 == bob) || (bd1 == bob && bd2 == annette) ||
					(bd1 == annette && bd2 == fred) || (bd1 == fred && bd2 ==annette) ||
					(bd1 == annette && bd2 == john) || (bd1 == john && bd2 ==annette)) {
				setFailure(true);
			}
			// Check if bird hits box
			if ((bd1 == distraction && bd2 == box) || (bd1==box && bd2==distraction)) {
				annette.setBird(false);
				distraction.setAlive(false);
//				distraction.deactivatePhysics(level.getWorld());
//				distraction.setDeactivated(false);
			}
			if ((bd1 == distraction && (bd2==maze1 || bd2 == maze2 || bd2 == maze3 || bd2 == maze4 ))|| //bd2==maze5) ||
					bd2 == distraction && (bd1==maze1 || bd1==maze2 || bd1 == maze3 || bd1 == maze4 )) { //bd1 == maze5))) {
				System.out.println("here");
				annette.setBird(false);
				distraction.setAlive(false);

			}
			if ((bd1 == distraction && (bd2==wall1 || bd2==wall2)) || //(bd2==maze1 || bd2 == maze2 || bd2 == maze3 || bd2 == maze4) ||
					(bd2 == distraction && (bd1==wall1 || bd2==wall2))) {//level.getMazes().contains(bd2))) {//(bd1==maze1 || bd1==maze2 || bd1 == maze3 || bd1 == maze4))) {
				System.out.println("here");
				annette.setBird(false);
				distraction.setAlive(false);
			}



			// check reactivation

			if ((bd1 == annette && bd2 == box  ) ||
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