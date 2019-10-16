package Solvers.ConstraintsAndConflicts;

import java.util.Collection;
import java.util.Iterator;

/**
 * Selects a {@link A_Conflict} with the minimum time.
 */
public class MinTimeConflictSelectionStrategy implements ConflictAvoidanceTable.ConflictSelectionStrategy {
    @Override
    public A_Conflict selectConflict(Collection<A_Conflict> conflicts) {
        if(conflicts == null || conflicts.isEmpty()) {return null;}
        Iterator<A_Conflict> iter = conflicts.iterator();
        A_Conflict minTimeConflict = iter.next();
        while(iter.hasNext()){
            A_Conflict candidate = iter.next();
            if(minTimeConflict.time > candidate.time) {minTimeConflict = candidate;}
        }
        return minTimeConflict;
    }
}
