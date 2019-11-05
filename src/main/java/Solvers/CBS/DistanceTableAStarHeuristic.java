package Solvers.CBS;

import Instances.Agents.Agent;
import Instances.Maps.*;
import Solvers.AStar.AStarHeuristic;
import Solvers.AStar.SingleAgentAStar_Solver;

import java.time.Instant;
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
        // imp - build a dictionary from agent to distance table (also a dictionary)
        distanceDictionaries = new HashMap<>();
        for(int i = 0; i<agents.size(); i++) {
            //System.out.println("agent:   "+ i);
            Map<I_MapCell,Integer> mapForAgent=new HashMap<>(); //this map will entered to distanceDictionaries for every agent
            distanceDictionaries.put(agents.get(i),mapForAgent);
            LinkedList<I_MapCell> queue = new LinkedList<>();
            I_Coordinate i_coordinate= agents.get(i).target;
            I_MapCell mapCell=  map.getMapCell(i_coordinate);
            distanceDictionaries.get(agents.get(i)).put(mapCell,0); //distance of a graphMapCell from him self
            List<I_MapCell> neighbors = mapCell.getNeighbors(); //all the neighbors of a graphMapCell
            for (int j = 0; j < neighbors.size(); j++) {
                queue.add(neighbors.get(j));
            }
            int distance = 1;
            int count = queue.size();

            while (!(queue.isEmpty())) {
                I_MapCell i_mapCell = queue.remove(0);
                if(!(distanceDictionaries.get(agents.get(i)).containsKey(i_mapCell))){ //if a graphMapCell didn't got a distance yet
                    distanceDictionaries.get(agents.get(i)).put(i_mapCell,distance);
                    //.out.println(i_mapCell.getCoordinate()+"     distance="+ distance);
                    List<I_MapCell> neighborsCell = i_mapCell.getNeighbors(); //add all the neighbors of the current graphMapCell to  the queue
                    queue.addAll(neighborsCell);
                }
                count--;
                if(count==0) { //full level/round of neighbors is finish
                    distance++;
                    count=queue.size(); //start new level with distance plus one
                }
            }
        }
    }

    @Override
    public float getH(SingleAgentAStar_Solver.AStarState state) {
        Map<I_MapCell, Integer> relevantDictionary = distanceDictionaries.get(state.getMove().agent);
        return relevantDictionary.get(state.getMove().currLocation);
    }

    public static void main(String[] args) {
        HashMap<I_Coordinate, GraphMapCell> hashMap=new HashMap<>();
        final Enum_MapCellType e = Enum_MapCellType.EMPTY;
        final Enum_MapCellType w = Enum_MapCellType.WALL;
        Enum_MapCellType[][] map_2D_H = {
                { e, w, w, e},
                { e, e, e, e},
                { e, w, w, e},
        };
        GraphMap graphMap= MapFactory.newSimple4Connected2D_GraphMap(map_2D_H);
        Coordinate_2D[][] array=new Coordinate_2D[3][4];
        for(int i=0;i<array.length;i++){
            for(int j=0;j<array[0].length;j++){
                //array[i][j]=new Coordinate_2D(i,j);
                GraphMapCell graphMapCell= graphMap.getMapCell(new Coordinate_2D(i,j)); ///change to public
                hashMap.put(array[i][j],graphMapCell);
            }
        }


        Coordinate_2D coordinate_2D_1=new Coordinate_2D(0,0);
        Coordinate_2D coordinate_2D_2=new Coordinate_2D(0,3);
        Coordinate_2D coordinate_2D_3=new Coordinate_2D(2,0);
        Coordinate_2D coordinate_2D_4=new Coordinate_2D(2,3);

        Agent agent_1=new Agent(1,coordinate_2D_1,coordinate_2D_2);
        Agent agent_2=new Agent(2,coordinate_2D_3,coordinate_2D_4);

        List list=new LinkedList();
        list.add(agent_1);
        list.add(agent_2);

        DistanceTableAStarHeuristic distanceTableAStarHeuristic=new DistanceTableAStarHeuristic(list,graphMap);




    }
}
