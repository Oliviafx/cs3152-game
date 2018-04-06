package edu.cornell.gdiac.cityoflight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import java.lang.Math;
import edu.cornell.gdiac.physics.lights.LightSource;

public class AIController{

    /**
     * Enumeration to encode the finite state machine.
     */
    private static enum FSMState {

        /** The creature is patrolling around */
        PATROL,
        /** The creature has Annette in line of sight or senses her around */
        SEEK,
        /** The creature is being distracted */
        DISTRACT,
        /** The creature actively chases Annette (pathfinding) */
        CHASE
    }

    // Instance Attributes
    /** The creature being controlled by this AIController */
    private CreatureModel creature;
    /** The creature's line of sight */
    private LightSource light;
    /** Reference to the level */
    private LevelModel level;
    /** The creature's current state in the FSM */
    private FSMState state;
    /** The number of ticks since we started this controller */
    private long ticks;

    /** Annette's last seen position */
    private Vector2 lastseen;

    /**
     * Creates an AIController for the creature with the given id.
     *
     * @param creature The creature to control
     * @param level The level map
     */
    public AIController(CreatureModel creature, LevelModel level) {
        this.creature = creature;
        this.light = this.creature.getVision();
        this.level = level;

        state = FSMState.PATROL;
        ticks = 0;
    }

    private Vector2 cAngleCache = new Vector2();


    public void chooseAction(){
        // Increment the number of ticks.
        ticks++;

        creature.setTurnCool(creature.getTurnCool() - 1);
        creature.setAggroCool(creature.getAggroCool() - 1);

        if (ticks % 10 == 0) {
            changeStateIfApplicable();
        }
    }

    public void doAction(){

        switch(state){
            case PATROL:

                if (creature.getType() == 1) {
                    if (creature.getStuck() && creature.getTurnCool() <= 0) {
                        System.out.println("snail behavior: change direction");
                        creature.setXInput(-creature.getXInput());
                        creature.setYInput(-creature.getYInput());
                        creature.setStuck(false);
                        creature.setTurnCool(creature.getTurnLimit());
                    }
                }

                if (creature.getType() == 2){
                    if (creature.getStuck() && creature.getTurnCool() <= 0) {
                        System.out.println("dragon behavior: turns right");
                        if (creature.getXInput() > 0){
                            creature.setYInput(-creature.getXInput());
                            creature.setXInput(0);
                        } else if (creature.getXInput() < 0){
                            creature.setYInput(-creature.getXInput());
                            creature.setXInput(0);
                        } else if (creature.getYInput() > 0){
                            creature.setXInput(creature.getYInput());
                            creature.setYInput(0);
                        } else if (creature.getYInput() < 0){
                            creature.setXInput(creature.getYInput());
                            creature.setYInput(0);
                        }

                        creature.setStuck(false);
                        creature.setTurnCool(creature.getTurnLimit());
                    }
                }

                if (creature.getType() == 3){
                    // dame blanche.
                }

                cAngleCache.set(creature.getXInput(),creature.getYInput());
                //System.out.println("movement = " + currentcreature.getMovement());

                if (cAngleCache.len2() > 0.0f) {
                    float angle = cAngleCache.angle();
                    // Convert to radians with up as 0
                    angle = (float)Math.PI*(angle-90.0f)/180.0f;
                    creature.setAngle(angle);
                }
                cAngleCache.scl(creature.getForce());
                creature.setMovement(cAngleCache.x,cAngleCache.y);
                creature.applyForce();

                break;
            case SEEK:

                break;
            case DISTRACT:

                break;
            case CHASE:

                if (creature.getType() == 1) {
                    //System.out.println("snail behavior: go towards Annette's position");

//                    if (creature.getStuck() && creature.getTurnCool() <= 0) {
//                        System.out.println("snail behavior: change direction");
//                        creature.setXInput(-creature.getXInput());
//                        creature.setYInput(-creature.getYInput());
//                        creature.setStuck(false);
//                        creature.setTurnCool(creature.getTurnLimit());
//                    }
                    if (creature.getTurnCool() <= 0) {
                        cAngleCache.set(getNextMovement().x, getNextMovement().y);
                        creature.setTurnCool(creature.getTurnLimit());
                    }
                }


                if (cAngleCache.len2() > 0.0f) {
                    float angle = cAngleCache.angle();
                    // Convert to radians with up as 0
                    angle = (float)Math.PI*(angle-90.0f)/180.0f;
                    creature.setAngle(angle);
                }
                cAngleCache.scl(creature.getForce());
                creature.setMovement(cAngleCache.x,cAngleCache.y);
                creature.applyForce();
                break;
        }

    }

    /**
     * Change the state of the creature.
     *
     * A Finite State Machine (FSM) is just a collection of rules that,
     * given a current state, and given certain observations about the
     * environment, chooses a new state. For example, if we are currently
     * in the PATROL state, we may want to switch to the CHASE state if the
     * Annette comes into the line of sight.
     */
    private void changeStateIfApplicable() {
        // Add initialization code as necessary
        //#region PUT YOUR CODE HERE

        //#endregion

        // Next state depends on current state.
        switch (state) {
            case PATROL: // Do not pre-empt with FSMState in a case
                //#region PUT YOUR CODE HERE

                if (canSeeAnnette()) {
                    System.out.println("patrol -> chase");
                    recordLastSeen();
                    creature.setAggroCool(creature.getAggroLimit());
                    state = FSMState.CHASE;
                } else if (isDistracted()) {
                    System.out.println("patrol -> distract");
                    state = FSMState.DISTRACT;
                }
                //#endregion
                break;

            case SEEK: // Do not pre-empt with FSMState in a case
                //#region PUT YOUR CODE HERE

                //#endregion
                break;

            case DISTRACT: // Do not pre-empt with FSMState in a case
                //#region PUT YOUR CODE HERE

                if (canSeeAnnette()) {
                    System.out.println("distract -> chase");
                    recordLastSeen();
                    creature.setAggroCool(creature.getAggroLimit());
                    state = FSMState.CHASE;
                } else if (!isDistracted()) {
                    System.out.println("distract -> patrol");
                    state = FSMState.PATROL;
                }
                //#endregion
                break;

            case CHASE: // Do not pre-empt with FSMState in a case
                //#region PUT YOUR CODE HERE

                if (canSeeAnnette()){
                    creature.setAggroCool(creature.getAggroLimit());
                    recordLastSeen();
                }

                if (!canSeeAnnette() && creature.getAggroCool() <= 0 ) {
                    System.out.println("chase -> patrol");
                    state = FSMState.PATROL;
                }

                //#endregion
                break;

            default:
                // Unknown or unhandled state, should never get here
                assert (false);
                state = FSMState.PATROL; // If debugging is off
                break;
        }
    }

    /**
     * Checks whether Annette gets seen by this creature.
     * Only checks the four corners and the middle of the model.
     *
     * @return
     */
    public boolean canSeeAnnette(){
        AnnetteModel annette = level.getAnnette();
        return (light.contains(annette.getX(), annette.getY())||
                light.contains(annette.getX()-annette.getWidth()/2, annette.getY()-annette.getHeight()/2)||
                light.contains(annette.getX()-annette.getWidth()/2, annette.getY()+annette.getHeight()/2)||
                light.contains(annette.getX()+annette.getWidth()/2, annette.getY()-annette.getHeight()/2)||
                light.contains(annette.getX()+annette.getWidth()/2, annette.getY()+annette.getHeight()/2));
    }

    /**
     * Record Annette's position.
     */
    public void recordLastSeen(){
        //System.out.println("recording Annette position");
        lastseen = level.getAnnette().getPosition();
    }

    public Vector2 getNextMovement(){

        Vector2 nextMove = new Vector2();


        try {
            float x_diff = creature.getX() - lastseen.x;
            float y_diff = creature.getY() - lastseen.y;

            int dir = (Math.abs(x_diff) > Math.abs(y_diff)) ? 0 : 1; // 0: move horizontally; 1: vertical: move horizontally

            if (dir == 0) {
                nextMove.y = 0;

                if (x_diff >= 0) {
                    //System.out.println ("chase LEFT");
                    nextMove.x = -creature.getSpeedInput();
                } else {
                    //System.out.println ("chase RIGHT");
                    nextMove.x = creature.getSpeedInput();
                }

            } else if (dir == 1) {
                nextMove.x = 0;

                if (y_diff >= 0) {
                    //System.out.println ("chase DOWN");
                    nextMove.y = -creature.getSpeedInput();
                } else {
                    //System.out.println ("chase UP");
                    nextMove.y = creature.getSpeedInput();
                }
            }
        } catch (NullPointerException e){
            System.out.println ("null pointer");
        }

        return nextMove;
    }

    /**
     * Checks whether the creature is distracted by the distraction.
     * Onlu checks for the middle of the model (since the distraction is small.)
     *
     * @return
     */
    public boolean isDistracted(){
        try {
            DistractionModel distraction = level.getDistraction();
            return (light.contains(distraction.getX(), distraction.getY()));
        } catch (NullPointerException e){
            return false;
        }
    }

}