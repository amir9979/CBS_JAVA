package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;

/**
 * Represents a conflict between 2 {@link Agent}s which are swapping their {@link I_MapCell locations} at a certain time.
 * This is known as s Swapping Conflict or an Edge Conflict.
 */
public class MoveConflict extends LocationConflict{

    /**
     * In this class, agent2_destination will represent the second agent's destination, which is the same as the first
     * agent's previous location. The first agent's destination will be represented by the super class's
     * {@link #location} field.
     */
    public final I_MapCell agent2_destination;

    public MoveConflict(Agent agent1, Agent agent2, int time, I_MapCell agent1_destination, I_MapCell agent2_destination) {
        super(agent1, agent2, time, agent1_destination);
        this.agent2_destination = agent2_destination;
    }


    /**
     * returns an array of {@link MoveConstraint}, each of which could prevent this conflict.
     * @return an array of {@link MoveConstraint}, each of which could prevent this conflict.
     */
    @Override
    public LocationConstraint[] getPreventingConstraints() {
        return new MoveConstraint[]{
                /*
                 the order of locations:
                 agent1 will be prevented from moving from its previous location (agent2's destination) to its destination.
                 */
                new MoveConstraint(agent1,time, agent2_destination, location),
                /*
                 the order of locations:
                 agent2 will be prevented from moving from its previous location (agent1's destination) to its destination.
                 */
                new MoveConstraint(agent2,time, location, agent2_destination)};
    }

}
