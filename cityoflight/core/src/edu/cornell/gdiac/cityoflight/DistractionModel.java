/**
 DistractionModel.java
 This class represents a bird that Annette can mime to distract the creatures.

 */

package edu.cornell.gdiac.cityoflight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import java.lang.reflect.*;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class DistractionModel extends WheelObstacle {
    /**
     * The amount by which the bird's x and y coordinates will change each frame while it is moving.
     */
    private static final float BIRD_STEP = 1f;
    private static final float HOFFSET = 1f;
    private static final float VOFFSET = 1f;

    private static final int BIRD_LIFE = 300;
    private static final float BIRD_RADIUS = .25f;

    private static final float STOPPING_DISTANCE = 3f;
    /**
     * The sprite for the bird.
     */
    TextureRegion birdsprite = new TextureRegion(new Texture("textures/bird.png"));
    /**
     * The bird's x coordinate on the screen.
     */
    private float x;
    /**
     * The bird's y coordinate on the screen.
     */
    private float y;

    private Vector2 position;
    /**
     * Whether the bird is currently active.
     */
    private boolean alive;

    /**
     * The bird's max speed
     */
    private float maxspeed;

    private int life;

    private float damping;
    /**
     * The direction the bird is flying in
     */
    private AnnetteModel.Direction direction;

    /** The current horizontal movement of the character */
    private Vector2 movement = new Vector2();

    private float force;

    /** Returns if the bird is currently on the screen */
    public boolean getAlive() {
        return alive;
    }

    public void setAlive(boolean value) {
        this.alive = value;
//        if (!alive) {
//            super.releaseFixtures();
//        }
//        else {
//            super.createFixtures();
//        }
    }

    public DistractionModel(float x, float y, boolean alive, AnnetteModel.Direction direction) {
        super(x, y, BIRD_RADIUS);
        this.position = new Vector2(x,y);
        this.alive = true;
        this.direction = direction;
        this.life = BIRD_LIFE;
        setPositionByOffset();
    }


    /**
     * Returns the directional movement of this character.
     *
     * This is the result of input times  force.
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

    public float getForce() { return this.force; }

    public void setForce(float force) {
        this.force = force;
    }

    public void setPosition(Vector2 value) {
        this.x = value.x;
        this.y = value.y;
    }

    public void setDamping(float value) {
        damping = value;
    }
    public void setMaxSpeed(float value) {
        maxspeed = value;
    }
    /**
     * Returns whether or not the bird is active.
     *
     * @return whether or not the bird is active.
     */
    public boolean isActive() {
        return this.alive;
    }

    /**
     * Returns whether the box is deactivating
     *
     * @return true if the box is deactivating
     */
    public boolean getDeactivated() {
        return !alive;
    }

    /**
     * Sets whether the bird is alive or not
     *
     * @param value true if the bird is alive
     */
    public void setDeactivated(boolean value) {

        alive = value;
        if (!alive) {
            super.releaseFixtures();
        }
        else {
            super.createFixtures();
        }
    }


    /**
     * Sets whether the bird is active
     *
     *  whether the bird is active
     */

    public boolean activatePhysics(World world) {
        // Get the box body from our parent class
        if (!super.activatePhysics(world)) {
            return false;
        }

        //#region INSERT CODE HERE
        // Insert code here to prevent the body from rotating
        setFixedRotation(true);
        //#endregion

        return true;
    }

    public void setMovement(float dx, float dy) {
        movement.set(dx,dy);
    }

    private void setPositionByOffset() {

        switch(direction) {
            case RIGHT :
                this.x+=HOFFSET;
                break;
            case LEFT:
                this.x -= HOFFSET;
                break;
            case UP:
                this.y+=VOFFSET;
                break;
            case DOWN:
                this.y -= VOFFSET;
                break;
        }
    }

    public void initialize(JsonValue json, float xoff, float yoff) {

        setName(json.name());
//        float[] pos = json.get("pos").asFloatArray();
//        float width = json.get("radius").asFloat();
//        float height = json.get("radius").asFloat();
        setPosition(this.x + xoff, this.y + yoff);
//        System.out.println("this.x " + this.x);
//        setWidth(width);
//        setHeight(height);
        //setRadius(radius);

        // Technically, we should do error checking here.
        // A JSON field might accidentally be missing
        setBodyType(json.get("bodytype").asString().equals("static") ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
        setDensity(json.get("density").asFloat());
        setFriction(json.get("friction").asFloat());
        setRestitution(json.get("restitution").asFloat());
        setForce(json.get("force").asFloat());
        setDamping(json.get("damping").asFloat());
        setMaxSpeed(json.get("maxspeed").asFloat());
//        setStartFrame(json.get("startframe").asInt());
//        setWalkLimit(json.get("walklimit").asInt());

        // Create the collision filter (used for light penetration)
        short collideBits = LevelModel.bitStringToShort(json.get("collideBits").asString());
        short excludeBits = LevelModel.bitStringToComplement(json.get("excludeBits").asString());
        Filter filter = new Filter();
        filter.categoryBits = collideBits;
        filter.maskBits = excludeBits;
        setFilterData(filter);

        // Reflection is best way to convert name to color
//        Color debugColor;
//        try {
//            String cname = json.get("debugcolor").asString().toUpperCase();
//            Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(cname);
//            debugColor = new Color((Color) field.get(null));
//        } catch (Exception e) {
//            debugColor = null; // Not defined
//        }
//        int opacity = json.get("debugopacity").asInt();
//        debugColor.mul(opacity / 255.0f);
//        setDebugColor(debugColor);

        // Now get the texture from the AssetManager singleton
//        String key = json.get("texture").asString();
//        TextureRegion texture = JsonAssetManager.getInstance().getEntry(key, TextureRegion.class);
//        try {
//            birdsprite = (FilmStrip)texture;
//        } catch (Exception e) {
//            birdsprite = null;
//        }
        setTexture(texture);
    }

    public void draw(ObstacleCanvas canvas) {
        if ((birdsprite != null) && alive) {
            canvas.draw(birdsprite, Color.WHITE, origin.x, origin.y, this.body.getPosition().x * drawScale.x,
                    this.body.getPosition().y * drawScale.y, getAngle(), .25f, .25f);
        }
    }

    public void update(float dt) {
        if (life > 0) {

            if (alive && direction != null) {
                switch (direction) {
                    case RIGHT:
                        this.getBody().setLinearVelocity(BIRD_STEP, 0);
                        break;
                    case LEFT:
                        this.getBody().setLinearVelocity(-BIRD_STEP, 0);
                        break;
                    case UP:
                        this.getBody().setLinearVelocity(0, BIRD_STEP);
                        break;
                    case DOWN:
                        this.getBody().setLinearVelocity(0, -BIRD_STEP);
                        break;
                }

            }
            life -= 1;
            super.update(dt);
        } else {
            setAlive(false);
        }
    }
}