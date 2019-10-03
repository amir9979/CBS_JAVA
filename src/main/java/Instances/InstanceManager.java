package Instances;

import Instances.Agents.Agent;
import Instances.Maps.I_Map;

import java.util.Stack;

public class InstanceManager {

    private String sourceDirectory;
    private I_InstanceBuilder instanceBuilder;
    private InstanceProperties instanceProperties;

    private Stack<InstancePath> instancesPaths_stack = new Stack<InstancePath>();

    public I_InstanceBuilder getInstanceBuilder() {
        return instanceBuilder;
    }

    public Stack<InstancePath> getInstancesPaths_stack() {
        return instancesPaths_stack;
    }

    public InstanceManager(String sourceDirectory,
                           I_InstanceBuilder instanceBuilder,
                           InstanceProperties properties) {

        this.sourceDirectory    = sourceDirectory;
        this.instanceBuilder    = instanceBuilder;
        this.instanceProperties = properties;

        if(this.sourceDirectory != null){
            this.addInstancesPaths_toStack( this.sourceDirectory );
        }

    }

    public InstanceManager(I_InstanceBuilder instanceBuilder){
        this.instanceBuilder = instanceBuilder;
    }


    public MAPF_Instance getSpecificInstance(InstancePath currentPath){

        String regexSeparator = "\\\\"; //  this is actually: '\\'
        String[] splitedPath = currentPath.path.split(regexSeparator); // Done - check if the "/" is correct
        String instanceName = splitedPath[splitedPath.length-1];

        return this.instanceBuilder.getInstance(instanceName, currentPath, this.instanceProperties);

    }

    public MAPF_Instance getNextInstance(){
        /* Returns null in case of an error */

        MAPF_Instance nextInstance = null;
        while(nextInstance == null){

            if(this.instancesPaths_stack.empty()){
                // NiceToHave - create new instances
                return null;
            }

            InstancePath currentPath = this.instancesPaths_stack.pop();

//            if(currentPath == null) {
//                break;
//            }


            nextInstance = getSpecificInstance(currentPath);

        }

        return nextInstance;
    }


    private void addInstancesPaths_toStack(String directoryPath){

        InstancePath[] instancePaths = this.instanceBuilder.getInstancesPaths(directoryPath);

        for (int i = 0; i < instancePaths.length ; i++) {
            this.instancesPaths_stack.push(instancePaths[i]);
        }

    }

    /***  =Instance path wrapper=  ***/

    public static class InstancePath{

        public final String path;
        public InstancePath(String path){ this.path = path; }
    }

    public static class Moving_AI_Path extends InstancePath{

        public final String scenarioPath;
        public Moving_AI_Path(String mapPath, String scenarioPath) {
            super(mapPath);
            this.scenarioPath = scenarioPath;
        }

    }




    /***  =Scenario Class=  ***/
    // todo - go over this class

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
