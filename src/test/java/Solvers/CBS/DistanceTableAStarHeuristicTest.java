package Solvers.CBS;

import Instances.Agents.Agent;
import Instances.Maps.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DistanceTableAStarHeuristicTest {

    final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    final Enum_MapCellType w = Enum_MapCellType.WALL;
    Enum_MapCellType[][] map_2D_H = {
            { e, w, w, e},
            { e, e, e, e},
            { e, w, w, e},
    };

    I_Map map= MapFactory.newSimple4Connected2D_GraphMap(map_2D_H);

    private boolean equalsMaps(Map<Agent, Map<I_MapCell, Integer>> result, Map<Agent, Map<I_MapCell, Integer>> expected){

        if( result.size() != expected.size() ){
            return false;
        }
        Set set1=result.keySet();
        Set set2=expected.keySet();

        if(!(set2.containsAll(set1))){
            return false;
        }

//        Map<I_MapCell, Integer> map1= (Map<I_MapCell, Integer>) result.values();
//        Map<I_MapCell, Integer> map2= (Map<I_MapCell, Integer>) result.values();
//
//        Set set3=result.keySet();
//        Set set4=expected.keySet();
//
//        if(!(set3.containsAll(set4))){
//            return false;
//        }
//
//        Set set5= (Set) map1.values();
//        Set set6= (Set) map2.values();
//
//        if(!(set5.containsAll(set6))){
//            return false;
//        }



//        for(int i=0;i<result.size();i++){
//            System.out.println("1:  "+result.get(i));
//            System.out.println("2:  "+expected.get(result.get(i)));
//            if( result.get(i).size() != expected.get(result.get(i)).size() ){
//                return false;
//            }
//
//            if(!(result.get(i).containsKey(expected.get(result.get(i).keySet())))){
//                return false;
//            }
//            for(int j=0;j<result.get(i).size();j++){
//                //System.out.println("3:  "+expected.get(result.get(i).get(j)));
//                if(!(result.get(i).containsValue(expected.get(result.get(i).get(j))))){
//                    return false;
//                }
//            }
//        }
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
       // Agent agent_2=new Agent(2,coordinate_2D_3,coordinate_2D_4);

        List list=new LinkedList();
        list.add(agent_1);
       // list.add(agent_2);

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
       // expected.put(agent_2,insideMap2);

        /*  = Test actual values =  */
        DistanceTableAStarHeuristic distanceTableAStarHeuristic=new DistanceTableAStarHeuristic(list,map);

        Assert.assertTrue(equalsMaps(distanceTableAStarHeuristic.getDistanceDictionaries(),expected));
    }

}