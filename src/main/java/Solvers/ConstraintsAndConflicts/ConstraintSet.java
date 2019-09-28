package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;
import Solvers.Move;

import java.util.*;

/**
 * A set of {@link Constraint}s.
 * Doesn't actually store the {@link Constraint} objects, instead replacing them with a different representation. Note
 * that this implementation overrides the logic of {@link Constraint#rejects} and {@link Constraint#accepts(Move)}, meaning
 * any change to them will not be reflected in this class.
 * As a side effect of this implementation, it essentially eliminates all swapping constraints that are made redundant
 * by some vertex constraint.
 */
public class ConstraintSet {

    /**
     * Basically a dictionary from [time,location] to agents who can't go there at that time, and locations from which
     * they can't go there at that time.
     * As a side effect, it essentially eliminates all swapping constraints that are made redundant by some vertex constraint.
     */
    private Map<ConstraintWrapper, ConstraintWrapper> constraints = new HashMap<>();
    private Set<Constraint> originalConstraints = new HashSet<>();

    public ConstraintSet() {
    }

    public ConstraintSet(ConstraintSet toCopy){
        if(toCopy == null) {throw new IllegalArgumentException();}
        this.addAll(toCopy.originalConstraints);
    }

    public ConstraintSet(Collection<? extends Constraint> seedConstraints) {
        if(seedConstraints == null) {throw new IllegalArgumentException();}
        this.addAll(seedConstraints);
    }

    /*  = Set Interface =  */

    public int size() {
        return constraints.size();
    }

    public boolean isEmpty() {
        return constraints.isEmpty();
    }

    /**
     *
     * @param constraint
     * @return true if this caused the set to change.
     */
    public boolean add(Constraint constraint){
        boolean changed = false;
        ConstraintWrapper dummy = new ConstraintWrapper(constraint);

        if(!this.constraints.containsKey(dummy)){
            this.constraints.put(dummy, dummy);
            changed = true;
        }
        changed |= this.constraints.get(dummy).add(constraint.agent);
        changed |= this.constraints.get(dummy).add(constraint.prevLocation);

        this.originalConstraints.add(constraint);

        return  changed;
    }

    /**
     *
     * @param constraints
     * @return true if this caused the set to change.
     */
    public boolean addAll(Collection<? extends Constraint> constraints) {
        boolean changed = false;
        for (Constraint cons :
                constraints) {
            changed |= this.add(cons);
        }
        return changed;
    }

    public void clear() {
        this.constraints.clear();
    }

    /**
     *
     * @param move
     * @return the opposite of {@link #rejects(Move)}
     */
    public boolean accepts(Move move){
        return !rejects(move);
    }

    /**
     * Returns true iff any of the {@link Constraint}s that were {@link #add(Constraint) added} to this set conflict with
     * the given {@link Move}. Doesn't use {@link Constraint#rejects(Move)}, instead overriding its logic locally.
     * @param move a {@link Move} to check if it is rejected or not.
     * @return true iff any of the {@link Constraint}s that were {@link #add(Constraint) added} to this set
     *          conflict with the given {@link Move}.
     */
    public boolean rejects(Move move){
        ConstraintWrapper dummy = new ConstraintWrapper(null /*it's null to save one object creation each time this method is called*/,
                move.currLocation, move.timeNow, null /*it's null to save one object creation each time this method is called*/);

        return constraints.containsKey(dummy) // there is a constraint on this time and location
                && constraints.get(dummy).contains(move.agent) // it applies to the agent that is making this move
                // it is a vertex constraint, or there is a swapping constraint with the correct prevLocation:
                && constraints.get(dummy).contains(move.prevLocation);
    }

    /**
     * Returns true iff any of the {@link Constraint}s that were {@link #add(Constraint) added} to this set conflict with
     * the given {@link Move}. Doesn't use {@link Constraint#rejects(Move)}, instead overriding its logic locally.
     * @param moves a {@link Collection} of {@link Move}s to check if the are ejected or not..
     * @return true iff all of the given {@link Move}s conflict with any of the {@link Constraint}s that were
     *          {@link #add(Constraint) added} to this set.
     */
    public boolean rejectsAll(Collection<? extends Move> moves){
        boolean result = true;
        for (Move move :
                moves) {
            result &= this.rejects(move);
        }
        return result;
    }

    /**
     *
     * @param moves
     * @return the opposite of {@link #rejectsAll(Collection)}.
     */
    public boolean acceptsAll(Collection<? extends Move> moves){
        boolean result = true;
        for (Move move :
                moves) {
            result &= this.accepts(move);
        }
        return result;
    }

    public List<Constraint> getOriginalConstraints() {
        return new ArrayList<>(originalConstraints);
    }

    /**
     * Removes constraints for times that are not in the given range.
     * @param minTime the minimum time (inclusive).
     * @param maxTime the maximum time (exclusive).
     */
    public void trimToTimeRange(int minTime, int maxTime){
        this.originalConstraints.removeIf(constraint -> constraint.time < minTime || constraint.time >= maxTime);
        this.constraints.keySet().removeIf(constraintWrapper -> constraintWrapper.time < minTime || constraintWrapper.time >= maxTime);
    }

    /**
     * replaces the constraint with a simple wrapper that is quick to find in a set.
     */
    private class ConstraintWrapper{
        private Set<Agent> agents;
        private Set<I_MapCell> prevLocations;
        private I_MapCell location;
        private int time;

        public ConstraintWrapper(Agent agent, I_MapCell location, int time, I_MapCell prevLocation) {
            //null means "any agent"
            if (agent == null) {
                this.agents = null;
            }
            else{
                this.agents = new HashSet<>();
                this.agents.add(agent);
            }

            //null means vertex constraint (or "any previous location")
            if (prevLocation == null) {
                this.prevLocations = null;
            }
            else{
                this.prevLocations = new HashSet<>();
                this.prevLocations.add(prevLocation);
            }

            this.location = location;
            this.time = time;
        }

        public ConstraintWrapper(Constraint constraint) {
            this(constraint.agent, constraint.location, constraint.time, constraint.prevLocation);
        }

        public ConstraintWrapper(ConstraintWrapper toCopy){
            this.agents = new HashSet<>(toCopy.agents);
            this.prevLocations = new HashSet<>(toCopy.prevLocations);
            this.location = toCopy.location;
            this.time = toCopy.time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConstraintWrapper that = (ConstraintWrapper) o;

            if (time != that.time) return false;
            return location.equals(that.location);

        }

        @Override
        public int hashCode() {
            int result = location.hashCode();
            result = 31 * result + time;
            return result;
        }

        public boolean contains (Agent agent){
            if(this.agents == null) return true;
            else return this.agents.contains(agent);
        }

        public boolean contains(I_MapCell prevLocation) {
            if(this.prevLocations == null) return true;
            else return this.prevLocations.contains(prevLocation);
        }

        /**
         *
         * @param agent
         * @return true if this caused a change in the wrapper.
         */
        public boolean add(Agent agent){
            if (this.agents == null) {return false;} //already set to "all agents"
            else if (agent == null) { //set agents to "all"
                this.agents = null;
                return true;
            }
            else { // else add agent
                return this.agents.add(agent);
            }
        }

        /**
         *
         * @param prevLocation
         * @return true if this caused a change in the wrapper.
         */
        public boolean add(I_MapCell prevLocation) {
            if (this.prevLocations == null) {return false;} //there is already a vertex constraint for this time.
            else if (prevLocation == null) { //this is a vertex constraint, so replace existing swapping constraints with a single vertex constraint
                this.prevLocations = null;
                return true;
            }
            else { // else add another swapping constraint
                return this.prevLocations.add(prevLocation);
            }
        }
    }
}
