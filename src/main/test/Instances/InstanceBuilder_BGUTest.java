package Instances;

import Instances.Agents.Agent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceBuilder_BGUTest {

    private InstanceBuilder_BGU instanceBuilderBgu;
    InstanceManager.InstancePath instancePath;


    private String path;

    @Before
    public void Before() throws Exception {
        this.instanceBuilderBgu = new InstanceBuilder_BGU();
        this.path = ""; // Add path

        this.instancePath = new InstanceManager.InstancePath(path);
    }

    @After
    public void After() throws Exception {
    }

    @Test
    public void getInstance() {


        MAPF_Instance mapf_instance = instanceBuilderBgu.getInstance(this.instancePath);

        Agent[] agents = mapf_instance.getAgents();



        for (int i = 0; i < agents.length; i++) {

            // Assert.assertEquals(new Agent() , agents[i]);

        }





    }
}