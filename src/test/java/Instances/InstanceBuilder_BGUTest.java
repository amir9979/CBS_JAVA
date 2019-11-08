package Instances;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.Maps.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InstanceBuilder_BGUTest {

    // testme
    // todo - add test with obstacles

    private InstanceBuilder_BGU instanceBuilderBgu = new InstanceBuilder_BGU();

    private final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    private final Enum_MapCellType w = Enum_MapCellType.WALL;





    /*  Check that map is valid  */
    private boolean checkAllMapCells(Enum_MapCellType[][] expectedCellTypeMap, I_Map actualMap){

        for (int xAxis_value = 0; xAxis_value < expectedCellTypeMap.length; xAxis_value++) {
            for (int yAxis_value = 0; yAxis_value < expectedCellTypeMap[0].length; yAxis_value++) {
                // Create coordinate
                I_Coordinate coordinate = new Coordinate_2D(xAxis_value, yAxis_value);
                // Get the relevant mapCell
                I_MapCell actualMapCell = actualMap.getMapCell(coordinate);

                // Check that wall doesnt exists in actualMap
                if( actualMapCell == null && expectedCellTypeMap[xAxis_value][yAxis_value] == w){ continue; }

                // check that actualMapCell is the same as the expectedCellTypeMap[xAxis_value][yAxis_value]
                if( actualMapCell != null && actualMapCell.getType() == expectedCellTypeMap[xAxis_value][yAxis_value]){ continue; }

                Assert.assertFalse(true);
                return false; // Invalid value
            }
        }

        return true; // All cells are valid
    }




    @Test
    public void prepareInstances_Instance_16_0_7() {

        /*  Set properties  */
       InstanceProperties instanceProperties = new InstanceProperties(
                new MapDimensions(new int[]{16,16}),(float)0,new int[]{7,10,15}
        );


        /*  Set path  */
       String path_16_0_7 = IO_Manager.buildPath(
                                                new String[]{  IO_Manager.testResources_Directory,
                                                               "Instances\\Instance-16-0-7-0"}
       );

        InstanceManager.InstancePath instancePath_Instance_16_0_7 = new InstanceManager.InstancePath(path_16_0_7);


        /*****  =Expected values=   *****/
        List<Agent> expectedAgents = new ArrayList<Agent>(7);
        addAgents_Instance_16_0_7(expectedAgents);




        int yAxis_length = 16;
        int xAxis_length = 16;

        /*      =Create expected cellType Map=       */
        Enum_MapCellType[][] expectedCellTypeMap = new Enum_MapCellType[xAxis_length][yAxis_length];

        for (int xIndex = 0; xIndex < xAxis_length; xIndex++) {
            for (int yIndex = 0; yIndex < yAxis_length; yIndex++) {

                Enum_MapCellType cellType = Enum_MapCellType.EMPTY;
                expectedCellTypeMap[xIndex][yIndex] = cellType;
            }
        }


        /*****  =Actual values=   *****/

        String instanceName = "Instance-16-0-7"; // Name from the InstanceManager
        this.instanceBuilderBgu.prepareInstances(instanceName, instancePath_Instance_16_0_7, instanceProperties);
        MAPF_Instance mapf_instance = instanceBuilderBgu.getNextExistingInstance();

        Assert.assertNotNull(mapf_instance);

        List<Agent> actualAgents = mapf_instance.agents;

        /*  =Check Agents=  */
        Assert.assertTrue(actualAgents.size() == expectedAgents.size());
        for (int i = 0; i < actualAgents.size(); i++) {
            Assert.assertEquals(expectedAgents.get(i) , actualAgents.get(i));
        }


        /*  = Check map =  */
        I_Map actualMap = mapf_instance.map;
        Assert.assertTrue(checkAllMapCells(expectedCellTypeMap,actualMap));


    }


    private void addAgents_Instance_16_0_7(List<Agent> expectedAgents){

        /*
        Agents from file: instance-16-0-7-0
        Agent line meaning: < id > , < x_target , y_target > , < x_start , y_start >
        */


        // 0,5,2,9,7
        expectedAgents.add( new Agent(0,
                            new Coordinate_2D(9,7),
                            new Coordinate_2D(5,2)));
        // 1,1,7,10,6
        expectedAgents.add( new Agent(1,
                            new Coordinate_2D(10,6),
                            new Coordinate_2D(1,7)));
        // 2,12,10,3,1
        expectedAgents.add( new Agent(2,
                            new Coordinate_2D(3,1),
                            new Coordinate_2D(12,10)));
        // 3,4,11,13,8
        expectedAgents.add( new Agent(3,
                            new Coordinate_2D(13,8),
                            new Coordinate_2D(4,11)));
        // 4,13,6,10,1
        expectedAgents.add( new Agent(4,
                            new Coordinate_2D(10,1),
                            new Coordinate_2D(13,6)));
        // 5,1,1,15,10
        expectedAgents.add( new Agent(5,
                            new Coordinate_2D(15,10),
                            new Coordinate_2D(1,1)));
        // 6,7,7,7,11
        expectedAgents.add( new Agent(6,
                            new Coordinate_2D(7,11),
                            new Coordinate_2D(7,7)));
    }







    @Test
    public void prepareInstances_Instance_8_15_5() {

        /*  Set path  */
        String path_8_15_5 = IO_Manager.buildPath(
                                    new String[]{   IO_Manager.testResources_Directory,
                                    "Instances\\\\Instance-8-15-5-17 - hard one - cost 29 and some corridors"}
        );

        InstanceManager.InstancePath instancePath_Instance_8_15_5 = new InstanceManager.InstancePath(path_8_15_5);


        /*  Set properties  */
        InstanceProperties instanceProperties = new InstanceProperties(
                new MapDimensions(new int[]{8,8}), (float)0.15, new int[]{7,5,15}
        );





        /*****  =Expected values=   *****/
        List<Agent> expectedAgents = new ArrayList<Agent>(5);
        addAgents_Instance_8_15_5(expectedAgents);



        /*      =Create expected cellType Map=       */

        /* Note: Map is twisted, not like in the file
                ...@..@@
                ........
                ......@.
                ..@....@
                .......@
                ........
                ........
                .....@@.

        */
        Enum_MapCellType[][] expectedCellTypeMap = new Enum_MapCellType[][]{
                {e,e,e,w,e,e,w,w},
                {e,e,e,e,e,e,e,e},
                {e,e,e,e,e,e,w,e},
                {e,e,w,e,e,e,e,w},
                {e,e,e,e,e,e,e,w},
                {e,e,e,e,e,e,e,e},
                {e,e,e,e,e,e,e,e},
                {e,e,e,e,e,w,w,e},

        };


        /*****  =Actual values=   *****/

        String instanceName = "Instance-8-15-5"; // Name from the InstanceManager
        this.instanceBuilderBgu.prepareInstances(instanceName, instancePath_Instance_8_15_5, instanceProperties);
        MAPF_Instance mapf_instance = instanceBuilderBgu.getNextExistingInstance();

        Assert.assertNotNull(mapf_instance);

        List<Agent> actualAgents = mapf_instance.agents;


        /*  =Check Agents=  */
        Assert.assertTrue(actualAgents.size() == expectedAgents.size());
        for (int i = 0; i < actualAgents.size(); i++) {
            Assert.assertEquals(expectedAgents.get(i) , actualAgents.get(i));
        }


        /*  = Check map =  */
        I_Map actualMap = mapf_instance.map;
        Assert.assertTrue(checkAllMapCells(expectedCellTypeMap,actualMap));


    }


    private void addAgents_Instance_8_15_5(List<Agent> expectedAgents){

        /*
        Agents from file: instance-8-15-5
        Agent line meaning: < id > , < x_target , y_target > , < x_start , y_start >
        */


        // 0,7,2,5,1
        expectedAgents.add( new Agent(0,
                            new Coordinate_2D(5,1),
                            new Coordinate_2D(7,2)));
        // 1,4,5,3,5
        expectedAgents.add( new Agent(1,
                            new Coordinate_2D(3,5),
                            new Coordinate_2D(4,5)));
        // 2,7,1,7,1
        expectedAgents.add( new Agent(2,
                            new Coordinate_2D(7,1),
                            new Coordinate_2D(7,1)));
        // 3,0,7,3,1
        expectedAgents.add( new Agent(3,
                            new Coordinate_2D(3,1),
                            new Coordinate_2D(0,7)));
        // 4,5,1,2,5
        expectedAgents.add( new Agent(4,
                            new Coordinate_2D(2,5),
                            new Coordinate_2D(5,1)));
    }


}