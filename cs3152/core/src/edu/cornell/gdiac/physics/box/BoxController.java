package edu.cornell.gdiac.physics.box;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.physics.platform.PlatformController;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * Class for box.
 */
public class BoxController {

    /** The singleton instance of the box controller */
    private static BoxController theController = null;

    /**
     * Return the singleton instance of the box controller
     *
     * @return the singleton instance of the box controller
     */
    public static BoxController getInstance() {
        if (theController == null) {
            theController = new BoxController();
        }
        return theController;
    }

    // Fields
    /** Whether a box has already been summoned. */
    private boolean summoned;
    /** (LATER USAGE) Whether a box is active. */
    private boolean active;
    /** Whether a box collided with Annette. */
    private boolean cAnnette;
    /** (LATER USAGE) Whether a box collided with a rope. */
    private boolean cRope;
    /** (LATER USAGE) Whether a box collided with a creature. */
    private boolean cCreature;

    public boolean isSummoned() { return summoned; }

    public boolean isActive() { return active; }

    public boolean collidedAnnette() { return cAnnette; }

    public boolean collidedRope() { return cRope; }

    public boolean collidedCreature() { return cCreature; }

    /**
     * Creates a new box controller.(???)
     *
     * The box controller creates a box(???)
     */
    public BoxController() {
        // DONT KNOW WHAT TO DO
    }

    /**
     * need: Annette's direction (getDirection),
     */
    public void summonBox() {
        // DONT KNOW WHAT TO DO
    }

    public void pushBox() {
        // DONT KNOW WHAT TO DO
    }

    private void checkCollision() {
        // DONT KNOW WHAT TO DO
    }

    /**
     * Updates that animation for a single box
     *
     * This method is here instead of the the box model because of our philosophy
     * that models should always be lightweight.  Animation includes sounds and other
     * assets that we do not want to process in the model
     *
     * @param  on       Whether to turn the animation on or off
     */
    public void updateBox(BoxModel box, boolean on) {
//        String sound = box.getBoxSound();
        if (on) {
            box.animateBox(true);
            // (3/5/18): sample code; can be used later when we implement sound
//            if (!SoundController.getInstance().isActive(sound)) {
//                SoundController.getInstance().play(sound, sound, true);
//            }
        } else {
            box.animateBox(false);
            // (3/5/18): sample code; can be used later when we implement sound
//            if (SoundController.getInstance().isActive(sound)) {
//                SoundController.getInstance().stop(sound);
//            }
        }
    }

}
