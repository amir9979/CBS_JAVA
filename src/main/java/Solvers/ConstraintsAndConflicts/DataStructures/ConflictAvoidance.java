package Solvers.ConstraintsAndConflicts.DataStructures;

import Instances.Agents.Agent;
import Solvers.ConstraintsAndConflicts.A_Conflict;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConflictAvoidance {


    private final Set<A_Conflict> allConflicts; // Keeps all conflicts
    private final Map<Agent, Set<A_Conflict>> agent_conflicts; // Maps from Agent to all related conflicts

    public ConflictAvoidance(){
        this.allConflicts = new HashSet<>();
        this.agent_conflicts = new HashMap<>();
    }

    public ConflictAvoidance(ConflictAvoidance other){
        this.allConflicts = new HashSet<>();
        this.allConflicts.addAll(other.allConflicts);
        this.agent_conflicts = new HashMap<>();
        for (Map.Entry<Agent,Set<A_Conflict>> agentConflictsFromOther: other.agent_conflicts.entrySet()){
            this.agent_conflicts.put(agentConflictsFromOther.getKey(), new HashSet<>(agentConflictsFromOther.getValue()));
        }
    }

    public ConflictAvoidance copy(){
        return new ConflictAvoidance(this);
    }


    /**
     * Adds {@link A_Conflict} to {@link #agent_conflicts}, {@link #allConflicts}
     * @param agent - {@inheritDoc}
     * @param conflict - {@inheritDoc}
     */
    public void addConflictToAgent(Agent agent, A_Conflict conflict) {
        this.agent_conflicts.computeIfAbsent(agent, k -> new HashSet<>());
        this.agent_conflicts.get(agent).add(conflict);
        this.allConflicts.add(conflict);
    }


    /**
     * Removes all agent's conflicts:
     *      1. Removes from {@link #agent_conflicts}
     *      2. Removes from {@link #allConflicts}
     * @param agent
     */
    public void removeAgentConflicts(Agent agent) {

        Set<A_Conflict> agent_conflict = this.agent_conflicts.get(agent);

        if(agent_conflict == null){
            return; // No conflicts to remove
        }

        for (A_Conflict conflictToRemove : agent_conflict) {
            Agent conflictsWith = (agent == conflictToRemove.agent1 ? conflictToRemove.agent2 : conflictToRemove.agent1);
            this.agent_conflicts.get(conflictsWith).remove(conflictToRemove);
            if ( this.agent_conflicts.get(conflictsWith).isEmpty()){
                this.agent_conflicts.remove(conflictsWith); // Has no more conflicts
            }
            this.allConflicts.remove(conflictToRemove); // Remove conflicts
        }

        this.agent_conflicts.remove(agent); // Agents conflicts aren't relevant anymore
    }


    public Set<A_Conflict> getAllConflicts(){
        return this.allConflicts;
    }




    public static boolean equalsAllConflicts(Set<A_Conflict> expectedConflicts, Set<A_Conflict> actualConflicts){

        if( actualConflicts.size() != expectedConflicts.size() ){
            return false;
        }
        for (A_Conflict conflict: expectedConflicts){
            if (! actualConflicts.contains(conflict)){
                return false;
            }
        }
        return true;
    }


}
