/**
 DistractionModel.java
 This class represents a bird that Annette can mime to distract the creatures.

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

public class DistractionModel extends WheelObstacle {
    /** The amount by which the bird's x and y coordinates will change each frame while it is moving. */
    private static final float BIRD_SPEED = 1f;

    /** The sprite for the bird. */
    FilmStrip birdsprite;
    /** The bird's x coordinate on the screen. */
    private float x;
    /** The bird's y coordinate on the screen. */
    private float y;
    /** Whether the bird is currently active. */
    private boolean active;



    public DistractionModel(float x, float y, boolean active) {
        super(x,y,1);
        this.active = active;
    }

    /** Returns whether or not the bird is active.
     *
     * @return whether or not the bird is active.
     */
    public boolean isActive() { return this.active;}

    /** Sets whether the bird is active
     *
     * @param value whether the bird is active
     */
    public void setActive(boolean value) {
        this.active = value;
    }


    public void draw() {

    }

    public void update() {

    }

}