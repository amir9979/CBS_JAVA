package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;

/**
 * A constraint on a {@link I_MapCell location}, at a specific time. It may or may not apply to all agents.
 * This class is useful for preventing a vertex conflict.
 */
public class LocationConstraint {
    /**
     * The only agent this constraint applies to. If this is null, this constraint applies to all agents.
     */
    private final Agent agent; //todo should we also have agents that the constraint doesn't apply to?
    /**
     * The time the constraint applies to.
     */
    private final int time;
    /**
     * The location the constraint applies to.
     */
    private final I_MapCell location;

    /**
     * @param agent the specific agent that this constraint applies to. If null, the constraint applies to all agents.
     * @param time the time the constraint applies to.
     * @param location the location the constraint applies to.
     */
    public LocationConstraint(Agent agent, int time, I_MapCell location) {
        if(time<0 || location == null) throw new IllegalArgumentException();
        this.agent = agent;
        this.time = time;
        this.location = location;
    }

    /**
     * @param time the time the constraint applies to.
     * @param location the location the constraint applies to.
     */
    public LocationConstraint(int time, I_MapCell location) {
        this(null, time, location);
    }

    /**
     * Returns true iff the given {@link Move} conflicts with this constraint.
     * @param move a move that might conflict with this constraint
     * @return true iff the given {@link Move} conflicts with this constraint.
     */
    public boolean accepts(Move move){
        if(move == null) throw new IllegalArgumentException();
        return this.location != move.currLocation || this.time != move.timeNow ||
                (this.agent != null && !this.agent.equals(move.agent));
    }

    /**
     * Returns false iff the given {@link Move} conflicts with this constraint.
     * @param move a move that might conflict with this constraint
     * @return false iff the given {@link Move} conflicts with this constraint.
     */
    public boolean rejects(Move move){
        return !this.accepts(move);
    }
}

