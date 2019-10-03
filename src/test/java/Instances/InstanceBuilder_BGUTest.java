package Instances;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.Maps.Coordinate_2D;
import Instances.Maps.Enum_MapCellType;
import Instances.Maps.GraphMap;
import Instances.Maps.MapFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InstanceBuilder_BGUTest {

    // testme

    private InstanceBuilder_BGU instanceBuilderBgu;
    private InstanceManager.InstancePath instancePath;
    private InstanceProperties instanceProperties = new InstanceProperties(
                                                        new int[]{16,16},(float)0,7,"-"
                                                    );

    private final String path = IO_Manager.buildPath(
                                                        new String[]{   IO_Manager.testResources_Directory,
                                                                        "Instances\\Instance-16-0-7-0"}
                                                     );

//
//    private final String path = IO_Manager.buildPath(
//            new String[]{   IO_Manager.testResources_Directory,
//                    "Instances\\Instance-8-15-5-17 - hard one - cost 29 and some corridors"}
//    );



    @Before
    public void Before() throws Exception {
        this.instanceBuilderBgu = new InstanceBuilder_BGU();
        this.instancePath = null; // init in the beginning of every test

    }

    @After
    public void After() throws Exception {
    }


    @Test
    public void getInstance_valid_values() {


        /*************      =Valid Values=     *************/


        this.instancePath = new InstanceManager.InstancePath(path);


        /*****  =Expected values=   *****/
        List<Agent> expectedAgents = new ArrayList<Agent>(7);
        addAgents(expectedAgents);




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

        GraphMap expectedMap = MapFactory.newSimple4Connected2D_GraphMap(expectedCellTypeMap);



        /*****  =Actual values=   *****/



        String instanceName = "Instance-16-0-7"; // Name from the InstanceManager
        MAPF_Instance mapf_instance = instanceBuilderBgu.getInstance(instanceName, this.instancePath, this.instanceProperties);

        Assert.assertNotNull(mapf_instance);

        List<Agent> actualAgents = mapf_instance.agents;

        /*  =Check Agents=  */

        for (int i = 0; i < actualAgents.size(); i++) {
            Assert.assertEquals(expectedAgents.get(i) , actualAgents.get(i));
        }








    }


    private void addAgents(List<Agent> expectedAgents){

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



//
//
//    /*****      =Test Map=      *****/
//
//
//    @Test
//    public void buildMapAsStringArray() {
//        // imp - test
//    }
//
//
//
//    @Test
//    public void buildGraphMap() {
//        // imp - test
//    }
//
//
//
//    @Test
//    public void build_2D_cellTypeMap(){
//        // imp - test
//    }
//
//
//
//    @Test
//    public void getDimensions() {
//        // imp - test
//    }
//
//
//
//    /*****      =Test Agents=      *****/
//
//
//    @Test
//    public void buildSingleAgent(){
//        // imp - test
//    }
//
//
//    @Test
//    public void buildAgents() {
//        // imp - test
//    }


    }