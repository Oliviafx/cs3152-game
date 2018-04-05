package edu.cornell.gdiac.cityoflight;

public class AIController{

    /**
     * Enumeration to encode the finite state machine.
     */
    private static enum FSMState {

        /** The creature is patrolling around */
        PATROL,
        /** The creature has Annette in line of sight or senses her around*/
        SEEK,
        /** The creature actively chases Annette (pathfinding) */
        CHASE
    }

}