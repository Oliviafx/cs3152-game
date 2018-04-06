package edu.cornell.gdiac.cityoflight;

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
    /** Reference to the level */
    private LevelModel level;
    /** The creature's current state in the FSM */
    private FSMState state;
    /** The number of ticks since we started this controller */
    private long ticks;

    /**
     * Creates an AIController for the creature with the given id.
     *
     * @param id The unique creature identifier
     * @param level The level map
     */
    public AIController(int id, LevelModel level) {
        this.creature = level.getCreature(id);
        this.level = level;

        state = FSMState.PATROL;
        ticks = 0;
    }




}