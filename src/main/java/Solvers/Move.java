package Solvers;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;

/**
 * A single move for a single agent. Immutable.
 */
public class Move {
    final Agent agent;
    /**
     * The time at the end of the move.
     * If an agent was at v0 at t0, and it moved to v1 at t1, then timeNow for that move equals t1.
     */
    final int timeNow;
    /**
     * The {@link #agent}'s location at the end of the move
     */
    final I_MapCell currLocation;
    /**
     * The agent's location before the move. Can equal {@link #currLocation}.
     */
    final I_MapCell prevLocation;

    public Move(Agent agent, int timeNow, I_MapCell currLocation, I_MapCell prevLocation) {
        if(agent == null || timeNow<0 || currLocation == null || prevLocation == null){
            throw new IllegalArgumentException();
        }
        this.agent = agent;
        this.timeNow = timeNow;
        this.currLocation = currLocation;
        this.prevLocation = prevLocation;
    }
}
