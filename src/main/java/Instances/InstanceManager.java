package Instances;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.Maps.I_Map;

import java.util.Stack;

public class InstanceManager {

    // Todo - no use for instanceFormat if we pass an InstanceBuilder

    private IO_Manager io_manager;
    // private final InstanceFormat instanceFormat;
    private final String sourceDirectory;
    private I_InstanceBuilder instanceBuilder;

    private Stack<InstancePath> instancesPaths_stack;


    public InstanceManager( /* InstanceFormat instanceFormat, */
                           String sourceDirectory,
                           I_InstanceBuilder instanceBuilder,
                           InstanceProperties properties) {

        this.io_manager         = IO_Manager.getInstance();
        this.sourceDirectory    = sourceDirectory;
        this.instanceBuilder    = instanceBuilder;
        this.instancesPaths_stack = new Stack<InstancePath>();
        this.addInstancesPaths_toStack();

        // this.instancesPaths_stack     = this.addInstancesPaths_toStack(this.sourceDirectory, this.instanceFormat);
        // this.instanceFormat     = instanceFormat;

    }



    public MAPF_Instance getNextInstance(){
        /* Returns null in case of an error */

        MAPF_Instance nextInstance = null;
        while(nextInstance == null){
            InstancePath nextPath = instancesPaths_stack.pop();
            if(nextPath == null){
                break;
            }

            String instanceName = ""; // imp - get instance name

            nextInstance = this.instanceBuilder.getInstance(instanceName, nextPath);

        }

        return nextInstance;
    }


    private void addInstancesPaths_toStack(){

        String directoryPath = this.sourceDirectory; // todo - might be a different path ?
        InstancePath[] instancePaths = this.instanceBuilder.getInstancesPaths(directoryPath);

        for (int i = 0; i < instancePaths.length ; i++) {
            this.instancesPaths_stack.push(instancePaths[i]);
        }

    }



//    public enum InstanceFormat{
//        MOVING_AI,
//        SEARCH_AT_BGU
//    }



    /***  =Instance path wrapper=  ***/

    public static class InstancePath{

        public final String path;
        public InstancePath(String path){ this.path = path; }
    }

    private class Moving_AI_Path extends InstancePath{

        public final String scenarioPath;
        public Moving_AI_Path(String mapPath, String scenarioPath) {
            super(mapPath);
            this.scenarioPath = scenarioPath;
        }

    }




    /***  =Scenario Class=  ***/

    private class Scenario {

        private String name;
        private I_Map map;
        private Agent[] agents;

        public Scenario(String name, I_Map map, Agent[] agents) {
            this.name   = name;
            this.map    = map;
            this.agents = agents;
        }

        public MAPF_Instance getInstance(int numAgents){
            return null; //imp
        }

    }
}
