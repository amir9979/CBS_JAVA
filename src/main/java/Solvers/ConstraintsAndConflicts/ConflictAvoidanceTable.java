package Solvers.ConstraintsAndConflicts;

import Instances.Agents.Agent;
import Instances.Maps.I_Map;
import Instances.Maps.I_MapCell;
import Solvers.Move;
import Solvers.SingleAgentPlan;

import java.sql.Time;
import java.util.*;

public class ConflictAvoidanceTable implements I_ConflictAvoidanceTable {
    // first commit

    public final Set<A_Conflict> allConflicts;
    public final HashMap<Agent, HashSet<A_Conflict>> agent_Conflicts;
    public final HashMap<TimeLocation, HashSet<Agent>> timeLocation_Agents;
    public final HashMap<Agent,SingleAgentPlan> agent_plan;
    public final ConflictSelectionStrategy conflictSelectionStrategy;
    public final HashMap<I_MapCell,HashSet<AgentAtGoal>> goal_agentTime;
    public final Comparator<A_Conflict> comparator = Comparator.comparing((A_Conflict conflict) -> conflict.time);


    public ConflictAvoidanceTable(ConflictSelectionStrategy conflictSelectionStrategy) {
        /* Changed allConflicts from a HashSet to a TreeSet to make MinTimeConflictSelectionStrategy more efficient.
         If we want to make this more generic, we should scrap ConflictSelectionStrategy and instead make this field
         an instance of some new class, thus combining storage and selection of conflicts. @Jonathan Morag 28/10/2019
         */
        this.allConflicts = new TreeSet<>(this.comparator);
        this.agent_Conflicts = new HashMap<>();
        this.timeLocation_Agents = new HashMap<>();
        this.agent_plan = new HashMap<>();
        this.goal_agentTime = new HashMap<>();
        this.conflictSelectionStrategy = conflictSelectionStrategy;
    }


    // Copy constructor
    public ConflictAvoidanceTable(ConflictAvoidanceTable other){
        this.allConflicts = new TreeSet<>(this.comparator);
        this.allConflicts.addAll(other.allConflicts);
        this.agent_Conflicts = new HashMap<>();
        for (Map.Entry<Agent,HashSet<A_Conflict>> agentConflictsFromOther: other.agent_Conflicts.entrySet()){
            this.agent_Conflicts.put(agentConflictsFromOther.getKey(), new HashSet<>(agentConflictsFromOther.getValue()));
        }
        this.timeLocation_Agents = new HashMap<>();
        for ( Map.Entry<TimeLocation,HashSet<Agent>> timeLocationAgentFromOther: other.timeLocation_Agents.entrySet()){
            this.timeLocation_Agents.put(timeLocationAgentFromOther.getKey(), new HashSet<>(timeLocationAgentFromOther.getValue()));
        }
        this.agent_plan = new HashMap<>();
        for ( Map.Entry<Agent,SingleAgentPlan> agentPlanFromOther: other.agent_plan.entrySet()){
            this.agent_plan.put(agentPlanFromOther.getKey(),agentPlanFromOther.getValue());
        }
        this.goal_agentTime = new HashMap<>();
        for ( Map.Entry<I_MapCell,HashSet<AgentAtGoal>> goalAgentTimeFromOther : other.goal_agentTime.entrySet()){
            this.goal_agentTime.put(goalAgentTimeFromOther.getKey(),new HashSet<>(goalAgentTimeFromOther.getValue()));
        }
        this.conflictSelectionStrategy = other.conflictSelectionStrategy;
    }


    /***
     * This method adds a new plan for SingleAgentPlan.
     * Note that if agent's plan already exists, it removes before adding.
     * = Removes =
     * 1. All of previous plan {@link TimeLocation} from 'this.timeLocation_agent'
     * 2. The {@link AgentAtGoal} of the plan.
     * 3. All {@link A_Conflict} for every other {@link Agent} that it conflicts with from 'this.agent_conflicts'
     * 4. The {@link Agent} of the new plan from 'this.agent_conflicts'
     * = Adds =
     * 1. The {@link SingleAgentPlan} to 'this.agent_plan'
     * 2. All of the new plan {@link TimeLocation} to 'this.timeLocation_agent'
     * 3. All {@link A_Conflict} for every other {@link Agent} that it conflicts with to 'this.agent_conflicts'
     *
     * @param singleAgentPlan a new {@link SingleAgentPlan}. The {@link SingleAgentPlan#agent} may already have a plan
     */
    @Override
    public void add(SingleAgentPlan singleAgentPlan) {

        /*  = Remove methods =  */
        SingleAgentPlan previousPlan = this.agent_plan.get(singleAgentPlan.agent);
        removeAgentPreviousPlan(previousPlan);
        removeAgentConflicts(singleAgentPlan.agent);

        /*  = Add methods =  */
        this.agent_plan.put(singleAgentPlan.agent, singleAgentPlan); // Updates if already exists
        addAgentNewPlan(singleAgentPlan);
    }





    /*  = Add methods =  */

    private void addAgentNewPlan(SingleAgentPlan singleAgentPlan) {

        if ( singleAgentPlan == null ){
            return;
        }

        // Todo - check if time starts at t = 1
        // Adds the plan's start location
        TimeLocation timeLocation_0 = new TimeLocation(0, singleAgentPlan.moveAt(1).prevLocation);
        this.timeLocation_Agents.computeIfAbsent(timeLocation_0, k -> new HashSet<>());
        this.timeLocation_Agents.get(timeLocation_0).add(singleAgentPlan.agent);
        addConflictsByTimeLocation(timeLocation_0, singleAgentPlan); // checks for conflicts

        for (int time = 1; time < singleAgentPlan.size(); time++) {
            TimeLocation timeLocation = new TimeLocation(time, singleAgentPlan.moveAt(time).currLocation);
            this.timeLocation_Agents.computeIfAbsent(timeLocation, k -> new HashSet<>());
            this.timeLocation_Agents.get(timeLocation).add(singleAgentPlan.agent);
            addConflictsByTimeLocation(timeLocation, singleAgentPlan);// Checks for conflicts
        }

        // Add the plan's goal location
        int goalTime = singleAgentPlan.size();
        this.goal_agentTime.computeIfAbsent(singleAgentPlan.moveAt(goalTime).currLocation, k -> new HashSet<AgentAtGoal>());
        this.goal_agentTime.get(singleAgentPlan.moveAt(goalTime).currLocation).add(new AgentAtGoal(singleAgentPlan.agent,goalTime));
    }


    private void addConflictToAgent(Agent agent, A_Conflict conflict) {
        this.agent_Conflicts.computeIfAbsent(agent, k -> new HashSet<A_Conflict>());
        this.agent_Conflicts.get(agent).add(conflict);
        this.allConflicts.add(conflict);
    }


    private void addConflictsByTimeLocation(TimeLocation timeLocation, SingleAgentPlan singleAgentPlan) {

        HashSet<Agent> agentsAtTimeLocation = this.timeLocation_Agents.get(timeLocation);
        addVertexConflicts(timeLocation, singleAgentPlan.agent, agentsAtTimeLocation);

        /*  = Check conflicts with agents at their goal =    */
        HashSet<AgentAtGoal> agentsAtGoal = this.goal_agentTime.get(timeLocation.location);
        if( agentsAtGoal != null ){
            for (AgentAtGoal agentAtGoal : agentsAtGoal) {
                if ( timeLocation.time >= agentAtGoal.time ){
                    // Adds a Vertex conflict if time at location is greater than another agent time at goal
                    addVertexConflicts(timeLocation, singleAgentPlan.agent, new HashSet<>(){{add(agentAtGoal.agent);}});
                }
            }
        }


        /*      = Check for swapping conflicts =     */
        addSwappingConflicts(timeLocation.time, singleAgentPlan);
    }


    /***
     * Looks for Swapping conflicts and add if exists
     * @param time - The move's time.
     * @param singleAgentPlan - Agent's plan
     */
    private void addSwappingConflicts(int time, SingleAgentPlan singleAgentPlan) {
        if( time < 1 ){ return;}
        I_MapCell previousLocation = singleAgentPlan.moveAt(time).prevLocation;
        I_MapCell nextLocation = singleAgentPlan.moveAt(time).currLocation;
        HashSet<Agent> agentsMovingToPrevLocations = this.timeLocation_Agents.get(new TimeLocation(time,previousLocation));
        if ( agentsMovingToPrevLocations == null ){
            return;
        }

        /* Add conflict with all the agents that:
            1. Coming from agent's moveAt(time).currLocation
            2. Going to agent's moveAt(time).prevLocation
        */
        for (Agent agentMovingToPrevPosition : agentsMovingToPrevLocations) {
            if( agentMovingToPrevPosition.equals(singleAgentPlan.agent) ){
                continue; // Self Conflict
            }
            if ( this.agent_plan.get(agentMovingToPrevPosition).moveAt(time).prevLocation.equals(nextLocation)){

                SwappingConflict swappingConflict = new SwappingConflict(singleAgentPlan.agent,agentMovingToPrevPosition,time,previousLocation,nextLocation);
                // Add conflict to both of the agents
                addConflictToAgent(singleAgentPlan.agent,swappingConflict);
                addConflictToAgent(agentMovingToPrevPosition,swappingConflict);
            }
        }

    }



    private void addVertexConflicts(TimeLocation timeLocation, Agent agent, HashSet<Agent> agentsAtTimeLocation) {


        for (Agent agentConflictsWith : agentsAtTimeLocation) {
            if( agentConflictsWith.equals(agent) ){
                continue; // Self Conflict
            }
            VertexConflict vertexConflict = new VertexConflict(agent,agentConflictsWith,timeLocation);
            // Add conflict to both of the agents
            addConflictToAgent(agent, vertexConflict);
            addConflictToAgent(agentConflictsWith, vertexConflict);
        }
    }





    /*  = Remove methods =  */

    private void removeAgentPreviousPlan(SingleAgentPlan previousPlan) {
        if ( previousPlan == null ){
            return; // Agent has no previous plan
        }

        Move prevMove_time1 = previousPlan.moveAt(1);
        if ( prevMove_time1 != null ){
            // Remove the plan's start location
            TimeLocation timeLocation = new TimeLocation(0, prevMove_time1.prevLocation);
            HashSet<Agent> agentsAtTimeLocation = this.timeLocation_Agents.get(timeLocation);
            agentsAtTimeLocation.remove(previousPlan.agent);
            if (agentsAtTimeLocation.size() == 0){
                this.timeLocation_Agents.remove(timeLocation);
            }
        }


        for (int time = 1; time < previousPlan.size(); time++) {
            Move prevMove = previousPlan.moveAt(time);
            if ( prevMove != null ){
                TimeLocation timeLocation = new TimeLocation(time, prevMove.currLocation);
                HashSet<Agent> agentsAtTimeLocation = this.timeLocation_Agents.get(timeLocation);
                agentsAtTimeLocation.remove(previousPlan.agent);
                if (agentsAtTimeLocation.size() == 0){
                    this.timeLocation_Agents.remove(timeLocation);
                }
            }
        }

        // Remove the plan's goal location
        int goalTime = previousPlan.size();
        HashSet<AgentAtGoal> agentsAtGoal = this.goal_agentTime.get(previousPlan.moveAt(goalTime).currLocation);
        if( agentsAtGoal != null ){
            agentsAtGoal.remove(new AgentAtGoal(previousPlan.agent,goalTime));
            if ( agentsAtGoal.size() == 0 ){
                this.goal_agentTime.remove(previousPlan.moveAt(goalTime).currLocation);
            }
        }


    }


    private void removeAgentConflicts(Agent agent) {

        HashSet<A_Conflict> agent_conflict = this.agent_Conflicts.get(agent);

        if(agent_conflict == null){
            return; // No conflicts to remove
        }

        for (A_Conflict conflictToRemove : agent_conflict) {
            Agent conflictsWith = (agent == conflictToRemove.agent1 ? conflictToRemove.agent2 : conflictToRemove.agent1);
            this.agent_Conflicts.get(conflictsWith).remove(conflictToRemove);
            if ( this.agent_Conflicts.get(conflictsWith).size() == 0 ){
                this.agent_Conflicts.remove(conflictsWith); // Has no more conflicts
            }
            this.allConflicts.remove(conflictToRemove); // Remove conflicts
        }

        this.agent_Conflicts.remove(agent); // Agents conflicts aren't relevant anymore
    }








    @Override
    public A_Conflict selectConflict() {
        return conflictSelectionStrategy.selectConflict(allConflicts);
    }






    public static class TimeLocation {
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


    private class AgentAtGoal{
        public final Agent agent;
        public final int time;

        public AgentAtGoal(Agent agent, int time) {
            this.agent = agent;
            this.time = time;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AgentAtGoal)) return false;
            AgentAtGoal that = (AgentAtGoal) o;
            return time == that.time &&
                    Objects.equals(agent, that.agent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(agent, time);
        }
    }


    public interface ConflictSelectionStrategy {
        A_Conflict selectConflict(Collection<A_Conflict> conflicts);
    }
}
