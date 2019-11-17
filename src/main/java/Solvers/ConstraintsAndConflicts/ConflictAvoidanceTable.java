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

    /*  = Data structures =   */
    public final Set<A_Conflict> allConflicts; // Keeps all conflicts
    private final Map<Agent, Set<A_Conflict>> agent_Conflicts; // Maps from Agent to all related conflicts

    // Maps from a time&location to all relevant agents
    public final Map<TimeLocation, Set<Agent>> timeLocation_Agents;

    // Maps from a location to all time units where at least one agent is occupying the location
    private final Map<I_MapCell,Set<Integer>> location_timeList;

    private final Map<Agent,SingleAgentPlan> agent_plan; // Maps from Agent to Agent's plan
    private final Map<I_MapCell,AgentAtGoal> goal_agentTime; // Maps from GoalLocation to Agent&time
    private final ConflictSelectionStrategy conflictSelectionStrategy; // Strategy for selecting conflicts



    /**
     * Constructor.
     * @param conflictSelectionStrategy how to choose conflicts.
     */
    public ConflictAvoidanceTable(ConflictSelectionStrategy conflictSelectionStrategy) {
        /* Might want to changed allConflicts from a HashSet to a TreeSet to make MinTimeConflictSelectionStrategy more efficient.
         If we want to make this more generic, we should scrap ConflictSelectionStrategy and instead make this field
         an instance of some new class, thus combining storage and selection of conflicts. @Jonathan Morag 28/10/2019
         */
        this.allConflicts = new HashSet<>();
        this.agent_Conflicts = new HashMap<>();
        this.timeLocation_Agents = new HashMap<>();
        this.location_timeList = new HashMap<>();
        this.agent_plan = new HashMap<>();
        this.goal_agentTime = new HashMap<>();
        this.conflictSelectionStrategy = conflictSelectionStrategy;
    }

    /**
     * Default constructor.
     */
    public ConflictAvoidanceTable() {
        this(new MinTimeConflictSelectionStrategy());
    }

    /**
     * Copy constructor.
     * @param other another {@link ConflictAvoidanceTable} to copy.
     */
    public ConflictAvoidanceTable(ConflictAvoidanceTable other){
        this.allConflicts = new HashSet<>();
        this.allConflicts.addAll(other.allConflicts);
        this.agent_Conflicts = new HashMap<>();
        for (Map.Entry<Agent,Set<A_Conflict>> agentConflictsFromOther: other.agent_Conflicts.entrySet()){
            this.agent_Conflicts.put(agentConflictsFromOther.getKey(), new HashSet<>(agentConflictsFromOther.getValue()));
        }
        this.timeLocation_Agents = new HashMap<>();
        for ( Map.Entry<TimeLocation,Set<Agent>> timeLocationAgentFromOther: other.timeLocation_Agents.entrySet()){
            this.timeLocation_Agents.put(timeLocationAgentFromOther.getKey(), new HashSet<>(timeLocationAgentFromOther.getValue()));
        }
        this.location_timeList = new HashMap<>();
        for ( Map.Entry<I_MapCell,Set<Integer>> location_timeListFromOther: other.location_timeList.entrySet()){
            this.location_timeList.put(location_timeListFromOther.getKey(), new HashSet<>(location_timeListFromOther.getValue()));
        }
        this.agent_plan = new HashMap<>();
        for ( Map.Entry<Agent,SingleAgentPlan> agentPlanFromOther: other.agent_plan.entrySet()){
            this.agent_plan.put(agentPlanFromOther.getKey(),agentPlanFromOther.getValue());
        }
        this.goal_agentTime = new HashMap<>();
        for ( Map.Entry<I_MapCell,AgentAtGoal> goalAgentTimeFromOther : other.goal_agentTime.entrySet()){
            this.goal_agentTime.put(goalAgentTimeFromOther.getKey(),goalAgentTimeFromOther.getValue());
        }
        this.conflictSelectionStrategy = other.conflictSelectionStrategy;
    }

    @Override
    public I_ConflictAvoidanceTable copy() {
        return new ConflictAvoidanceTable(this);
    }

    /***
     * This method adds a new plan for SingleAgentPlan.
     * Note that if agent's plan already exists, it removes before adding.
     * = Removes =
     * 1. All of previous plan {@link TimeLocation} from 'this.timeLocation_agent'
     * 2. The {@link AgentAtGoal} of the plan.
     * 3. All {@link A_Conflict} for every other {@link Agent} that it conflicts with from 'this.agent_conflicts'
     * 4. The {@link Agent} of the new plan from 'this.agent_conflicts' , 'this.location_timeList'
     * = Adds =
     * 1. The {@link SingleAgentPlan} to 'this.agent_plan'
     * 2. All of the new plan {@link TimeLocation} to 'this.timeLocation_agent' , 'this.location_timeList'
     * 3. All {@link A_Conflict} for every other {@link Agent} that it conflicts with to 'this.agent_conflicts'
     * 4. All Conflicts regarding the goal of {@link SingleAgentPlan}
     *
     * @param singleAgentPlan a new {@link SingleAgentPlan}.
     *                        The {@link SingleAgentPlan#agent} may already have a plan
     */
    @Override
    public void addPlan(SingleAgentPlan singleAgentPlan) {

        /*  = Remove methods =  */
        SingleAgentPlan previousPlan = this.agent_plan.get(singleAgentPlan.agent);
        removeAgentPreviousPlan(previousPlan);
        removeAgentConflicts(singleAgentPlan.agent);

        /*  = Add methods =  */
        this.agent_plan.put(singleAgentPlan.agent, singleAgentPlan); // Updates if already exists
        addAgentNewPlan(singleAgentPlan);
    }





    /*  = Add methods =  */

    /**
     * = Adds =
     * 2. All of plan's {@link TimeLocation} to {@link #timeLocation_Agents}, {@link #location_timeList}
     * 3. All {@link A_Conflict} for every other {@link Agent} that it conflicts with to 'this.agent_conflicts'
     * 4. All Conflicts regarding the goal of {@link SingleAgentPlan}
     * @param singleAgentPlan - {@inheritDoc}
     */
    private void addAgentNewPlan(SingleAgentPlan singleAgentPlan) {

        if ( singleAgentPlan == null ){
            return;
        }

        int agentFirstMoveTime = singleAgentPlan.getFirstMoveTime();
        int goalTime = singleAgentPlan.getEndTime();

        /*  Add time locations */
        for (int time = agentFirstMoveTime; time <= goalTime; time++) {
            // Move's from location is 'prevLocation' , therefor timeLocation is time - 1
            I_MapCell location = singleAgentPlan.moveAt(time).prevLocation;
            TimeLocation timeLocation = new TimeLocation(time - 1, location);
            this.addTimeLocation(timeLocation, singleAgentPlan);
        }

        // Add the plan's goal location, that's the currentLocation at the goalTime
        I_MapCell goalLocation = singleAgentPlan.moveAt(goalTime).currLocation;
        this.addTimeLocation(new TimeLocation(goalTime, goalLocation), singleAgentPlan);


        // Add to goal_agentTime, 'put' method will update it's value if already exists
        this.goal_agentTime.put(goalLocation, new AgentAtGoal(singleAgentPlan.agent,goalTime));

        /*  = Check if this agentAtGoal conflicts with other agents =   */
        addVertexConflictsWithGoal(new TimeLocation(goalTime, goalLocation), singleAgentPlan);

    }


    /**
     * Updates {@link #timeLocation_Agents}, {@link #location_timeList}
     * Calls {@link #addConflictsByTimeLocation(TimeLocation, SingleAgentPlan)}
     * @param timeLocation - {@inheritDoc}
     * @param singleAgentPlan - {@inheritDoc}
     */
    private void addTimeLocation(TimeLocation timeLocation , SingleAgentPlan singleAgentPlan){

        this.timeLocation_Agents.computeIfAbsent(timeLocation, k -> new HashSet<>());
        this.timeLocation_Agents.get(timeLocation).add(singleAgentPlan.agent);
        this.location_timeList.computeIfAbsent(timeLocation.location, k -> new HashSet<>());
        this.location_timeList.get(timeLocation.location).add(timeLocation.time);

        // Add conflict of time location
        addConflictsByTimeLocation(timeLocation, singleAgentPlan);// Checks for conflicts
    }


    /**
     * Checks if agent's {@link TimeLocation} at goal conflicts with other agents plans
     * Adds
     * @param timeLocation - {@inheritDoc}
     * @param singleAgentPlan - {@inheritDoc}
     */
    private void addVertexConflictsWithGoal(TimeLocation timeLocation, SingleAgentPlan singleAgentPlan){

        I_MapCell location = timeLocation.location;
        this.location_timeList.computeIfAbsent(location,k -> new HashSet<>());
        // A Set of time that at least one agent is occupying
        Set<Integer> timeList = this.location_timeList.get(location);
        timeList.add(timeLocation.time); // add the plan's timeLocation at goal

        // Check if other plans are using this location after the agent arrived at goal
        for (int time : timeList) {
            if( time > timeLocation.time){
                Set<Agent> agentsAtTimeLocation = this.timeLocation_Agents.get(new TimeLocation(time,location));

                // Adds if agent != agentAtTimeLocation
                addVertexConflicts(new TimeLocation(time, location), singleAgentPlan.agent, agentsAtTimeLocation);
            }
        }

    }


    /**
     * Adds {@link A_Conflict} to {@link #agent_Conflicts}, {@link #allConflicts}
     * @param agent - {@inheritDoc}
     * @param conflict - {@inheritDoc}
     */
    private void addConflictToAgent(Agent agent, A_Conflict conflict) {
        this.agent_Conflicts.computeIfAbsent(agent, k -> new HashSet<>());
        this.agent_Conflicts.get(agent).add(conflict);
        this.allConflicts.add(conflict);
    }


    /**
     * Adds {@link VertexConflict},{@link SwappingConflict} with agents at a given {@link TimeLocation}
     * Check for {@link VertexConflict} with agents at their goal
     * @param timeLocation - {@inheritDoc}
     * @param singleAgentPlan - {@inheritDoc}
     */
    private void addConflictsByTimeLocation(TimeLocation timeLocation, SingleAgentPlan singleAgentPlan) {

        Set<Agent> agentsAtTimeLocation = this.timeLocation_Agents.get(timeLocation);
        addVertexConflicts(timeLocation, singleAgentPlan.agent, agentsAtTimeLocation);

        /*  = Check conflicts with agents at their goal =    */
        AgentAtGoal agentAtGoal = this.goal_agentTime.get(timeLocation.location);
        if( agentAtGoal != null ){
            if ( timeLocation.time >= agentAtGoal.time ){
                // Adds a Vertex conflict if time at location is greater than another agent time at goal
                addVertexConflicts(timeLocation, singleAgentPlan.agent, new HashSet<>(){{add(agentAtGoal.agent);}});
            }
        }


        /*      = Check for swapping conflicts =     */
        addSwappingConflicts(timeLocation.time, singleAgentPlan);
    }


    /***
     * Looks for {@link SwappingConflict}
     * If {@link SwappingConflict} is found:
     *      1. Create two {@link SwappingConflict} for both direction.
     *      2. Add conflicts to both agents in {@link #agent_Conflicts}
     * @param time - The move's time.
     * @param singleAgentPlan - {@inheritDoc}
     */
    private void addSwappingConflicts(int time, SingleAgentPlan singleAgentPlan) {
        if( time < 1 ){ return;}
        I_MapCell previousLocation = singleAgentPlan.moveAt(time).prevLocation;
        I_MapCell nextLocation = singleAgentPlan.moveAt(time).currLocation;
        Set<Agent> agentsMovingToPrevLocations = this.timeLocation_Agents.get(new TimeLocation(time,previousLocation));
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


                // Create two conflicts
                SwappingConflict swappingConflict_addedAgentFirst = new SwappingConflict(   singleAgentPlan.agent,
                                                                                            agentMovingToPrevPosition,
                                                                                            time,
                                                                                            nextLocation,
                                                                                            previousLocation);

                SwappingConflict swappingConflict_addedAgentSecond = new SwappingConflict(  agentMovingToPrevPosition,
                        singleAgentPlan.agent,
                        time,
                        previousLocation,
                        nextLocation);



                // Add conflicts to both of the agents
                addConflictToAgent(singleAgentPlan.agent, swappingConflict_addedAgentFirst);
                addConflictToAgent(agentMovingToPrevPosition, swappingConflict_addedAgentFirst);

                addConflictToAgent(singleAgentPlan.agent, swappingConflict_addedAgentSecond);
                addConflictToAgent(agentMovingToPrevPosition, swappingConflict_addedAgentSecond);
            }
        }
    }


    /**
     * Adds {@link VertexConflict} with other agents at a given {@link TimeLocation}
     * @param timeLocation - {@inheritDoc}
     * @param agent - {@inheritDoc}
     * @param agentsAtTimeLocation - {@inheritDoc}
     */
    private void addVertexConflicts(TimeLocation timeLocation, Agent agent, Set<Agent> agentsAtTimeLocation) {

        if( agentsAtTimeLocation == null ){
            return;
        }

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

    /**
     * Agent has a new plan, therefor the old plan needs to be removed.
     * Removes the plan's goal location from:
     *    1. this.timeLocation_Agents
     *    2. this.location_timeList
     *    3. this.goal_agentTime
     * @param previousPlan - Agent's previous plan in {@link #agent_plan}
     */
    private void removeAgentPreviousPlan(SingleAgentPlan previousPlan) {
        if ( previousPlan == null ){
            return; // Agent has no previous plan
        }

        for (int time = previousPlan.getFirstMoveTime(); time <= previousPlan.getEndTime(); time++) {
            Move prevMove = previousPlan.moveAt(time);
            if ( prevMove != null ){
                TimeLocation timeLocation = new TimeLocation(time - 1, prevMove.prevLocation);
                // 1. remove from this.timeLocation_Agents
                // 2. remove from this.location_timeList
                this.removeTimeLocation(timeLocation, previousPlan);
            }
        }

        /*  = Plan's goal =  */
        int goalTime = previousPlan.size();
        I_MapCell goalLocation = previousPlan.moveAt(goalTime).currLocation;
        TimeLocation timeLocation = new TimeLocation(goalTime, goalLocation);
        // 1. remove from this.timeLocation_Agents
        // 2. remove from this.location_timeList
        this.removeTimeLocation(timeLocation, previousPlan);

        // 3. remove from this.goal_agentTime
        AgentAtGoal agentAtGoal = this.goal_agentTime.get(goalLocation);
        if ( agentAtGoal != null ){
            this.goal_agentTime.remove(goalLocation);
        }


    }


    /**
     * Removes {@link TimeLocation}
     *      1. remove from {@link #timeLocation_Agents}
     *      2. remove from {@link #location_timeList}
     * @param timeLocation - agent's {@link TimeLocation} to remove
     * @param plan - agent's {@link SingleAgentPlan} to remove
     */
    private void removeTimeLocation(TimeLocation timeLocation, SingleAgentPlan plan){

        Set<Agent> agentsAtTimeLocation = this.timeLocation_Agents.get(timeLocation);
        agentsAtTimeLocation.remove(plan.agent);
        if (agentsAtTimeLocation.isEmpty()){
            this.timeLocation_Agents.remove(timeLocation);
            Set<Integer> timeList = this.location_timeList.get(timeLocation.location);
            timeList.remove(timeLocation.time);
            if ( timeList.isEmpty() ){
                this.location_timeList.remove(timeLocation.location); // No agents at this timeLocation
            }
        }
    }


    /**
     * Removes all agent's conflicts:
     *      1. Removes from {@link #agent_Conflicts}
     *      2. Removes from {@link #allConflicts}
     * @param agent
     */
    private void removeAgentConflicts(Agent agent) {

        Set<A_Conflict> agent_conflict = this.agent_Conflicts.get(agent);

        if(agent_conflict == null){
            return; // No conflicts to remove
        }

        for (A_Conflict conflictToRemove : agent_conflict) {
            Agent conflictsWith = (agent == conflictToRemove.agent1 ? conflictToRemove.agent2 : conflictToRemove.agent1);
            this.agent_Conflicts.get(conflictsWith).remove(conflictToRemove);
            if ( this.agent_Conflicts.get(conflictsWith).isEmpty()){
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

        /**
         * This class Wraps up both time and location as one element.
         * Class is used in timeLocation_agents data structure
         * @param time - An int of the time unit in the solution
         * @param location - {@inheritDoc}
         */
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

        /**
         *
         * @param agent - {@inheritDoc}
         * @param time - An int of the time unit in the solution
         */
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
