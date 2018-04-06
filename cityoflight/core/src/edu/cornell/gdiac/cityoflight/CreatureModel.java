/*
 * DudeModel.java
 *
 * This is a refactored version of DudeModel that allows us to read its properties
 * from a JSON file.  As a result, it has a lot more getter and setter "hooks" than
 * in lab.
 *
 * While the dude can support lights, these are completely decoupled from this object.
 * The dude is not aware of any of the lights. These are attached to the associated
 * body and move with the body.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * B2Lights version, 3/12/2016
 */
package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.lights.LightSource;

import java.lang.reflect.*;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * Creature model for the game.
 *
 * Note that the constructor does very little.  The true initialization happens
 * by reading the JSON value.
 */
public class CreatureModel extends WheelObstacle {
    // Physics constants
    /** The factor to multiply by the input */
    private float force;
    /** The amount to slow the character down */
    private float damping;
    /** The maximum character speed */
    private float maxspeed;

    /** The current directional movement of the creature */
    private Vector2 movement = new Vector2();
    /** Whether or not to animate the current frame */
    private boolean animate = false;

    /** How many frames until the creature can walk again */
    private int walkCool;
    /** The standard number of frames to wait until the creature can walk again */
    private int walkLimit;

    /** FilmStrip pointer to the texture region */
    private FilmStrip filmstrip;
    /** The current animation frame of the creature */
    private int startFrame;

    /** Cache for internal force calculations */
    private Vector2 forceCache = new Vector2();

    // Behavior and AI related constants

    /** Creature type identifier. */
    // 1: Lou Carcolh | 2: Tarasque | 3: Dame Blanche
    private int type;
    /** Base horizontal movement speed of a creature */
    private float xinput;
    /** Base vertical movement speed of a creature */
    private float yinput;
    /** Whether a creature can no longer go in its intended direction and should change routes. */
    private boolean isStuck = false;
    /** How many frames until the creature can turn again */
    private int turnCool = 0;
    /** The standard number of frames to wait until the creature can turn again */
    private int turnLimit;
    /** How many frames until the creature will drop chase aggro again */
    private int aggroCool = 0;
    /** The standard number of frames to wait until the creature will drop aggro */
    private int aggroLimit;
    /** refers to the vision of the creature */
    private LightSource vision = null;

    /**
     * Returns the directional movement of the creature.
     *
     * This is the result of input times creature force.
     *
     * @return the directional movement of this creature.
     */
    public Vector2 getMovement() {
        return movement;
    }

    /**
     * Sets the directional movement of this creature.
     *
     * This is the result of input times creature force.
     *
     * @param value the directional movement of this creature.
     */
    public void setMovement(Vector2 value) {
        setMovement(value.x,value.y);
    }

    /**
     * Sets the directional movement of this creature.
     *
     * This is the result of input times dude creature.
     *
     * @param dx the horizontal movement of this creature.
     * @param dy the vertical movement of this creature.
     */
    public void setMovement(float dx, float dy) {
        movement.set(dx,dy);
    }

    /**
     * Returns how much force to apply to get the creature moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @return how much force to apply to get the creature moving
     */
    public float getForce() {
        return force;
    }

    /**
     * Sets how much force to apply to get the creature moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @param value	how much force to apply to get the creature moving
     */
    public void setForce(float value) {
        force = value;
    }

    /**
     * Returns how hard the brakes are applied to get a creature to stop moving
     *
     * @return how hard the brakes are applied to get a creature to stop moving
     */
    public float getDamping() {
        return damping;
    }

    /**
     * Sets how hard the brakes are applied to get a creature to stop moving
     *
     * @param value	how hard the brakes are applied to get a creature to stop moving
     */
    public void setDamping(float value) {
        damping = value;
    }

    /**
     * Returns the upper limit on creature left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @return the upper limit on creature left-right movement.
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
     * Returns the current animation frame of this creature.
     *
     * @return the current animation frame of this creature.
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
     * Returns the cooldown limit between walk animations
     *
     * @return the cooldown limit between walk animations
     */
    public int getWalkLimit() {
        return walkLimit;
    }

    /**
     * Sets the cooldown limit between walk animations
     *
     * @param value	the cooldown limit between walk animations
     */
    public void setWalkLimit(int value) {
        walkLimit = value;
    }

    /**
     * Returns whether the monster can advance on its route
     *
     * @return whether the monster can advance on its route
     */
    public boolean getStuck(){ return isStuck; }

    /**
     * Sets whether the monster can advance on its route
     *
     * @param value	whether the monster can advance on its route
     */
    public void setStuck(boolean value){ isStuck = value; }

    /**
     * Returns the cooldown between successive turns
     *
     * @return the cooldown between successive turns
     */
    public int getTurnCool(){ return turnCool; }

    /**
     * Sets the cooldown between successive turns
     *
     * @param value	the cooldown between successive turns
     */
    public void setTurnCool(int value){ turnCool = value; }

    /**
     * Returns the cooldown limit between successive turns
     *
     * @return the cooldown limit between successive turns
     */
    public int getTurnLimit(){ return turnLimit; }

    /**
     * Sets the cooldown limit between successive turns
     *
     * @param value	the cooldown limit between successive turns
     */
    public void setTurnLimit(int value){ turnLimit = value; }

    /**
     * Returns the cooldown between successive turns
     *
     * @return the cooldown between successive turns
     */
    public int getAggroCool(){ return aggroCool; }

    /**
     * Sets the cooldown between successive turns
     *
     * @param value	the cooldown between successive turns
     */
    public void setAggroCool(int value){ aggroCool = value; }

    /**
     * Returns the cooldown limit between successive turns
     *
     * @return the cooldown limit between successive turns
     */
    public int getAggroLimit(){ return aggroLimit; }

    /**
     * Sets the cooldown limit between successive turns
     *
     * @param value	the cooldown limit between successive turns
     */
    public void setAggroLimit(int value){ aggroLimit = value; }

    /**
     * Returns the horizontal base speed of the creature
     *
     * @return the horizontal base speed of the creature
     */
    public float getXInput(){ return xinput; }

    /**
     * Sets the horizontal base speed of the creature
     *
     * @param value	the horizontal base speed of the creature
     */
    public void setXInput(float value){ xinput = value; }

    /**
     * Returns the vertical base speed of the creature
     *
     * @return the vertical base speed of the creature
     */
    public float getYInput(){ return yinput; }

    /**
     * Sets the vertical base speed of the creature
     *
     * @param value	the vertical base speed of the creature
     */
    public void setYInput(float value){ yinput = value; }

    /**
     * Returns the base movement speed of the creature
     *
     * @return the base movement speed of the creature
     */
    public float getSpeedInput() {
        return (float) Math.sqrt((xinput * xinput) + (yinput * yinput));
    }

    public int getType(){ return type; }
    public void setType(int value) { type = value; }

    public LightSource getVision() { return vision; }
    public void setVision(LightSource l){ vision = l; }


    /**
     * Creates a new creature with degenerate settings
     *
     * The main purpose of this constructor is to set the initial capsule orientation.
     */
    public CreatureModel() {
        super(0,0,1.0f);
        setFixedRotation(false);
    }

    /**
     * Initializes the creature via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the creature subtree.
     *
     * @param json	the JSON subtree defining the creature
     */
    public void initialize(JsonValue json) {
        setName(json.name());
        float[] pos  = json.get("pos").asFloatArray();
        float radius = json.get("radius").asFloat();
        setPosition(pos[0],pos[1]);
        setRadius(radius);

        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
        setBodyType(json.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        setDensity(json.get("density").asFloat());
        setFriction(json.get("friction").asFloat());
        setRestitution(json.get("restitution").asFloat());
        setForce(json.get("force").asFloat());
        setDamping(json.get("damping").asFloat());
        setMaxSpeed(json.get("maxspeed").asFloat());
        setStartFrame(json.get("startframe").asInt());
        setWalkLimit(json.get("walklimit").asInt());
        setAggroLimit(json.get("aggrolimit").asInt());

        setType(json.get("type").asInt());
        setTurnLimit(json.get("turnlimit").asInt());
        setXInput(json.get("xinput").asFloat());
        setYInput(json.get("yinput").asFloat());

        // Create the collision filter (used for light penetration)
        short collideBits = LevelModel.bitStringToShort(json.get("collideBits").asString());
        short excludeBits = LevelModel.bitStringToComplement(json.get("excludeBits").asString());
        Filter filter = new Filter();
        filter.categoryBits = collideBits;
        filter.maskBits = excludeBits;
        setFilterData(filter);

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
    }

    /**
     * Applies the force to the body of this creature
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
            forceCache.set(getMovement());
            body.applyForce(forceCache,getPosition(),true);
            animate = true;
        } else {
            animate = false;
        }
    }

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        // Animate if necessary
        if (animate && walkCool == 0) {
            if (filmstrip != null) {
                int next = (filmstrip.getFrame()+1) % filmstrip.getSize();
                filmstrip.setFrame(next);
            }
            walkCool = walkLimit;
        } else if (walkCool > 0) {
            walkCool--;
        } else if (!animate) {
            if (filmstrip != null) {
                filmstrip.setFrame(startFrame);
            }
            walkCool = 0;
        }

        super.update(dt);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(ObstacleCanvas canvas) {
        if (texture != null) {
            canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,0,0.75f,0.75f);
        }
    }
}
