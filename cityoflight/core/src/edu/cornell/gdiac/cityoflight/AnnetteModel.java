/*
 * Annette.java
 *
 * This is a refactored version of AnnetteModel that allows us to read its properties
 * from a JSON file.  As a result, it has a lot more getter and setter "hooks" than
 * in lab.
 *
 * Author of DudeModel: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * B2Lights version, 3/12/2016
 */
package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import java.lang.reflect.*;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * Player avatar.
 *
 * Note that the constructor does very little.  The true initialization happens
 * by reading the JSON value.
 */
public class AnnetteModel extends BoxObstacle {
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
    private boolean animate = false;

    /** How many frames until we can walk again */
    private int walkCool;
    /** The standard number of frames to wait until we can walk again */
    private int walkLimit;

    /** Whether we are actively summoning */
    private boolean isSummoning;

    /** Whether we are actively walking in place */
    private boolean isWalkingInPlace;

    /** FilmStrip pointer to the texture region */
    private FilmStrip sidefilmstrip;
    private FilmStrip upfilmstrip;
    private FilmStrip downfilmstrip;


    /** The current animation frame of the avatar */
    private int startFrame;

    /** The current direction Annette is facing. */
    private Direction direction;

    /** If Annette is calling a bird */
    private boolean isbird;
    /** The current horizontal movement of the character */
    private float   hormovement;

    /** The color to show off the debug shape */
    private Color debugColor;

    private PolygonShape sensorShapeD;
    private Fixture sensorFixtureD;
    private PolygonShape sensorShapeU;
    private Fixture sensorFixtureU;
    private PolygonShape sensorShapeR;
    private Fixture sensorFixtureR;
    private PolygonShape sensorShapeL;
    private Fixture sensorFixtureL;
    private PolygonShape annetteShape;
    private Fixture annetteFixture;

    private static float SENSOR_SIZE = 0.4f;


    /**
     * Enumeration to identify which direction Annette is facing.
     */
    public enum Direction {
        UP, LEFT, RIGHT, DOWN
    }

    /** Cache for internal force calculations */
    private Vector2 forceCache = new Vector2();

    /**
     * Returns the directional movement of this character.
     *
     * This is the result of input times Annette force.
     *
     * @return the directional movement of this character.
     */
    public Vector2 getMovement() {
        return movement;
    }

    /**
     * Sets the directional movement of this character.
     *
     * This is the result of input times Annette force.
     *
     * @param value the directional movement of this character.
     */
    public void setMovement(Vector2 value) {
        setMovement(value.x,value.y);
    }

    /**
     * Sets the directional movement of this character.
     *
     * This is the result of input times Annette force.
     *
     * @param dx the horizontal movement of this character.
     * @param dy the horizontal movement of this character.
     */
    public void setMovement(float dx, float dy) {
        movement.set(dx,dy);
    }

    /**
     * Returns how much force to apply to get the Annette moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @return how much force to apply to get the Annette moving
     */
    public float getForce() {
        return force;
    }

    /**
     * Sets how much force to apply to get the Annette moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @param value	how much force to apply to get the Annette moving
     */
    public void setForce(float value) {
        force = value;
    }

    /**
     * Returns how hard the brakes are applied to get a Annette to stop moving
     *
     * @return how hard the brakes are applied to get a Annette to stop moving
     */
    public float getDamping() {
        return damping;
    }

    /**
     * Sets how hard the brakes are applied to get a Annette to stop moving
     *
     * @param value	how hard the brakes are applied to get a Annette to stop moving
     */
    public void setDamping(float value) {
        damping = value;
    }

    /**
     * Returns the upper limit on Annette left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @return the upper limit on Annette left-right movement.
     */
    public float getMaxSpeed() {
        return maxspeed;
    }

    /**
     * Sets the upper limit on Annette left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @param value	the upper limit on Annette left-right movement.
     */
    public void setMaxSpeed(float value) {
        maxspeed = value;
    }

    /**
     * Returns the current animation frame of this Annette.
     *
     * @return the current animation frame of this Annette.
     */
    public float getStartFrame() {
        return startFrame;
    }

    /**
     * Sets the animation frame of this Annette.
     *
     * @param value	animation frame of this Annette.
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
     * Checks if Annette is summoning a box
     *
     * @return true if Annette is actively summoning a box
     */
    public boolean isSummoning() { return isSummoning; }

    /**
     * Sets whether Annette is summoning a box.
     *
     * @param value whether the Annette is actively summoning.
     */
    public void setSummoning(boolean value) { isSummoning = value; }


    public Direction getDirection() {
//        System.out.println("annette direction null");
//        System.out.println(direction == null);
        return this.direction;
    }

    public void setDirection(Direction value) {
        this.direction = value;
    }


    public void setBird(boolean value) {
        this.isbird = value;
    }

    public boolean getBird() {return this.isbird;}

    /**
     * Returns the color to display the physics outline
     *
     * @return the color to display the physics outline
     */
    public Color getDebugColor() {
        return debugColor;
    }


    /**
     * Creates Annette with degenerate settings
     *
     * The main purpose of this constructor is to set the initial capsule orientation.
     */
    public AnnetteModel() {
        super(0,0,1.0f, 1.0f);
        setFixedRotation(true);
        this.direction = Direction.RIGHT;
        this.isbird = false;
        debugColor = Color.BLUE;
    }

    /**
     * Initializes Annette via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the Annette subtree
     *
     * @param sideJson	the JSON subtree defining the Annette
     */
    public void initialize(JsonValue sideJson, JsonValue aBounds, float pSize1) {
        setName("annette");
        float x = aBounds.get("x").asFloat() / 64; //+ 200/64;
//        System.out.println("drawScale.x " + drawScale.x);
        float y = pSize1 - aBounds.get("y").asFloat() / 64;// -200/64;

//        System.out.println(x*64 + " " + y*64 + " annette initialize");

        float width = aBounds.get("width").asFloat()/64;
        float height = aBounds.get("height").asFloat()/64;
        setPosition(x,y);
//        setPosition(1, 1);
        setWidth(width);
        setHeight(height);



        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
        setBodyType(sideJson.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        setDensity(sideJson.get("density").asFloat());
        setFriction(sideJson.get("friction").asFloat());
        setRestitution(sideJson.get("restitution").asFloat());
        setForce(sideJson.get("force").asFloat());
        setDamping(sideJson.get("damping").asFloat());
        setMaxSpeed(sideJson.get("maxspeed").asFloat());
        setStartFrame(sideJson.get("startframe").asInt());
        setWalkLimit(sideJson.get("walklimit").asInt());

        // Create the collision filter (used for light penetration)
        short collideBits = LevelModel.bitStringToShort(sideJson.get("collideBits").asString());
        short excludeBits = LevelModel.bitStringToComplement(sideJson.get("excludeBits").asString());
        Filter filter = new Filter();
        filter.categoryBits = collideBits;
        filter.maskBits = excludeBits;
        setFilterData(filter);

        // Reflection is best way to convert name to color
        Color debugColor;
        try {
            String cname = sideJson.get("debugcolor").asString().toUpperCase();
            Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(cname);
            debugColor = new Color((Color)field.get(null));
        } catch (Exception e) {
            debugColor = null; // Not defined
        }
        int opacity = sideJson.get("debugopacity").asInt();
        debugColor.mul(opacity/255.0f);
        setDebugColor(debugColor);

        // Now get the texture from the AssetManager singleton
        String key = sideJson.get("texture").asString();
        TextureRegion texture = JsonAssetManager.getInstance().getEntry(key, TextureRegion.class);
        try {
            sidefilmstrip = (FilmStrip)texture;
        } catch (Exception e) {
            sidefilmstrip = null;
        }
        setTexture(texture);

        String key2 = sideJson.get("texture2").asString();
        TextureRegion texture2 = JsonAssetManager.getInstance().getEntry(key2, TextureRegion.class);
        try {
            downfilmstrip = (FilmStrip)texture2;
        } catch (Exception e) {
            downfilmstrip = null;
        }

        String key3 = sideJson.get("texture3").asString();
        TextureRegion texture3 = JsonAssetManager.getInstance().getEntry(key3, TextureRegion.class);
        try {
            upfilmstrip = (FilmStrip)texture3;
        } catch (Exception e) {
            upfilmstrip = null;
        }


    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * Implementations of this method should NOT retain a reference to World.
     * That is a tight coupling that we should avoid.
     *
     * @param world Box2D world to store body
     *
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        if (!super.activatePhysics(world)) {
            return false;
        }
        Vector2 sensorCenterD = new Vector2(0, -((getHeight()/2) + 0.5f));
        FixtureDef sensorDefD = new FixtureDef();
        sensorDefD.isSensor = true;
        sensorShapeD = new PolygonShape();
        sensorShapeD.setAsBox(SENSOR_SIZE, SENSOR_SIZE, sensorCenterD,0);
        sensorDefD.shape = sensorShapeD;
        sensorFixtureD = body.createFixture(sensorDefD);
        sensorFixtureD.setUserData("annetteDown");

        Vector2 sensorCenterU = new Vector2(0, ((getHeight()/2) + 0.5f));
        FixtureDef sensorDefU = new FixtureDef();
        sensorDefU.isSensor = true;
        sensorShapeU = new PolygonShape();
        sensorShapeU.setAsBox(SENSOR_SIZE, SENSOR_SIZE, sensorCenterU,0);
        sensorDefU.shape = sensorShapeU;
        sensorFixtureU = body.createFixture(sensorDefU);
        sensorFixtureU.setUserData("annetteUp");

        Vector2 sensorCenterR = new Vector2(getWidth() / 2 + 0.5f, 0);
        FixtureDef sensorDefR = new FixtureDef();
        sensorDefR.isSensor = true;
        sensorShapeR = new PolygonShape();
        sensorShapeR.setAsBox(SENSOR_SIZE, SENSOR_SIZE, sensorCenterR,0);
        sensorDefR.shape = sensorShapeR;
        sensorFixtureR = body.createFixture(sensorDefR);
        sensorFixtureR.setUserData("annetteRight");

        Vector2 sensorCenterL = new Vector2(-(getWidth() / 2 + 0.5f), 0);
        FixtureDef sensorDefL = new FixtureDef();
        sensorDefL.isSensor = true;
        sensorShapeL = new PolygonShape();
        sensorShapeL.setAsBox(SENSOR_SIZE, SENSOR_SIZE, sensorCenterL,0);
        sensorDefL.shape = sensorShapeL;
        sensorFixtureL = body.createFixture(sensorDefL);
        sensorFixtureL.setUserData("annetteLeft");

        Vector2 annetteCenter = new Vector2(0, 0);
        FixtureDef annetteDef = new FixtureDef();
        annetteDef.isSensor = true;
        annetteShape = new PolygonShape();
        annetteShape.setAsBox((getWidth() / 2) + 0.05f, (getHeight() / 2) + 0.05f, annetteCenter,0);
        annetteDef.shape = annetteShape;
        annetteFixture = body.createFixture(annetteDef);
        annetteFixture.setUserData("center");

        short collideBits = LevelModel.bitStringToShort("0010");
        Filter filter = new Filter();
        filter.categoryBits = collideBits;
        setFilterData(filter);

        return true;
    }

    /**
     * Applies the force to the body of Annette
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
            if (isWalkingInPlace){ animate = true; }
            else{ animate = false; }
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
            switch (direction) {
                case RIGHT:
                    if (sidefilmstrip != null) {
                        int next = (sidefilmstrip.getFrame()+1) % sidefilmstrip.getSize();
                        sidefilmstrip.setFrame(next);
                    }
                    break;
                case LEFT:
                    if (sidefilmstrip != null) {
                        int next = (sidefilmstrip.getFrame()+1) % sidefilmstrip.getSize();
                        sidefilmstrip.setFrame(next);
                    }
                    break;
                case UP:
                    if (upfilmstrip != null){
                        int next = (upfilmstrip.getFrame()+1) % upfilmstrip.getSize();
                        upfilmstrip.setFrame(next);
                    }
                    break;
                case DOWN:
                    setTexture(sidefilmstrip);
                    if (downfilmstrip != null){
                        int next = (downfilmstrip.getFrame()+1) % downfilmstrip.getSize();
                        downfilmstrip.setFrame(next);
                    }
                }
            walkCool = walkLimit;
        } else if (walkCool > 0) {
            walkCool--;
        } else if (!animate) {
            if (sidefilmstrip != null) {
                sidefilmstrip.setFrame(startFrame);
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

        FilmStrip dirTexture = null;
        float flipped = 0.6f;

        switch (direction) {
            case RIGHT:
                setTexture(sidefilmstrip);
                dirTexture = sidefilmstrip;

                break;
            case LEFT:
                flipped = -0.6f;
                setTexture(sidefilmstrip);
                dirTexture = sidefilmstrip;

                break;
            case UP:
                setTexture(sidefilmstrip);
                dirTexture = upfilmstrip;

                break;
            case DOWN:
                setTexture(sidefilmstrip);
                dirTexture = downfilmstrip;
        }

        if (texture != null) {
//            System.out.println("annette x " + getX() * drawScale.x + " annette y " + getY() * drawScale.y);
            canvas.draw(dirTexture, Color.WHITE, origin.x, origin.y - dirTexture.getRegionHeight()/4, getX() * drawScale.x, getY() * drawScale.y, getAngle(), flipped, Math.abs(flipped));
        }
        else{
//            System.out.println("can't find annette texture");
        }
    }

    /**
     * Draws the outline of the physics body.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(ObstacleCanvas canvas) {
        super.drawDebug(canvas);
        if (debugColor != null) {
            canvas.drawPhysics(sensorShapeD,debugColor,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
            canvas.drawPhysics(sensorShapeU,debugColor,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
            canvas.drawPhysics(sensorShapeR,debugColor,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
            canvas.drawPhysics(sensorShapeL,debugColor,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
            canvas.drawPhysics(annetteShape,Color.ORANGE,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
//            canvas.drawPhysics(annetteShape, Color.WHITE, 400, 300, getAngle(), GameController.TEMP_SCALE, Math.abs(flipped) * GameController.TEMP_SCALE);
        }
    }

    public boolean isWalkingInPlace(){
        return isWalkingInPlace;
    }

    public void setWalkingInPlace(boolean value){
        isWalkingInPlace = value;
    }

}
