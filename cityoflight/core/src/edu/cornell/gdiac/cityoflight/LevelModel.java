/*
 * LevelMode.java
 *
 * This stores all of the information to define a level for a top down game with light
 * and shadows.  As with Lab 2, it has an avatar, some walls, and an exit.  This model
 * supports JSON loading, and so the world is part of this object as well.  See the
 * JSON demo for more information.
 *
 * There are two major differences from JSON Demo.  First is the fixStep method.  This
 * ensures that the physics engine is really moving at the same rate as the visual
 * framerate. You can usually survive without this addition.  However, when the physics
 * adjusts shadows, it is very important.  See this website for more information about
 * what is going on here.
 *
 * http://gafferongames.com/game-physics/fix-your-timestep/
 *
 * The second addition is the RayHandler.  This is an attachment to the physics world
 * for drawing shadows.  Technically, this is a view, and really should be part of
 * GameCanvas.  However, in true graphics programmer garbage design, this is tightly
 * coupled the the physics world and cannot be separated.  So we store it here and
 * make it part of the draw method.  This is the best of many bad options.
 *
 * TODO: Refactor this design to decouple the RayHandler as much as possible.  Next
 * year, maybe.
 *
 * Author: Walker M. White
 * Initial version, 3/12/2016
 */
package edu.cornell.gdiac.cityoflight;

import box2dLight.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.lights.*;
import edu.cornell.gdiac.physics.obstacle.*;
import java.util.*;

/**
 * Represents a single level in our game
 *
 * Note that the constructor does very little.  The true initialization happens
 * by reading the JSON value.  To reset a level, dispose it and reread the JSON.
 *
 * The level contains its own Box2d World, as the World settings are defined by the
 * JSON file.  There is generally no controller code in this class, except for the
 * update method for moving ahead one timestep.  All of the other methods are getters
 * and setters.  The getters allow the GameController class to modify the level elements.
 */
public class LevelModel {
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;

	// Physics objects for the game
	/** Reference to the Annette avatar */
	private AnnetteModel annette;
	/** Reference to the creatures */
	private Array<CreatureModel> creatures = new Array<CreatureModel>();
	/** Reference to the goalDoor (for collision detection) */
	private ExitModel goalDoor;
	/** Reference to the box */
	private BoxModel box;
	/** Reference to the distraction bird */
	private DistractionModel distraction;
	/**Reference to background tiles*/
	private Array<BackgroundModel> tiles = new Array<BackgroundModel>();



	private IndicatorModel indicator;

	/** The interior models */
	private ArrayList<Obstacle> mazes = new ArrayList<Obstacle>();
	/** The exterior models */
	private ArrayList<Obstacle> barriers = new ArrayList<Obstacle>();
	/** Reference to the interior models */

	/** Whether or not the level is in debug mode (showing off physics) */
	private boolean debug;

	/** Alpha constant for box deactivation*/
	private int alpha = 255;

	private static final int MAX_ALPHA = 255;
	private static final float BOX_MARGIN = 0.8f;
	public static final float TRANSLATION = -64;

	/** All the objects in the world. */
	protected ArrayList<Obstacle> objects  = new ArrayList<Obstacle>();

	// LET THE TIGHT COUPLING BEGIN
	/** The Box2D world */
	protected World world;
	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;

	/** The camera defining the RayHandler view; scale is in physics coordinates */
	protected OrthographicCamera raycamera;
	/** The rayhandler for storing lights, and drawing them (SIGH) */
	protected RayHandler rayhandler;
	/** All of the active lights that we loaded from the JSON file */
	private Array<LightSource> lights = new Array<LightSource>();
	/** The indicator for the radius of the "move in place" power */
	private LightSource radiusOfPower;

	Affine2 oTran = new Affine2();
	Affine2 wTran = new Affine2();

	private float normal_r, normal_g, normal_b, normal_alp;

	// TO FIX THE TIMESTEP
	/** The maximum frames per second setting for this level */
	protected int maxFPS;
	/** The minimum frames per second setting for this level */
	protected int minFPS;
	/** The amount of time in to cover a single animation frame */
	protected float timeStep;
	/** The maximum number of steps allowed before moving physics forward */
	protected float maxSteps;
	/** The maximum amount of time allowed in a frame */
	protected float maxTimePerFrame;
	/** The amount of time that has passed without updating the frame */
	protected float physicsTimeLeft;

	private static final String BACKGROUND_FILE = "textures/black.png";
	private Texture background;

	private static final String BLANCHE_LAYER_NAME = "Blanche";
	private static final String TARASQUE_LAYER_NAME = "Tarasque";
	private static final String SNAIL_LAYER_NAME = "Snail";

	/**
	 * Returns the bounding rectangle for the physics world
	 *
	 * The size of the rectangle is in physics, coordinates, not screen coordinates
	 *
	 * @return the bounding rectangle for the physics world
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Returns the scaling factor to convert physics coordinates to screen coordinates
	 *
	 * @return the scaling factor to convert physics coordinates to screen coordinates
	 */
	public Vector2 getScale() {
		return scale;
	}

	/**
	 * Returns a reference to the Box2D World
	 *
	 * @return a reference to the Box2D World
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Returns a reference to the lighting rayhandler
	 *
	 * @return a reference to the lighting rayhandler
	 */
	public RayHandler getRayHandler() {
		return rayhandler;
	}

	/**
	 * Returns a reference to the player avatar
	 *
	 * @return a reference to the player avatar
	 */
	public AnnetteModel getAnnette() {
		return annette;
	}

	/**
	 * Returns a reference to the creature array
	 *
	 * @return a reference to the creature array
	 */
	public Array<CreatureModel> getCreature(){ return creatures; }

	/**
	 * Returns a creature of the specified index
	 *
	 * @param index the index of the creature to return
	 * @return a creature of the specified index
	 */
	public CreatureModel getCreature(int index) {
		try {
			return creatures.get(index);
		} catch (IndexOutOfBoundsException e){
			//System.out.println(" creature of index " + index + " does not exist.");
			return null;
		}
	}

	/**
	 * Returns a reference to the vision array
	 *
	 * @return a reference to the vision array
	 */
	public Array<LightSource> getVision() { return lights; }

	/**
	 * Returns a reference to a light
	 *
	 * @return a reference to a light
	 */
	public LightSource getVision(int index) {
		try {
			return lights.get(index);
		} catch (IndexOutOfBoundsException e){
//			System.out.println(" visioncone of index " + index + " does not exist.");
			return null;
		}
	}

	public LightSource getRadiusOfPower(){
		return radiusOfPower;
	}

	/**
	 * Returns a reference to the box
	 *
	 * @return a reference to the box
	 */
	public BoxModel getBox() {
		return box;
	}

	public void setBox(BoxModel value) {
	    box = value;
    }


	public DistractionModel getDistraction() {
		return distraction;
	}

	public ArrayList<Obstacle> getMazes() { return mazes; }
	public ArrayList<Obstacle> getBarriers() { return barriers; }

	/**
	 * Returns a reference to the exit door
	 *
	 * @return a reference to the exit door
	 */
	public ExitModel getExit() {
		return goalDoor;
	}

	/**
	 * Returns whether this level is currently in debug node
	 *
	 * If the level is in debug mode, then the physics bodies will all be drawn as
	 * wireframes onscreen
	 *
	 * @return whether this level is currently in debug node
	 */
	public boolean getDebug() {
		return debug;
	}

	/**
	 * Sets whether this level is currently in debug node
	 *
	 * If the level is in debug mode, then the physics bodies will all be drawn as
	 * wireframes onscreen
	 *
	 * @param value	whether this level is currently in debug node
	 */
	public void setDebug(boolean value) {
		debug = value;
	}

	/**
	 * Returns the maximum FPS supported by this level
	 *
	 * This value is used by the rayhandler to fix the physics timestep.
	 *
	 * @return the maximum FPS supported by this level
	 */
	public int getMaxFPS() {
		return maxFPS;
	}

	/**
	 * Sets the maximum FPS supported by this level
	 *
	 * This value is used by the rayhandler to fix the physics timestep.
	 *
	 * @param value the maximum FPS supported by this level
	 */
	public void setMaxFPS(int value) {
		maxFPS = value;
	}

	/**
	 * Returns the minimum FPS supported by this level
	 *
	 * This value is used by the rayhandler to fix the physics timestep.
	 *
	 * @return the minimum FPS supported by this level
	 */
	public int getMinFPS() {
		return minFPS;
	}

	/**
	 * Sets the minimum FPS supported by this level
	 *
	 * This value is used by the rayhandler to fix the physics timestep.
	 *
	 * @param value the minimum FPS supported by this level
	 */
	public void setMinFPS(int value) {
		minFPS = value;
	}

	/**
	 * Returns whether this level is currently in debug node
	 *
	 * If the level is in debug mode, then the physics bodies will all be drawn as
	 * wireframes onscreen
	 *
	 * @return whether this level is currently in debug node
	 */
	public int getAlpha() { return alpha; }

	/**
	 * Sets whether this level is currently in debug node
	 *
	 * If the level is in debug mode, then the physics bodies will all be drawn as
	 * wireframes onscreen
	 *
	 * @param value	whether this level is currently in debug node
	 */
	public void setAlpha(int value) { alpha = value; }


	public float getTranslation() { return TRANSLATION; }

	/**
	 * Creates a new LevelModel
	 *
	 * The level is empty and there is no active physics world.  You must read
	 * the JSON file to initialize the level
	 */
	public LevelModel() {
		world  = null;
		bounds = new Rectangle(0,0,1,1);
		scale = new Vector2(1,1);
		debug  = false;
		background = null;
	}

	public void populate(JsonValue levelFormat, ObstacleCanvas canvas) {


		background = new Texture(BACKGROUND_FILE);

		int tileHeight = levelFormat.get("height").asInt();
		int tileWidth = levelFormat.get("width").asInt();
		int tileSize = levelFormat.get("tilewidth").asInt();

		//pixels width, height
//		int[] gSize = {tileWidth*tileSize,tileHeight*tileSize};
		//how many tiles
//		float[] pSize = {(float)tileHeight * tileWidth, (float)tileWidth * tileWidth};

		// how many tiles
		float[] pSize = {(float) tileWidth, (float) tileHeight};
		// pixels width, height
		int[] gSize = {tileWidth * tileSize, tileHeight * tileSize};


		world = new World(Vector2.Zero, false);
		bounds = new Rectangle(0, 0, pSize[0], pSize[1]);
//		scale.x = tileSize;
		scale.x = gSize[0] / pSize[0];
//		System.out.println("scale.x: " + scale.x);
//		scale.y = tileSize;
		scale.y = gSize[1] / pSize[1];

		// FPS is hardcoded now
		int[] fps = {20, 60};
		maxFPS = fps[1];
		minFPS = fps[0];
		timeStep = 1.0f / maxFPS;
		maxSteps = 1.0f + maxFPS / minFPS;
		maxTimePerFrame = timeStep * maxSteps;

		// Create the lighting if appropriate
		if (levelFormat.has("lighting")) {
			float c0 = levelFormat.get("lightingColor0").asFloat();
			float c1 = levelFormat.get("lightingColor1").asFloat();
			float c2 = levelFormat.get("lightingColor2").asFloat();
			float c3 = levelFormat.get("lightingColor3").asFloat();
			float[] colors = {c0, c1, c2, c3};
			boolean gamma = levelFormat.get("lightingGamma").asBoolean();
			boolean diffuse = levelFormat.get("lightingDiffuse").asBoolean();
			int blur = levelFormat.get("lightingBlur").asInt();
			initLighting(colors, gamma, diffuse, blur, canvas);
		} else {

			float[] colors = {0.6f, 0.6f, 0.6f, 0.6f};
			boolean gamma = true;
			boolean diffuse = true;
			int blur = 3;
			initLighting(colors, gamma, diffuse, blur, canvas);
		}
//		createPointLights(levelFormat.get("pointlights"));
//		createConeLights(levelFormat.get("conelights"));


		JsonValue idMap = levelFormat.get("tilesets");
		tiles = new Array<BackgroundModel>();

		//loop through layers to find specified objects and initialize
		JsonValue layers = levelFormat.get("layers");
		JsonValue lineOfSightJSON = null;

		//loop through to find the name keys of textures used
		HashMap<Integer, String> idToTexture = new HashMap<Integer, String>();
		for (int j = 0; j < idMap.size; j++) {
			JsonValue obj = idMap.get(j);

			int id = obj.get("firstgid").asInt();
			String tex = obj.get("name").asString();
//			System.out.println(tex + " : " + id);
			idToTexture.put(id, tex);
		}

		for (int i = 0; i < layers.size; i++) {
			JsonValue layer = layers.get(i);
			String layerName = layer.get("name").asString();

			// just for the Object layers
			JsonValue objects = layer.get("objects");

			if (layerName.equals("box_Boundaries")) {
				HashMap<String, JsonValue> boundaryArray = new HashMap<String, JsonValue>();


				//assign building and box values to indexes in hashmaps
				for (int j = 0; j < objects.size; j++) {

					JsonValue obj = objects.get(j);
					InteriorModel obj2 = new InteriorModel();
//					setPosition(pos[0]+ size[0]/2,height - (pos[1]-size[1]/2));
					float[] pos = {obj.get("x").asFloat() / 64, obj.get("y").asFloat() / 64 + obj.get("height").asFloat()/64 - 1};
					float[] size = {obj.get("width").asFloat() / 64, obj.get("height").asFloat() / 64};
					float[] pad = {0.1f, 0.1f};
					String debugColor = "red";


//                        System.out.println("psize");
//                        System.out.println(pSize[0]);
//                        System.out.println(pSize[1]);

					obj2.initialize(pos, size, pad, debugColor, null, pSize[1], 0, 0);
					obj2.setDrawScale(scale);
					activate(obj2);
//					System.out.println(pos[0] + " " + pos[1]);
//					System.out.println("activating building");
					mazes.add(obj2);
					String objName = obj.get("name").asString();
//					System.out.println(objName);

					String[] bSplit = objName.split("boundary");

//					boundaryArray.put(bSplit[1], obj);
//                    System.out.println(bSplit[1]);
//                    System.out.println("boundary "+j + "2 : "+ bSplit[1]);

				}

//			} else if (layerName.equals("vision_properties")) {
//				DEFINE THIS BEFORE CREATURES GET INITIALIZED
//				System.out.println("loading vision");

//				lineOfSightJSON = objects;
//				System.out.println(objects);
//				for (CreatureModel c : creatures) {
//				createLineofSight(lineOfSightJSON);
//				}
			} else if (layerName.equals(SNAIL_LAYER_NAME)) {

				//				System.out.println("loading creatures");

				HashMap<String, JsonValue> numToCreature = new HashMap<String, JsonValue>();
				HashMap<String, JsonValue> numToBox = new HashMap<String, JsonValue>();


				//assign building and box values to indexes in hashmaps
				for (int j = 0; j < objects.size; j++) {
//					System.out.println(j);
					JsonValue obj = objects.get(j);
//					System.out.println(obj.get("name"));
					if (obj.get("name").asString().contains("snail_vision")) {
						lineOfSightJSON = obj.get("properties");
//				System.out.println(objects);
//				for (CreatureModel c : creatures) {
						createLineofSight(lineOfSightJSON);
					}
					else if (obj.get("name").asString().contains("snail")) {
//						System.out.println(obj);
						String objName = obj.get("name").asString();
						String[] bSplit = objName.split(layerName.toLowerCase());
						bSplit[0] = objName.substring(0, objName.length()-bSplit[1].length());
//					System.out.println("bSplit[1]: " + bSplit[1]);

						if (bSplit[1].length() > 3) {
							//add to box list
							numToBox.put(bSplit[1].split("box")[1], obj);
//							System.out.println(bSplit[1].split("box")[1]+" j ");
						} else {
							//add to building list
							numToCreature.put(bSplit[1], obj);
//							System.out.println(bSplit[1]);


						}

					}




//					int index = 0;
//					if (name.contains("tarasque"))
//						index = 1;
//					else if (name.contains("blanche"))
//						index = 2;

//					System.out.println("index = " + index);

				}
				//initialize creatures
				for (int n = 0; n < numToCreature.size(); n++) {
//						System.out.println(n);

					FilmStrip[] film = new FilmStrip[3];
					JsonValue buildingJSON = numToCreature.get("" + (n + 1)).get("properties");
//					System.out.println(buildingJSON);
					JsonValue boxJSON = numToBox.get("" + (n + 1));
//					System.out.println(boxJSON);
					String textName = buildingJSON.get("texture").asString();
					String textName2 = buildingJSON.get("texture2").asString();
					String textName3 = buildingJSON.get("texture3").asString();
					String[] textures = {textName, textName2, textName3};

					String name = numToCreature.get("" + (n + 1)).get("name").asString();

					HashMap<String, FilmStrip> idToFilmStrip = new HashMap<String, FilmStrip>();
					for (int f = 0; f < 3; f++) {
						if (idToFilmStrip.containsKey(textName)) {
							film[f] = idToFilmStrip.get(textures[f]);
						} else {
							TextureRegion texture = JsonAssetManager.getInstance().getEntry(textures[f], TextureRegion.class);
							FilmStrip tileTexture;
							try {
								tileTexture = (FilmStrip) texture;
							} catch (Exception e) {
								tileTexture = null;
							}
							film[f] = tileTexture;
							idToFilmStrip.put(textName + f, tileTexture);
						}
					}
					CreatureModel creature = new CreatureModel();
//					System.out.println(creature.getPosition().x + " " + creature.getPosition().y);
					creature.initialize(buildingJSON, boxJSON, film[0], film[1], film[2], pSize[1]);
//					System.out.println(creature.getPosition().x*64 + " " + creature.getPosition().y*64);
					creature.setDrawScale(scale);
					activate(creature);
//					System.out.println(lights.size + ": lights size");
//					System.out.println(lights.get(index) + ": lights");
//					System.out.println("lights "+lights.get(index).getX() + " "+lights.get(index).getY());

					attachVision(creature, lights.get(n));

					creatures.add(creature);
				}
			} else if (layerName.equals(TARASQUE_LAYER_NAME)) {
//				System.out.println("loading creatures");

				HashMap<String, JsonValue> numToCreature = new HashMap<String, JsonValue>();
				HashMap<String, JsonValue> numToBox = new HashMap<String, JsonValue>();


				//assign building and box values to indexes in hashmaps
				for (int j = 0; j < objects.size; j++) {
//					System.out.println(j);
					JsonValue obj = objects.get(j);
//					System.out.println(obj.get("name"));
					if (obj.get("name").asString().contains("dragon")) {
						lineOfSightJSON = obj.get("properties");
//				System.out.println(objects);
//				for (CreatureModel c : creatures) {
						createLineofSight(lineOfSightJSON);
					}
					else if (obj.get("name").asString().contains("tarasque")) {
//						System.out.println(obj);
						String objName = obj.get("name").asString();
						String[] bSplit = objName.split(layerName.toLowerCase());
						bSplit[0] = objName.substring(0, objName.length()-bSplit[1].length());
//					System.out.println("bSplit[1]: " + bSplit[1]);

						if (bSplit[1].length() > 3) {
							//add to box list
							numToBox.put(bSplit[1].split("box")[1], obj);
//							System.out.println(bSplit[1].split("box")[1]+" j ");
						} else {
							//add to building list
							numToCreature.put(bSplit[1], obj);
//							System.out.println(bSplit[1]);


						}

					}




//					int index = 0;
//					if (name.contains("tarasque"))
//						index = 1;
//					else if (name.contains("blanche"))
//						index = 2;

//					System.out.println("index = " + index);

				}
				//initialize creatures
				for (int n = 0; n < numToCreature.size(); n++) {
						System.out.println(n);

					FilmStrip[] film = new FilmStrip[3];
					JsonValue buildingJSON = numToCreature.get("" + (n + 1)).get("properties");
//					System.out.println(buildingJSON);
					JsonValue boxJSON = numToBox.get("" + (n + 1));
//					System.out.println(boxJSON);
					String textName = buildingJSON.get("texture").asString();
					String textName2 = buildingJSON.get("texture2").asString();
					String textName3 = buildingJSON.get("texture3").asString();
					String[] textures = {textName, textName2, textName3};

					String name = numToCreature.get("" + (n + 1)).get("name").asString();

					HashMap<String, FilmStrip> idToFilmStrip = new HashMap<String, FilmStrip>();
					for (int f = 0; f < 3; f++) {
						if (idToFilmStrip.containsKey(textName)) {
							film[f] = idToFilmStrip.get(textures[f]);
						} else {
							TextureRegion texture = JsonAssetManager.getInstance().getEntry(textures[f], TextureRegion.class);
							FilmStrip tileTexture;
							try {
								tileTexture = (FilmStrip) texture;
							} catch (Exception e) {
								tileTexture = null;
							}
							film[f] = tileTexture;
							idToFilmStrip.put(textName + f, tileTexture);
						}
					}
					CreatureModel creature = new CreatureModel();
//					System.out.println(creature.getPosition().x + " " + creature.getPosition().y);
					creature.initialize(buildingJSON, boxJSON, film[0], film[1], film[2], pSize[1]);
//					System.out.println(creature.getPosition().x*64 + " " + creature.getPosition().y*64);
					creature.setDrawScale(scale);
					activate(creature);
//					System.out.println(lights.size + ": lights size");
//					System.out.println(lights.get(index) + ": lights");
//					System.out.println("lights "+lights.get(index).getX() + " "+lights.get(index).getY());

					attachVision(creature, lights.get(n));

					creatures.add(creature);
				}
			} else if (layerName.equals(BLANCHE_LAYER_NAME) ) {
//				System.out.println("loading creatures");

				HashMap<String, JsonValue> numToCreature = new HashMap<String, JsonValue>();
				HashMap<String, JsonValue> numToBox = new HashMap<String, JsonValue>();


				//assign building and box values to indexes in hashmaps
				for (int j = 0; j < objects.size; j++) {
//					System.out.println(j);
					JsonValue obj = objects.get(j);
//					System.out.println(obj.get("name"));
					if (obj.get("name").asString().contains("lady")) {
						lineOfSightJSON = obj.get("properties");
//				System.out.println(objects);
//				for (CreatureModel c : creatures) {
						createLineofSight(lineOfSightJSON);
					}
					else if (obj.get("name").asString().contains("blanche")) {
//						System.out.println(obj);
						String objName = obj.get("name").asString();
						String[] bSplit = objName.split(layerName.toLowerCase());
						bSplit[0] = objName.substring(0, objName.length()-bSplit[1].length());
//					System.out.println("bSplit[1]: " + bSplit[1]);

						if (bSplit[1].length() > 3) {
							//add to box list
							numToBox.put(bSplit[1].split("box")[1], obj);
//							System.out.println(bSplit[1].split("box")[1]+" j ");
						} else {
							//add to building list
							numToCreature.put(bSplit[1], obj);
//							System.out.println(bSplit[1]);


													}

					}




//					int index = 0;
//					if (name.contains("tarasque"))
//						index = 1;
//					else if (name.contains("blanche"))
//						index = 2;

//					System.out.println("index = " + index);

				}
				//initialize creatures
				for (int n = 0; n < numToCreature.size(); n++) {
//						System.out.println(n);

					FilmStrip[] film = new FilmStrip[3];
					JsonValue buildingJSON = numToCreature.get("" + (n + 1)).get("properties");
//					System.out.println(buildingJSON);
					JsonValue boxJSON = numToBox.get("" + (n + 1));
//					System.out.println(boxJSON);
					String textName = buildingJSON.get("texture").asString();
					String textName2 = buildingJSON.get("texture2").asString();
					String textName3 = buildingJSON.get("texture3").asString();
					String[] textures = {textName, textName2, textName3};

					String name = numToCreature.get("" + (n + 1)).get("name").asString();

					HashMap<String, FilmStrip> idToFilmStrip = new HashMap<String, FilmStrip>();
					for (int f = 0; f < 3; f++) {
						if (idToFilmStrip.containsKey(textName)) {
							film[f] = idToFilmStrip.get(textures[f]);
						} else {
							TextureRegion texture = JsonAssetManager.getInstance().getEntry(textures[f], TextureRegion.class);
							FilmStrip tileTexture;
							try {
								tileTexture = (FilmStrip) texture;
							} catch (Exception e) {
								tileTexture = null;
							}
							film[f] = tileTexture;
							idToFilmStrip.put(textName + f, tileTexture);
						}
					}
					CreatureModel creature = new CreatureModel();
//					System.out.println(creature.getPosition().x + " " + creature.getPosition().y);
					creature.initialize(buildingJSON, boxJSON, film[0], film[1], film[2], pSize[1]);
//					System.out.println(creature.getPosition().x*64 + " " + creature.getPosition().y*64);
					creature.setDrawScale(scale);
					activate(creature);
//					System.out.println(lights.size + ": lights size");
//					System.out.println(lights.get(index) + ": lights");
//					System.out.println("lights "+lights.get(index).getX() + " "+lights.get(index).getY());

					attachVision(creature, lights.get(n));

					creatures.add(creature);
				}


			} else if (layerName.equals("creature_bounds")) {
				//This is the creature patrol area




			} else if (layerName.equals("Annette")) {
//				System.out.println("loading annette");


				annette = new AnnetteModel();
				JsonValue annetteData = null;
				JsonValue annetteBounds = null;
				for (int f = 0; f < 2; f++) {
					JsonValue obj = objects.get(f);
					if (obj.get("name").asString().equals("annette")) {
						annetteData = obj.get("properties");
					} else if (obj.get("name").asString().equals("annette_box")) {
						annetteBounds = obj;
//						System.out.println(obj);
					}
				}

				annette.initialize(annetteData, annetteBounds, pSize[1]);
				annette.setDrawScale(scale);
				activate(annette);

				// Create the light indicating the move in place range.
				createRadiusofPower();
				attachPowerRadius(getAnnette(), radiusOfPower);


			} else if (layerName.equals("box_Boundaries")) {
//				System.out.println("loading boundaries");
				HashMap<String, JsonValue> boundaryArray = new HashMap<String, JsonValue>();

//				float offsetx = 0;
//                float offsety = 0;


				//assign building and box values to indexes in hashmaps
//				System.out.println(objects.size);
				for (int j = 0; j < objects.size; j++) {
					JsonValue obj = objects.get(j);
					InteriorModel obj2 = new InteriorModel();
					float[] pos = {obj.get("x").asFloat() / 64, obj.get("y").asFloat() / 64 + obj.get("height").asFloat()/64};
					float[] size = {obj.get("width").asFloat() / 64, obj.get("height").asFloat() / 64};
					float[] pad = {0.1f, 0.1f};
					String debugColor = "red";


//                    System.out.println(objName);
//						System.out.println(pSize[0]);
//						System.out.println(pSize[1]);
//                    String objName = obj.get("name").asString();
					obj2.initialize(pos, size, pad, debugColor, null, pSize[1], 0, 0);
					obj2.setDrawScale(scale);
					activate(obj2);
//					System.out.println(pos[0] + " " + pos[1]);
//					System.out.println("activating building");
					mazes.add(obj2);



//					String[] bSplit = objName.split("boundary");
//
//					boundaryArray.put(bSplit[1], obj);
//					System.out.println(bSplit[1]);
//					System.out.println("boundary "+j + "2 : "+ bSplit[1]);

				}

			}




			else if(layerName.equals("Buildings")){
//				System.out.println("loading buildings");

				HashMap<String, JsonValue> numToBuilding = new HashMap<String, JsonValue>();
				HashMap<String, JsonValue> numToBox = new HashMap<String, JsonValue>();

                // default offset is 0
                float offsetx = 0;
                float offsety = 0;

				//assign building and box values to indexes in hashmaps
				for(int j = 0; j< objects.size; j++){

					JsonValue obj = objects.get(j);
					String objName = obj.get("name").asString();

					// if offsets are being defined
					if (layer.has("offsetx") || layer.has("offsety")) {
					    // offsets are defined in the physics scale
                        offsetx = layer.get("offsetx").asInt()/scale.x;
                        offsety = layer.get("offsety").asInt()/scale.y;
//                        System.out.println("offsetx: " + offsetx + ", offsety: " + offsety);
                    }

					String[] bSplit = objName.split("building");

					if(objName.contains("box")){
						numToBox.put(objName.substring(3),obj);
						//add to building list

						//System.out.println("building "+j + " : "+ objName.substring(3));

					}
					else{
						//add to box list
						numToBuilding.put(bSplit[1],obj);
						//System.out.println("building "+j + "2 : "+ bSplit[1]);

					}

				}

				//initialize buildings
				for(int j = 0; j<numToBuilding.size(); j++){

					TextureRegion film = null;
					JsonValue buildingJSON = numToBuilding.get((j+1) + "").get("properties");
					JsonValue boxJSON = numToBox.get((j+1) +"");
					String textName = buildingJSON.get("texture").asString();


					TextureRegion texture = JsonAssetManager.getInstance().getEntry(textName.trim(), TextureRegion.class);
					film = texture;

//					System.out.println("textname: "+textName + " : " + texture);








                    // BUILDINGS
					InteriorModel obj2 = new InteriorModel();
					float[] pos = {boxJSON.get("x").asFloat()/64,boxJSON.get("y").asFloat()/64 + 1.75f};
					float[] size = {boxJSON.get("width").asFloat()/64,boxJSON.get("height").asFloat()/64};
					float[] pad = { 0.1f, 0.1f};
					String debugColor = "red";

					if(film!= null) {

//						System.out.println("psize");
//						System.out.println(pSize[0]);
//						System.out.println(pSize[1]);

						obj2.initialize(pos, size, pad, debugColor, film, pSize[1], offsetx, offsety);
						obj2.setDrawScale(scale);
						activate(obj2);
//						System.out.println(pos[0] + " " + pos[1]);
//						System.out.println("activating building");
						mazes.add(obj2);
					}
				}


			}
			else if(layerName.equals("Outline")){
				int[] data = layer.get("data").asIntArray();
				int height = layer.get("height").asInt();
				int width = layer.get("width").asInt();


			}
			else if(layerName.equals("Base")){
//				System.out.println("loading background");


				int[] data = layer.get("data").asIntArray();
				int height = layer.get("height").asInt();
				int width = layer.get("width").asInt();


				for(int j = 0; j < height*width; j++){
					//dataMatrix[j%width][height - 1 - ((j - (6%width))/height)] = data[j];
//                    System.out.println("width: " + width + ", height: " + height);
					int newx = j % width ; //(height - 1 - ((j - (6%width))/height));
					int newy = height - (j / width);//(j%width);
//					System.out.println("newx "+ newx + " new y " + newy);


					int f = 0;
					while(f<data[j] && !idToTexture.containsKey(data[j] - f)){

						f++;
					}
					//System.out.println(data[j] + " : "+ (data[j] - f));
					String texName = idToTexture.get(data[j] - f);
//					System.out.println(texName);
					TextureRegion texture = JsonAssetManager.getInstance().getEntry(texName, TextureRegion.class);

					// IMPORTANT PROBLEM: TEXTURE IS NULL
					if(texture != null) {
//						System.out.println(texture.getRegionHeight());
						TextureRegion[][] textures = texture.split(64, 64);
//						System.out.println(f % textures.length + " " + f / textures.length);
						TextureRegion texNew = textures[f / textures[0].length][f % textures[0].length];
						tiles.add(new BackgroundModel(newx, newy, texNew));
					}



				}
			}
			else if (layerName.equals("Exit")){
//				System.out.println("loading exit");


				JsonValue exitValues = null;
				JsonValue boundValues = null;


				for(int j = 0; j< objects.size; j++){

					JsonValue obj = objects.get(j);
					String name = obj.get("name").asString();
					if(name.equals("exit")){
						exitValues = obj.get("properties");
					}
					else{
						boundValues = obj;
					}


				}
				//check ids equal to specific objects
				goalDoor = new ExitModel();
				float x = boundValues.get("x").asFloat()/64;
				float y = boundValues.get("y").asFloat()/64;
				float[] pos = {x,y};
				float width = boundValues.get("width").asFloat();
				float height = boundValues.get("height").asFloat();
				String debugC = "yellow";
				String tex = exitValues.get("texture").asString();
				goalDoor.initialize(pos,width,height,debugC, tex, pSize);
				goalDoor.setDrawScale(scale);
				activate(goalDoor);

			}
		}





//		JsonValue bounds = levelFormat.getChild("exterior");
//		while (bounds != null) {
//			ExteriorModel obj = new ExteriorModel();
//			obj.initialize(bounds);
//			obj.setDrawScale(scale);
//			activate(obj);
//			barriers.add(obj);
//			bounds = bounds.next();
//		}

		//attachLights(creature);

		// Create cone lights to be line of sights of creatures.

		// Create the creatures and attach light sources
		// Create box
		box = new BoxModel(1, 1);

		if (distraction != null) {
			distraction.setAlive(false);
		}

	}

	/**
	 * Lays out the game geography from the given JSON file
	 *
	 */
//	public void populateOld(JsonValue levelFormat) {
//
//		background = new Texture(BACKGROUND_FILE);
////        float[] pSize = levelFormat.get("physicsSize").asFloatArray();
////        int[] gSize = levelFormat.get("graphicSize").asIntArray();
//
//		float[] pSize = {32, 24};
//		int[] gSize = {32*64,24*64};
//
//		world = new World(Vector2.Zero, false);
//		bounds = new Rectangle(0, 0, pSize[0], pSize[1]);
//		scale.x = gSize[0] / pSize[0];
//		scale.y = gSize[1] / pSize[1];
//
//		// FPS is hardcoded now
//		int[] fps = { 20,  60};
//		maxFPS = fps[1];
//		minFPS = fps[0];
//		timeStep = 1.0f / maxFPS;
//		maxSteps = 1.0f + maxFPS / minFPS;
//		maxTimePerFrame = timeStep * maxSteps;
//
//
//        // Create the lighting if appropriate
//        if (levelFormat.has("lighting")) {
//            //initLighting(levelFormat.get("lighting"));
//        }
//
//        // Add level goal
//        goalDoor = new ExitModel();
//        //goalDoor.initialize(levelFormat.get("exit"));
//        goalDoor.setDrawScale(scale);
//        activate(goalDoor);
//
//        JsonValue bounds = levelFormat.getChild("exterior");
//        while (bounds != null) {
//            ExteriorModel obj = new ExteriorModel();
//            obj.initialize(bounds);
//            obj.setDrawScale(scale);
//            activate(obj);
//            barriers.add(obj);
//            bounds = bounds.next();
//        }
//
//        JsonValue walls = levelFormat.getChild("interior");
//        while (walls != null) {
//            InteriorModel obj = new InteriorModel();
//           // obj.initialize(walls);
//            obj.setDrawScale(scale);
//            activate(obj);
//            mazes.add(obj);
//            walls = walls.next();
//        }
//
//        // Create Annette
//        annette = new AnnetteModel();
//        JsonValue annettedata = levelFormat.get("annette");
//		JsonValue downdata = levelFormat.get("annetteDown");
//		JsonValue updata = levelFormat.get("annetteUp");
//
//        //annette.initialize(annettedata);
//        annette.setDrawScale(scale);
//        activate(annette);
//
//		// Create the light indicating the move in place range.
//		createRadiusofPower();
//		attachPowerRadius(getAnnette(),radiusOfPower);
//
//
//        // Create cone lights to be line of sights of creatures.
//        createLineofSight(levelFormat.get("vision"));
//        // Create the creatures and attach light sources
//		createCreatures(levelFormat.get("creatures"));
//        //createCreature(levelFormat.get("creatures"), "snail", 0);
//        //createCreature(levelFormat.get("creatures"), "tarasque", 1);
//        //createCreature(levelFormat.get("creatures"), "blanche", 2);
//
//        // Create box
//        box = new BoxModel(1, 1);
//
//        if (distraction != null) {
//            distraction.setAlive(false);
//        }
//
//        // Creater indicator
//        indicator = new IndicatorModel();
//
//    }

//    public void addObjects(JsonValue levelFormat) {
//		JsonValue buildings = levelFormat.getChild("Buildings");
//		while (buildings != null) {
//			ExteriorModel obj = new ExteriorModel();
//			obj.initialize(buildings);
//			obj.setDrawScale(scale);
//			activate(obj);
//			barriers.add(obj);
//			buildings = buildings.next();
//		}
//
//		JsonValue boundaries = levelFormat.getChild("Boundaries");
//		while (boundaries != null) {
//			InteriorModel obj = new InteriorModel();
//			//obj.initialize(boundaries);
//			obj.setDrawScale(scale);
//			activate(obj);
//			mazes.add(obj);
//			boundaries = boundaries.next();
//		}
//
//
//	}


    public boolean isDistraction() {
        if (distraction != null) {
            return distraction.getAlive();
        }
        else {
            return false;
        }
    }

    public void createBox(JsonValue levelFormat) {
        box = new BoxModel(annette.getX(), annette.getY());
        JsonValue layers = levelFormat.get("layers");
        for(int i =0; i< layers.size; i++){
            if(layers.get(i).get("name").asString().equals("Bird")){
                //initialize bird here
                JsonValue distractiondata = layers.get(i).get("objects");
                JsonValue birdinfo = distractiondata.get(0).get("properties");
                JsonValue birdbox = distractiondata.get(1);
                distraction.initialize(birdinfo, birdbox);

                distraction.setDrawScale(scale);
                activate(distraction);
                distraction.setActive(true);
                distraction.setAlive(true);
            }
        }

//		distraction.activatePhysics(world);
    }

    public void createDistraction(JsonValue levelFormat) {
        distraction = new DistractionModel(annette.getX(), annette.getY(), false, annette.getDirection());
        JsonValue layers = levelFormat.get("layers");
        for(int i =0; i< layers.size; i++){
        	if(layers.get(i).get("name").asString().equals("Bird")){
        		//initialize bird here
				JsonValue distractiondata = layers.get(i).get("objects");
				JsonValue birdinfo = distractiondata.get(0).get("properties");
				JsonValue birdbox = distractiondata.get(1);
				distraction.initialize(birdinfo, birdbox);

				distraction.setDrawScale(scale);
				activate(distraction);
				distraction.setActive(true);
				distraction.setAlive(true);
			}
		}

//		distraction.activatePhysics(world);
    }


    /**
	 * Creates the ambient lighting for the level
	 *
	 * This is the amount of lighting that the level has without any light sources.
	 * completely visible.
	 *
	 *
	 */
	private void initLighting(float[] color, boolean gamma, boolean diffuse, int blur, ObstacleCanvas canvas) {
		raycamera = new OrthographicCamera(bounds.width,bounds.height);
//		raycamera.position.set(bounds.width/2.0f, bounds.height/2.0f, 0);
		raycamera.update();

		RayHandler.setGammaCorrection(gamma);
		RayHandler.useDiffuseLight(diffuse);
		rayhandler = new RayHandler(world, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		rayhandler.setCombinedMatrix(raycamera);

		rayhandler.setAmbientLight(color[0], color[1], color[2], color[3]);
		normal_r = color[0];
		normal_g = color[1];
		normal_b = color[2];
		normal_alp = color[3];
		rayhandler.setBlur(blur > 0);
		rayhandler.setBlurNum(blur);
	}

	public void darkenLights(RayHandler r){
		r.setAmbientLight(normal_r * 0.2f,normal_g * 0.f,normal_b * 0.2f,normal_alp * 0.2f);
	}

	public void brightenLights(RayHandler r){
		r.setAmbientLight(normal_r,normal_g,normal_b,normal_alp);
	}

	/**
	 * Creates the points lights for the level
	 *
	 * Point lights show light in all direction.  We treat them differently from cone
	 * lights because they have different defining attributes.  However, all lights are
	 * added to the lights array.  This allows us to cycle through both the point lights
	 * and the cone lights with activateNextLight().
	 *
	 * All lights are deactivated initially.  We only want one active light at a time.
	 *
	 *
	 */
	private void createRadiusofPower() {
		float[] color = {0.6f, 0.0f, 0.0f, 0.3f};
		float[] pos = {  0,   0};
		float dist  = 11;
		int rays = 512;

		PointSource point = new PointSource(rayhandler, rays, Color.WHITE, dist, pos[0], pos[1]);
		point.setColor(color[0],color[1],color[2],color[3]);
		point.setSoft(false);
		point.setXray(true);

		// Create a filter to exclude see through items
		Filter f = new Filter();
		f.maskBits = bitStringToComplement("0010");
		point.setContactFilter(f);
		point.setActive(false); // TURN ON LATER

		radiusOfPower = point;
	}

	/**
	 * Creates the line of sights (cone lights) for the level
	 *
	 * Cone lights show light in a cone with a direction.  We treat them differently from
	 * point lights because they have different defining attributes.
	 *
	 * @param  json	the JSON tree defining the list of cone lights
	 */
	private void createLineofSight(JsonValue json) {
		ConeSource[] lightArr = new ConeSource[3];
		int type = 0;
//		System.out.println(json);
//		for (int i = 0; i < json.size; i++) {
//			JsonValue obj = json.get(i);
//			JsonValue light = obj.get("properties");
//			System.out.println(obj);
			float r = json.get("r").asFloat();
			float g = json.get("g").asFloat();
			float b = json.get("b").asFloat();
			float a = json.get("a").asFloat();
			float[] color = {r, g, b, a};
			float[] pos = {0, 0};//{obj.get("x").asFloat() / 64, obj.get("x").asFloat() / 64};
//			System.out.println("pos lights "+pos[0]*64+" "+ pos[1]*64);
			float dist = json.get("distance").asFloat();
			float face = json.get("facing").asFloat();
			float angle = json.get("angle").asFloat();
			int rays = json.get("rays").asInt();


			ConeSource cone = new ConeSource(rayhandler, rays, Color.WHITE, dist, pos[0], pos[1], face, angle);
			cone.setColor(color[0], color[1], color[2], color[3]);
			cone.setSoft(json.getBoolean("soft"));

			// Create a filter to exclude see through items
			Filter f = new Filter();
			f.maskBits = bitStringToComplement(json.getString("excludeBits"));
			cone.setContactFilter(f);
			lights.add(cone);
			System.out.println(lights.size);
		}
//	}
			//cone.setActive(false); // TURN ON LATER
//			int index = 0;
//			String name = obj.get("name").asString();
//			if(name.contains("dragon_vision")) {
//				index = 1;
//				type = 1;
//			}
//			else if (name.equals("lady_vision")) {
//				index = 2;
//				type = 2;
//			}
////			System.out.println("index is: " + index);
//			lightArr[index] = cone;
//
//			}
//		for(int i = 0; i<lightArr.length;i++){
//			int t = 0;
//			while (t<type) {
//				lights.add(lightArr[i]);
//				t++;
//			}
//			System.out.println(lights.get(i));
//		}
//        System.out.println("lightArr.length = "+lightArr.length);
//        System.out.println("lights.size: " + lights.size);
//        System.out.println("lights 0 == null: "+lights.get(0));



	/**
	 * loop through object list and add
	 * @param creaturejson
	 */
//	public void createCreatures(JsonValue creaturejson){
//		JsonValue creaturedata = creaturejson.child();
//		int index = 0;
//    	while (creaturedata != null) {
////			System.out.println("index = " + index);
//			CreatureModel creature = new CreatureModel();
//			//creature.initialize(creaturedata);
//			creature.setDrawScale(scale);
//			activate(creature);
//			attachVision(creature, lights.get(index));
//			creatures.add(creature);
//			creaturedata = creaturedata.next();
//			index = index + 1;
//		}
//	}



	/**
	 * Attaches a cone of vision to a creature.
	 *
	 * Lights are offset form the center of the avatar according to the initial position.
	 * By default, a light ignores the body.  This means that putting the light inside
	 * of these bodies fixtures will not block the light.  However, if a light source is
	 * offset outside of the bodies fixtures, then they will cast a shadow.
	 *
	 */
	public void attachVision (CreatureModel creature, LightSource light){
//		System.out.println(light.getX() + " " + light.getY());
		light.setPosition(creature.getX()+creature.getWidth()/2, creature.getY()+creature.getHeight()/2);
		light.setDirection(0);
//		light.attachToBody(creature.getBody(), 0, 0, light.getDirection());

		creature.setVision(light);
	}

	public void attachPowerRadius (AnnetteModel annette, LightSource light){
		light.attachToBody(annette.getBody());
	}

	/**
	 * Disposes of all resources for this model.
	 *
	 * Because of all the heavy weight physics stuff, this method is absolutely
	 * necessary whenever we reset a level.
	 */
	public void dispose() {
		for(LightSource light : lights) {
			if(light!= null)
			light.remove();
		}
		lights.clear();

		if (rayhandler != null) {
			rayhandler.dispose();
			rayhandler = null;
		}

		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
			obj.dispose();
		}
		objects.clear();

		for(CreatureModel c : creatures) {
			c.deactivatePhysics(world);
			c.dispose();
		}
		creatures.clear();

		if (world != null) {
			world.dispose();
			world = null;
		}

		background = null;

		tiles.clear();

//		if (distraction != null) {
//			distraction.setAlive(false);
//			objects.remove(distraction);
////			distraction = null;
//		}
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * @param obj The object to add
	 */
	protected void activate(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);
		obj.activatePhysics(world);
	}

	/**
	 * Returns true if the object is in bounds.
	 *
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
	 *
	 * @return true if the object is in bounds.
	 */
	private boolean inBounds(Obstacle obj) {
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
		boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
		return horiz && vert;
	}

	/**
	 * Updates all of the models in the level.
	 *
	 * This is borderline controller functionality.  However, we have to do this because
	 * of how tightly coupled everything is.
	 *
	 * @param dt the time passed since the last frame
	 */
	public boolean update(float dt) {
		if (fixedStep(dt)) {
			if (rayhandler != null) {
				rayhandler.update();

				}


			annette.update(dt);
			for (CreatureModel creature : creatures){
				creature.update(dt);
				creature.getVision().setPosition(creature.getX()+creature.getWidth()/2,creature.getY()+creature.getHeight());
				if (creature.getMovement().x > 0) {
					creature.getVision().setDirection(0);
					creature.getVision().setPosition(creature.getX()-creature.getWidth()/2,creature.getY()+creature.getHeight());
				}
				else if (creature.getMovement().x < 0) {
					creature.getVision().setDirection(180);
					creature.getVision().setPosition(creature.getX()-creature.getWidth()/4,creature.getY()+creature.getHeight());
				}
				else if (creature.getMovement().y < 0) {
					creature.getVision().setDirection(270);
					creature.getVision().setPosition(creature.getX()+creature.getWidth()/4,creature.getY()-creature.getHeight()/2);
				}
				else if (creature.getMovement().y > 0) {
					creature.getVision().setDirection(90);
					creature.getVision().setPosition(creature.getX()+creature.getWidth()/4,creature.getY()+creature.getHeight()/2);
				}
//				System.out.println(creature.getPosition());
			}
			goalDoor.update(dt);
			box.update(dt);
			if (indicator != null){
			    indicator.update(dt);
            }
            if (distraction!=null) {
//				System.out.println(distraction.getX());
//				System.out.println(distraction.getY());

				distraction.update(dt);
				if (!distraction.getAlive()) {
					objects.remove(distraction);
				}
            }

			return true;
		}
		return false;
	}

	/**
	 * Fixes the physics frame rate to be in sync with the animation framerate
	 *
	 * http://gafferongames.com/game-physics/fix-your-timestep/
	 *
	 * @param dt the time passed since the last frame
	 */
	private boolean fixedStep(float dt) {
		if (world == null) return false;

		physicsTimeLeft += dt;
		if (physicsTimeLeft > maxTimePerFrame) {
			physicsTimeLeft = maxTimePerFrame;
		}

		boolean stepped = false;
		while (physicsTimeLeft >= timeStep) {
			world.step(timeStep, WORLD_VELOC, WORLD_POSIT);
			physicsTimeLeft -= timeStep;
			stepped = true;
		}
		return stepped;
	}

	/**
	 * Draws the level to the given game canvas
	 *
	 * If debug mode is true, it will outline all physics bodies as wireframes. Otherwise
	 * it will only draw the sprite representations.
	 *
	 * @param canvas	the drawing context
	 */
	public void draw(ObstacleCanvas canvas) {

		canvas.clear();
		Color color;

		AnnetteModel annette = getAnnette();
		BoxModel box = getBox();

		Vector2 pos = annette.getPosition();
		Vector2 scale = annette.getDrawScale();

		// Accounts for edges of screen
		float cameraXStart = canvas.getWidth() * 2.5f/(5.0f * scale.x);
//		float cameraXStart = 0;
		float cameraYStart = canvas.getHeight() * 3.05f/(5.0f * scale.y);
//		float cameraYStart = 0;
		float cameraXEnd = canvas.getWidth() * 1.74f / scale.x;
		float cameraYEnd = canvas.getHeight() * 1.1f / scale.y;
		float tx = pos.x <= cameraXStart ? cameraXStart : (pos.x >= cameraXEnd ? cameraXEnd : pos.x);
		float ty = pos.y <= cameraYStart ? cameraYStart : (pos.y >= cameraYEnd ? cameraYEnd : pos.y);
//		//System.out.println(bounds.x + " " + bounds.y+" "+bounds.width+" "+bounds.height);

		//System.out.println(pos.x + " " + pos.y);

//		float tx = pos.x;
//		float ty = pos.y;

		oTran.setToTranslation(TRANSLATION*tx + canvas.getWidth()/2, TRANSLATION*ty + canvas.getHeight()/2);
		wTran.setToTranslation(canvas.getWidth()/2,canvas.getHeight()/2);
//		oTran.mul(wTran);

		// Draw the sprites first (will be hidden by shadows)
		canvas.begin();//oTran);
		canvas.draw(background, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
		//canvas.draw(background, 0, 0);

		//DRAWS BACKGROUND TILES HERE
		for(int i =0;i< tiles.size; i++){
			tiles.get(i).draw(canvas);
//			System.out.println(annette.getX());

		}



		canvas.end();

		if (rayhandler != null) {
//			rayhandler.useCustomViewport((int)(TRANSLATION*tx) + canvas.getWidth()/2, (int)(TRANSLATION*ty) + canvas.getHeight()/2, canvas.getWidth(), canvas.getHeight());
//			raycamera.position.set(tx, ty, 0);
//			raycamera.zoom = raycamera.zoom 2;
			raycamera.update();
			rayhandler.setCombinedMatrix(raycamera);

			rayhandler.render();

		}

		canvas.begin();//oTran);

		int n = objects.size();
		for (int x=0; x<n; x++) // bubble sort outer loop
		{
			for (int i=0; i < n - x - 1; i++) {
				if (objects.get(i).getLowestY() < (objects.get(i+1).getLowestY()) )
				{
					Obstacle temp = objects.get(i);
					objects.set(i,objects.get(i+1) );
					objects.set(i+1, temp);
				}
			}
		}

//		canvas.begin();

//		System.out.println("BACKGROUND!!!");
//		canvas.end();


		for(Obstacle obj : objects) {
			obj.draw(canvas);
//			System.out.println(obj.getName());
		}

		if (box.getDeactivated()) {
			color = Color.DARK_GRAY;
			color.a = 1;
			box.drawState(canvas, color);
			alpha = 255;
		}
		else if (box.getDeactivating())	{
			color = Color.GRAY;
			float dist = (float)Math.hypot(Math.abs(box.getPosition().x - annette.getPosition().x), Math.abs(box.getPosition().y - annette.getPosition().y));
			float temp = (1 - ((dist - BOX_MARGIN) / BoxModel.OUTER_RADIUS)) * MAX_ALPHA;
			temp = temp - 5;
			alpha = (int) temp;
			color.a = alpha;
			box.drawState(canvas, color);
		}

		canvas.end();

		// Draw debugging on top of everything.
		if (debug) {
			canvas.beginDebug();//oTran);
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}

	}

	public Affine2 getoTran(){return oTran;}

	/**
	 * Returns a string equivalent to the sequence of bits in s
	 *
	 * This function assumes that s is a string of 0s and 1s of length < 16.
	 * This function allows the JSON file to specify bit arrays in a readable
	 * format.
	 *
	 * @param s the string representation of the bit array
	 *
	 * @return a string equivalent to the sequence of bits in s
	 */
	public static short bitStringToShort(String s) {
		short value = 0;
		short pos = 1;
		for(int ii = s.length()-1; ii >= 0; ii--) {
			if (s.charAt(ii) == '1') {
				value += pos;
			}
			pos *= 2;
		}
		return value;
	}

	/**
	 * Returns a string equivalent to the COMPLEMENT of bits in s
	 *
	 * This function assumes that s is a string of 0s and 1s of length < 16.
	 * This function allows the JSON file to specify exclusion bit arrays (for masking)
	 * in a readable format.
	 *
	 * @param s the string representation of the bit array
	 *
	 * @return a string equivalent to the COMPLEMENT of bits in s
	 */
	public static short bitStringToComplement(String s) {
		short value = 0;
		short pos = 1;
		for(int ii = s.length()-1; ii >= 0; ii--) {
			if (s.charAt(ii) == '0') {
				value += pos;
			}
			pos *= 2;
		}
		return value;
	}
}
