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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.util.*;
//import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

import java.lang.reflect.Field;

public class BoxModel extends BoxObstacle {

    /** The thrust factor to convert player input into thrust */
    /** (3/5/18) Not sure if this is needed */
    /** Random value set as of now */
    private static final float DEFAULT_THRUST = 15.0f;

    public static final float INNER_RADIUS = 1.0f;
    public static final float OUTER_RADIUS = 5.0f;
    public static final float GONE_RADIUS = 8.5f;

    /** The texture filmstrip for the box */
    FilmStrip mainBox;
    /** The associated sound for the box (later) */
    String mainSound;
    /** The animation phase for the box (later) */
    boolean mainCycle = true;

    /** Cache object for transforming the force according the object angle */
    /** Not sure if this is needed as of right now 3/5/2018 */
    public Affine2 affineCache = new Affine2();

    // Physics constants
    /** The factor to multiply by the input */
    private float force;
    /** The amount to slow the character down */
    private float damping;
    /** The maximum character speed */
    private float maxspeed;

    /** The current horizontal movement of the character */
    private Vector2 movement = new Vector2();
    /** Whether or not to animate the current frame */
    private boolean animate = true;

    /** If the box has been summoned */
    private boolean doesExist;
    /** If the box is deactivated */
    private boolean deactivated;
    /** If the box is deactivating */
    private boolean deactivating;

    /** FilmStrip pointer to the texture region */
    private FilmStrip filmstrip;
    /** The current animation frame of the avatar */
    private int startFrame;

    /** Cache for internal force calculations */
    private Vector2 forceCache = new Vector2();

    private int boxCool = 0;
    private int shineLimitCountdown = 0;

    /** Time between box frames*/
    private final int boxLimit = 5;
    /** Time between animations of box shining*/
    private final int shineLimit = 100;

    /**
     * Returns the directional movement of this character.
     *
     * This is the result of input times dude force.
     *
     * @return the directional movement of this character.
     */
    public Vector2 getMovement() {
        return movement;
    }

    /**
     * Sets the directional movement of this character.
     *
     * This is the result of input times dude force.
     *
     * @param value the directional movement of this character.
     */
    public void setMovement(Vector2 value) {
        setMovement(value.x,value.y);
    }

    /**
     * Sets the directional movement of this character.
     *
     * This is the result of input times dude force.
     *
     * @param dx the horizontal movement of this character.
     * @param dy the horizontal movement of this character.
     */
    public void setMovement(float dx, float dy) {
        movement.set(dx,dy);
    }

    /**
     * Returns how much force to apply to get the dude moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @return how much force to apply to get the dude moving
     */
    public float getForce() {
        return force;
    }

    /**
     * Sets how much force to apply to get the dude moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @param value	how much force to apply to get the dude moving
     */
    public void setForce(float value) {
        force = value;
    }

    /**
     * Returns how hard the brakes are applied to get a dude to stop moving
     *
     * @return how hard the brakes are applied to get a dude to stop moving
     */
    public float getDamping() {
        return damping;
    }

    /**
     * Sets how hard the brakes are applied to get a dude to stop moving
     *
     * @param value	how hard the brakes are applied to get a dude to stop moving
     */
    public void setDamping(float value) {
        damping = value;
    }

    /**
     * Returns the upper limit on dude left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @return the upper limit on dude left-right movement.
     */
    public float getMaxSpeed() {
        return maxspeed;
    }

    /**
     * Sets the upper limit on dude left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @param value	the upper limit on dude left-right movement.
     */
    public void setMaxSpeed(float value) {
        maxspeed = value;
    }

    /**
     * Returns the current animation frame of this dude.
     *
     * @return the current animation frame of this dude.
     */
    public float getStartFrame() {
        return startFrame;
    }

    /**
     * Sets the animation frame of this dude.
     *
     * @param value	animation frame of this dude.
     */
    public void setStartFrame(int value) {
        startFrame = value;
    }

    /**
     * Returns whether the box has been summoned and is active
     *
     * @return true if the box is active
     */
    public boolean getDoesExist() {
        return doesExist;
    }

    /**
     * Sets whether the box is active or not
     *
     * @param value true if the box is active
     */
    public void setDoesExist(boolean value) {
        doesExist = value;
    }

    /**
     * Returns whether the box is deactivating
     *
     * @return true if the box is deactivating
     */
    public boolean getDeactivated() {
        return deactivated;
    }

    /**
     * Sets whether the box is active or not
     *
     * @param value true if the box is active
     */
    public void setDeactivated(boolean value) {

        deactivated = value;
    }

    /**
     * Returns whether the box is deactivating
     *
     * @return true if the box is deactivating
     */
    public boolean getDeactivating() {
        return deactivating;
    }

    /**
     * Sets whether the box is active or not
     *
     * @param value true if the box is active
     */
    public void setDeactivating(boolean value) {

        deactivating = value;
    }

    /**
     * Returns the force applied to this box.
     *
     * This method returns a reference to the force vector, allowing it to be modified.
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the force applied to this box.
     */
//    public Vector2 getForce() {
//        return force;
//    }

    /**
     * Returns the x-component of the force applied to this box.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the x-component of the force applied to this box.
     */
//    public float getFX() {
//        return force.x;
//    }

    /**
     * Sets the x-component of the force applied to this box.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this box.
     */
//    public void setFX(float value) {
//        force.x = value;
//    }

    /**
     * Returns the y-component of the force applied to this box.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the y-component of the force applied to this box.
     */
//    public float getFY() {
//        return force.y;
//    }

    /**
     * Sets the x-component of the force applied to this box.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this box.
     */
//    public void setFY(float value) {
//        force.y = value;
//    }

    /**
     * Returns the amount of thrust that this box has.
     *
     * Multiply this value times the horizontal and vertical values in the
     * input controller to get the force.
     *
     * @return the amount of thrust that this box has.
     *
     * Not sure if this is needed as of 3/5/2018
     */
    public float getThrust() {
        return DEFAULT_THRUST;
    }

    /**
     * Creates a new box at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public BoxModel(float width, float height) {
        super(width,height);
    }


    public void initialize(JsonValue json, Vector2 annettepos, float xoff, float yoff) {
        setName(json.name());
        float width = json.get("width").asFloat();
        float height = json.get("height").asFloat();
        setWidth(width);
        setHeight(height);
        setPosition(annettepos.x + xoff,annettepos.y + yoff);

        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
        if (json.get("bodytype").asString().equals("static")) {
            setBodyType(BodyDef.BodyType.StaticBody);
        }
        else if (json.get("bodytype").asString().equals("dynamic")) {
            setBodyType(BodyDef.BodyType.DynamicBody);
        }
        else { // kinematic
            setBodyType(BodyDef.BodyType.KinematicBody);
        }
        setDensity(json.get("density").asFloat());
        setFriction(json.get("friction").asFloat());
        setRestitution(json.get("restitution").asFloat());
        setForce(json.get("force").asFloat());
        setDamping(json.get("damping").asFloat());
        setMaxSpeed(json.get("maxspeed").asFloat());
        setStartFrame(json.get("startframe").asInt());

        // Create the collision filter (used for light penetration)
        short collideBits = LevelModel.bitStringToShort(json.get("collideBits").asString());
        short excludeBits = LevelModel.bitStringToComplement(json.get("excludeBits").asString());
        Filter filter = new Filter();
        filter.categoryBits = collideBits;
        filter.maskBits = excludeBits;
        super.setFilterData(filter);

        // Reflection is best way to convert name to color
        Color debugColor;
        try {
            String cname = json.get("debugcolor").asString().toUpperCase();
            Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(cname);
            debugColor = new Color((Color)field.get(null));
        } catch (Exception e) {
            debugColor = null; // Not defined
        }
        int opacity = json.get("debugopacity").asInt();
        debugColor.mul(opacity/255.0f);
        setDebugColor(debugColor);

        // Now get the texture from the AssetManager singleton
        String key = json.get("texture").asString();
        TextureRegion texture = JsonAssetManager.getInstance().getEntry(key, TextureRegion.class);
        try {
            filmstrip = (FilmStrip)texture;
        } catch (Exception e) {
            filmstrip = null;
        }
        setTexture(texture);
//        setSensor(true);
    }


    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method to keep your box from spinning.
     *
     * @param world Box2D world to store body
     *
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // Get the box body from our parent class
        if (!super.activatePhysics(world)) {
            return false;
        }

        // To prevent the body from rotating
        setFixedRotation(true);

        // Initially box fixture is false
        setActive(false);

        return true;
    }

    /**
     * Immediately changes collision of box to "inactive"
     */
    protected void deactivate() {
        short collideBits = LevelModel.bitStringToShort("0010");
        short excludeBits = LevelModel.bitStringToComplement("0001");
        Filter filter = new Filter();
        filter.categoryBits = collideBits;
        filter.maskBits = excludeBits;
        setFilterData(filter);
    }

    /**
     * Immediately resets collision bits of box to "active"
     */
    protected void reactivate() {
        short collideBits = LevelModel.bitStringToShort("0001");
        short excludeBits = LevelModel.bitStringToComplement("0000");
        Filter filter = new Filter();
        filter.categoryBits = collideBits;
        filter.maskBits = excludeBits;
        setFilterData(filter);

    }

    /**
     * Applies the force to the body of the box
     *
     * This method should be called after the force attribute is set.
     */
    public void applyForce() {
        if (!isActive()) {
            return;
        }

        // Only walk or spin if we allow it
        setLinearVelocity(Vector2.Zero);
        setAngularVelocity(0.0f);

        // Apply force for movement
        if (getMovement().len2() > 0f) {
            System.out.println("getmovement " + getMovement().x + " , " + getMovement().y);
            System.out.println("in apply force of boxmodel");
            forceCache.set(getMovement());
            body.applyForce(forceCache,getPosition(),true);

        }
    }

    // Animation methods (DO NOT CHANGE)
    /**
     * Returns the animation node for this box
     *
     * @return the animation node for this box
     */
    public FilmStrip getBoxStrip() { return mainBox; }

    /**
     * Sets the animation node for this box
     *
     * @param  strip the animation node for this box
     */
    public void setBoxStrip(FilmStrip strip) { mainBox = strip; }

    /**
     * Returns the key for the sound to accompany this box
     *
     * The key should either refer to a valid sound loaded in the AssetManager or
     * be empty ("").  If the key is "", then no sound will play.
     *
     * @return the key for the sound to accompany this box
     */
    public String getBoxSound() { return mainSound; }

    /**
     * Sets the key for the sound to accompany this box
     *
     * The key should either refer to a valid sound loaded in the AssetManager or
     * be empty ("").  If the key is "", then no sound will play.
     *
     * @param  key      the key for the sound to accompany this box
     */
    public void setBoxSound(String key) { mainSound = key; }

    /**
     * Animates this box.
     *
     * If the animation is not active, it will reset to the initial animation frame.
     *
     */
    public void update(float dt) {
        // Animate if necessary
        if(shineLimitCountdown==0)
            animate = true;

        if (animate && boxCool == 0) {
                    if (filmstrip != null) {
                        int next = (filmstrip.getFrame()+1) % filmstrip.getSize();
                        if(next < filmstrip.getFrame()) {
                            animate = false;
                            shineLimitCountdown = shineLimit;
                        }
                        else
                        filmstrip.setFrame(next);
                    }
            boxCool = boxLimit;
        }

        else if (boxCool > 0) {
            boxCool--;
        } else if (!animate) {
            if (filmstrip != null) {
                filmstrip.setFrame(startFrame);
            }
            shineLimitCountdown--;
            boxCool = 0;
        }

        super.update(dt);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(ObstacleCanvas canvas) {
//        super.draw(canvas); // Box

        /** (3/5/2018) might need to change the code below; copied from RocketModel */
//        float offsety = mainBox.getRegionHeight()-origin.y;
        canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1 * GameController.TEMP_SCALE,1 * GameController.TEMP_SCALE);
    }

    public void drawState(ObstacleCanvas canvas, Color color) {
        if (texture != null) {
            if (doesExist) {
                canvas.draw(texture, color, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 1 * GameController.TEMP_SCALE, 1 * GameController.TEMP_SCALE);
            }
        }
    }
}