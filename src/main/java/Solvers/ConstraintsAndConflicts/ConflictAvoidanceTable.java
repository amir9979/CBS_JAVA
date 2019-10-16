package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;
import Solvers.SingleAgentPlan;

import java.util.*;

public class ConflictAvoidanceTable implements I_ConflictAvoidanceTable {

    private Set<A_Conflict> allConflicts = new HashSet<>();
    private HashMap<Agent, HashSet<A_Conflict>> agent_Conflicts = new HashMap<>();
    private HashMap<TimeLocation, HashSet<Agent>> timeLocation_Agents = new HashMap<>();

    private ConflictSelectionStrategy conflictSelectionStrategy;

    public ConflictAvoidanceTable(ConflictSelectionStrategy conflictSelectionStrategy) {
        this.conflictSelectionStrategy = conflictSelectionStrategy;
    }

    public ConflictAvoidanceTable(ConflictAvoidanceTable other){
        //imp - deep copy.
    }

    @Override
    public void add(SingleAgentPlan singleAgentPlan) {
        //imp - use lots of private methods
    }

    @Override
    public A_Conflict selectConflict() {
        return conflictSelectionStrategy.selectConflict(allConflicts);
    }


    private static class TimeLocation {
        public int time;
        public I_MapCell location;

        public TimeLocation(int time, I_MapCell location) {
            this.time = time;
            this.location = location;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TimeLocation)) return false;
            TimeLocation that = (TimeLocation) o;
            return time == that.time &&
                    location.equals(that.location);
        }

        @Override
        public int hashCode() {
            return Objects.hash(time, location);
        }
    }


    public interface ConflictSelectionStrategy {
        A_Conflict selectConflict(Collection<A_Conflict> conflicts);
    }
}
