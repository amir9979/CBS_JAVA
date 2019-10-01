package Solvers;

import Instances.Agents.Agent;
import Solvers.ConstraintsAndConflicts.SwappingConflict;
import Solvers.ConstraintsAndConflicts.VertexConflict;

import java.util.*;
import java.util.function.Consumer;

/**
 * A collection of {@link SingleAgentPlan}s, representing a solution to a Path Finding problem.
 * If the collection contains more than one plan, it is a solution to a Multi Agent Path Finding problem.
 */
public class Solution implements Iterable<SingleAgentPlan>{
    /**
     * An unmodifiable {@link Map}, mapping {@link Agent agents} to their {@link SingleAgentPlan plans}.
     */
    private final Map<Agent, SingleAgentPlan> agentPlans;

    public Solution(Map<Agent, SingleAgentPlan> agentPlans) {
        this.agentPlans = Map.copyOf(agentPlans);
    }

    public Solution(Collection<? extends SingleAgentPlan> plans) {
        Map<Agent, SingleAgentPlan> agentPlanMap = new HashMap<>();
        for (SingleAgentPlan plan :
                plans) {
            agentPlanMap.put(plan.agent, plan);
        }
        this.agentPlans = agentPlanMap;
    }

    public SingleAgentPlan getPlanFor(Agent agent){
        return agentPlans.get(agent);
    }

    /**
     * Looks for vertex conflicts ({@link VertexConflict}) or swapping conflicts ({@link SwappingConflict}). Runtime is
     * O( (n-1)*mTotal ) , where n = the number of {@link SingleAgentPlan plans}/{@link Agent agents} in this solution,
     * and mTotal = the total number of moves in all plans together.
     * @return true if the solution is valid (contains no vertex or swapping conflicts).
     */
    public boolean isValidSolution(){
        // todo improve by using a Set of Moves.
//        Set<Move> previousMoves = new HashSet<>();
//        for (SingleAgentPlan plan :
//                agentPlans.values()) {
//            for (Move move :
//                    plan) {
//                Move reverseMove = new Move(move.agent, move.timeNow, move.currLocation, move.prevLocation);
//                if()
//            }
//        }
        for (SingleAgentPlan plan :
                agentPlans.values()) {
            for (SingleAgentPlan otherPlan :
                    agentPlans.values()) {
                if(! (plan == otherPlan) ){ //don't compare with self
                    if(plan.conflictsWith(otherPlan)) {return false;}
                }
            }
        }
        return true;
    }

    //todo add String serialization and deserialization

    @Override
    public String toString() {
        return agentPlans.values().toString();
        //nicetohave JSON string or something
    }

    public String readableToString(){
        StringBuilder sb = new StringBuilder();
        List<Agent> agents = new ArrayList<>(this.agentPlans.keySet());
        Collections.sort(agents, Comparator.comparing(agent -> agent.iD));
        for(Agent agent : agents){
            sb.append("\nPlan for agent ").append(agent.iD);
            for(Move move : this.agentPlans.get(agent)){
                sb.append('\n').append(move.timeNow).append(": ").append(move.prevLocation.getCoordinate()).append(" -> ").append(move.currLocation.getCoordinate());
            }
        }
        sb.append('\n');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solution that = (Solution) o;
        return agentPlans.equals(that.agentPlans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentPlans);
    }

    /*  = Iterator Interface =  */

    @Override
    public Iterator<SingleAgentPlan> iterator() {
        return agentPlans.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super SingleAgentPlan> action) {
        agentPlans.values().forEach(action);
    }

    @Override
    public Spliterator<SingleAgentPlan> spliterator() {
        return agentPlans.values().spliterator();
    }

    //nicetohave validations?
}
