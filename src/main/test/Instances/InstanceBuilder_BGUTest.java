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

public class InstanceBuilder_BGUTest {

    private InstanceBuilder_BGU instanceBuilderBgu;
    InstanceManager.InstancePath instancePath;

    @Before
    public void Before() throws Exception {
        this.instanceBuilderBgu = new InstanceBuilder_BGU();
        this.instancePath = null; // init in the beginning of every test

    }

    @After
    public void After() throws Exception {
    }


    @Test
    public void getInstance_not_valid_values() {
        // imp - test
    }


    @Test
    public void getInstance_valid_values() {


        /*************      =Valid Values=     *************/
        String path = IO_Manager.buildPath(new String[]{  IO_Manager.workingDirectory,
                "test_resources\\Instances\\Instance-16-0-7-0"}); // Add path

        this.instancePath = new InstanceManager.InstancePath(path);


        /*****  =Expected values=   *****/
        Agent[] expectedAgents = new Agent[7];

        expectedAgents[0] = new Agent(0, new Coordinate_2D(9,7),
                                            new Coordinate_2D(5,2)); // 0,5,2,9,7
        // todo - add agents according to the file




        int yAxis_length = 16;
        int xAxis_length = 16;

        /*      =Create expected cellType Map=       */
        Enum_MapCellType[][] expectedCellTypeMap = new Enum_MapCellType[xAxis_length][yAxis_length];

        for (int yIndex = 0; yIndex < yAxis_length; yIndex++) {

            for (int xIndex = 0; xIndex < xAxis_length; xIndex++) {

                Enum_MapCellType cellType = Enum_MapCellType.EMPTY;
                expectedCellTypeMap[xIndex][yIndex] = cellType;
            }

        }

        GraphMap expectedMap = MapFactory.newSimple4Connected2D_GraphMap(expectedCellTypeMap);



        /*****  =Actual values=   *****/



        String instanceName = "Instance-16-0-7-0"; // Name from the InstanceManager
        MAPF_Instance mapf_instance = instanceBuilderBgu.getInstance(instanceName, this.instancePath);

        Agent[] actualAgents = mapf_instance.getAgents();
        GraphMap actualGraphMap = (GraphMap) mapf_instance.map;


        /*  =Check Agents=  */

        for (int i = 0; i < actualAgents.length; i++) {
            Assert.assertEquals(expectedAgents[i] , actualAgents[i]);
        }


        /*  =Check Map=  */
        // Blocking - check that GraphMap is fully implemented
        // imp - test the actual Graph Map




    }





    /*****      =Test Map=      *****/


    @Test
    public void buildMapAsStringArray() {
        // imp - test
    }



    @Test
    public void buildGraphMap() {
        // imp - test
    }



    @Test
    public void build_2D_cellTypeMap(){
        // imp - test
    }



    @Test
    public void getDimensions() {
        // imp - test
    }



    /*****      =Test Agents=      *****/


    @Test
    public void buildSingleAgent(){
        // imp - test
    }


    @Test
    public void buildAgents() {
        // imp - test
    }








    }