package Solvers.PrioritisedPlanning;

import Instances.Agents.Agent;
import Instances.Agents.OnlineAgent;
import Instances.MAPF_Instance;
import Solvers.ConstraintsAndConflicts.Constraint;
import Solvers.I_Solver;
import Solvers.OnlineSolution;
import Solvers.SingleAgentPlan;
import Solvers.Solution;

import java.util.*;

/**
 * An online version of {@link PrioritisedPlanning_Solver}.
 */
public class OnlinePP_Solver extends PrioritisedPlanning_Solver {

    /**
     * A custom arrival time to give {@link Agent offline agents} that this solver attempts to solve for.
     * Defaults to {@link OnlineAgent#DEFAULT_ARRIVAL_TIME}.
     */
    private int arrivalTimeForOfflineAgents = OnlineAgent.DEFAULT_ARRIVAL_TIME;

    /**
     * Constructor.
     *
     * @param lowLevelSolver A {@link I_Solver solver}, to be used for solving sub-problems where only one agent is to
     *                       be planned for, and the existing {@link SingleAgentPlan plans} for other
     *                       {@link Agent}s are to be avoided.
     */
    public OnlinePP_Solver(I_Solver lowLevelSolver) {
        super(lowLevelSolver);
    }


    /**
     * Constructor.
     * @param lowLevelSolver A {@link I_Solver solver}, to be used for solving sub-problems where only one agent is to
     *                       be planned for, and the existing {@link SingleAgentPlan plans} for other
     *                       {@link Agent}s are to be avoided.
     * @param arrivalTimeForOfflineAgents A custom arrival time to give {@link Agent offline agents} that this solver attempts to solve for.
     */
    public OnlinePP_Solver(I_Solver lowLevelSolver, int arrivalTimeForOfflineAgents) {
        super(lowLevelSolver);
        this.arrivalTimeForOfflineAgents = arrivalTimeForOfflineAgents >= 0 ? arrivalTimeForOfflineAgents
                : this.arrivalTimeForOfflineAgents;
    }


    @Override
    protected Solution solvePrioritisedPlanning(List<? extends Agent> agents, MAPF_Instance instance, List<Constraint> initialConstraints) {
        Map<Integer, Solution> solutionsAtTimes = new HashMap<>();
        Map<Integer, List<OnlineAgent>> agentsForTimes = groupAgentsByTime(agents);
        for (int timestepWithNewAgents :
                agentsForTimes.keySet()) {
            if(super.checkTimeout()) break;
            List<OnlineAgent> newArrivals = agentsForTimes.get(timestepWithNewAgents);
            // no need to change the starting positions of old agents or modify their plans, since their plans will be avoided, not modified.
            trimOutdatedConstraints(initialConstraints, timestepWithNewAgents); //avoid huge constraint sets in problems with many agents
            Solution subgroupSolution = super.solvePrioritisedPlanning(newArrivals, instance, initialConstraints);
            solutionsAtTimes.put(timestepWithNewAgents, subgroupSolution);
        }

        super.endTime = System.currentTimeMillis();
        return new OnlineSolution(solutionsAtTimes);
    }

    private void trimOutdatedConstraints(List<Constraint> initialConstraints, int minTime) {
        initialConstraints.removeIf(constraint -> constraint.time < minTime);
    }

    private Map<Integer, List<OnlineAgent>> groupAgentsByTime(List<? extends Agent> agents){
        Map<Integer, List<OnlineAgent>> result = new HashMap<>();
        ArrayList<OnlineAgent> onlineAgents = offlineToOnlineAgents(agents);

        //sort by time
        onlineAgents.sort(Comparator.comparing(OnlineAgent::getArrivalTime));

        //group by time
        for (int i = 0; i < onlineAgents.size();) {
            int currentTime = onlineAgents.get(i).arrivalTime;
            //find range with same arrival time
            int j = i;
            while(j < onlineAgents.size() && onlineAgents.get(j).arrivalTime == currentTime){
                j++;
            }
            //so the range we found is [i,j)

            result.put(currentTime, onlineAgents.subList(i, j /*end index is non-inclusive*/ ));

            i=j; //next group
        }

        return result;
    }

    /**
     * Cast agents to online agents. If they are regular Agents, create new OnlineAgents out of them with the default arrival time.
     * @param agents
     */
    private ArrayList<OnlineAgent> offlineToOnlineAgents(List<? extends Agent> agents) {
        ArrayList<OnlineAgent> onlineAgents = new ArrayList<>(agents.size());
        for (Agent a :
                agents) {
            onlineAgents.add(a instanceof OnlineAgent ? (OnlineAgent)a : new OnlineAgent(a, arrivalTimeForOfflineAgents));
        }
        return onlineAgents;
    }


}
