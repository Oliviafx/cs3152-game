package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.JsonAssetManager;

public class DrawHelper {

    /** Filmstrip for the general menu transitions */
    private FilmStrip general_transition;
    /** number of frames in the transition */
    int GENERAL_TRANSITION_FRAME_NUM = 36;
    /** the start frame of the second part of the transition */
    private int GENERAL_TRANSITION_SECOND_PART = 21;
    /** did the transition reach the second part yet? */
    private boolean general_transition_second_part;
    /** did the transition go through yet? */
    private boolean general_transition_hasAnimated = false;

    /** The screen to show on a winning/losing screen */
    private String chosenScreenKey;
    /** The text to show on a winning/losing screen */
    private String chosenText;
    /** Whether chosenScreenKey and chosenText has been chosen */
    private boolean hasChosenScreenandText = false;


    public boolean get_general_transition_second_part() {
        return general_transition_second_part;
    }

    public DrawHelper(){
    }

    /**
     * reset the boolean values needed for drawing the correct animations.
     */
    public void reset(){
        general_transition_hasAnimated = false;
        general_transition_second_part = false;
        hasChosenScreenandText = false;
    }

    /**
     * draw the general transition.
     */
    public void drawGeneralTransition(ObstacleCanvas canvas){
        TextureRegion texture = JsonAssetManager.getInstance().getEntry("general_transition", TextureRegion.class);
        try {
            general_transition = (FilmStrip) texture;
        } catch (Exception e) {
            general_transition = null;
        }

        if (general_transition != null) {
            int next;

            if (general_transition_hasAnimated){
                next = 0; // if hasAnimated, stay at frame 0;
                general_transition.setFrame(next);
            } else {
                next = (general_transition.getFrame() + 1);
                if (next >= GENERAL_TRANSITION_SECOND_PART){general_transition_second_part = true;}
                if (next < GENERAL_TRANSITION_FRAME_NUM - 1) {
                    general_transition.setFrame(next);
                } else {
                    general_transition_hasAnimated = true;
                }
            }

            general_transition.setFrame(next);
            canvas.begin();
            canvas.draw(general_transition, Color.WHITE, 224, 128f,
                    canvas.getWidth()/2, canvas.getHeight()/2, 0f, 2f, 2f);
            canvas.end();
        }
    }

    /**
     * draw the win / lose screen
     * @param didWin 1 for win, 0 for lose.
     */
    public void drawEndScreen(ObstacleCanvas canvas,BitmapFont textFont, int didWin){
        if (! hasChosenScreenandText) {
            // decide the winning screen to display.
            int random = (int) (Math.random() * 3 + 1);
            chosenScreenKey = ((didWin == 1) ? ("win_screen0" + Integer.toString(random)):
                    ("lose_screen0" + Integer.toString(random)));
            // decide the text to display.
            chosenText = chooseEndText(canvas, didWin);
            hasChosenScreenandText = true;
        }

        TextureRegion end_screen = JsonAssetManager.getInstance().getEntry(chosenScreenKey, TextureRegion.class);
        textFont.setColor(Color.WHITE);
        canvas.begin();
        canvas.draw(end_screen,0,0);
        canvas.drawText(chosenText, textFont, 110 ,200);
        canvas.end();
    }

    /**
     * A helper function to determine what text to show up in a win/lose screen.
     * @param didWin 1 for win, 0 for lose.
     * @return
     */
    public String chooseEndText(ObstacleCanvas canvas, int didWin){
        int end_text_num = (int)(Math.random() * 10 + 1) - 1; //can use a random number if we just want random text.
        String encouragement;

        if (didWin == 1) {
            switch (end_text_num) {
                case 0:
                    encouragement = "Enlightenment.";
                    break;
                case 1:
                    encouragement = "Now are you ready for a real challenge?";
                    break;
                case 2:
                    encouragement = "The creatures shall face banishment!";
                    break;
                case 3:
                    encouragement = "City of Light is safe... for now.";
                    break;
                case 4:
                    encouragement = "And Annette emerges victorious!";
                    break;
                case 5:
                    encouragement = "The light leads the path to victory.";
                    break;
                case 6:
                    encouragement = "You did stunningly well.";
                    break;
                case 7:
                    encouragement = "Ah, I see we have a smart player.";
                    break;
                case 8:
                    encouragement = "And the mime once again does it!";
                    break;
                case 9:
                    encouragement = "Cleansed by the Sigil of Power!";
                    break;
                default:
                    encouragement = "Should never get here";
            }
        } else {
            switch(end_text_num){
                case 0:
                    encouragement = "Try to think outside the box...";
                    break;
                case 1:
                    encouragement = "Cheer up, mime over matter!";
                    break;
                case 2:
                    encouragement = "C'mon, you can do it!";
                    break;
                case 3:
                    encouragement = "Even my grandma beat this level.";
                    break;
                case 4:
                    encouragement = "The City of Light needs your help.";
                    break;
                case 5:
                    encouragement = "The streets grow a little bit darker.";
                    break;
                case 6:
                    encouragement = "Shadows seep onto the streets... ";
                    break;
                case 7:
                    encouragement = "May the light guide you.";
                    break;
                case 8:
                    encouragement = "Perhaps I overestimated you.";
                    break;
                case 9:
                    encouragement = "Failure is the best teacher.";
                    break;
                default:
                    encouragement = "Should never get here";
            }
        }
        return encouragement;
    }
}
