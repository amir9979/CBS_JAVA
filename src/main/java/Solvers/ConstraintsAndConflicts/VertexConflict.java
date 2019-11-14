package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;
import Solvers.Move;

import java.util.Objects;

/**
 * Represents a conflict between 2 {@link Agent}s, at a certain time, in a certain {@link I_MapCell location}.
 * This is known as a Vertex Conflict.
 */
public class VertexConflict extends A_Conflict {


    public VertexConflict(Agent agent1, Agent agent2, int time, I_MapCell location) {
        super(agent1, agent2, time, location);
    }


    public VertexConflict(Agent agent1, Agent agent2, ConflictAvoidanceTable.TimeLocation timeLocation){
        super(agent1,agent2,timeLocation.time,timeLocation.location);
    }

    /**
     * returns an array of {@link Constraint}, each of which could prevent this conflict.
     * @return an array of {@link Constraint}, each of which could prevent this conflict.
     */
    public Constraint[] getPreventingConstraints(){
        return new Constraint[]{
                new Constraint(agent1,time, location),
                new Constraint(agent2,time, location)};
    }

    /**
     * assumes both moves have the same {@link Move#timeNow}.
     * @return true if these moves have a vertex conflict.
     */
    public static boolean haveConflicts(Move move1, Move move2){
        return move1.currLocation.equals(move2.currLocation);
    }




    /**
     * Override A_Conflict equals and hashcode, because we don't differ between:
     *     1. < agent1, agent2 >
     *     2. < agent2, agent1 >
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof A_Conflict)) return false;
        A_Conflict conflict = (A_Conflict) o;
        return time == conflict.time &&
                ((  Objects.equals(agent1, conflict.agent1) && Objects.equals(agent2, conflict.agent2)) ||
                (   Objects.equals(agent1, conflict.agent2) && Objects.equals(agent2, conflict.agent1))  ) &&
                    Objects.equals(location, conflict.location);
    }


    @Override
    public int hashCode() {
        return agent1.hashCode() + agent2.hashCode() + Objects.hash( time, location );
    }


}
