/*
 * ExitModel.java
 *
 * This is a refactored version of the exit door from Lab 4.  We have made it a specialized
 * class so that we can import its properties from a JSON file.  
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * B2Lights version, 3/12/2016
 */
package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import java.lang.reflect.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * A sensor obstacle representing the end of the level
 *
 * Note that the constructor does very little.  The true initialization happens
 * by reading the JSON value.
 */
public class ExitModel extends BoxObstacle {

	/**
	 * Create a new ExitModel with degenerate settings
	 */	
	public ExitModel() {
		super(0,0,1,1);
//		super(0,0,1);
		setSensor(true);
	}


	
	/**
	 * Initializes the exit door via the given JSON value
	 *
	 * The JSON value has been parsed and is part of a bigger level file.  However, 
	 * this JSON value is limited to the exit subtree
	 *
	 *
	 *
	 */
	public void initialize(float[] pos, float width, float height, String debugColors, String textr, float[] pSize) {
//
//		"bodytype":    	"static",
//				"density":  	  0.0,
//				"friction":       0.0,
//				"restitution":    0.0,
//				"collideBits":	"0010",
//				"excludeBits":	"0000",


		setName("exit");
		setPosition(pSize[0] - pos[0]/64 - 1,pSize[1] - pos[1]/64 - 1 );
//		setRadius(radius);
		setWidth(width/64);
		setHeight(height/64);

		// Technically, we should do error checking here.
		// A JSON field might accidentally be missing
		setBodyType(BodyDef.BodyType.StaticBody );
		setDensity(0);
		setFriction(0);
		setRestitution(0);
		
		// Create the collision filter (used for light penetration)
      	short collideBits = LevelModel.bitStringToShort("0010");
      	short excludeBits = LevelModel.bitStringToComplement("0000");
      	Filter filter = new Filter();
      	filter.categoryBits = collideBits;
      	filter.maskBits = excludeBits;
      	setFilterData(filter);

		// Reflection is best way to convert name to color
		Color debugColor;
		try {
			String cname = debugColors.toUpperCase();
		    Field field = Class.forName("com.badlogic.gdx.graphics.Color").getField(cname);
		    debugColor = new Color((Color)field.get(null));
		} catch (Exception e) {
			debugColor = null; // Not defined
		}

		debugColor.mul(192/255.0f);
		setDebugColor(debugColor);
		
		// Now get the texture from the AssetManager singleton
		String key = textr;
		TextureRegion texture = JsonAssetManager.getInstance().getEntry(key, TextureRegion.class);
		setTexture(texture);
	}

	/**
	 * Draws the physics object.
	 *
	 * @param canvas Drawing context
	 */
	public void draw(ObstacleCanvas canvas) {

		if (texture != null) {

			canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y+texture.getRegionHeight()/3, getAngle(),GameController.TEMP_SCALE * 2, GameController.TEMP_SCALE  * 2);
//			canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y, getAngle(),GameController.TEMP_SCALE * 2, GameController.TEMP_SCALE  * 2);
		}
	}
}
