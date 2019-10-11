package Solvers.ConstraintsAndConflicts;

import Instances.Maps.I_MapCell;
import Solvers.Move;

import java.util.*;

/**
 * A set of {@link Constraint}s.
 * Adding and removing constraints is O(1). Checking if the set {@link #rejects(Move)} or {@link #accepts(Move)} is O(n)
 * in the worst case. However, there will usually be very few unique constraints for every pair of [time,location], with
 * many unique pairs of [time,location]. Therefore, it is on average O(1).
 */
public class ConstraintSet{

    /**
     * Basically a dictionary from [time,location] to agents who can't go there at that time, and locations from which
     * they can't go there at that time.
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
        // using this instead of ConstraintWrapper(Constraint) because this doesn't create an unnecessary Set<Constraint>s
        // in every dummy we create.
        ConstraintWrapper dummy = new ConstraintWrapper(constraint.location, constraint.time);

        if(!this.constraints.containsKey(dummy)){
            this.constraints.put(dummy, dummy);
        }

        return this.constraints.get(dummy).add(constraint);
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

    /**
     *
     * @param constraint
     * @return true if this caused the set to change.
     */
    public boolean remove(Constraint constraint){
        // using this instead of ConstraintWrapper(Constraint) because this doesn't create an unnecessary Set<Constraint>s
        // in every dummy we create.
        ConstraintWrapper dummy = new ConstraintWrapper(constraint.location, constraint.time);

        if(!this.constraints.containsKey(dummy)){
            return false;
        }
        else{
            return this.constraints.get(dummy).remove(constraint);
        }
    }

    /**
     *
     * @param constraints
     * @return true if this caused the set to change.
     */
    public boolean removeAll(Collection<? extends Constraint> constraints) {
        boolean changed = false;
        for (Constraint cons :
                constraints) {
            changed |= this.remove(cons);
        }
        return changed;
    }

    public void clear() {
        this.constraints.clear();
        this.originalConstraints.clear();
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
     * the given {@link Move}.
     * @param move a {@link Move} to check if it is rejected or not.
     * @return true iff any of the {@link Constraint}s that were {@link #add(Constraint) added} to this set
     *          conflict with the given {@link Move}.
     */
    public boolean rejects(Move move){
        ConstraintWrapper dummy = new ConstraintWrapper(move.currLocation, move.timeNow);
        if(!constraints.containsKey(dummy)) {return false;}
        else {
            return constraints.get(dummy).rejects(move);
        }
    }

    /**
     * Returns true iff any of the {@link Constraint}s that were {@link #add(Constraint) added} to this set conflict with
     * the given {@link Move}.
     * @param moves a {@link Collection} of {@link Move}s to check if the are ejected or not.
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

    /**
     * Returns a list of the constraints in this set. It is not a view of this set. Changes to the list will not affect
     * this set.
     * @return a list of the constraints in this set.
     */
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
    private static class ConstraintWrapper{
        private I_MapCell location;
        private int time;
        private Set<Constraint> relevantConstraints;

        public ConstraintWrapper(I_MapCell location, int time) {
            this.location = location;
            this.time = time;
        }

        public ConstraintWrapper(Constraint constraint) {
            this(constraint.location, constraint.time);
            this.add(constraint);
        }

        public ConstraintWrapper(ConstraintWrapper toCopy){
            this.location = toCopy.location;
            this.time = toCopy.time;
            this.relevantConstraints = toCopy.relevantConstraints;
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

        /**
         *
         * @param constraint
         * @return true if this caused a change in the wrapper.
         */
        public boolean remove(Constraint constraint){
            if(constraint == null || this.relevantConstraints == null || !this.relevantConstraints.contains(constraint)){
                return false;
            }
            else{
                return this.relevantConstraints.remove(constraint);
            }
        }

        /**
         *
         * @param constraint a {@link Constraint} with the same time and location as this wrapper.
         * @return true if this caused a change in the wrapper.
         */
        public boolean add(Constraint constraint){
            if(constraint.time != this.time || constraint.location != this.location){return false;}
            if (this.relevantConstraints == null) {
                this.relevantConstraints = new HashSet<>();
            }
            return this.relevantConstraints.add(constraint);
        }

        public boolean rejects(Move move){
            for (Constraint constraint : this.relevantConstraints){
                if(constraint.rejects(move)) return true;
            }
            return false;
        }

        public boolean accepts(Move move){
            return !this.rejects(move);
        }


    }
}
