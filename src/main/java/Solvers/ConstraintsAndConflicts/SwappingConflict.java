package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;
import Solvers.Move;

import java.util.Objects;

/**
 * Represents a conflict between 2 {@link Agent}s which are swapping their {@link I_MapCell locations} at a certain time.
 * This is known as s Swapping Conflict or an Edge Conflict.
 */
public class SwappingConflict extends A_Conflict{
    /**
     * In this class, agent2_destination will represent the second agent's destination, which is the same as the first
     * agent's previous location. The first agent's destination will be represented by the super class's
     * {@link #location} field.
     */
    public final I_MapCell agent2_destination;

    public SwappingConflict(Agent agent1, Agent agent2, int time, I_MapCell agent1_destination, I_MapCell agent2_destination) {
        super(agent1, agent2, time, agent1_destination);
        this.agent2_destination = agent2_destination;
    }


    /**
     * returns an array of {@link Constraint}, each of which could prevent this conflict.
     * @return an array of {@link Constraint}, each of which could prevent this conflict.
     */
    @Override
    public Constraint[] getPreventingConstraints() {
        return new Constraint[]{
                /*
                 the order of locations:
                 agent1 will be prevented from moving from its previous location (agent2's destination) to its destination.
                 */
                new Constraint(agent1, time, agent2_destination, location),
                /*
                 the order of locations:
                 agent2 will be prevented from moving from its previous location (agent1's destination) to its destination.
                 */
                new Constraint(agent2, time, location, agent2_destination)};
    }

    /**
     * assumes both moves have the same {@link Move#timeNow}.
     * @return true if these moves have a swapping conflict.
     */
    public static boolean haveConflicts(Move move1, Move move2){
        return move1.prevLocation.equals(move2.currLocation)
                && move2.prevLocation.equals(move1.currLocation);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SwappingConflict that = (SwappingConflict) o;

        return agent2_destination.equals(that.agent2_destination);

    }

    @Override
    public int hashCode() {
        return  Objects.hash( this.agent1 ) * Objects.hash( this.agent2 ) * Objects.hash( this.time ) *
                Objects.hash( this.location ) * Objects.hash( this.agent2_destination );
    }
}
