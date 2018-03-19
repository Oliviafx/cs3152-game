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

    /** FilmStrip pointer to the texture region */
<<<<<<< HEAD
    private FilmStrip filmstrip;
=======
    private FilmStrip sidefilmstrip;
    private FilmStrip upfilmstrip;
    private FilmStrip downfilmstrip;


>>>>>>> master
    /** The current animation frame of the avatar */
    private int startFrame;

    /** The current direction Annette is facing. */
    private Direction direction;
<<<<<<< HEAD
    /** The current horizontal movement of the character */
    private float   hormovement;
    /** Which direction is the character facing */
    private boolean faceRight;
    private boolean faceLeft;
    private boolean faceUp;
    private boolean faceDown;
=======

    /** If Annette is calling a bird */
    private boolean isbird;
    /** The current horizontal movement of the character */
    private float   hormovement;
>>>>>>> master
    /**
     * Enumeration to identify which direction Annette is facing.
     */
    public enum Direction {
        UP, LEFT, RIGHT, DOWN
<<<<<<< HEAD
    };
=======
    }
>>>>>>> master

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
<<<<<<< HEAD
//        if (direction == Direction.RIGHT) {
//            System.out.println("right");
//
//        }
//        if (direction == Direction.LEFT) {
//            System.out.println("left");
//
//        }
//        if (direction == Direction.UP) {
//            System.out.println("up");
//
//        }
//        if (direction == Direction.DOWN) {
//            System.out.println("down");
//
//        }
        return direction;
    }

    public void setDirection(Direction value) {
        direction = value;
    }

=======
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

>>>>>>> master
    /** Taken from Lab 4.
     * Sets left/right movement of this character.
     *
     * This is the result of input times dude force.
     *
     * @param value left/right movement of this character.
     */
<<<<<<< HEAD
    public void setMovement(Direction value) {
        if (value != null) {
            switch (direction) {
                case RIGHT:
                    setMovement(new Vector2(force, 0));
                    break;
                case LEFT:
                    setMovement(new Vector2(-force, 0));
                    break;
                case UP:
                    setMovement(new Vector2(0, force));
                    break;
                case DOWN:
                    setMovement(new Vector2(0, -force));
            }
        }
    }

    //        hormovement = value;
=======
//    public void setMovement(Direction value) {
//        if (value != null) {
//            switch (direction) {
//                case RIGHT:
//                    setMovement(new Vector2(force, 0));
//                    break;
//                case LEFT:
//                    setMovement(new Vector2(-force, 0));
//                    break;
//                case UP:
//                    setMovement(new Vector2(0, force));
//                    break;
//                case DOWN:
//                    setMovement(new Vector2(0, -force));
//            }
//        }
//    }

        //        hormovement = value;
>>>>>>> master
//        // Change facing if appropriate
//        if (hormovement < 0) {
//            faceRight = false;
//        } else if (hormovement > 0) {
//            faceRight = true;
//        }
//    }


    /**
     * Creates Annette with degenerate settings
     *
     * The main purpose of this constructor is to set the initial capsule orientation.
     */
    public AnnetteModel() {
        super(0,0,1.0f, 1.0f);
<<<<<<< HEAD
        setFixedRotation(false);
=======
        setFixedRotation(true);
        this.direction = Direction.RIGHT;
        this.isbird = false;
>>>>>>> master
    }

    /**
     * Initializes Annette via the given JSON value
     *
     * The JSON value has been parsed and is part of a bigger level file.  However,
     * this JSON value is limited to the Annette subtree
     *
<<<<<<< HEAD
     * @param json	the JSON subtree defining the Annette
     */
    public void initialize(JsonValue json) {
        setName(json.name());
        float[] pos  = json.get("pos").asFloatArray();
        float width = json.get("width").asFloat();
        float height = json.get("height").asFloat();
=======
     * @param sideJson	the JSON subtree defining the Annette
     */
    public void initialize(JsonValue sideJson) {
        setName(sideJson.name());
        float[] pos  = sideJson.get("pos").asFloatArray();
        float width = sideJson.get("width").asFloat();
        float height = sideJson.get("height").asFloat();
>>>>>>> master
        setPosition(pos[0],pos[1]);
        setWidth(width);
        setHeight(height);
        //setRadius(radius);

        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
<<<<<<< HEAD
        setBodyType(json.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        setDensity(json.get("density").asFloat());
        setFriction(json.get("friction").asFloat());
        setRestitution(json.get("restitution").asFloat());
        setForce(json.get("force").asFloat());
        setDamping(json.get("damping").asFloat());
        setMaxSpeed(json.get("maxspeed").asFloat());
        setStartFrame(json.get("startframe").asInt());
        setWalkLimit(json.get("walklimit").asInt());

        // Create the collision filter (used for light penetration)
        short collideBits = LevelModel.bitStringToShort(json.get("collideBits").asString());
        short excludeBits = LevelModel.bitStringToComplement(json.get("excludeBits").asString());
=======
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
>>>>>>> master
        Filter filter = new Filter();
        filter.categoryBits = collideBits;
        filter.maskBits = excludeBits;
        setFilterData(filter);

        // Reflection is best way to convert name to color
        Color debugColor;
        try {
<<<<<<< HEAD
            String cname = json.get("debugcolor").asString().toUpperCase();
=======
            String cname = sideJson.get("debugcolor").asString().toUpperCase();
>>>>>>> master
            Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(cname);
            debugColor = new Color((Color)field.get(null));
        } catch (Exception e) {
            debugColor = null; // Not defined
        }
<<<<<<< HEAD
        int opacity = json.get("debugopacity").asInt();
=======
        int opacity = sideJson.get("debugopacity").asInt();
>>>>>>> master
        debugColor.mul(opacity/255.0f);
        setDebugColor(debugColor);

        // Now get the texture from the AssetManager singleton
<<<<<<< HEAD
        String key = json.get("texture").asString();
        TextureRegion texture = JsonAssetManager.getInstance().getEntry(key, TextureRegion.class);
        try {
            filmstrip = (FilmStrip)texture;
        } catch (Exception e) {
            filmstrip = null;
        }
        setTexture(texture);
=======
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

>>>>>>> master
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
<<<<<<< HEAD
            if (filmstrip != null) {
                int next = (filmstrip.getFrame()+1) % filmstrip.getSize();
                filmstrip.setFrame(next);
=======
            if (sidefilmstrip != null) {
                int next = (sidefilmstrip.getFrame()+1) % sidefilmstrip.getSize();
                sidefilmstrip.setFrame(next);
>>>>>>> master
            }
            walkCool = walkLimit;
        } else if (walkCool > 0) {
            walkCool--;
        } else if (!animate) {
<<<<<<< HEAD
            if (filmstrip != null) {
                filmstrip.setFrame(startFrame);
=======
            if (sidefilmstrip != null) {
                sidefilmstrip.setFrame(startFrame);
>>>>>>> master
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
<<<<<<< HEAD
        if (texture != null) {
            canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1.0f,1.0f);
        }
    }
}
=======

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
            canvas.draw(dirTexture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), flipped, 0.6f);
        }
    }

}
>>>>>>> master
