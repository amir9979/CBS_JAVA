package Instances;

import IO_Package.IO_Manager;
import IO_Package.Reader;
import Instances.Agents.Agent;
import Instances.Maps.Coordinate_2D;
import Instances.Maps.GraphMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceBuilder_BGUTest {

    private InstanceBuilder_BGU_New instanceBuilderBgu;
    InstanceManager.InstancePath instancePath;

    @Before
    public void Before() throws Exception {
        this.instanceBuilderBgu = new InstanceBuilder_BGU_New();
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


        /*  =Expected values=   */
        Agent[] expectedAgents = new Agent[7];

        expectedAgents[0] = new Agent(0, new Coordinate_2D(9,7),
                                            new Coordinate_2D(5,2)); // 0,5,2,9,7
        // todo - add agents according to the file

        String[] expectedMapAsString = new String[16];
        for (int i = 0; i < expectedMapAsString.length; i++) {
            String line = "................";
            expectedMapAsString[i] = line;
        }

        GraphMap expectedMap = new GraphMap(expectedMapAsString);



        /*  =Actual values=   */



        String instanceName = "Instance-16-0-7-0"; // Name from the InstanceManager
        MAPF_Instance mapf_instance = instanceBuilderBgu.getInstance(instanceName, this.instancePath);

        Agent[] agents = mapf_instance.getAgents();
        GraphMap graphMap = (GraphMap) mapf_instance.map;


        /*  =Check Agents=  */
        // imp - Check the expected agents


        for (int i = 0; i < agents.length; i++) {

            // Assert.assertEquals(expectedAgents[i] , agents[i]);

        }


        /*  =Check Map=  */
        // Blocking - check that GraphMap is fully implemented
        // imp - check the Graph Map




    }




    @Test
    public void buildSingleAgent(){
        // imp - test
    }


    @Test
    public void buildAgents() {
        // imp - test
    }


    @Test
    public void getDimensions() {
        // imp - test
    }


    @Test
    public void buildMap() {
        // imp - test
    }



    }