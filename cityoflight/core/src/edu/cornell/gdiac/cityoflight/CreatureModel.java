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
public class CreatureModel extends BoxObstacle {
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
    /**FilmStrip for side animation*/
    private FilmStrip sideAnim;
    /**FilmStrip for side animation*/
    private FilmStrip upAnim;
    /**FilmStrip for side animation*/
    private FilmStrip downAnim;

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
    /** Whether a creature is, specifically, colliding with a box. */
    private boolean isStuckBox = false;
    /** Whether a creature is distracted */
    private boolean isDistracted = false;
    /** How many frames until the creature can turn again */
    private int turnCool = 0;
    /** The standard number of frames to wait until the creature can turn again */
    private int turnLimit;
    /** How many frames until the creature will drop chase aggro again */
    private int aggroCool = 0;
    /** The standard number of frames to wait until the creature will drop aggro */
    private int aggroLimit;
    /** How many frames until the creature will recover from being distracted */
    private int distractCool = 0;
    /** The standard number of frames to wait until the creature stop being distracted */
    private int distractLimit;
    /** refers to the vision of the creature */
    private LightSource vision = null;
    /** refers to how to creature behaves upon colliding. See AIController for more details. */
    private int turnBehavior = 1;

    private float dragon_y_offset = -20;

    /** constants for creature characteristics */

    private int LOU_TURN_LIMIT = 30; // as a snail, Lou turns pretty slowly.
    private int DRAGON_TURN_LIMIT = 20; // the dragon should turn quick, otherwise it would seem like it's "bumping" into a wall for too long.
    private int BLANCHE_TURN_LIMIT = 30;

    private int LOU_AGGRO_COUNTDOWN = 150;
    private int DRAGON_AGGRO_COUNTDOWN = 50;
    private int BLANCHE_AGGRO_COUNTDOWN = 400;

    private int LOU_DISTRACT_COUNTDOWN = 450;
    private int BLANCHE_DISTRACT_COUNTDOWN = 250;

    private float LOU_MAX_SPEED = 15.0f;
    private float DRAGON_MAX_SPEED = 30.0f;
    private float BLANCHE_MAX_SPEED = 30.0f;

    private int CREATURE_START_FRAME = 0;
    private int CREATURE_WALK_COOL = 4;
    private float CREATURE_DENSITY = 6.0f;
    private float CREATURE_FRICTION = 0.0f;
    private float CREATURE_RESTITUTION = 0.0f;
    private float CREATURE_FORCE = 1.0f;
    private float CREATURE_DAMPING = 1.0f;
    private String CREATURE_BODY_TYPE = "dynamic";

    /**  The transparent color that Dame Blanche can spontaneously become. */
    private Color BLANCHE_DRESS_OF_DOOM = new Color (1.0f, 1.0f, 1.0f, 0.2f);
    private int DRESS_COOLDOWN_LIMIT = 300;
    private int DRESS_DURATION = 150;
    private int DRESS_COOLDOWN = DRESS_COOLDOWN_LIMIT;

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
     * This is the result of input times creature force.
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

    public boolean getStuckBox(){ return isStuckBox; }
    public void setStuckBox(boolean value){isStuckBox = value;}

    /**
     * Returns whether the monster is distracted.
     *
     * @return whether the monster is distracted.
     */
    public boolean getDistracted(){ return isDistracted; }

    /**
     * Sets whether the monster is distracted.
     *
     * @param value	whether whether the monster is distracted.
     */
    public void setDistracted(boolean value){ isDistracted = value; }

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

    public void setDistractCool(int value){ distractCool = value; }

    public int getDistractCool(){ return distractCool; }

    public void setDistractLimit(int value){distractLimit = value; }

    public int getDistractLimit(){return distractLimit;}

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

    public void setTurnBehavior(int value){turnBehavior = value;}
    public int getTurnBehavior(){return turnBehavior;}


    /**
     * Creates a new creature with degenerate settings
     *
     * The main purpose of this constructor is to set the initial capsule orientation.
     */
    public CreatureModel() {
        super(0,0, 1.0f,1.0f);
        setFixedRotation(false);
    }

    /**
     * Initializes the creature via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the creature subtree.
     *
     * @param json	the
     */
    public void initialize(JsonValue json, JsonValue bounds, FilmStrip tex1, FilmStrip tex2, FilmStrip tex3, float pSize1) {

        setName(json.name());
        float[] pos  = {bounds.get("x").asFloat() / 64, pSize1 - bounds.get("y").asFloat() / 64};
//        float radius = json.get("radius").asFloat();
        float width = bounds.get("width").asFloat() / 64;
        float height = bounds.get("height").asFloat() / 64;
        //)
        try{
            setTurnBehavior(json.get("turn").asInt());
        }
        catch (Exception e){
            System.out.println("turn behavior not loaded");

        }

        setPosition(pos[0],pos[1]);

//        setRadius(radius);
        setWidth(width);
        setHeight(height);

        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
        setType(json.get("type").asInt());

        if(type != 2)
            dragon_y_offset = 0f;

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


        filmstrip = tex1;
        sideAnim = tex1;

        downAnim = tex2;

        upAnim = tex3;
        setTexture(tex1);

        setBodyType(CREATURE_BODY_TYPE.equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        setStartFrame(CREATURE_START_FRAME);
        setWalkLimit(CREATURE_WALK_COOL);
        setDensity (CREATURE_DENSITY);
        setFriction(CREATURE_FRICTION);
        setRestitution(CREATURE_RESTITUTION);
        setForce(CREATURE_FORCE);
        setDamping(CREATURE_DAMPING);

        if (type == 1){
            turnLimit = LOU_TURN_LIMIT;
            aggroLimit = LOU_AGGRO_COUNTDOWN;
            distractLimit = LOU_DISTRACT_COUNTDOWN;
            setMaxSpeed(LOU_MAX_SPEED);
        }else if (type == 2){
            turnLimit = DRAGON_TURN_LIMIT;
            aggroLimit = DRAGON_AGGRO_COUNTDOWN;
            setMaxSpeed(DRAGON_MAX_SPEED);
        }else if (type == 3){
            turnLimit = BLANCHE_TURN_LIMIT;
            aggroLimit = BLANCHE_AGGRO_COUNTDOWN;
            distractLimit = BLANCHE_DISTRACT_COUNTDOWN;
            setMaxSpeed(BLANCHE_MAX_SPEED);
        }else{
            System.out.println ("wrong type of creature. should never get here");
        }
    }

    /**
     * Applies the force to the body of this creature
     *
     * This method should be called after the force attribute is set.
     */
    public void applyForce(boolean isWalkingInPlace) {
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
            if (isWalkingInPlace) {
                animate = true;
            } else {
                animate = false;
            }
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
//        if (getVision() != null) {
//            System.out.println("lights "+getVision().getX()+" "+getVision().getY());
//            System.out.println("creature "+getX()+" "+getY());
//        }
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(ObstacleCanvas canvas) {
//        System.out.println(origin.x + " "+ origin.y);
        int isReflected = movement.x < 0 ? -1 : 1;
        int xOffset = 0;
        FilmStrip dirTexture = null;
        if(movement.x != 0)
        {
            setTexture(sideAnim);
            dirTexture = sideAnim;
            filmstrip = sideAnim;
        }
        else if(movement.y > 0)
        {
            setTexture(upAnim);
            dirTexture = upAnim;
            filmstrip = upAnim;
            //change so that sprite bounds are not offset for tarasque
           // if(type == 2) xOffset = 0;
        }
        else if(movement.y < 0){
            setTexture(downAnim);
            dirTexture = downAnim;
            filmstrip = downAnim;
            //if(type == 2) xOffset = 0;
        }
        else {
            if (texture != null) {
                canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y+texture.getRegionHeight()/6 + dragon_y_offset,
                        0, 0.75f * isReflected, 0.75f );
            }
        }

        if (texture != null && dirTexture != null) {
//          System.out.println("creature position " + getX() + " " + getY());
            if (type != 3) {
                if (type == 2 && dirTexture == upAnim) {
//                    System.out.println("here");
                    canvas.draw(dirTexture, Color.WHITE, origin.x, origin.y, (getX() + xOffset) * drawScale.x, getY() * drawScale.y + texture.getRegionHeight() / 6 + dragon_y_offset - 40,
                            0, 0.75f * isReflected, 0.75f);
                }
                if (type == 1 && (dirTexture == upAnim || dirTexture == downAnim)) {
//                    System.out.println("here");
                    canvas.draw(dirTexture, Color.WHITE, origin.x, origin.y, (getX() + xOffset) * drawScale.x, getY() * drawScale.y + texture.getRegionHeight() / 6 + dragon_y_offset - 20,
                            0, 0.75f * isReflected, 0.75f);
                }
//                else if (type == 1 && dirTexture == downAnim) {
////                    System.out.println("here");
//                    canvas.draw(dirTexture, Color.WHITE, origin.x, origin.y, (getX() + xOffset) * drawScale.x, getY() * drawScale.y + texture.getRegionHeight() / 6 + dragon_y_offset + 20,
//                            0, 0.75f * isReflected, 0.75f);
//                }
                else {
                    canvas.draw(dirTexture, Color.WHITE, origin.x, origin.y, (getX() + xOffset) * drawScale.x, getY() * drawScale.y + texture.getRegionHeight() / 6 + dragon_y_offset,
                            0, 0.75f * isReflected, 0.75f);
                }
            }else{
                if (DRESS_COOLDOWN <= 0) {
                    dressOfDoom();
                    canvas.draw(dirTexture, BLANCHE_DRESS_OF_DOOM,origin.x,origin.y,(getX() + xOffset)* drawScale.x,getY()* drawScale.y+texture.getRegionHeight()/6,
                            0,0.75f* isReflected,0.75f );
                    if (DRESS_COOLDOWN < -DRESS_DURATION - 100) {
                        DRESS_COOLDOWN = DRESS_COOLDOWN_LIMIT;
                        enddressOfDoom();
                    }
                } else{
                    canvas.draw(dirTexture,Color.WHITE,origin.x,origin.y,(getX() + xOffset)* drawScale.x,getY()* drawScale.y+texture.getRegionHeight()/6,
                            0,0.75f* isReflected,0.75f );
                }
                DRESS_COOLDOWN --;
            }
        }
    }

    public void dressOfDoom(){
        BLANCHE_DRESS_OF_DOOM.a = Math.max(1.0f - (-(float)DRESS_COOLDOWN / (float)DRESS_DURATION), 0.05f);
        //System.out.println("a = " + BLANCHE_DRESS_OF_DOOM.a);
        getVision().setColor(getVision().getColor().r, getVision().getColor().g, getVision().getColor().b, BLANCHE_DRESS_OF_DOOM.a);
    }
    public void enddressOfDoom(){
        getVision().setColor(getVision().getColor().r, getVision().getColor().g, getVision().getColor().b, 1);
    }
}
