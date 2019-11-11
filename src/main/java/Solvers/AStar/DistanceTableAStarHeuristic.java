package Solvers.AStar;

import Instances.Agents.Agent;
import Instances.Maps.*;

import java.util.*;

/**
 * A {@link AStarHeuristic} that uses a pre-calculated dictionary of distances from possible goal locations to every
 * accessible {@link I_MapCell location} to provide a perfectly tight heuristic.
 */
public class DistanceTableAStarHeuristic implements AStarHeuristic {
    // nicetohave avoid duplicates (when two agents have the same goal)

    private Map<Agent, Map<I_MapCell, Integer>> distanceDictionaries;

    public Map<Agent, Map<I_MapCell, Integer>> getDistanceDictionaries() {
        return distanceDictionaries;
    }

    public DistanceTableAStarHeuristic(List<? extends Agent> agents, I_Map map) {

        this.distanceDictionaries = new HashMap<>();
        for (int i = 0; i < agents.size(); i++) {

            //this map will entered to distanceDictionaries for every agent
            Map<I_MapCell, Integer> mapForAgent = new HashMap<>();
            this.distanceDictionaries.put(agents.get(i), mapForAgent);
            LinkedList<I_MapCell> queue = new LinkedList<>();
            I_Coordinate i_coordinate = agents.get(i).target;
            I_MapCell mapCell = map.getMapCell(i_coordinate);

            //distance of a graphMapCell from itself
            this.distanceDictionaries.get(agents.get(i)).put(mapCell, 0);

            //all the neighbors of a graphMapCell
            List<I_MapCell> neighbors = mapCell.getNeighbors();
            for (int j = 0; j < neighbors.size(); j++) {
                queue.add(neighbors.get(j));
            }

            int distance = 1;
            int count = queue.size();

            while (!(queue.isEmpty())) {
                I_MapCell i_mapCell = queue.remove(0);

                //if a graphMapCell didn't got a distance yet
                if (!(this.distanceDictionaries.get(agents.get(i)).containsKey(i_mapCell))) {
                    this.distanceDictionaries.get(agents.get(i)).put(i_mapCell, distance);

                    //add all the neighbors of the current graphMapCell to  the queue
                    List<I_MapCell> neighborsCell = i_mapCell.getNeighbors();
                    queue.addAll(neighborsCell);
                }

                count--;
                if (count == 0) { //full level/round of neighbors is finish
                    distance++;
                    count = queue.size(); //start new level with distance plus one
                }
            }
        }
    }

    @Override
    public float getH(SingleAgentAStar_Solver.AStarState state) {
        Map<I_MapCell, Integer> relevantDictionary = this.distanceDictionaries.get(state.getMove().agent);
        return relevantDictionary.get(state.getMove().currLocation);
    }

}
