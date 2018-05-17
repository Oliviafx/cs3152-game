package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.physics.obstacle.ObstacleCanvas;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.JsonAssetManager;
import com.badlogic.gdx.graphics.*;

import java.util.logging.Level;

public class DrawHelper {

    /** filmstrip for walk in place circle to animate out */
    private FilmStrip indicator_out;
    /** filmstrip for walk in place circle to loop */
    private FilmStrip indicator_loop;
    /** walk in place circle has animated out already */
    private boolean walkhasAnimated = false;
    /** constant cooldown time */
    private int animateCOOLTIME = 2;
    /** keep track of cooldowns */
    private int animateCool = animateCOOLTIME;

    /** filmstrip for exclamation to animate when seen */
    private FilmStrip indicator_seen;
    /** exclamation mark has animated out already */
    private boolean seenhasAnimated = false;

    /** Filmstrip for the general menu transitions */
    private FilmStrip general_transition;
    /** number of frames in the transition */
    int GENERAL_TRANSITION_FRAME_NUM = 36;
    /** the start frame of the second part of the transition */
    private int GENERAL_TRANSITION_SECOND_PART = 21;
    /** did the transition reach the second part yet? */
    private boolean general_transition_second_part = false;
    /** did the transition go through yet? */
    private boolean general_transition_hasAnimated = false;
    /** if the asset has not been loaded yet, jump out */
    private boolean jumpOut = false;

    /** Filmstrip for the win transition */
    private FilmStrip win_transition;
    /** number of frames in the transition */
    int WIN_TRANSITION_FRAME_NUM = 22;
    /** the start frame of the second part of the transition */
    private int WIN_TRANSITION_SECOND_PART = 16;
    /** did the transition reach the second part yet? */
    private boolean win_transition_second_part;
    /** did the transition go through yet? */
    private boolean win_transition_hasAnimated = false;
    /** cooldown counter */
    private int nextframeCooldown = 0;
    /** a white with transparency 80% */
    private Color LITTLE_TRANSPARENT_COLOR = new Color(1.0f,1.0f,1.0f,0.92f);

    /** The screen to show on a winning/losing screen */
    private String chosenScreenKey;
    /** The text to show on a winning/losing screen */
    private String chosenText;
    /** Whether chosenScreenKey and chosenText has been chosen */
    private boolean hasChosenScreenandText = false;

    /** getters and setters start here */
    public void setWalkHasAnimatedFalse(){walkhasAnimated = false;}
    public FilmStrip getIndicator_out(){return indicator_out;}
    public void setSeenHasAnimatedFalse(){seenhasAnimated = false;}
    public FilmStrip getIndicator_seen(){return indicator_seen;}

    public boolean get_general_transition_second_part() {
        return general_transition_second_part;
    }

    public boolean get_general_transition_hasAnimated(){
        return general_transition_hasAnimated;
    }

    public boolean getJumpOut(){
        return jumpOut;
    }

    public boolean get_win_transition_second_part() {
        return win_transition_second_part;
    }
    /** getters and setters end here */

    public DrawHelper(){
    }

    /**
     * reset the boolean values needed for drawing the correct animations.
     */
    public void reset(){
        general_transition_hasAnimated = false;
        general_transition_second_part = false;
        win_transition_hasAnimated = false;
        win_transition_second_part = false;
        hasChosenScreenandText = false;
    }

    public void drawTutorial(ObstacleCanvas canvas, LevelModel level, int whichlevel, boolean isSeen){

        if (whichlevel == 1){
            TextureRegion level_one_movement = JsonAssetManager.getInstance().getEntry("level_one_movement", TextureRegion.class);
            TextureRegion level_one_box = JsonAssetManager.getInstance().getEntry("level_one_box", TextureRegion.class);
            TextureRegion level_one_block = JsonAssetManager.getInstance().getEntry("level_one_block", TextureRegion.class);
            TextureRegion level_one_unbox = JsonAssetManager.getInstance().getEntry("level_one_unbox", TextureRegion.class);
            TextureRegion level_one_creature = JsonAssetManager.getInstance().getEntry("level_one_creature", TextureRegion.class);
            TextureRegion level_one_aggro = JsonAssetManager.getInstance().getEntry("level_one_aggro", TextureRegion.class);
            TextureRegion level_one_sigil = JsonAssetManager.getInstance().getEntry("level_one_sigil", TextureRegion.class);
            TextureRegion level_one_tremor = JsonAssetManager.getInstance().getEntry("level_one_tremor", TextureRegion.class);
            //System.out.println("LEVEL ONE");
            if (level.getAnnette().getPosition().x > 0 && level.getAnnette().getPosition().x < 1.5){
                //System.out.println("drawing movement");
                canvas.begin(level.oTran);
                canvas.draw(level_one_movement,10,380);
                canvas.end();
            }
            if (level.getAnnette().getPosition().x >= 1.5 && level.getAnnette().getPosition().x < 7){
                canvas.begin(level.oTran);

                if (isSeen && level.getBox().getDoesExist() == false) {
                    canvas.draw(level_one_box, 200, 380);
                    canvas.draw(level_one_block,200,120);
                }
                if (isSeen && level.getBox().getDoesExist() ){
                    canvas.draw(level_one_aggro, 200, 120);
                }
                if (level.getBox().getDoesExist() == true && !isSeen){
                    canvas.draw(level_one_unbox, 200, 380);
                }
                if (!level.getBox().getDoesExist() && !isSeen){
                    canvas.draw(level_one_creature, 200, 380);
                }
                canvas.end();
            }
            if (level.getAnnette().getPosition().x >= 7 && level.getAnnette().getPosition().x < 9) {
                canvas.begin(level.oTran);
                canvas.draw(level_one_sigil,500,120);
                canvas.end();
            }
            if (level.getAnnette().getPosition().y < 2.5 || level.getAnnette().getPosition().y > 7.3) {
                if (!isSeen) {
                    canvas.begin(level.oTran);
                    canvas.draw(level_one_tremor, 300, 300);
                    canvas.end();
                }
            }
        }

        if (whichlevel == 2){
            TextureRegion level_two_deactivate = JsonAssetManager.getInstance().getEntry("level_two_deactivate", TextureRegion.class);
            TextureRegion level_two_deactivate2 = JsonAssetManager.getInstance().getEntry("level_two_deactivate2", TextureRegion.class);
            TextureRegion level_two_crate = JsonAssetManager.getInstance().getEntry("level_two_crate", TextureRegion.class);
            TextureRegion level_two_single = JsonAssetManager.getInstance().getEntry("level_two_single", TextureRegion.class);
            TextureRegion level_two_tail = JsonAssetManager.getInstance().getEntry("level_two_tail", TextureRegion.class);
            TextureRegion level_two_stand = JsonAssetManager.getInstance().getEntry("level_two_stand", TextureRegion.class);
            TextureRegion level_two_try = JsonAssetManager.getInstance().getEntry("level_two_try", TextureRegion.class);
            TextureRegion level_two_walk = JsonAssetManager.getInstance().getEntry("level_two_walk", TextureRegion.class);
            TextureRegion level_two_here = JsonAssetManager.getInstance().getEntry("level_two_here", TextureRegion.class);
            TextureRegion level_two_disappear = JsonAssetManager.getInstance().getEntry("level_two_disappear", TextureRegion.class);
            TextureRegion level_two_boxhere = JsonAssetManager.getInstance().getEntry("level_two_boxhere", TextureRegion.class);

            if (level.getBox().getDoesExist()){
                if (level.getBox().getDeactivated()){
                    canvas.begin(level.oTran);
                    canvas.draw(level_two_deactivate, 220, 400);
                    canvas.draw(level_two_deactivate2, 220, 100);
                    canvas.draw(level_two_disappear, 500, 100);
                    canvas.draw(level_two_here, 500, 400);
                    canvas.end();
                }
                if (level.getAnnette().getPosition().x > 4 && level.getAnnette().getPosition().x < 5){
                    canvas.begin(level.oTran);
                    canvas.draw(level_two_single, 150, 425);
                    canvas.draw(level_two_tail, 150, 100);
                    canvas.end();
                }
                if (level.getBox().getPosition().x >= 12 && level.getBox().getPosition().y > 5.5) {
                    canvas.begin(level.oTran);
                    canvas.draw(level_two_stand, 600, 100);
                    canvas.draw(level_two_try, 350, 100);
                    canvas.end();
                }
                if (level.getBox().getPosition().x >= 12 && level.getBox().getPosition().y <= 5.5 && level.getAnnette().getPosition().x < 11){
                    canvas.begin(level.oTran);
                    canvas.draw(level_two_walk, 300, 300);
                    canvas.end();
                }
            }

            if (!level.getBox().getDoesExist()){
                if (level.getAnnette().getPosition().x > 2 && level.getAnnette().getPosition().x < 3.5){
                    canvas.begin(level.oTran);
                    canvas.draw(level_two_crate, 100, 425);
                    canvas.end();
                }
                if (level.getAnnette().getPosition().x >= 9 && level.getAnnette().getPosition().x < 14) {
                    canvas.begin(level.oTran);

                    if (!level.getBox().getDoesExist()){
                        canvas.draw(level_two_boxhere, 650, 350);
                    }
                    canvas.end();
                }
            }
        }

        if (whichlevel == 3){
            TextureRegion level_three_bird = JsonAssetManager.getInstance().getEntry("level_three_bird", TextureRegion.class);
            TextureRegion level_three_center = JsonAssetManager.getInstance().getEntry("level_three_center", TextureRegion.class);
            TextureRegion level_three_safe = JsonAssetManager.getInstance().getEntry("level_three_safe", TextureRegion.class);

            if (level.getAnnette().getPosition().x > 0 && level.getAnnette().getPosition().x < 3.5){
                canvas.begin(level.oTran);
                canvas.draw(level_three_bird,10,380);
                canvas.end();
            }

            if (level.getDistraction() != null) {
                if (level.getDistraction().isSeen()) {
                    canvas.begin(level.oTran);
                    canvas.draw(level_three_safe, 300, 380);
                    canvas.end();
                }
            }

            if (level.getAnnette().getPosition().x > 11 && level.getAnnette().getPosition().x < 13.5){
                canvas.begin(level.oTran);
                canvas.draw(level_three_center,800,400);
                canvas.end();
            }
        }

        if (whichlevel == 4) {
            TextureRegion level_four_phase = JsonAssetManager.getInstance().getEntry("level_four_phase", TextureRegion.class);
            TextureRegion level_four_dame = JsonAssetManager.getInstance().getEntry("level_four_dame", TextureRegion.class);

            if (isSeen && level.getAnnette().getPosition().y < 5 && level.getAnnette().getPosition().x < 5){
                canvas.begin(level.oTran);
                canvas.draw(level_four_dame,450,380);
                canvas.end();
            }

            if (level.getAnnette().getPosition().y > 11){
                canvas.begin(level.oTran);
                canvas.draw(level_four_phase,300,700);
                canvas.end();
            }
        }


    }

    /**
     * draw the walk in place indicators.
     * @param canvas
     * @param level
     */
    public void drawWalkInPlace(ObstacleCanvas canvas, LevelModel level){

        TextureRegion texture = JsonAssetManager.getInstance().getEntry("indicator_out", TextureRegion.class);
        TextureRegion texture2 = JsonAssetManager.getInstance().getEntry("indicator_loop", TextureRegion.class);

        try {
            indicator_out = (FilmStrip)texture;
            indicator_loop = (FilmStrip)texture2;
        } catch (Exception e) {
            indicator_out = null;
            indicator_loop = null;
        }

        if(walkhasAnimated == false && indicator_out != null){
            if (animateCool <= 0) {
                int next = (indicator_out.getFrame() + 1);
                if (next < indicator_out.getSize()) {
                    indicator_out.setFrame(next);
                } else {
                    indicator_out.setFrame(0);
                    walkhasAnimated = true;
                    //System.out.println("set animated to : " + walkhasAnimated);
                }
                animateCool = animateCOOLTIME;
            }

            canvas.begin(level.getoTran());

            canvas.draw(indicator_out,Color.SLATE,150f,150f,
                    (level.getAnnette().getX() * level.scale.x),
                    (level.getAnnette().getY() * level.scale.y), 0f, 1.8f, 1.8f);
            canvas.end();

        }else if (walkhasAnimated == true && indicator_loop != null && animateCool <= 0){
            if (animateCool <= 0) {
                int next2 = (indicator_loop.getFrame() + 1) % indicator_loop.getSize();
                indicator_loop.setFrame(next2);
                animateCool = animateCOOLTIME;
            }

            canvas.begin(level.getoTran());
            canvas.draw(indicator_loop,Color.WHITE,150f,150f,
                    (level.getAnnette().getX() * level.scale.x),
                    (level.getAnnette().getY() * level.scale.y), 0f, 1.8f, 1.8f);
            canvas.end();
        }

        animateCool --;
    }

    public void drawisSeen(ObstacleCanvas canvas, LevelModel level) {
        TextureRegion texture = JsonAssetManager.getInstance().getEntry("indicator_seen", TextureRegion.class);
        try {
            indicator_seen = (FilmStrip) texture;
        } catch (Exception e) {
            indicator_seen = null;
        }

        if (indicator_seen != null) {
            int next = (indicator_seen.getFrame() + 1);
            if (next < indicator_seen.getSize() && !seenhasAnimated) {
                indicator_seen.setFrame(next);
            }else{
                seenhasAnimated = true;
            }

            canvas.begin(level.oTran);
            canvas.draw(indicator_seen,Color.WHITE,30f,30f,
                    (level.getAnnette().getX() * level.scale.x),
                    (level.getAnnette().getY() * level.scale.y + 85), 0f, 1.0f, 1.0f);
            canvas.end();
        }
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

            if (general_transition_hasAnimated) {
                next = 0; // if hasAnimated, stay at frame 0;
                general_transition_second_part = false;
                general_transition.setFrame(next);
            } else {
                if (nextframeCooldown == 1) {
                    next = (general_transition.getFrame() + 1);
                    nextframeCooldown = 0;
                } else {
                    next = (general_transition.getFrame());
                    nextframeCooldown = 1;
                }
                if (next >= GENERAL_TRANSITION_SECOND_PART) {
                    general_transition_second_part = true;
                }
                if (next < GENERAL_TRANSITION_FRAME_NUM - 1) {
                    general_transition.setFrame(next);
                } else {
                    general_transition_hasAnimated = true;
                }
            }

            System.out.println("next = " + next);

            general_transition.setFrame(next);
            canvas.begin();
            canvas.draw(general_transition, Color.WHITE, 224, 128f,
                    canvas.getWidth() / 2, canvas.getHeight() / 2, 0f, 2f, 2f);
            canvas.end();
        }
    }

    public void drawGeneralTransition(ObstacleCanvas canvas, FilmStrip general_transition){

        if (general_transition != null) {
            int next;

            if (general_transition_hasAnimated){
                next = 0; // if hasAnimated, stay at frame 0;
                general_transition_second_part = false;
                general_transition.setFrame(next);
            } else {
                if (nextframeCooldown == 1) {
                    next = (general_transition.getFrame() + 1);
                    nextframeCooldown = 0;
                }else{
                    next = (general_transition.getFrame());
                    nextframeCooldown = 1;
                }
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

    public void drawGeneralTransition2(ObstacleCanvas canvas, FilmStrip general_transition){

        if (general_transition != null) {
            int next;

            if (general_transition_hasAnimated){
                next = GENERAL_TRANSITION_FRAME_NUM - 1; // if hasAnimated, stay at frame 0;
                general_transition_second_part = false;
                general_transition.setFrame(next);
            } else {
                if (nextframeCooldown == 1) {
                    next = (general_transition.getFrame() - 1);
                    nextframeCooldown = 0;
                }else{
                    next = (general_transition.getFrame());
                    nextframeCooldown = 1;
                }
                if (next < GENERAL_TRANSITION_SECOND_PART){general_transition_second_part = true;}
                if (next > 1) {
                    general_transition.setFrame(next);
                } else {
                    general_transition_hasAnimated = true;
                }
            }

            System.out.println("next = " + next);

            general_transition.setFrame(next);
            canvas.begin();
            canvas.draw(general_transition, Color.WHITE, 224, 128f,
                    canvas.getWidth()/2, canvas.getHeight()/2, 0f, 2f, 2f);
            canvas.end();
        }
    }

    /**
     * draw the win transition.
     */
    public void drawLevelTransition(ObstacleCanvas canvas, LevelModel level, int didWin){
        TextureRegion texture = JsonAssetManager.getInstance().getEntry("win_transition", TextureRegion.class);
        try {
            win_transition = (FilmStrip) texture;
        } catch (Exception e) {
            win_transition = null;
        }


        if (win_transition != null) {
            int current_frame;

            if (win_transition_hasAnimated){
                current_frame = 0;
                win_transition.setFrame(current_frame);
            } else {
                if (nextframeCooldown == 1) {
                    current_frame = (win_transition.getFrame() + 1);
                    nextframeCooldown = 0;
                }else{
                    current_frame = (win_transition.getFrame());
                    nextframeCooldown = 1;
                }
                if (current_frame >= WIN_TRANSITION_SECOND_PART){win_transition_second_part = true;}
                if (current_frame < WIN_TRANSITION_FRAME_NUM - 1) {
                    win_transition.setFrame(current_frame);
                } else {
                    win_transition_hasAnimated = true;
                }
            }

            win_transition.setFrame(current_frame);

            if (didWin == 1) {
                canvas.begin(level.oTran);
                canvas.draw(win_transition, LITTLE_TRANSPARENT_COLOR, 112, 64,
                        level.getExit().getX() * 64, level.getExit().getY() * 64, 0f, 8f, 8f);
                canvas.end();
            }else{
                canvas.begin(level.oTran);
                canvas.draw(win_transition, Color.BLACK, 112, 64,
                        level.getAnnette().getX() * 64, level.getAnnette().getY() * 64, 0f, 8f, 8f);
                canvas.end();
            }
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

        if (didWin != 1){
            TextureRegion restart_tip = JsonAssetManager.getInstance().getEntry("restart_tip", TextureRegion.class);
            canvas.begin();
            canvas.draw(restart_tip, 750,30);
            canvas.end();
        }else{
            TextureRegion continue_tip = JsonAssetManager.getInstance().getEntry("continue_tip", TextureRegion.class);
            canvas.begin();
            canvas.draw(continue_tip, 100,35);
            canvas.end();
        }
    }

    public void drawTopAchievement(ObstacleCanvas canvas, boolean hasGottenAchievement, int achievementType){
        String achievementKey = (hasGottenAchievement) ?
                ("achievement0" + Integer.toString(achievementType) + "_tip"):
                ("achievement0" + Integer.toString(achievementType) + "_get");
        TextureRegion achievement = JsonAssetManager.getInstance().getEntry(achievementKey, TextureRegion.class);
        canvas.begin();
        canvas.draw(achievement,620,233);
        canvas.end();
    }

    public void drawBottomAchievement(ObstacleCanvas canvas, boolean hasGottenAchievement, int achievementType){
        String achievementKey = (hasGottenAchievement) ?
                ("achievement0" + Integer.toString(achievementType) + "_tip"):
                ("achievement0" + Integer.toString(achievementType) + "_get");
        TextureRegion achievement = JsonAssetManager.getInstance().getEntry(achievementKey, TextureRegion.class);
        canvas.begin();
        canvas.draw(achievement,620,33);
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
                    encouragement = "Try to think outside the box ;)";
                    break;
                case 1:
                    encouragement = "Cheer up, mime over matter!";
                    break;
                case 2:
                    encouragement = "Tip: The Lady in White phases through boxes once you're seen.";
                    break;
                case 3:
                    encouragement = "Tip: Shift + Arrow Key to walk in place!";
                    break;
                case 4:
                    encouragement = "The City of Light needs your help.";
                    break;
                case 5:
                    encouragement = "The streets grow a little bit darker.";
                    break;
                case 6:
                    encouragement = "Tip: Press SPACE to mime or un-mime a box.";
                    break;
                case 7:
                    encouragement = "May the light guide you.";
                    break;
                case 8:
                    encouragement = "Tip: Press X to mime a distraction!";
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
