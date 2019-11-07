package Solvers.AStar;

import Instances.Agents.Agent;
import Instances.Maps.*;
import Solvers.AStar.DistanceTableAStarHeuristic;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DistanceTableAStarHeuristicTest {

    final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    final Enum_MapCellType w = Enum_MapCellType.WALL;
    Enum_MapCellType[][] map_2D_H = {
            { e, w, w, e},
            { e, e, e, e},
            { e, w, w, e},
    };

    I_Map map= MapFactory.newSimple4Connected2D_GraphMap(map_2D_H);

    /*   = Equals Maps =    */
    private boolean equalsAllAgentMap(Map<Agent, Map<I_MapCell, Integer>> expectedValues, Map<Agent, Map<I_MapCell, Integer>> actualValues){

        if( expectedValues.size() != actualValues.size() ){
            return false;
        }
        for (Map.Entry<Agent, Map<I_MapCell, Integer>> agentMapEntry: expectedValues.entrySet()){

            Agent agent = agentMapEntry.getKey();
            Map<I_MapCell, Integer> expectedCellMap = expectedValues.get(agent);
            Map<I_MapCell, Integer> actualCellMap = actualValues.get(agent);

            if (! this.equalsAllCellMap(expectedCellMap,actualCellMap)){
                return false;
            }
        }
        return true;
    }

    private boolean equalsAllCellMap(Map<I_MapCell, Integer> expectedCellMap, Map<I_MapCell, Integer> actualCellMap) {
        if( expectedCellMap.size() != actualCellMap.size() ){
            return false;
        }
        for (Map.Entry<I_MapCell,Integer> MapCellEntry: expectedCellMap.entrySet()){

            I_MapCell mapCell = MapCellEntry.getKey();
            int expectedDistance = expectedCellMap.get(mapCell);
            int actualDistance = actualCellMap.get(mapCell);

            if ( expectedDistance != actualDistance){
                return false;
            }
        }
        return true;
    }


    @Test
    public void test(){
        HashMap<I_Coordinate, I_MapCell> hashMap=new HashMap<>();

        Coordinate_2D[][] array=new Coordinate_2D[3][4];
        for(int i=0;i<array.length;i++){
            for(int j=0;j<array[0].length;j++){
                I_MapCell mapCell= map.getMapCell(new Coordinate_2D(i,j)); ///change to public
                hashMap.put(array[i][j],mapCell);
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

        /*      = Expected values =     */

        Map<Agent, Map<I_MapCell, Integer>> expected=new HashMap<>();
        Map<I_MapCell, Integer> insideMap=new HashMap<>();
        Map<I_MapCell, Integer> insideMap2=new HashMap<>();
        insideMap.put(map.getMapCell(new Coordinate_2D(1,3)),1);
        insideMap.put(map.getMapCell(new Coordinate_2D(2,3)),2);
        insideMap.put(map.getMapCell(new Coordinate_2D(1,2)),2);
        insideMap.put(map.getMapCell(new Coordinate_2D(1,1)),3);
        insideMap.put(map.getMapCell(new Coordinate_2D(1,0)),4);
        insideMap.put(map.getMapCell(new Coordinate_2D(0,0)),5);
        insideMap.put(map.getMapCell(new Coordinate_2D(2,0)),5);
        insideMap.put(map.getMapCell(new Coordinate_2D(0,3)),0);

        insideMap2.put(map.getMapCell(new Coordinate_2D(1,3)),1);
        insideMap2.put(map.getMapCell(new Coordinate_2D(0,3)),2);
        insideMap2.put(map.getMapCell(new Coordinate_2D(1,2)),2);
        insideMap2.put(map.getMapCell(new Coordinate_2D(1,1)),3);
        insideMap2.put(map.getMapCell(new Coordinate_2D(1,0)),4);
        insideMap2.put(map.getMapCell(new Coordinate_2D(0,0)),5);
        insideMap2.put(map.getMapCell(new Coordinate_2D(2,0)),5);
        insideMap2.put(map.getMapCell(new Coordinate_2D(2,3)),0);

        expected.put(agent_1,insideMap);
        expected.put(agent_2,insideMap2);

        /*  = Test actual values =  */
        DistanceTableAStarHeuristic distanceTableAStarHeuristic=new DistanceTableAStarHeuristic(list,map);

        Assert.assertTrue(equalsAllAgentMap(expected, distanceTableAStarHeuristic.getDistanceDictionaries()));
    }

}