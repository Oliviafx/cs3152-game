package edu.cornell.gdiac.physics.rocket;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class BoxModel extends BoxObstacle {

    // Default physics values
    /** The density of this box */
    private static final float DEFAULT_DENSITY = 1.0f;
    /** The friction of this box */
    private static final float DEFAULT_FRICTION = 0.0f;
    /** The restitution of this box */
    private static final float DEFAULT_RESTITUTION = 0.0f;
    /** The thrust factor to convert player input into thrust */
    /** (3/5/18) Not sure if this is needed */
    /** Random value set as of now */
    private static final float DEFAULT_THRUST = 15.0f;

    /** The force to apply to this box */
    private Vector2 force;

    /** The texture filmstrip for the box */
    FilmStrip mainBox;
    /** The associated sound for the box (later) */
    String mainSound;
    /** The animation phase for the box (later) */
    boolean mainCycle = true;

    /** Cache object for transforming the force according the object angle */
    /** Not sure if this is needed as of right now 3/5/2018 */
    public Affine2 affineCache = new Affine2();

    /**
     * Returns the force applied to this box.
     *
     * This method returns a reference to the force vector, allowing it to be modified.
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the force applied to this box.
     */
    public Vector2 getForce() {
        return force;
    }

    /**
     * Returns the x-component of the force applied to this box.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the x-component of the force applied to this box.
     */
    public float getFX() {
        return force.x;
    }

    /**
     * Sets the x-component of the force applied to this box.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this box.
     */
    public void setFX(float value) {
        force.x = value;
    }

    /**
     * Returns the y-component of the force applied to this box.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the y-component of the force applied to this box.
     */
    public float getFY() {
        return force.y;
    }

    /**
     * Sets the x-component of the force applied to this box.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this box.
     */
    public void setFY(float value) {
        force.y = value;
    }

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
     * @param x  		Initial x position of the box center
     * @param y  		Initial y position of the box center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public BoxModel(float x, float y, float width, float height) {
        super(x,y,width,height);
        force = new Vector2();
        setDensity(DEFAULT_DENSITY);
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        setName("box");

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
     * Applies the force to the body of this box
     *
     * This method should be called after the force attribute is set.
     *
     * (3/5/2018) Might need to modify
     */
    public void applyForce() {
        if (!isActive()) {
            return;
        }

        // Orient the force with rotation.
        affineCache.setToRotationRad(getAngle());
        affineCache.applyTo(force);

        // Apply force to the box BODY, not the box
        body.applyForceToCenter(force, true);
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
     * @param  on       Whether the animation is active
     */
    public void animateBox(boolean on) {
        FilmStrip node = mainBox;
        boolean cycle = mainCycle;

        if (on) {
            // Turn on the flames and go back and forth
            if (node.getFrame() == 0 || node.getFrame() == 1) {
                cycle = true;
            } else if (node.getFrame() == node.getSize()-1) {
                cycle = false;
            }

            // Increment
            if (cycle) {
                node.setFrame(node.getFrame()+1);
            } else {
                node.setFrame(node.getFrame()-1);
            }
        } else {
            node.setFrame(0);
        }

        mainCycle = cycle;
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        super.draw(canvas); // Box

        /** (3/5/2018) might need to change the code below; copied from RocketModel */
//        float offsety = mainBox.getRegionHeight()-origin.y;
//        canvas.draw(mainBox,Color.WHITE,origin.x,offsety,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
    }

}
