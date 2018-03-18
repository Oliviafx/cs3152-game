package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class CreatureModel extends WheelObstacle {

    /** FilmStrip pointer to the texture region */
    private FilmStrip filmstrip;

    public CreatureModel (){
        super(0,0,1.0f);
        setFixedRotation(false);

        setName("Fred the Head");
        setPosition(1.5f,1.5f);
        setRadius(0.5f);

        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(1.0f);
        setFriction(0.0f);
        setRestitution(0.0f);

        Color debugColor = Color.PINK;
        debugColor.mul(192/255.0f);
        setDebugColor(debugColor);

        Filter filter = new Filter();
        filter.categoryBits = 0000;
        filter.maskBits = 0001;
        setFilterData(filter);


        TextureRegion texture = JsonAssetManager.getInstance().getEntry("textures/creature.png", TextureRegion.class);
        try {
            filmstrip = (FilmStrip)texture;
        } catch (Exception e) {
            filmstrip = null;
        }
        setTexture(texture);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(ObstacleCanvas canvas) {
        if (texture != null) {
            canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1.0f,1.0f);
        }
    }

}
