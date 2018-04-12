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
	public static final float TRANSLATION = -50.0f;

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

	private static final String BACKGROUND_FILE = "textures/alpha_demo.png";
	private Texture background;

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
			System.out.println(" visioncone of index " + index + " does not exist.");
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

	public void populateNew(JsonValue levelFormat){


		background = new Texture(BACKGROUND_FILE);

		int tileHeight = levelFormat.get("height").asInt();
		int tileWidth = levelFormat.get("width").asInt();
		//how many tiles
		float[] pSize = {(float)tileHeight, (float)tileWidth};

		int tileSize = levelFormat.get("tilewidth").asInt();

        //pixels width, height
		int[] gSize = {tileWidth*tileSize,tileHeight*tileSize};


		world = new World(Vector2.Zero, false);
		bounds = new Rectangle(0, 0, pSize[0], pSize[1]);
		scale.x = gSize[0] / pSize[0];
		scale.y = gSize[1] / pSize[1];

		// FPS is hardcoded now
		int[] fps = { 20,  60};
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
			float[] colors = {c0,c1,c2,c3};
			boolean gamma = levelFormat.get("lightingGamma").asBoolean();
			boolean diffuse = levelFormat.get("lightingDiffuse").asBoolean();
			int blur = levelFormat.get("lightingBlur").asInt();
			initLighting(colors, gamma, diffuse, blur);
		}
//		createPointLights(levelFormat.get("pointlights"));
//		createConeLights(levelFormat.get("conelights"));

		// Add level goal

		JsonValue idMap = levelFormat.get("tilesets");
		JsonValue exitId = null;
		int annetteId = 0;

		//assign ID numbers to assets
		for(int i = 0; i< idMap.size; i++){
			String name = idMap.get(i).get("name").asString();
			//TODO: this only works for tiles
			//TODO: add this to work for object ids too

			if(name.equals("exit")){
				exitId = idMap.get(i);
			}
			else if (name.equals("Annette")){
				annetteId = idMap.get(i).get("firstgid").asInt();
			}
		}

		//loop through tiles to find specified ids and initialize
		JsonValue layers = levelFormat.get("layers");
		for(int i = 0; i < layers.size; i++){
			JsonValue layer = layers.get(i);

			if(layer.get("type").asString().equals("tilelayer")){
				//GOES THRU TILE LAYER HERE

				int[] data = layer.get("data").asIntArray();
				int height = layer.get("height").asInt();
				int width = layer.get("width").asInt();
				int[][] dataMatrix = new int[width][height];

				for(int j = 0; j < height*width; j++){
					dataMatrix[j%width][(j - (6%width))/height] = data[j];
				}

				//TODO: draw tiles here

			}
			else{
				//GOES THRU OBJECT LAYER HERE
				JsonValue objects = layer.get("objects");
				for(int j = 0; j< objects.size; j++){
					JsonValue obj = objects.get(j);
					int id = obj.get("id").asInt();
					//check ids equal to specific objects
					if(exitId.get("firstgid").asInt() == id){
						goalDoor = new ExitModel();
						float x = obj.get("x").asFloat();
						float y = obj.get("y").asFloat();
						float[] pos = {x,y};
						float width = obj.get("width").asFloat();
						float height = obj.get("height").asFloat();
						String debugC = obj.get("debugColor").asString();
						String tex = obj.get("texture").asString();
						goalDoor.initialize(pos,width,height,debugC, tex);
						goalDoor.setDrawScale(scale);
						activate(goalDoor);
					}
					else if (annetteId == id){
						// Create Annette
						annette = new AnnetteModel();
						JsonValue annettedata = levelFormat.get("annette");
						JsonValue annetteBounds = levelFormat.get("annette_box");
						annette.initialize(annettedata, annetteBounds);
						annette.setDrawScale(scale);
						activate(annette);
					}
//					else if( == id){
//						createLineofSight(levelFormat.get("vision"));
//						// Create the creatures and attach light sources
//						createCreatures(levelFormat.get("creatures"));
//					}
//					else{
//
//					}
				}

				//this is an object layer

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

		JsonValue walls = levelFormat.getChild("interior");
		while (walls != null) {
			InteriorModel obj = new InteriorModel();
			obj.initialize(walls);
			obj.setDrawScale(scale);
			activate(obj);
			mazes.add(obj);
			walls = walls.next();
		}


		//attachLights(creature);

		// Create cone lights to be line of sights of creatures.
		createLineofSight(levelFormat.get("vision"));
		// Create the creatures and attach light sources
		createCreatures(levelFormat.get("creatures"));
		//createCreature(levelFormat.get("creatures"), "snail", 0);
		//createCreature(levelFormat.get("creatures"), "tarasque", 1);
		//createCreature(levelFormat.get("creatures"), "blanche", 2);

		// Create box
		box = new BoxModel(1, 1);

		if (distraction != null) {
			distraction.setAlive(false);
		}

	}

	/**
	 * Lays out the game geography from the given JSON file
	 *
	 * @param levelFormat	the JSON tree defining the level
	 */
	public void populate(JsonValue levelFormat) {

		background = new Texture(BACKGROUND_FILE);
        float[] pSize = levelFormat.get("physicsSize").asFloatArray();
        int[] gSize = levelFormat.get("graphicSize").asIntArray();

        world = new World(Vector2.Zero, false);
        bounds = new Rectangle(0, 0, pSize[0], pSize[1]);
        scale.x = gSize[0] / pSize[0];
        scale.y = gSize[1] / pSize[1];

        // Compute the FPS
        int[] fps = levelFormat.get("fpsRange").asIntArray();
        maxFPS = fps[1];
        minFPS = fps[0];
        timeStep = 1.0f / maxFPS;
        maxSteps = 1.0f + maxFPS / minFPS;
        maxTimePerFrame = timeStep * maxSteps;

        // Create the lighting if appropriate
        if (levelFormat.has("lighting")) {
            //initLighting(levelFormat.get("lighting"));
        }

        // Add level goal
        goalDoor = new ExitModel();
        //goalDoor.initialize(levelFormat.get("exit"));
        goalDoor.setDrawScale(scale);
        activate(goalDoor);

        JsonValue bounds = levelFormat.getChild("exterior");
        while (bounds != null) {
            ExteriorModel obj = new ExteriorModel();
            obj.initialize(bounds);
            obj.setDrawScale(scale);
            activate(obj);
            barriers.add(obj);
            bounds = bounds.next();
        }

        JsonValue walls = levelFormat.getChild("interior");
        while (walls != null) {
            InteriorModel obj = new InteriorModel();
            obj.initialize(walls);
            obj.setDrawScale(scale);
            activate(obj);
            mazes.add(obj);
            walls = walls.next();
        }

        // Create Annette
        annette = new AnnetteModel();
        JsonValue annettedata = levelFormat.get("annette");
		JsonValue downdata = levelFormat.get("annetteDown");
		JsonValue updata = levelFormat.get("annetteUp");

        //annette.initialize(annettedata);
        annette.setDrawScale(scale);
        activate(annette);

		// Create the light indicating the move in place range.
		createRadiusofPower(levelFormat.get("powerradius"));
		attachPowerRadius(getAnnette(),radiusOfPower);


        // Create cone lights to be line of sights of creatures.
        createLineofSight(levelFormat.get("vision"));
        // Create the creatures and attach light sources
		createCreatures(levelFormat.get("creatures"));
        //createCreature(levelFormat.get("creatures"), "snail", 0);
        //createCreature(levelFormat.get("creatures"), "tarasque", 1);
        //createCreature(levelFormat.get("creatures"), "blanche", 2);

        // Create box
        box = new BoxModel(1, 1);

        if (distraction != null) {
            distraction.setAlive(false);
        }
    }

    public void addObjects(JsonValue levelFormat) {
		JsonValue buildings = levelFormat.getChild("Buildings");
		while (buildings != null) {
			ExteriorModel obj = new ExteriorModel();
			obj.initialize(buildings);
			obj.setDrawScale(scale);
			activate(obj);
			barriers.add(obj);
			buildings = buildings.next();
		}

		JsonValue boundaries = levelFormat.getChild("Boundaries");
		while (boundaries != null) {
			InteriorModel obj = new InteriorModel();
			obj.initialize(boundaries);
			obj.setDrawScale(scale);
			activate(obj);
			mazes.add(obj);
			boundaries = boundaries.next();
		}


	}


    public boolean isDistraction() {
        if (distraction != null) {
            return distraction.getAlive();
        }
        else {
            return false;
        }
    }

    public void createDistraction(JsonValue levelFormat) {
        distraction = new DistractionModel(annette.getX(), annette.getY(), false, annette.getDirection());
        JsonValue distractiondata = levelFormat.get("distraction");
		distraction.initialize(distractiondata);
        distraction.setDrawScale(scale);
        activate(distraction);
        distraction.setActive(true);
        distraction.setAlive(true);
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
	private void initLighting(float[] color, boolean gamma, boolean diffuse, int blur) {
		raycamera = new OrthographicCamera(bounds.width,bounds.height);
		raycamera.position.set(bounds.width/2.0f, bounds.height/2.0f, 0);
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
		r.setAmbientLight(normal_r * 0.3f,normal_g * 0.3f,normal_b * 0.3f,normal_alp * 0.2f);
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
	 * @param  light	the JSON tree defining the list of point lights
	 */
	private void createRadiusofPower(JsonValue light) {
		float[] color = light.get("color").asFloatArray();
		float[] pos = light.get("pos").asFloatArray();
		float dist  = light.getFloat("distance");
		int rays = light.getInt("rays");

		PointSource point = new PointSource(rayhandler, rays, Color.WHITE, dist, pos[0], pos[1]);
		point.setColor(color[0],color[1],color[2],color[3]);
		point.setSoft(light.getBoolean("soft"));
		point.setXray(true);

		// Create a filter to exclude see through items
		Filter f = new Filter();
		f.maskBits = bitStringToComplement(light.getString("excludeBits"));
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
		JsonValue light = json.child();
		while (light != null) {
			float[] color = light.get("color").asFloatArray();
			float[] pos = light.get("pos").asFloatArray();
			float dist  = light.getFloat("distance");
			float face  = light.getFloat("facing");
			float angle = light.getFloat("angle");
			int rays = light.getInt("rays");

			ConeSource cone = new ConeSource(rayhandler, rays, Color.WHITE, dist, pos[0], pos[1], face, angle);
			cone.setColor(color[0],color[1],color[2],color[3]);
			cone.setSoft(light.getBoolean("soft"));

			// Create a filter to exclude see through items
			Filter f = new Filter();
			f.maskBits = bitStringToComplement(light.getString("excludeBits"));
			cone.setContactFilter(f);
			//cone.setActive(false); // TURN ON LATER
			lights.add(cone);
			light = light.next();
		}
	}

	/**
	 *
	 * @param creaturejson
	 */
	public void createCreatures(JsonValue creaturejson){
		JsonValue creaturedata = creaturejson.child();
		int index = 0;
    	while (creaturedata != null) {
			System.out.println("index = " + index);
			CreatureModel creature = new CreatureModel();
			creature.initialize(creaturedata);
			creature.setDrawScale(scale);
			activate(creature);
			attachVision(creature, lights.get(index));
			creatures.add(creature);
			creaturedata = creaturedata.next();
			index = index + 1;
		}
	}

	/**
	 *
	 * @param creaturejson
	 * @param name
	 * @param index the index for the creature and the light which the creature is attached to
	 */
	public void createCreature(JsonValue creaturejson, String name, int index){
		CreatureModel creature = new CreatureModel();
		JsonValue creaturedata = creaturejson.get(name);
			creature.initialize(creaturedata);
			creature.setDrawScale(scale);
			creatures.add(creature);
			activate(creature);

			if (creature.getType() == 3){
				lights.get(index).setXray(true);
			}
			attachVision(creature, lights.get(index));
	}

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
		light.attachToBody(creature.getBody(), light.getX(), light.getY(), light.getDirection());
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
			}
			goalDoor.update(dt);
			box.update(dt);
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
		Affine2 oTran = new Affine2();
		Affine2 wTran = new Affine2();

		// Accounts for edges of screen
		float cameraXStart = canvas.getWidth() * 1.25f/(5.0f * scale.x);
//		float cameraXStart = 0;
		float cameraYStart = canvas.getHeight() * 1.25f/(5.0f * scale.y);
//		float cameraYStart = 0;
		float cameraXEnd = canvas.getWidth() * 0.75f / scale.x;
		float cameraYEnd = canvas.getHeight() * 0.75f / scale.y;
		float tx = pos.x <= cameraXStart ? cameraXStart : (pos.x >= cameraXEnd ? cameraXEnd : pos.x);
		float ty = pos.y <= cameraYStart ? cameraYStart : (pos.y >= cameraYEnd ? cameraYEnd : pos.y);

//		oTran.setToTranslation(TRANSLATION*tx, TRANSLATION*ty);
//		wTran.setToTranslation(canvas.getWidth()/2,canvas.getHeight()/2);
//		oTran.mul(wTran);

		// Draw the sprites first (will be hidden by shadows)
		canvas.begin();
//		canvas.draw(background, Color.LIGHT_GRAY, 0, 0, canvas.getWidth(), canvas. getHeight());
		canvas.draw(background, 0, 0);
		canvas.end();

		if (rayhandler != null) {
			rayhandler.useCustomViewport((int)(TRANSLATION*tx) + canvas.getWidth()/2, (int)(TRANSLATION*ty) + canvas.getHeight()/2, canvas.getWidth() * 2, canvas.getHeight() * 2);
			rayhandler.render();

		}

		canvas.begin();

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
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}

	}


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
