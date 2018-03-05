/**
 * Barrier.java
 *
 * This class represents real world obstacles: objects that Annette is not able to interact with.
 * Barriers will limit Annette's movement throughout the levels. They will also block the monsters' patrol paths
 * and lines of sight. Annette can thus hide behind barriers.
 *
 * Heavily influenced by RocketModel from the Physics Lab and InteriorModel from the B2Lights demo.
 *
 */


package edu.cornell.gdiac.physics.barrier;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class Barrier extends BoxObstacle{

    // Default physics values, modeled after RocketModel
    /** The density of the barrier */
    private static final float DEFAULT_DENSITY  =  1.0f;
    /** The friction of the barrier */
    private static final float DEFAULT_FRICTION = 1.0f;
    /** The restitution of the barrier */
    private static final float DEFAULT_RESTITUTION = 0.4f;

    /** Image for the barrier */
    FilmStrip barrier;

    /** X coordinate of the barrier's center. */
    private float x;
    /** Y coordinate of the barrier's center. */
    private float y;
    /** Width of the barrier. */
    private float width;
    /** Height of the barrier. This is its height in terms of collisions, not of the actual sprite, which will
     * allow characters to move behind the barrier in a top-down perspective. */
    private float height;
    /** Name of this specific barrier. */
    private String name;

    // TODO: Incorporate padding and drawScale
    /** The padding (in physics coordinates) to increase the sprite beyond the physics body */
    protected Vector2 padding;





    public Barrier(float x, float y, float width, float height, FilmStrip barrier, String name, Vector2 padding) {
        super(x, y, width, height);
        this.barrier = barrier;
        this.name = name;
        this.padding = padding;
    }

    public void reset() {

    }


    public void draw (GameCanvas canvas) {
        canvas.draw(barrier, Color.WHITE, this.x, this.y, this.width, this.height);
    }
}
