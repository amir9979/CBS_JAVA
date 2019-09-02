package Instances;

import IO_Package.IO_Manager;
import Instances.Agents.Agent;
import Instances.Maps.I_Map;

import java.util.ArrayList;
import java.util.Stack;

public class InstanceManager {

    private IO_Manager io_manager;
    private final InstanceFormat instanceFormat;
    private final String sourceDirectory;

    private Stack<InstancePath> instancesPaths;


    public InstanceManager(InstanceFormat instanceFormat, String sourceDirectory, InstanceProperties properties) {
        this.io_manager = IO_Manager.getInstance();
        this.instanceFormat = instanceFormat;
        this.sourceDirectory = sourceDirectory;

        this.instancesPaths = this.getInstancesPaths(this.sourceDirectory, this.instanceFormat);


    }



    public MAPF_Instance getNextInstance(){
        MAPF_Instance nextInstance = null;
        while(nextInstance == null){
            InstancePath nextPath = instancesPaths.pop();
            if(nextPath == null){break;}

            switch (this.instanceFormat){
                case MOVING_AI:
                    //imp nextPath= f(InstancePath):MAPF_Instance in this format
                    break;
                case SEARCH_AT_BGU:
                    // imp nextPath= f(InstancePath):MAPF_Instance in this format
                    break;
            }
        }
        return nextInstance;

    }


    private Stack<InstancePath> getInstancesPaths(String sourceDirectory,
                                                      InstanceFormat instanceFormat){

        // Todo - filter by properties?
        return null; // imp
    }



    public enum InstanceFormat{
        MOVING_AI,
        SEARCH_AT_BGU
    }


    public static class InstancePath{

        public final String path;

        public InstancePath(String path){
            this.path = path;
        }


    }

    private class Moving_AI_Path extends InstancePath{

        public final String scenarioPath;

        public Moving_AI_Path(String mapPath, String scenarioPath) {
            super(mapPath);
            this.scenarioPath = scenarioPath;
        }



    }




    private class Scenario {

        private String name;
        private I_Map map;
        private Agent[] agents;

        public Scenario(String name, I_Map map, Agent[] agents) {
            this.name = name;
            this.map = map;
            this.agents = agents;
        }

        public MAPF_Instance getInstance(int numAgents){
            return null; //imp
        }

    }
}
