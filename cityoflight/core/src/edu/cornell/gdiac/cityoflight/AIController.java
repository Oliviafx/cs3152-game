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
        /** The creature senses Annette around (by some means other than sight) */
        SENSE,
        /** The creature is being distracted (by Annette's distraction) */
        DISTRACT,
        /** The creature chases Annette */
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

    /** The distraction's last seen position */
    private Vector2 lastseendistraction;

    /** Stores original line of sight distance */
    private float sightDistanceCache;
    /** Stores original speed */
    private float speedCache;

    /**
     * Constants for creatures' specific characteristics and/or behavior
     */
    private float LouSenseDistance = 6.0f;
    private float TarasqueSpeedGain = 5.0f;
    private float BlancheMaxSpeedGain = 4.5f;
    private float BlancheCurrentSpeedGain = BlancheMaxSpeedGain;

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
        this.sightDistanceCache = creature.getVision().getDistance();
        this.speedCache = creature.getSpeedInput();

        state = FSMState.PATROL;
        ticks = 0;
    }

    private Vector2 cAngleCache = new Vector2();


    public CreatureModel getCreature(){
        return creature;
    }


    /**
     * Use this method for the AIcontroller to decide what to do next.
     */
    public void chooseAction(){
        // Increment the number of ticks.
        ticks++;

        creature.setTurnCool(creature.getTurnCool() - 1);
        creature.setAggroCool(creature.getAggroCool() - 1);

        if (ticks % 10 == 0) {
            changeStateIfApplicable();
        }
    }

    /**
     * This method defines what the action to do actually is.
     */
    public void doAction(){

        switch(state){
            case PATROL:

                if (creature.getType() == 1) {

                    // reset vision radius to normal.
                    if (creature.getVision().getDistance() > sightDistanceCache) {
                        creature.getVision().setDistance(creature.getVision().getDistance() - 0.1f);
                    }

                    // if snail collides with wall
                    if (creature.getStuck() && creature.getTurnCool() <= 0) {
                        System.out.println("snail behavior: go other way around");
                        creature.setXInput(-creature.getXInput());
                        creature.setYInput(-creature.getYInput());
                        creature.setStuck(false);
                        creature.setTurnCool(creature.getTurnLimit());
                    }

                } else if (creature.getType() == 2){

                    // if dragon collides with wall
                    if (creature.getStuck() && creature.getTurnCool() <= 0) {

                        if (turnRight()) {
                            System.out.println("dragon behavior: go in opposite direction");
                            if (creature.getXInput() > 0) {
                                creature.setYInput(-creature.getXInput());
                                creature.setXInput(0);
                            } else if (creature.getXInput() < 0) {
                                creature.setYInput(-creature.getXInput());
                                creature.setXInput(0);
                            } else if (creature.getYInput() > 0) {
                                creature.setXInput(creature.getYInput());
                                creature.setYInput(0);
                            } else if (creature.getYInput() < 0) {
                                creature.setXInput(creature.getYInput());
                                creature.setYInput(0);
                            }
                        } else /* turn left */ {
                            System.out.println("dragon behavior: turns left");
                            if (creature.getXInput() > 0) {
                                creature.setYInput(creature.getXInput());
                                creature.setXInput(0);
                            } else if (creature.getXInput() < 0) {
                                creature.setYInput(creature.getXInput());
                                creature.setXInput(0);
                            } else if (creature.getYInput() > 0) {
                                creature.setXInput(-creature.getYInput());
                                creature.setYInput(0);
                            } else if (creature.getYInput() < 0) {
                                creature.setXInput(-creature.getYInput());
                                creature.setYInput(0);
                            }
                        }

                        creature.setStuck(false);
                        creature.setTurnCool(creature.getTurnLimit());
                    }

                } else if (creature.getType() == 3){
                    BlancheCurrentSpeedGain = BlancheMaxSpeedGain;

                    // if blanche collides with wall
                    if (creature.getStuck() && creature.getTurnCool() <= 0) {
                        System.out.println("blanche behavior: go in opposite direction");
                        creature.setXInput(-creature.getXInput());
                        creature.setYInput(-creature.getYInput());
                        creature.setStuck(false);
                        creature.setTurnCool(creature.getTurnLimit());
                    }
                }

                cAngleCache.set(creature.getXInput(),creature.getYInput());
                //System.out.println("movement = " + currentcreature.getMovement());
                break;

            case SENSE:

                if (creature.getType() == 1){
                    if (creature.getVision().getDistance() <= LouSenseDistance) {
                        creature.getVision().setDistance(creature.getVision().getDistance() + 0.1f);
                    }

                    // if snail collides with wall
                    if (creature.getStuck() && creature.getTurnCool() <= 0) {
                        System.out.println("snail behavior: change direction");
                        creature.setXInput(-creature.getXInput());
                        creature.setYInput(-creature.getYInput());
                        creature.setStuck(false);
                        creature.setTurnCool(creature.getTurnLimit());
                    }
                }

                cAngleCache.set(creature.getXInput(),creature.getYInput());
                break;

            case DISTRACT:

                if (creature.getType() == 1){
                    cAngleCache.set(0,0); // snail does not move when distracted

                }else if (creature.getType() == 3){

                    if (creature.getTurnCool() <= 0) {

                        cAngleCache.set(getNextDistractMovement().x * BlancheCurrentSpeedGain, getNextDistractMovement().y * BlancheCurrentSpeedGain);
                        creature.setTurnCool(creature.getTurnLimit());

                        if (BlancheCurrentSpeedGain > 0.5) {
                            BlancheCurrentSpeedGain -= 0.1;
                            System.out.println("current speed gain = " + BlancheCurrentSpeedGain);
                        }
                    }
                }
                break;

            case CHASE:

                if (creature.getType() == 1) {
                    //System.out.println("snail: chasing Annette");

                    // extending snail vision range to max if it wan't already
                    if (creature.getVision().getDistance() < LouSenseDistance) {
                        creature.getVision().setDistance(creature.getVision().getDistance() + 0.1f);
                    }

                    if (creature.getTurnCool() <= 0) {
                        cAngleCache.set(getNextMovement().x, getNextMovement().y);
                        creature.setTurnCool(creature.getTurnLimit());
                    }

                } else if (creature.getType() == 2) {

                    if (creature.getTurnCool() <= 0) {
                        cAngleCache.set(creature.getXInput()*TarasqueSpeedGain, creature.getYInput()*TarasqueSpeedGain);
                        creature.setTurnCool(creature.getTurnLimit());
                    }

                } else if (creature.getType() == 3){

                    if (creature.getTurnCool() <= 0) {
                            cAngleCache.set(getNextMovement().x * BlancheCurrentSpeedGain, getNextMovement().y * BlancheCurrentSpeedGain);
                        creature.setTurnCool(creature.getTurnLimit());
                        if (BlancheCurrentSpeedGain > 0) {
                            BlancheCurrentSpeedGain -= 0.1;
                            System.out.println("current speed gain = " + BlancheCurrentSpeedGain);
                        }
                    }
                }
                break;
        }

        // code for actual movement - cAngleCache should be set already
        if (cAngleCache.len2() > 0.0f) {
            float angle = cAngleCache.angle();
            // Convert to radians with up as 0
            angle = (float)Math.PI*(angle-90.0f)/180.0f;
            creature.setAngle(angle);
        }
        cAngleCache.scl(creature.getForce());


        if(level.getAnnette().isWalkingInPlace() && !level.getAnnette().getBird()){
            creature.setX(creature.getX() + InputController.getInstance().getcHoriz());
            creature.setY(creature.getY() + InputController.getInstance().getcVert());
            creature.setMovement(cAngleCache.x , cAngleCache.x );

        } else {
            creature.setMovement(cAngleCache.x,cAngleCache.y);
        }

        creature.applyForce();
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
                } else if (canSenseAnnette()){
                    System.out.println("patrol -> sense");
                    creature.setAggroCool(creature.getAggroLimit());
                    state = FSMState.SENSE;
                }
                //#endregion
                break;

            case SENSE: // Do not pre-empt with FSMState in a case
                //#region PUT YOUR CODE HERE

                if (canSeeAnnette()) {
                    System.out.println("sense -> chase");
                    recordLastSeen();
                    creature.setAggroCool(creature.getAggroLimit());
                    state = FSMState.CHASE;
                } else if (isDistracted()){
                    System.out.println("sense -> distract");
                    state = FSMState.DISTRACT;
                } else if (!canSenseAnnette() && creature.getAggroCool() <= 0){
                    System.out.println("sense -> patrol");
                    state = FSMState.PATROL;
                }

                //#endregion
                break;

            case DISTRACT: // Do not pre-empt with FSMState in a case
                //#region PUT YOUR CODE HERE

                if (canSeeAnnette()) {
                    System.out.println("distract -> chase");
                    recordLastSeen();
                    creature.setAggroCool(creature.getAggroLimit());
                    state = FSMState.CHASE;
                } else if (!isDistracted() && canSenseAnnette()){
                    System.out.println("distract -> sense");
                    creature.setAggroCool(creature.getAggroLimit());
                    state = FSMState.SENSE;
                } else if (!isDistracted()) {
                    System.out.println("distract -> patrol");
                    state = FSMState.PATROL;
                }
                //#endregion
                break;

            case CHASE: // Do not pre-empt with FSMState in a case
                //#region PUT YOUR CODE HERE
                /*
                if (canSeeAnnette()){
                    creature.setAggroCool(creature.getAggroLimit());
                    recordLastSeen();
                }

                if (!canSeeAnnette() && canSenseAnnette() && creature.getAggroCool() <= 0 ){
                    System.out.println("chase -> sense");
                    creature.setAggroCool(creature.getAggroLimit());
                    state = FSMState.SENSE;
                }else if (!canSeeAnnette() && !canSenseAnnette() && creature.getAggroCool() <= 0 ) {
                    System.out.println("chase -> patrol");
                    state = FSMState.PATROL;
                }
*/
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
     * Checks whether Annette is moving.
     *
     * @return
     */
    public boolean annetteHasMoved() {
        //System.out.println("checking if Annette moved");
        return (level.getAnnette().getMovement().x != 0 || level.getAnnette().getMovement().y != 0);
    }

    /**
     * Checks whether Annette can be sensed by some means other than vision.
     *
     * @return
     */
    public boolean canSenseAnnette(){
        if (creature.getType() == 1) {
            return (creature.getPosition().sub(level.getAnnette().getPosition()).len() <= LouSenseDistance &&
                    annetteHasMoved());
        } else {
            return false;
        }
    }

    /**
     * Record Annette's position.
     */
    public void recordLastSeen(){
        //System.out.println("recording Annette position");
        lastseen = level.getAnnette().getPosition();
    }


    /**
     * Determine next movement for active chasing Annette
     */
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

    public Vector2 getNextDistractMovement(){

        Vector2 nextMove = new Vector2();


        try {
            float x_diff = creature.getX() - lastseendistraction.x;
            float y_diff = creature.getY() - lastseendistraction.y;

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
     * Only checks for the middle of the model (since the distraction is small.)
     *
     * @return
     */
    public void testDistracted(){
        //System.out.println("testing for distraction");
        try {
            DistractionModel distraction = level.getDistraction();
            if (light.contains(distraction.getX(), distraction.getY()) && creature.getType() != 2){
                //System.out.println("distracted = true");
                lastseendistraction = distraction.getPosition();
                //System.out.println("distractionposition = " + lastseendistraction);
                creature.setDistracted(true);
            }else{
                creature.setDistracted(false);
            }
        } catch (NullPointerException e){
            //System.out.println("distraction is null");
            creature.setDistracted(false);
        }
    }

    public boolean isDistracted(){
        //System.out.println("testing for distraction");
        testDistracted();
        return creature.getDistracted();
    }

    public boolean turnRight(){
        return (Math.random() > 0.5);
    }

}