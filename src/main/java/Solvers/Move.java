package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;

/**
 * A single move for a single agent. Immutable.
 */
public class Move {
    /**
     * The {@link Agent} making the move.
     */
    final Agent agent;
    /**
     * The time at the end of the move.
     * If an agent was at v0 at t0, and it moved to v1 at t1, then timeNow for that move equals t1.
     */
    final int timeNow;
    /**
     * The agent's location before the move. Can equal {@link #currLocation}.
     */
    final I_MapCell prevLocation;
    /**
     * The {@link #agent}'s location at the end of the move
     */
    final I_MapCell currLocation;

    public Move(Agent agent, int timeNow, I_MapCell prevLocation, I_MapCell currLocation) {
        if(agent == null || timeNow<1 || prevLocation == null || currLocation == null){
            throw new IllegalArgumentException();
        }
        this.agent = agent;
        this.timeNow = timeNow;
        this.prevLocation = prevLocation;
        this.currLocation = currLocation;
    }
}
