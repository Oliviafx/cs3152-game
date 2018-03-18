/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * B2Lights version, 3/12/2016
 */
package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.*;

import edu.cornell.gdiac.util.XBox360Controller;

/**
 * Class for reading player input. 
 *
 * This supports both a keyboard and X-Box controller. In previous solutions, we only 
 * detected the X-Box controller on start-up.  This class allows us to hot-swap in
 * a controller via the new XBox360Controller class.
 */
public class InputController {
	/** The singleton instance of the input controller */
	private static InputController theController = null;
	
	/** 
	 * Return the singleton instance of the input controller
	 *
	 * @return the singleton instance of the input controller
	 */
	public static InputController getInstance() {
		if (theController == null) {
			theController = new InputController();
		}
		return theController;
	}
	
	// Fields to manage buttons
	/** Whether the reset button was pressed. */
	private boolean resetPressed;
	private boolean resetPrevious;
//	/** Whether the button to advanced worlds was pressed. */
//	private boolean nextPressed;
//	private boolean nextPrevious;
//	/** Whether the button to step back worlds was pressed. */
//	private boolean prevPressed;
//	private boolean prevPrevious;
	/** Whether the debug toggle was pressed. */
	private boolean debugPressed;
	private boolean debugPrevious;
	/** Whether the exit button was pressed. */
	private boolean exitPressed;
	private boolean exitPrevious;
	/** Whether the space bar was pressed. */
	private boolean spacePressed;
	private boolean spacePrevious;


	/** How much did Annette move horizontally? */
	private float aHoriz;
	/** How much did Annette move vertically? */
	private float aVert;

	/** How much did creature move horizontally? */
	private float cHoriz;
	/** How much did creature move vertically? */
	private float cVert;
	
	/** An X-Box controller (if it is connected) */
	XBox360Controller xbox;
	
	/**
	 * Returns the amount of Annette sideways movement.
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways movement. 
	 */
	public float getaHoriz() {
		return aHoriz;
	}
	
	/**
	 * Returns the amount of Annette vertical movement.
	 *
	 * -1 = down, 1 = up, 0 = still
	 *
	 * @return the amount of vertical movement. 
	 */
	public float getaVert() {
		return aVert;
	}

	/**
	 * Returns the amount of creature sideways movement.
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways movement.
	 */
	public float getcHoriz() { return cHoriz; }

	/**
	 * Returns the amount of vertical movement.
	 *
	 * -1 = down, 1 = up, 0 = still
	 *
	 * @return the amount of vertical movement.
	 */
	public float getcVert() {
		return cVert;
	}

	/**
	 * Returns true if the reset button was pressed.
	 *
	 * @return true if the reset button was pressed.
	 */

	/**
	 * Returns true if the space bar was pressed.
	 *
	 * @return true if the space bar was pressed.
	 */
	public boolean didSpace() {
		return spacePressed && !spacePrevious;
	}

	public boolean didReset() {
		return resetPressed && !resetPrevious;
	}

//	/**
//	 * Returns true if the player wants to go to the next level.
//	 *
//	 * @return true if the player wants to go to the next level.
//	 */
//	public boolean didForward() {
//		return nextPressed && !nextPrevious;
//	}
//
//	/**
//	 * Returns true if the player wants to go to the previous level.
//	 *
//	 * @return true if the player wants to go to the previous level.
//	 */
//	public boolean didBack() {
//		return prevPressed && !prevPrevious;
//	}
	
	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didDebug() {
		return debugPressed && !debugPrevious;
	}
	
	/**
	 * Returns true if the exit button was pressed.
	 *
	 * @return true if the exit button was pressed.
	 */
	public boolean didExit() {
		return exitPressed && !exitPrevious;
	}
	
	/**
	 * Creates a new input controller
	 * 
	 * The input controller attempts to connect to the X-Box controller at device 0,
	 * if it exists.  Otherwise, it falls back to the keyboard control.
	 */
	public InputController() { 
		// If we have a game-pad for id, then use it.
		xbox = new XBox360Controller(0);
	}

	/**
	 * Reads the input for the player and converts the result into game logic.
	 */
	public void readInput() {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down
		resetPrevious  = resetPressed;
		debugPrevious  = debugPressed;
		exitPrevious = exitPressed;
//		nextPrevious = nextPressed;
//		prevPrevious = prevPressed;
		spacePrevious = spacePressed;

		// Check to see if a GamePad is connected
		if (xbox.isConnected()) {
			readGamepad();
			readKeyboard(true); // Read as a back-up
		} else {
			readKeyboard(false);
		}
}

	/**
	 * Reads input from an X-Box controller connected to this computer.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the crosshair.  They cannot go outside of this zone.
	 *
	 */
	private void readGamepad() {
		resetPressed = xbox.getStart();
		exitPressed  = xbox.getBack();
//		nextPressed  = xbox.getRB();
//		prevPressed  = xbox.getLB();
		debugPressed  = xbox.getY();

		// Increase animation frame, but only if trying to move
		aHoriz = xbox.getLeftX();
		aVert   = xbox.getLeftY();
	}

	/**
	 * Reads input from the keyboard.
	 *
	 * This controller reads from the keyboard regardless of whether or not an X-Box
	 * controller is connected.  However, if a controller is connected, this method
	 * gives priority to the X-Box controller.
	 *
	 * @param secondary true if the keyboard should give priority to a gamepad
	 */
	private void readKeyboard(boolean secondary) {
		// Give priority to gamepad results
		resetPressed = (secondary && resetPressed) || (Gdx.input.isKeyPressed(Input.Keys.R));
		debugPressed = (secondary && debugPressed) || (Gdx.input.isKeyPressed(Input.Keys.O));
//		prevPressed = (secondary && prevPressed) || (Gdx.input.isKeyPressed(Input.Keys.P));
		//nextPressed = (secondary && nextPressed) || (Gdx.input.isKeyPressed(Input.Keys.N));
		exitPressed  = (secondary && exitPressed) || (Gdx.input.isKeyPressed(Input.Keys.ESCAPE));
		spacePressed = (secondary && spacePressed) || (Gdx.input.isKeyPressed(Input.Keys.SPACE));
		
		// Annette Directional controls
		aHoriz = (secondary ? aHoriz : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			aHoriz += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			aHoriz -= 1.0f;
		}

		aVert = (secondary ? aVert : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			aVert += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			aVert -= 1.0f;
		}

		// Creature Directional controls
		cHoriz = (secondary ? cHoriz : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cHoriz += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cHoriz -= 1.0f;
		}

		cVert = (secondary ? cVert : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			cVert += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			cVert -= 1.0f;
		}
	}
}