package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;

/**
 * A constraint on a move from a specific location, to another location, at a specific time.
 * This class is useful for preventing a swapping conflict (AKA edge conflict).
 *
 * It is important to note that while this class extends {@link LocationConstraint}, it is in fact less restrictive.
 * Meaning:
 * a. It accepts all {@link Move moves} accepted by the {@link LocationConstraint} it extends.
 * b. It may also accept other {@link Move moves}, that are rejected by the {@link LocationConstraint} it extends.
 */
public class MoveConstraint extends LocationConstraint{
    /**
     * The previous location of the move.
     */
    private final I_MapCell prevLocation;

    public MoveConstraint(Agent agent, int time, I_MapCell prevLocation, I_MapCell location) {
        super(agent, time, location);
        if(prevLocation == null) throw new IllegalArgumentException();
        this.prevLocation = prevLocation;
    }

    public MoveConstraint(int time, I_MapCell prevLocation, I_MapCell location) {
        this(null, time, location, prevLocation);
    }

    @Override
    public boolean accepts(Move move) {
        return super.accepts(move) ||
                !move.prevLocation.equals(this.prevLocation) /*the previous location is different*/;
    }

    @Override
    public boolean rejects(Move move) {
        return !this.accepts(move);
    }
}
