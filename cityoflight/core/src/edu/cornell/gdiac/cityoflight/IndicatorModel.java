package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.JsonAssetManager;

public class IndicatorModel {

    /** FilmStrip pointer to the texture region */
    private FilmStrip filmstrip;
    /** The current animation frame of the avatar */
    private int startFrame;

    private SpriteBatch batcher = new SpriteBatch();

    private boolean hasAnimated = false;

    TextureRegion texture = JsonAssetManager.getInstance().getEntry("circle", TextureRegion.class);


    public void initialize(){
        try {
            filmstrip = (FilmStrip)texture;
        } catch (Exception e) {
            filmstrip = null;
        }
    }

    /**
     * Animates this indicator.
     *
     * If the animation is not active, it should stay at the final animation frame.
     *
     */
    public void update(float dt) {
        // Animate if necessary
        if(hasAnimated == false && filmstrip != null){

            int next = (filmstrip.getFrame()+1);
            if (next < filmstrip.getSize()) {
                filmstrip.setFrame(next);
            }else{
                hasAnimated = true;
            }
        }
        //super.update(dt);
    }

    public void draw(ObstacleCanvas canvas) {
//        super.draw(canvas); // Box

        /** (3/5/2018) might need to change the code below; copied from RocketModel */
//        float offsety = mainBox.getRegionHeight()-origin.y;
        batcher.begin();
        batcher.draw(filmstrip,0,0,300,300);
        batcher.end();
    }
}
