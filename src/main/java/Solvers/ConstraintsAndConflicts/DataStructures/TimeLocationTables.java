package Solvers.ConstraintsAndConflicts.DataStructures;

import Instances.Agents.Agent;
import Instances.Maps.I_MapCell;
import Solvers.SingleAgentPlan;

import java.util.*;


public class TimeLocationTables {

    // Maps from a time&location to all relevant agents
    public final Map<TimeLocation, Set<Agent>> timeLocation_Agents;

    // Maps from a location to all time units where at least one agent is occupying the location
    private final Map<I_MapCell,Set<Integer>> location_timeList;

    // Maps from GoalLocation to Agent&time
    private final Map<I_MapCell, AgentAtGoal> goal_agentTime;



    public TimeLocationTables(){
        this.timeLocation_Agents = new HashMap<>();
        this.location_timeList = new HashMap<>();
        this.goal_agentTime = new HashMap<>();
    }


    public TimeLocationTables(TimeLocationTables other){
        this.timeLocation_Agents = new HashMap<>();
        for ( Map.Entry<TimeLocation,Set<Agent>> timeLocationAgentFromOther: other.timeLocation_Agents.entrySet()){
            this.timeLocation_Agents.put(timeLocationAgentFromOther.getKey(), new HashSet<>(timeLocationAgentFromOther.getValue()));
        }
        this.location_timeList = new HashMap<>();
        for ( Map.Entry<I_MapCell,Set<Integer>> location_timeListFromOther: other.location_timeList.entrySet()){
            this.location_timeList.put(location_timeListFromOther.getKey(), new HashSet<>(location_timeListFromOther.getValue()));
        }

        this.goal_agentTime = new HashMap<>();
        for ( Map.Entry<I_MapCell,AgentAtGoal> goalAgentTimeFromOther : other.goal_agentTime.entrySet()){
            this.goal_agentTime.put(goalAgentTimeFromOther.getKey(),goalAgentTimeFromOther.getValue());
        }
    }


    public TimeLocationTables copy(){
        return new TimeLocationTables(this);
    }



    /**
     * Updates {@link #timeLocation_Agents}, {@link #location_timeList}
     * @param timeLocation - {@inheritDoc}
     * @param singleAgentPlan - {@inheritDoc}
     */
    public void addTimeLocation(TimeLocation timeLocation , SingleAgentPlan singleAgentPlan){

        this.timeLocation_Agents.computeIfAbsent(timeLocation, k -> new HashSet<>());
        this.timeLocation_Agents.get(timeLocation).add(singleAgentPlan.agent);
        this.location_timeList.computeIfAbsent(timeLocation.location, k -> new HashSet<>());
        this.location_timeList.get(timeLocation.location).add(timeLocation.time);
    }


    public void addGoalTimeLocation(TimeLocation goalTimeLocation, SingleAgentPlan singleAgentPlan){

        this.addTimeLocation(new TimeLocation(goalTimeLocation.time, goalTimeLocation.location), singleAgentPlan);

        // Add to goal_agentTime, 'put' method will update it's value if already exists
        this.goal_agentTime.put(goalTimeLocation.location, new AgentAtGoal(singleAgentPlan.agent, goalTimeLocation.time));


        this.location_timeList.computeIfAbsent(goalTimeLocation.location, k -> new HashSet<>());
        // A Set of time that at least one agent is occupying
        Set<Integer> timeList = this.location_timeList.get(goalTimeLocation.location);
        timeList.add(goalTimeLocation.time); // add the plan's timeLocation at goal
    }



    public Set<Agent> getAgentsAtTimeLocation(TimeLocation timeLocation){
        return this.timeLocation_Agents.get(timeLocation);
    }

    public AgentAtGoal getAgentAtGoalTime(I_MapCell goalLocation){
        return this.goal_agentTime.get(goalLocation);
    }

    public Set<Integer> getTimeListAtLocation(I_MapCell location){
        return this.location_timeList.get(location);
    }

    public void removeTimeLocationFromAgentAtTimeLocation(TimeLocation timeLocation){
        this.timeLocation_Agents.remove(timeLocation);
    }

    public void removeGoalLocation(I_MapCell goalLocation){
        this.goal_agentTime.remove(goalLocation);
    }

    public void removeLocationFromTimeList(I_MapCell location){
        this.location_timeList.remove(location);
    }




    public static boolean equalsTimeLocations(Map<TimeLocation,Set<Agent>> expectedTimeLocation_agents, Map<TimeLocation,Set<Agent>> actualTimeLocation_agents){

        if( actualTimeLocation_agents.size() != expectedTimeLocation_agents.size() ){
            return false;
        }
        for (Map.Entry<TimeLocation,Set<Agent>> timeLocation_agents: expectedTimeLocation_agents.entrySet()){

            TimeLocation timeLocation = timeLocation_agents.getKey();
            Set<Agent> expectedAgents = expectedTimeLocation_agents.get(timeLocation);
            Set<Agent> actualAgents = actualTimeLocation_agents.get(timeLocation);
            if (! equalsAllAgents(expectedAgents,actualAgents)){
                return false;
            }
        }
        return true;
    }


    private static boolean equalsAllAgents(Set<Agent> expectedAgents, Set<Agent> actualAgents){

        if( expectedAgents.size() != actualAgents.size() ){
            return false;
        }

        for (Agent agent: expectedAgents){
            if (! actualAgents.contains(agent)){
                return false;
            }
        }
        return true;
    }

}
