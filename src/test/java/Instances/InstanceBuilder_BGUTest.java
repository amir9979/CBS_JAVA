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
    private InstanceManager.InstancePath instancePath_Instance_16_0_7;
    private InstanceProperties instanceProperties = new InstanceProperties(
                                                        new MapDimensions(new int[]{16,16}),(float)0,new int[]{7,10,15}
                                                    );



    @Before
    public void Before() throws Exception {
        this.instancePath_Instance_16_0_7 = null; // init in the beginning of every test

    }

    @After
    public void After() throws Exception {
    }


    @Test
    public void prepareInstances_Instance_16_0_7() {


        /*************      =Valid Values=     *************/
        String path_16_0_7 = IO_Manager.buildPath(
                                            new String[]{   IO_Manager.testResources_Directory,
                                            "Instances\\Instance-16-0-7-0"}
        );


        this.instancePath_Instance_16_0_7 = new InstanceManager.InstancePath(path_16_0_7);


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

        // GraphMap expectedMap = MapFactory.newSimple4Connected2D_GraphMap(expectedCellTypeMap);



        /*****  =Actual values=   *****/



        String instanceName = "Instance-16-0-7"; // Name from the InstanceManager
        this.instanceBuilderBgu.prepareInstances(instanceName, this.instancePath_Instance_16_0_7, this.instanceProperties);
        MAPF_Instance mapf_instance = instanceBuilderBgu.getNextExistingInstance();

        Assert.assertNotNull(mapf_instance);

        List<Agent> actualAgents = mapf_instance.agents;

        /*  =Check Agents=  */

        for (int i = 0; i < actualAgents.size(); i++) {
            Assert.assertEquals(expectedAgents.get(i) , actualAgents.get(i));
        }



        // Blocking - add map test

        /*  =Check Map=  */










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



}