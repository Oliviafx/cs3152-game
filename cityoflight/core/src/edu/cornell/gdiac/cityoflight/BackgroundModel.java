package edu.cornell.gdiac.cityoflight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.JsonAssetManager;

/**Provides drawing functions for background tiles with no obstacles*/
public class BackgroundModel {
    private int x;
    private int y;
    private FilmStrip tileTexture;

    public BackgroundModel(int x1, int y1, FilmStrip tileTexture1){
        x=x1;
        y=y1;
        tileTexture = tileTexture1;
    }

    public void draw(ObstacleCanvas canvas){
        float scale = GameController.TEMP_SCALE;
        if (tileTexture != null) {
           // System.out.println("x: "+x+", y: "+y);
            canvas.draw(tileTexture, Color.WHITE, x, y, x * scale, y * scale, 0,scale , scale);
           // System.out.println("drawing tiles");
        }
        else{
            System.out.println("tex not found");
        }


    }

}
