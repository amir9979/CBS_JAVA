package Instances;

import IO_Package.IO_Manager;
import Instances.Maps.MapDimensions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceBuilder_MovingAITest {

    InstanceBuilder_MovingAI instanceBuilder_movingAI = new InstanceBuilder_MovingAI();

    @Before
    public void before(){

    }



    @Test
    public void prepareInstance_16_0_7(){
        String expectedMapPath = IO_Manager.buildPath(new String[]{IO_Manager.testResources_Directory,"Instances\\MovingAI\\Instance-16-0-7.map"});
        String expectedScenPath = IO_Manager.buildPath(new String[]{IO_Manager.testResources_Directory,"Instances\\MovingAI\\Instance-16-0-7.map.scen"});
        InstanceManager.Moving_AI_Path expectedMovingAiPath = new InstanceManager.Moving_AI_Path(expectedMapPath,expectedScenPath);

        InstanceProperties properties = new InstanceProperties(new MapDimensions(new int[]{16,16}),(float)-1, new int[]{5,7,10});
        this.instanceBuilder_movingAI.prepareInstances("Default name", expectedMovingAiPath,properties);

        // Check that instance was created successfully and added to the list
        MAPF_Instance nextInstance = this.instanceBuilder_movingAI.getNextExistingInstance();
        while (nextInstance != null){
            System.out.println("Found Instance-16-0-7");
        }

    }



    @Test
    /*  Must have files in resources: resources\\Instances\\MovingAI\\Instance-16-0-7.map    */
    public void getInstancesPath_16_0_7() {

        String expectedMapPath = IO_Manager.buildPath(new String[]{IO_Manager.testResources_Directory,"Instances\\MovingAI\\Instance-16-0-7.map"});
        String expectedScenPath = IO_Manager.buildPath(new String[]{IO_Manager.testResources_Directory,"Instances\\MovingAI\\Instance-16-0-7.map.scen"});
        InstanceManager.Moving_AI_Path expectedMovingAiPath = new InstanceManager.Moving_AI_Path(expectedMapPath,expectedScenPath);


        InstanceManager.InstancePath[] instancePaths = instanceBuilder_movingAI.getInstancesPaths(IO_Manager.buildPath(new String[]{IO_Manager.testResources_Directory,"Instances\\MovingAI"}));

        for (InstanceManager.InstancePath path :instancePaths ) {
            InstanceManager.Moving_AI_Path moving_ai_path = (InstanceManager.Moving_AI_Path) path;
            if (moving_ai_path.equals(expectedMovingAiPath)){
                Assert.assertTrue(true); // Found the expected path
                break;
            }
        }

    }
}