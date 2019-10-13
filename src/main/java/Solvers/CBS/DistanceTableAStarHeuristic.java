package Solvers.CBS;

import Instances.Agents.Agent;
import Instances.Maps.I_Map;
import Instances.Maps.I_MapCell;
import Solvers.AStar.AStarHeuristic;
import Solvers.AStar.SingleAgentAStar_Solver;

import java.util.List;
import java.util.Map;

/**
 * A {@link AStarHeuristic} that uses a pre-calculated dictionary of distances from possible goal locations to every
 * accessible {@link I_MapCell location} to provide a perfectly tight heuristic.
 */
public class DistanceTableAStarHeuristic implements AStarHeuristic {
    // nicetohave avoid duplicates (when two agents have the same goal)

    private Map<Agent, Map<I_MapCell, Integer>> distanceDictionaries;

    public DistanceTableAStarHeuristic(List<? extends Agent> agents, I_Map map) {
        // imp - build a dictionary from agent to distance table (also a dictionary)
        this.distanceDictionaries = null;
    }

    @Override
    public float getH(SingleAgentAStar_Solver.AStarState state) {
        Map<I_MapCell, Integer> relevantDictionary = distanceDictionaries.get(state.getMove().agent);
        return relevantDictionary.get(state.getMove().currLocation);
    }
}
